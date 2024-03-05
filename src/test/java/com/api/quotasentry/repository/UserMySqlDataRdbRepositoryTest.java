package com.api.quotasentry.repository;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.api.quotasentry.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jdbc.DataSourceBuilder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserMySqlDataRdbRepositoryTest {

    private final String USER_ID = UUID.randomUUID().toString();
    private final String SEEDED_USER_ID = UUID.randomUUID().toString();
    private ConnectionProvider connectionProvider;
    private UserMySqlDataRdbRepository userRepository;
    private ListAppender<ILoggingEvent> listAppender;
    private Logger logger;

    @BeforeEach
    void setUp() throws SQLException {
        // Use H2 in-memory database for testing
        DataSource dataSource = DataSourceBuilder
                .create()
                .url("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false")
                .username("sa")
                .password("")
                .driverClassName("org.h2.Driver")
                .build();

        // Initialize connectionProvider with the DataSource
        this.connectionProvider = new ConnectionProvider(dataSource);

        // Create the repository with the connectionProvider
        this.userRepository = new UserMySqlDataRdbRepository(connectionProvider);

        // Initialize default logger (redefine in test for another class)
        setupLogger(UserMySqlDataRdbRepository.class);

        // Create test tables and data
        createTestTable();
        seedTestData();
    }

    @AfterEach
    void cleanUp() throws SQLException {
        clearTestData();
    }

    @Test
    public void testCreateAndGetUser() {
        LocalDateTime localDateTime = LocalDateTime.now();
        User user = new User(USER_ID, "Frank", "Doe", null, 3, true, false, localDateTime, localDateTime);
        userRepository.createUser(user);

        User retrievedUser = userRepository.getUser(USER_ID);

        assertEquals(user.getId(), retrievedUser.getId());
        assertEquals(user.getFirstName(), retrievedUser.getFirstName());
        assertEquals(user.getLastName(), retrievedUser.getLastName());
        assertNull(retrievedUser.getLastLoginTimeUtc());
        assertEquals(user.getRequests(), retrievedUser.getRequests());
        assertTrue(retrievedUser.isLocked());
        assertFalse(retrievedUser.isDeleted());
        assertEquals(user.getCreated(), retrievedUser.getCreated());
        assertEquals(user.getModified(), retrievedUser.getModified());
    }

    @Test
    public void testUpdateAndGetUser() {
        User retrievedUser = userRepository.getUser(SEEDED_USER_ID);   // get seeded user
        retrievedUser.setFirstName("Franky");
        retrievedUser.setLastName("Sinatra");
        retrievedUser.setLocked(true);
        retrievedUser.setRequests(5);
        userRepository.updateUser(SEEDED_USER_ID, retrievedUser);
        User updatedUser = userRepository.getUser(SEEDED_USER_ID);

        assertEquals(updatedUser.getId(), retrievedUser.getId());
        assertEquals(updatedUser.getFirstName(), retrievedUser.getFirstName());
        assertEquals(updatedUser.getLastName(), retrievedUser.getLastName());
        assertNull(updatedUser.getLastLoginTimeUtc());  // validating the last login not changed due to an update
        assertEquals(updatedUser.getRequests(), retrievedUser.getRequests());
        assertTrue(retrievedUser.isLocked());
        assertFalse(retrievedUser.isDeleted());
        assertEquals(updatedUser.getCreated(), retrievedUser.getCreated());
        assertTrue(updatedUser.getModified().isAfter(retrievedUser.getModified())); // validating the 'modified' changed due to an update
    }

    @Test
    public void testConsumeQuotaAndGetUser() {
        User retrievedUser = userRepository.getUser(SEEDED_USER_ID);   // get seeded user
        userRepository.consumeQuota(SEEDED_USER_ID);
        User updatedUser = userRepository.getUser(SEEDED_USER_ID);

        assertEquals(updatedUser.getId(), retrievedUser.getId());
        assertEquals(updatedUser.getFirstName(), retrievedUser.getFirstName());
        assertEquals(updatedUser.getLastName(), retrievedUser.getLastName());
        assertNotNull(updatedUser.getLastLoginTimeUtc());  // validating the last login did chang due to an update
        assertEquals(updatedUser.getLastLoginTimeUtc().truncatedTo(ChronoUnit.SECONDS), LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        assertTrue(updatedUser.getRequests() - retrievedUser.getRequests() == 1);   // validating the quota increased by 1
        assertFalse(retrievedUser.isLocked());
        assertFalse(retrievedUser.isDeleted());
        assertEquals(updatedUser.getCreated(), retrievedUser.getCreated());
        assertTrue(updatedUser.getModified().isAfter(retrievedUser.getModified()));
    }

    @Test
    public void testDeleteAndGetUser() {
        setupLogger(UserDataRdbBaseRepository.class);

        User retrievedUser = userRepository.getUser(SEEDED_USER_ID);   // get seeded user
        retrievedUser.setFirstName("Franky");
        retrievedUser.setLastName("Sinatra");
        retrievedUser.setLocked(true);
        retrievedUser.setDeleted(true); // user softly deleted
        userRepository.updateUser(SEEDED_USER_ID, retrievedUser);
        User updatedUser = userRepository.getUser(SEEDED_USER_ID);
        assertNull(updatedUser);

        String expectedMsg = String.format("User with id %s not found", SEEDED_USER_ID);
        assertNull(userRepository.getUser(SEEDED_USER_ID));
        listAppender.list.stream().filter(logMsg -> logMsg.getFormattedMessage().equals(expectedMsg) && Level.INFO.equals(logMsg.getLevel())).findAny().get();
    }

    @Test
    public void testCreateDuplicateUserWithLaterModifiedDate() {
        // in case the user exists, the app just will try to update it
        // in this test user should be updated, as it has 'modified' later than the original one
        setupLogger(UserDataRdbBaseRepository.class);

        LocalDateTime localDateTime = LocalDateTime.now().plusDays(1);
        User user = new User(SEEDED_USER_ID, "John", "Doe", null, 3, true, false, localDateTime, localDateTime);
        String expectedMsg = String.format("User %s updated: %s", SEEDED_USER_ID, user);

        // Attempt to create a user with a duplicate ID
        assertDoesNotThrow(() -> userRepository.createUser(user));
        listAppender.list.stream().filter(logMsg -> logMsg.getFormattedMessage().equals(expectedMsg) && Level.INFO.equals(logMsg.getLevel())).findAny().get();
    }

    @Test
    public void testCreateDuplicateUserWithEarlierModifiedDate() {
        // in case the user exists, the app just will try to update it
        // in this test user should NOT be updated, as it has 'modified' earlier than the original one
        setupLogger(UserDataRdbBaseRepository.class);

        LocalDateTime localDateTime = LocalDateTime.now().minusDays(1);
        User user = new User(SEEDED_USER_ID, "John", "Doe", null, 3, true, false, localDateTime, localDateTime);
        String expectedMsg = String.format("User %s not updated as a data to update is older than user has", SEEDED_USER_ID);

        // Attempt to create a user with a duplicate ID
        assertDoesNotThrow(() -> userRepository.createUser(user));
        listAppender.list.stream().filter(logMsg -> logMsg.getFormattedMessage().equals(expectedMsg) && Level.INFO.equals(logMsg.getLevel())).findAny().get();
    }

    @Test
    public void testGetNonExistingUser() {
        // Attempt to retrieve a user that does not exist
        setupLogger(UserDataRdbBaseRepository.class);

        String userId = UUID.randomUUID().toString();
        String expectedMsg = String.format("User with id %s not found", userId);
        assertNull(userRepository.getUser(userId));
        listAppender.list.stream().filter(logMsg -> logMsg.getFormattedMessage().equals(expectedMsg) && Level.INFO.equals(logMsg.getLevel())).findAny().get();
    }

    @Test
    public void testUpdateNonExistingUser() {
        setupLogger(UserDataRdbBaseRepository.class);
        LocalDateTime localDateTime = LocalDateTime.now();
        String userId = UUID.randomUUID().toString();
        User user = new User(userId, "Frank", "Doe", null, 3, true, false, localDateTime, localDateTime);

        String expectedMsg1 = String.format("User with id %s not found", userId);
        String expectedMsg2 = String.format("User %s created", user);

        // Attempt to update a user that does not exist
        assertDoesNotThrow(() -> userRepository.updateUser(user.getId(), user));
        Optional<ILoggingEvent> logMsg1 = listAppender.list.stream()
                .filter(logMsg -> logMsg.getFormattedMessage().equals(expectedMsg1) && Level.INFO.equals(logMsg.getLevel()))
                .findAny();
        Optional<ILoggingEvent> logMsg2 = listAppender.list.stream()
                .filter(logMsg -> logMsg.getFormattedMessage().equals(expectedMsg2) && Level.INFO.equals(logMsg.getLevel()))
                .findAny();

        // Ensure that both log messages are present
        assertTrue(logMsg1.isPresent() && logMsg2.isPresent());
    }

    @Test
    public void testDeleteNonExistingUser() {
        // Attempt to delete a user that does not exist
        String userId = UUID.randomUUID().toString();
        String expectedMsg = String.format("User %s doesn't exist, nothing to delete", userId);
        assertDoesNotThrow(() -> userRepository.deleteUser(userId));
        listAppender.list.stream().filter(logMsg -> logMsg.getFormattedMessage().equals(expectedMsg) && Level.ERROR.equals(logMsg.getLevel())).findAny().get();
    }

    @Test
    public void testConsumeQuotaForDeletedUser() {
        // Attempt to consume quota for a user that has been deleted
        String expectedMsg = String.format("User %s doesn't exist, nothing to consume", SEEDED_USER_ID);
        userRepository.deleteUser(SEEDED_USER_ID);
        assertDoesNotThrow(() -> userRepository.consumeQuota(SEEDED_USER_ID));
        listAppender.list.stream().filter(logMsg -> logMsg.getFormattedMessage().equals(expectedMsg) && Level.ERROR.equals(logMsg.getLevel())).findAny().get();
    }

    @Test
    public void testGetAllUsers() {
        // Retrieve all users and verify the size
        List<User> allUsers = userRepository.getAllUsers();
        assertEquals(1, allUsers.size()); // We expect only the seeded user to exist
    }

    @Test
    public void testDeleteAllUsers() {
        // Delete all users and verify the count
        userRepository.deleteDataFromDb();
        List<User> allUsers = userRepository.getAllUsers();
        assertTrue(allUsers.isEmpty());
    }


    // method to create the test table
    private void createTestTable() throws SQLException {
        try (Connection connection = connectionProvider.getConnection()) {
            String sql = "CREATE TABLE IF NOT EXISTS user (" +
                    "id VARCHAR(50) PRIMARY KEY, " +
                    "firstName VARCHAR(50), " +
                    "lastName VARCHAR(50), " +
                    "lastLoginTimeUtc TIMESTAMP," +
                    "requests INT, " +
                    "locked BOOLEAN, " +
                    "deleted BOOLEAN, " +
                    "created TIMESTAMP, " +
                    "modified TIMESTAMP)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.execute();
            }
        }
    }

    // method to seed test data
    private void seedTestData() throws SQLException {
        try (Connection connection = connectionProvider.getConnection()) {
            String sql = "INSERT INTO user (id, firstName, lastName, lastLoginTimeUtc, requests, locked, deleted, created, modified) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, SEEDED_USER_ID);
                preparedStatement.setString(2, "John");
                preparedStatement.setString(3, "Dalton");
                preparedStatement.setObject(4, null);
                preparedStatement.setInt(5, 0);
                preparedStatement.setBoolean(6, false);
                preparedStatement.setBoolean(7, false);
                preparedStatement.setObject(8, LocalDateTime.now());    // created
                preparedStatement.setObject(9, LocalDateTime.now());    // modified
                preparedStatement.execute();
            }
        }
    }

    private void setupLogger(Class<?> clazz) {
        logger = (Logger) LoggerFactory.getLogger(clazz);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    private void  clearTestData() throws SQLException {
        try (Connection connection = connectionProvider.getConnection()) {
            String sql = "DELETE FROM user";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.execute();
            }
        }
    }
}

