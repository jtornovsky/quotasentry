package com.api.quotasentry.service;

import com.api.quotasentry.model.User;
import com.api.quotasentry.repository.UserInitialDataRdbBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service to work with user initial data
 */
@Service
class UserInitialDataService {

    private final UserInitialDataRdbBaseRepository userInitialDataRepository;

    @Autowired
    UserInitialDataService(UserInitialDataRdbBaseRepository userInitialDataRepository) {
        this.userInitialDataRepository = userInitialDataRepository;
    }

    List<User> getUserInitialData() {
        return userInitialDataRepository.getUserInitialData();
    }
}
