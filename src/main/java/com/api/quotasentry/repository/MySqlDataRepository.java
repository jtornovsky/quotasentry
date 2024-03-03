package com.api.quotasentry.repository;

import com.api.quotasentry.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class MySqlDataRepository extends RdbDataRepository implements DataRepository {

    private final ConnectionProvider connectionProvider;

    @Autowired
    public MySqlDataRepository(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public void createUser(User user) {
        try (Connection connection = connectionProvider.getConnection()) {
            String sql = "INSERT INTO user (id, firstName, lastName, lastLoginTimeUtc, requests, isLocked) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, user.getId());
                statement.setString(2, user.getFirstName());
                statement.setString(3, user.getLastName());
                statement.setTimestamp(4, Timestamp.valueOf(user.getLastLoginTimeUtc()));
                statement.setInt(5, user.getRequests());
                statement.setBoolean(6, user.isLocked());
                statement.executeUpdate();
            }
            log.info("User {} created", user);
        } catch (SQLException e) {
            log.error("Failed to create user " + user, e);
        }
    }

    @Override
    public User getUser(String id) {
        try (Connection connection = connectionProvider.getConnection()) {
            String sql = "SELECT * FROM user WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, id);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return mapUserFromResultSet(resultSet);
                    }
                }
            }
        } catch (SQLException e) {
            log.error("Failed to retrieve user " + id, e);
        }
        log.info("User with id {} not found", id);
        return null;
    }

    public void saveUsers(List<User> users) {
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
        try (Connection connection = connectionProvider.getConnection()) {
            String sql = "DELETE FROM user WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, id);
                statement.executeUpdate();
            }
            log.info("User {} deleted", id);
        } catch (SQLException e) {
            log.error("Failed to delete user " + id, e);
        }
    }

    @Override
    public void consumeQuota(String id) {
        try (Connection connection = connectionProvider.getConnection()) {
            String sql = "UPDATE user SET requests = requests + 1, lastLoginTimeUtc = NOW(), modified = NOW() WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, id);
                statement.executeUpdate();
            }
            log.info("Quota consumed for the user {}", id);
        } catch (SQLException e) {
            log.error("Failed to set quota for the user " + id, e);
        }
    }

    @Override
    public List<User> getUsersQuota() {
        List<User> users = new ArrayList<>();
        try (Connection connection = connectionProvider.getConnection()) {
            String sql = "SELECT * FROM user";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        users.add(mapUserFromResultSet(resultSet));
                    }
                }
            }
        } catch (SQLException e) {
            log.error("Failed to get users quota.", e);
        }
        return users;
    }

    public void deleteDataFromDb() {
        try (Connection connection = connectionProvider.getConnection()) {
            String sql = "DELETE FROM user";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.executeUpdate();
            }
            log.info("All users deleted");
        } catch (SQLException e) {
            log.error("Failed to delete users", e);
        }
    }

    public void seedDataToDb(List<User> users) {
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER_SQL)) {
            for (User user : users) {
                preparedStatement.setString(1, user.getId());
                preparedStatement.setString(2, user.getFirstName());
                preparedStatement.setString(3, user.getLastName());
                preparedStatement.setObject(4, user.getLastLoginTimeUtc());
                preparedStatement.setInt(5, user.getRequests());
                preparedStatement.setBoolean(6, user.isLocked());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            log.error("Failed to seed users data", e);
        }
    }

    public List<User> getUsers() {
        return super.getUsers("user", connectionProvider);
    }
}
