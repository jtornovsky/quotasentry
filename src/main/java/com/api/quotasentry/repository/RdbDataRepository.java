package com.api.quotasentry.repository;

import com.api.quotasentry.model.User;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
abstract class RdbDataRepository {

    protected final String tableName;
    protected final UserSqlQueriesHolder userSqlQueriesHolder;

    protected RdbDataRepository(String tableName) {
        this.tableName = tableName;
        userSqlQueriesHolder = new UserSqlQueriesHolder(tableName);
    }

    protected User getUser(String id, ConnectionProvider connectionProvider) {
        try (Connection connection = connectionProvider.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(userSqlQueriesHolder.getSelectUserSql())) {
                statement.setString(1, id);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return mapUserFromResultSet(resultSet);
                    }
                }
            }
        } catch (SQLException e) {
            handleSqlException(e, "Failed to retrieve user " + id);
        }
        log.info("User with id {} not found", id);
        return null;
    }

    protected List<User> getAllUsers(ConnectionProvider connectionProvider) {
        List<User> userInitialDataList = new ArrayList<>();
        try (Connection connection = connectionProvider.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(userSqlQueriesHolder.getSelectAllUsersSql())) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        User userData = mapUserFromResultSet(resultSet);
                        userInitialDataList.add(userData);
                    }
                }
            }
        } catch (SQLException e) {
            handleSqlException(e, "Failed to retrieve user data from table " + tableName);
        }
        log.info("User data retrieved from table {}", tableName);
        return userInitialDataList;
    }

    protected void saveUser(String id, User user, ConnectionProvider connectionProvider) {
        User currentUser = getUser(id, connectionProvider);
        if (user.getModified().isBefore(currentUser.getModified())) {
            log.info("User {} not updated as a data to update is older than user has", id);
            return;
        }
        try (Connection connection = connectionProvider.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(userSqlQueriesHolder.getUpdateUserSql())) {
                statement.setString(1, user.getFirstName());
                statement.setString(2, user.getLastName());
                statement.setObject(3, user.getLastLoginTimeUtc(), Types.TIMESTAMP);
                statement.setInt(4, user.getRequests());
                statement.setBoolean(5, user.isLocked());
                statement.setBoolean(6, user.isDeleted());
                statement.setString(7, id);
                statement.executeUpdate();
            }
            log.info("User {} saved: {}", id, user);
        } catch (SQLException e) {
            handleSqlException(e, "Failed to save user " + id + ", " + user);
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
        user.setDeleted(resultSet.getBoolean("isDeleted"));
        user.setCreated(resultSet.getTimestamp("created").toLocalDateTime());
        user.setModified(resultSet.getTimestamp("modified").toLocalDateTime());
        return user;
    }


    protected void mapUserToPreparedStatement(PreparedStatement preparedStatement, User user) throws SQLException {
        preparedStatement.setString(1, user.getId());
        preparedStatement.setString(2, user.getFirstName());
        preparedStatement.setString(3, user.getLastName());
        preparedStatement.setObject(4, user.getLastLoginTimeUtc(), Types.TIMESTAMP);
        preparedStatement.setInt(5, user.getRequests());
        preparedStatement.setBoolean(6, user.isLocked());
        preparedStatement.setBoolean(7, user.isDeleted());
        preparedStatement.setObject(8, user.getCreated());
        preparedStatement.setObject(9, user.getModified());
    }

    protected void handleSqlException(SQLException e, String message) {
        log.error(message, e);
        throw new RuntimeException(e);
    }
}

