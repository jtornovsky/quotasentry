package com.api.quotasentry.service;

import com.api.quotasentry.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DbService {

    private final int MAX_ALLOWED_REQUESTS;

    private final DataServiceFactory dataServiceFactory;

    @Autowired
    public DbService(@Value("${quota.MAX_ALLOWED_REQUESTS}") final int MAX_ALLOWED_REQUESTS,
                     DataServiceFactory dataServiceFactory) {
        this.MAX_ALLOWED_REQUESTS = MAX_ALLOWED_REQUESTS;
        this.dataServiceFactory = dataServiceFactory;
    }

    public void createUser(User user) {
        DataService dataService = dataServiceFactory.getDataService();
        dataService.createUser(user);
    }

    public User getUser(String id) {
        DataService dataService = dataServiceFactory.getDataService();
        return dataService.getUser(id);
    }

    public void updateUser(String id, User updatedUser) {
        DataService dataService = dataServiceFactory.getDataService();
        dataService.updateUser(id, updatedUser);
    }

    public void deleteUser(String id) {
        DataService dataService = dataServiceFactory.getDataService();
        dataService.deleteUser(id);
    }

    public void consumeQuota(String id) {
        DataService dataService = dataServiceFactory.getDataService();
        User user = dataService.getUser(id);
        if (user == null) {
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

    public List<User> getUsersQuota() {
        DataService dataService = dataServiceFactory.getDataService();
        return dataService.getUsersQuota();
    }
}
