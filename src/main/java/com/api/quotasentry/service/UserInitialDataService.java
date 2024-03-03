package com.api.quotasentry.service;

import com.api.quotasentry.model.User;
import com.api.quotasentry.repository.UserInitialDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service to work with user initial data
 */
@Service
class UserInitialDataService {

    private final UserInitialDataRepository userInitialDataRepository;

    @Autowired
    UserInitialDataService(UserInitialDataRepository userInitialDataRepository) {
        this.userInitialDataRepository = userInitialDataRepository;
    }

    List<User> getUserInitialDataByTargetDb() {
        return userInitialDataRepository.getUserInitialData();
    }
}
