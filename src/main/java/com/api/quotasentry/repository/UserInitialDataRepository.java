package com.api.quotasentry.repository;

import com.api.quotasentry.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class UserInitialDataRepository {

    private final ConnectionProvider connectionProvider;

    public UserInitialDataRepository(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public List<User> findUserInitialDataByTargetDb() {
        List<User> userInitialDataList = new ArrayList<>();
        try (Connection connection = connectionProvider.getConnection()) {
            String sql = "SELECT * FROM user_initial_data";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        User userInitialData = new User();
                        userInitialData.setId(resultSet.getString("id"));
                        userInitialData.setFirstName(resultSet.getString("firstName"));
                        userInitialData.setLastName(resultSet.getString("lastName"));
                        userInitialData.setLastLoginTimeUtc(resultSet.getObject("lastLoginTimeUtc", LocalDateTime.class));
                        userInitialData.setRequests(resultSet.getInt("requests"));
                        userInitialData.setLocked(resultSet.getBoolean("isLocked"));
                        userInitialDataList.add(userInitialData);
                    }
                }
            }
        } catch (SQLException e) {
            log.error("Failed to retrieve user initial data", e);
        }
        log.info("User initial data retrieved");
        return userInitialDataList;
    }
}