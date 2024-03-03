package com.api.quotasentry.repository;

import com.api.quotasentry.model.User;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
abstract class RdbDataRepository {
    protected final String INSERT_USER_SQL = "INSERT INTO user (id, firstName, lastName, lastLoginTimeUtc, requests, isLocked) VALUES (?, ?, ?, ?, ?, ?)";
    protected final String UPDATE_USER_SQL = "UPDATE user SET firstName = ?, lastName = ?, lastLoginTimeUtc = ?, requests = ?, isLocked = ?, modified = NOW() WHERE id = ?";

    protected List<User> getUsers(String tableName, ConnectionProvider connectionProvider) {
        List<User> userInitialDataList = new ArrayList<>();
        try (Connection connection = connectionProvider.getConnection()) {
            String sql = "SELECT * FROM " + tableName;
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        User userData = new User();
                        userData.setId(resultSet.getString("id"));
                        userData.setFirstName(resultSet.getString("firstName"));
                        userData.setLastName(resultSet.getString("lastName"));
                        userData.setLastLoginTimeUtc(resultSet.getObject("lastLoginTimeUtc", LocalDateTime.class));
                        userData.setRequests(resultSet.getInt("requests"));
                        userData.setLocked(resultSet.getBoolean("isLocked"));
                        userInitialDataList.add(userData);
                    }
                }
            }
        } catch (SQLException e) {
            log.error("Failed to retrieve user data from table " + tableName, e);
        }
        log.info("User data retrieved from table " + tableName);
        return userInitialDataList;
    }

    protected void saveUser(String id, User user, ConnectionProvider connectionProvider) {
        try (Connection connection = connectionProvider.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(UPDATE_USER_SQL)) {
                statement.setString(1, user.getFirstName());
                statement.setString(2, user.getLastName());
                if (user.getLastLoginTimeUtc() == null) {
                    statement.setString(3, null);
                } else {
                    statement.setTimestamp(3, Timestamp.valueOf(user.getLastLoginTimeUtc()));
                }
                statement.setInt(4, user.getRequests());
                statement.setBoolean(5, user.isLocked());
                statement.setString(6, id);
                statement.executeUpdate();
            }
            log.info("User {} saved: {}", id, user);
        } catch (SQLException e) {
            log.error("Failed to save user " + id + ", " + user, e);
        }
    }

    protected User mapUserFromResultSet(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getString("id"));
        user.setFirstName(resultSet.getString("firstName"));
        user.setLastName(resultSet.getString("lastName"));
        user.setLastLoginTimeUtc(resultSet.getTimestamp("lastLoginTimeUtc").toLocalDateTime());
        user.setRequests(resultSet.getInt("requests"));
        user.setLocked(resultSet.getBoolean("isLocked"));
        return user;
    }
}
