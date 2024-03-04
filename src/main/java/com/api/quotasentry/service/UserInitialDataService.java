package com.api.quotasentry.service;

import com.api.quotasentry.model.User;
import com.api.quotasentry.repository.UserInitialDataRdbBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class providing methods for working with user initial (seed) data.
 * This service works with the RDB (Relational Database) for the initial data.
 */
@Service
class UserInitialDataService {

    private final UserInitialDataRdbBaseRepository userInitialDataRepository;

    /**
     * Constructor for the UserInitialDataService class.
     *
     * @param userInitialDataRepository The repository handling data operations for user initial data.
     */
    @Autowired
    UserInitialDataService(UserInitialDataRdbBaseRepository userInitialDataRepository) {
        this.userInitialDataRepository = userInitialDataRepository;
    }

    /**
     * Retrieves user initial data from the database.
     *
     * @return A list of User objects representing the initial data.
     */
    List<User> getUserInitialData() {
        return userInitialDataRepository.getUserInitialData();
    }
}
