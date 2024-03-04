package com.api.quotasentry.service;

import com.api.quotasentry.model.User;
import com.api.quotasentry.repository.UserMySqlDataRdbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * MySQL's implementation of the DB
 */
@Service
class MySqlDataService implements DataService, AdminDataService, SyncDataService {

    private final UserMySqlDataRdbRepository mySqlDataRepository;

    @Autowired
    MySqlDataService(UserMySqlDataRdbRepository mySqlDataRepository) {
        this.mySqlDataRepository = mySqlDataRepository;
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
    public void seedDataToDb(List<User> users) {
        mySqlDataRepository.seedDataToDb(users);
    }

    @Override
    public List<User> getAllUsers() {
        return mySqlDataRepository.getAllUsers();
    }

    @Override
    public void saveUsers(List<User> users) {
        mySqlDataRepository.saveUsers(users);
    }

    @Override
    public void removeAllSoftDeletedUsers() {
        mySqlDataRepository.removeAllSoftDeletedUsers();
    }
}
