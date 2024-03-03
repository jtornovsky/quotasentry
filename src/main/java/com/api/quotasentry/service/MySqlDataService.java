package com.api.quotasentry.service;

import com.api.quotasentry.model.DbType;
import com.api.quotasentry.model.User;
import com.api.quotasentry.model.UserInitialData;
import com.api.quotasentry.repository.MySqlDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * MySQL's implementation of the DataService and AdminDataService interfaces
 */
@Service
class MySqlDataService implements DataService, AdminDataService {

    private final MySqlDataRepository mySqlDataRepository;
    private final UserInitialDataService userInitialDataService;

    @Autowired
    MySqlDataService(MySqlDataRepository mySqlDataRepository, UserInitialDataService userInitialDataService) {
        this.mySqlDataRepository = mySqlDataRepository;
        this.userInitialDataService = userInitialDataService;
    }

    @Override
    public void createUser(User user) {
        mySqlDataRepository.createUser(user);
    }

    @Override
    public User getUser(String id) {
        return mySqlDataRepository.getUser(id);
    }

    @Override
    public void updateUser(String id, User updatedUser) {
        mySqlDataRepository.updateUser(id, updatedUser);
    }

    @Override
    public void deleteUser(String id) {
        mySqlDataRepository.deleteUser(id);
    }

    @Override
    public void consumeQuota(String id) {
        mySqlDataRepository.consumeQuota(id);
    }

    @Override
    public List<User> getUsersQuota() {
        return mySqlDataRepository.getUsersQuota();
    }

    @Override
    public void deleteDataFromDb() {
        mySqlDataRepository.deleteDataFromDb();
    }

    @Override
    public void seedDataToDb() {
        List<UserInitialData> userInitialDataList = userInitialDataService.getUserInitialDataByTargetDb(DbType.Mysql);
        List<User> users = new ArrayList<>();
        for (UserInitialData userInitialData : userInitialDataList) {
            User user = new User();
            user.setId(UUID.randomUUID().toString());
            user.setFirstName(userInitialData.getFirstName());
            user.setLastName(userInitialData.getLastName());
            user.setLastLoginTimeUtc(userInitialData.getLastLoginTimeUtc());
            user.setRequests(0);
            user.setLocked(false);
            users.add(user);
        }
        mySqlDataRepository.seedDataToDb(users);
    }
}
