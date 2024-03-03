package com.api.quotasentry.repository;

import com.api.quotasentry.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class MySqlDataRepository extends RdbDataRepository implements DataRepository {

    private final static String USER_TABLE = "user";
    private final static String INSERT_USER_SQL = "INSERT INTO " + USER_TABLE + " (id, firstName, lastName, lastLoginTimeUtc, requests, isLocked, created, modified) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private final static String DELETE_USER_SQL = "DELETE FROM " + USER_TABLE + " WHERE id = ?";
    private final static String DELETE_ALL_USERS = "DELETE FROM " + USER_TABLE;
    private final static String UPDATE_USER_SQL = "UPDATE " + USER_TABLE + " SET requests = requests + 1, lastLoginTimeUtc = NOW(), modified = NOW() WHERE id = ?";
    private final static String SELECT_ALL_USERS_SQL = "SELECT * FROM " + USER_TABLE;

    private final ConnectionProvider connectionProvider;

    @Autowired
    public MySqlDataRepository(ConnectionProvider connectionProvider) {
        super(USER_TABLE);
        this.connectionProvider = connectionProvider;
    }

    @Override
    public void createUser(User user) {
        User usr = getUser(user.getId());
        if (usr != null) {
            log.warn("User {} already exists, nothing to create", user.getId());
            return;
        }
        try (Connection connection = connectionProvider.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER_SQL)) {
                mapUserToPreparedStatement(preparedStatement, user);
                preparedStatement.executeUpdate();
            }
            log.info("User {} created", user);
        } catch (SQLException e) {
            handleSqlException(e, "Failed to create user " + user);
        }
    }

    @Override
    public User getUser(String id) {
        return super.getUser(id, connectionProvider);
    }

    public void saveUsers(List<User> users) {
        if (CollectionUtils.isEmpty(users)) {
            log.warn("Empty users list, nothing to save");
            return;
        }
        for (User user : users) {
            saveUser(user.getId(), user, connectionProvider);
        }
    }

    @Override
    public void updateUser(String id, User updatedUser) {
        saveUser(id, updatedUser, connectionProvider);
    }

    @Override
    public void deleteUser(String id) {
        User usr = getUser(id);
        if (usr == null) {
            log.error("User {} doesn't exist, nothing to delete", id);
            return;
        }
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_USER_SQL)) {
            statement.setString(1, id);
            statement.executeUpdate();
            log.info("User {} deleted", id);
        } catch (SQLException e) {
            handleSqlException(e, "Failed to delete user " + id);
        }
    }

    @Override
    public void consumeQuota(String id) {
        User usr = getUser(id);
        if (usr == null) {
            log.error("User {} doesn't exist, nothing to consume", id);
            return;
        }
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_USER_SQL)) {
            statement.setString(1, id);
            statement.executeUpdate();
            log.info("Quota consumed for the user {}", id);
        } catch (SQLException e) {
            handleSqlException(e, "Failed to set quota for the user " + id);
        }
    }

    @Override
    public List<User> getUsersQuota() {
        List<User> users = new ArrayList<>();
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_USERS_SQL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                users.add(mapUserFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            handleSqlException(e, "Failed to get users quota.");
        }
        return users;
    }

    public void deleteDataFromDb() {
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_ALL_USERS)) {
            statement.executeUpdate();
            log.info("All users deleted");
        } catch (SQLException e) {
            handleSqlException(e, "Failed to delete users");
        }
    }

    public void seedDataToDb(List<User> users) {
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER_SQL)) {
            for (User user : users) {
                mapUserToPreparedStatement(preparedStatement, user);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
            log.info("Seeded {} users", users.size());
        } catch (SQLException e) {
            handleSqlException(e, "Failed to seed users data");
        }
    }

    public List<User> getUsers() {
        return super.getUsers(connectionProvider);
    }
}
