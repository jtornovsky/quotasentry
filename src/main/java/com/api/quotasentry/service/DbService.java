package com.api.quotasentry.service;

import com.api.quotasentry.dto.UserDTO;
import com.api.quotasentry.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DbService {

    private final int MAX_ALLOWED_REQUESTS;

    private final DataServiceFactory dataServiceFactory;
    private final UserService userService;

    @Autowired
    public DbService(@Value("${quota.MAX_ALLOWED_REQUESTS}") final int MAX_ALLOWED_REQUESTS,
                     DataServiceFactory dataServiceFactory, UserService userService) {
        this.MAX_ALLOWED_REQUESTS = MAX_ALLOWED_REQUESTS;
        this.dataServiceFactory = dataServiceFactory;
        this.userService = userService;
    }

    public void createUser(User user) {
        DataService dataService = dataServiceFactory.getActiveDataService();
        if (user == null) {
            log.error("User to create is null");
            return;
        }
        dataService.createUser(user);
    }

    public UserDTO getUser(String id) {
        DataService dataService = dataServiceFactory.getActiveDataService();
        User user = dataService.getUser(id);
        if (user == null) {
            log.error("User {} doesn't exist", id);
            return null;
        }
        return userService.convertUserToUserDto(user);
    }

    public void updateUser(String id, User updatedUser) {
        DataService dataService = dataServiceFactory.getActiveDataService();
        dataService.updateUser(id, updatedUser);
    }

    public void deleteUser(String id) {
        DataService dataService = dataServiceFactory.getActiveDataService();
        dataService.deleteUser(id);
    }

    public void consumeQuota(String id) {
        DataService dataService = dataServiceFactory.getActiveDataService();
        User user = dataService.getUser(id);
        if (user == null) {
            log.error("User {} doesn't exist", id);
            return;
        }
        if (user.isLocked()) {
            log.info("user {} is locked, no more quota allowed.", id);
            return;
        }
        if (user.getRequests() >= MAX_ALLOWED_REQUESTS) {
            log.info("user {} is being locked, all quota consumed.", id);
            user.setLocked(true);
            updateUser(id, user);
            return;
        }
        dataService.consumeQuota(id);
    }

    public List<UserDTO> getUsersQuota() {
        DataService dataService = dataServiceFactory.getActiveDataService();
        List<User> usersList = dataService.getUsersQuota();
        return usersList.stream()
                .map(userService::convertUserToUserDto)
                .collect(Collectors.toList());
    }
}
