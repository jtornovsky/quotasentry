package com.api.quotasentry.repository;

import com.api.quotasentry.model.DbType;
import com.api.quotasentry.model.UserInitialData;
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

    public List<UserInitialData> findUserInitialDataByTargetDb(DbType targetDb) {
        List<UserInitialData> userInitialDataList = new ArrayList<>();
        try (Connection connection = connectionProvider.getConnection()) {
            String sql = "SELECT * FROM user_initial_data WHERE targetDb = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, targetDb.name());
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        UserInitialData userInitialData = new UserInitialData();
                        userInitialData.setFirstName(resultSet.getString("firstName"));
                        userInitialData.setLastName(resultSet.getString("lastName"));
                        userInitialData.setLastLoginTimeUtc(resultSet.getObject("lastLoginTimeUtc", LocalDateTime.class));
                        userInitialData.setRequests(resultSet.getInt("requests"));
                        userInitialData.setLocked(resultSet.getBoolean("isLocked"));
                        userInitialData.setTargetDb(targetDb);
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