package com.api.quotasentry.repository;

import com.api.quotasentry.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository class for accessing initial (seed) user data stored in the relational database.
 * This class extends UserDataRdbBaseRepository, providing methods for accessing user data in the specified table.
 * It is specifically designed to fetch initial (seed) user data.
 */
@Slf4j
@Repository
public class UserInitialDataRdbBaseRepository extends UserDataRdbBaseRepository {

    public UserInitialDataRdbBaseRepository(ConnectionProvider connectionProvider) {
        super("user_initial_data", connectionProvider);
    }

    /**
     * Retrieves the initial (seed) data for all users from the database.
     *
     * @return a list of User objects representing the initial user data
     */
    public List<User> getUserInitialData() {
        return getAllUsers();
    }
}