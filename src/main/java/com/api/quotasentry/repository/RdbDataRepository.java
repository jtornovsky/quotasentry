package com.api.quotasentry.repository;

import com.api.quotasentry.model.User;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
abstract class RdbDataRepository {

    protected final String TABLE_NAME;

    protected RdbDataRepository(String TABLE_NAME) {
        this.TABLE_NAME = TABLE_NAME;
    }

    protected User getUser(String id, ConnectionProvider connectionProvider) {
        try (Connection connection = connectionProvider.getConnection()) {
            String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
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

    protected List<User> getUsers(ConnectionProvider connectionProvider) {
        List<User> userInitialDataList = new ArrayList<>();
        try (Connection connection = connectionProvider.getConnection()) {
            String sql = "SELECT * FROM " + TABLE_NAME;
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        User userData = mapUserFromResultSet(resultSet);
                        userInitialDataList.add(userData);
                    }
                }
            }
        } catch (SQLException e) {
            log.error("Failed to retrieve user data from table " + TABLE_NAME, e);
        }
        log.info("User data retrieved from table {}", TABLE_NAME);
        return userInitialDataList;
    }

    protected void saveUser(String id, User user, ConnectionProvider connectionProvider) {
        User currentUser = getUser(id, connectionProvider);
        if (user.getModified().isBefore(currentUser.getModified())) {
            log.info("User {} not updated as a data to update is older than user has", id);
            return;
        }
        try (Connection connection = connectionProvider.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE " + TABLE_NAME + " SET firstName = ?, lastName = ?, lastLoginTimeUtc = ?, requests = ?, isLocked = ?, modified = NOW() WHERE id = ?")) {
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
        user.setLastLoginTimeUtc(Optional.ofNullable(resultSet.getTimestamp("lastLoginTimeUtc")).map(Timestamp::toLocalDateTime).orElse(null));
        user.setRequests(resultSet.getInt("requests"));
        user.setLocked(resultSet.getBoolean("isLocked"));
        user.setCreated(resultSet.getTimestamp("created").toLocalDateTime());
        user.setModified(resultSet.getTimestamp("modified").toLocalDateTime());
        return user;
    }


    protected void mapUserToPreparedStatement(PreparedStatement preparedStatement, User user) throws SQLException {
        preparedStatement.setString(1, user.getId());
        preparedStatement.setString(2, user.getFirstName());
        preparedStatement.setString(3, user.getLastName());
        if (user.getLastLoginTimeUtc() == null) {
            preparedStatement.setString(4, null);
        } else {
            preparedStatement.setTimestamp(4, Timestamp.valueOf(user.getLastLoginTimeUtc()));
        }
        preparedStatement.setInt(5, user.getRequests());
        preparedStatement.setBoolean(6, user.isLocked());
    }
}
