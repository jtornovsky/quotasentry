package com.api.quotasentry.repository;

import com.api.quotasentry.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class MySqlDataRepository extends RdbDataRepository implements DataRepository {

    private final static String USER_TABLE = "user";
    private final String INSERT_USER_SQL = "INSERT INTO " + USER_TABLE + " (id, firstName, lastName, lastLoginTimeUtc, requests, isLocked) VALUES (?, ?, ?, ?, ?, ?)";

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
            log.info("User {} already exists, nothing to create", user.getId());
            return;
        }
        try (Connection connection = connectionProvider.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER_SQL)) {
                mapUserToPreparedStatement(preparedStatement, user);
                preparedStatement.executeUpdate();
            }
            log.info("User {} created", user);
        } catch (SQLException e) {
            log.error("Failed to create user " + user, e);
        }
    }

    @Override
    public User getUser(String id) {
        return super.getUser(id, connectionProvider);
    }

    public void saveUsers(List<User> users) {
        if (CollectionUtils.isEmpty(users)) {
            log.info("Empty users list, nothing to save");
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
            log.info("User {} doesn't exist, nothing to delete", id);
            return;
        }
        try (Connection connection = connectionProvider.getConnection()) {
            String sql = "DELETE FROM " + USER_TABLE + " WHERE id = ?";
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
        User usr = getUser(id);
        if (usr == null) {
            log.info("User {} doesn't exist, nothing to consume", id);
            return;
        }
        try (Connection connection = connectionProvider.getConnection()) {
            String sql = "UPDATE " + USER_TABLE + " SET requests = requests + 1, lastLoginTimeUtc = NOW(), modified = NOW() WHERE id = ?";
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
                mapUserToPreparedStatement(preparedStatement, user);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            log.error("Failed to seed users data", e);
        }
    }

    public List<User> getUsers() {
        return super.getUsers(connectionProvider);
    }
}
