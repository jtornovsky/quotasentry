package com.api.quotasentry.repository;

import com.api.quotasentry.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
abstract class RdbDataRepository {

    protected final String tableName;
    private final ConnectionProvider connectionProvider;
    protected final UserSqlQueriesHolder userSqlQueriesHolder;

    protected RdbDataRepository(String tableName, ConnectionProvider connectionProvider) {
        this.tableName = tableName;
        userSqlQueriesHolder = new UserSqlQueriesHolder(tableName);
        this.connectionProvider = connectionProvider;
    }

    protected User getSingleUser(String id) {
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

    protected List<User> getAllUsers() {
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

    protected void saveSingleUser(String id, User user) {
        User currentUser = getSingleUser(id);
        if (currentUser == null) {
            // a new user
            insertSingleUser(user);
            return;
        }
        if (user.getModified().isBefore(currentUser.getModified())) {
            log.info("User {} not updated as a data to update is older than user has", id);
            return;
        }
        updateSingleUser(id, user);
    }

    private void insertSingleUser(User user) {
        try (Connection connection = connectionProvider.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(userSqlQueriesHolder.getInsertUserSql())) {
                mapUserToPreparedStatement(preparedStatement, user);
                preparedStatement.executeUpdate();
            }
            log.info("User {} created", user);
        } catch (SQLException e) {
            handleSqlException(e, "Failed to create user " + user);
        }
    }

    private void updateSingleUser(String id, User user) {
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(userSqlQueriesHolder.getUpdateUserSql())) {
            mapUserForUpdate(user, statement);
            statement.executeUpdate();
            log.info("User {} updated: {}", id, user);
        } catch (SQLException e) {
            handleSqlException(e, "Failed to update user " + id + ", " + user);
        }
    }

    protected void updateMultipleUsers(List<User> users) {
        if (CollectionUtils.isEmpty(users)) {
            log.warn("Empty users list, nothing to save");
            return;
        }
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(userSqlQueriesHolder.getUpdateUserSql())) {
            for (User user : users) {
                mapUserForUpdate(user, statement);
                statement.addBatch();
            }
            statement.executeBatch();
            log.info("Updated {} users", users.size());
        } catch (SQLException e) {
            handleSqlException(e, "Failed to update users data");
        }
    }

    protected void insertMultipleUsers(List<User> users) {
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(userSqlQueriesHolder.getInsertUserSql())) {
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

    protected void executeSql(String sqlQuery, String successMessage, String failureMessage) {
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.executeUpdate();
            log.info(successMessage);
        } catch (SQLException e) {
            handleSqlException(e, failureMessage);
        }
    }

    protected void executeUpdateSql(String id, String sqlQuery, String logMessage, String errorMessage) {
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setString(1, id);
            statement.executeUpdate();
            log.info(logMessage, id);
        } catch (SQLException e) {
            handleSqlException(e, errorMessage + id);
        }
    }

    protected List<User> getAllUsersWithoutDeleted() {
        List<User> users = new ArrayList<>();
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(userSqlQueriesHolder.getSelectAllUsersWithoutDeletedSql());
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                users.add(mapUserFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            handleSqlException(e, "Failed to get users quota.");
        }
        return users;
    }

    protected User mapUserFromResultSet(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getString("id"));
        user.setFirstName(resultSet.getString("firstName"));
        user.setLastName(resultSet.getString("lastName"));
        user.setLastLoginTimeUtc(Optional.ofNullable(resultSet.getTimestamp("lastLoginTimeUtc")).map(Timestamp::toLocalDateTime).orElse(null));
        user.setRequests(resultSet.getInt("requests"));
        user.setLocked(resultSet.getBoolean("locked"));
        user.setDeleted(resultSet.getBoolean("deleted"));
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

    private void mapUserForUpdate(User user, PreparedStatement statement) throws SQLException {
        statement.setString(1, user.getFirstName());
        statement.setString(2, user.getLastName());
        statement.setObject(3, user.getLastLoginTimeUtc(), Types.TIMESTAMP);
        statement.setInt(4, user.getRequests());
        statement.setBoolean(5, user.isLocked());
        statement.setBoolean(6, user.isDeleted());
        statement.setString(7, user.getId());
    }

    protected void handleSqlException(SQLException e, String message) {
        log.error(message, e);
        throw new RuntimeException(e);
    }
}

