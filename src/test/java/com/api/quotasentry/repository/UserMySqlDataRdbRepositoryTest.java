package com.api.quotasentry.repository;

import com.api.quotasentry.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.jdbc.DataSourceBuilder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class UserMySqlDataRdbRepositoryTest {

    private ConnectionProvider connectionProvider;
    private UserMySqlDataRdbRepository userRepository;

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

        // Create test tables and data
        createTestTable();
        seedTestData();
    }

    @Test
    public void testGetUser() {
        userRepository = new UserMySqlDataRdbRepository(connectionProvider);
        LocalDateTime localDateTime = LocalDateTime.now();
        User user = new User("1", "Frank", "Doe", null, 3, true, false, localDateTime, localDateTime);
        userRepository.createUser(user);

        User retrievedUser = userRepository.getUser("1");

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

    // Helper method to create the test table
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

    // Helper method to seed test data
    private void seedTestData() throws SQLException {
        try (Connection connection = connectionProvider.getConnection()) {
            String sql = "INSERT INTO user (id, firstName, lastName, lastLoginTimeUtc, requests, locked, deleted, created, modified) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, "11");
                preparedStatement.setString(2, "John");
                preparedStatement.setString(3, "Dalton");
                preparedStatement.setObject(4, null);
                preparedStatement.setInt(5, 0);
                preparedStatement.setBoolean(6, false);
                preparedStatement.setBoolean(7, false);
                preparedStatement.setObject(8, LocalDateTime.now());
                preparedStatement.setObject(9, LocalDateTime.now());
                preparedStatement.execute();
            }
        }
    }
}

