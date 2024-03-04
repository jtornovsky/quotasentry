package com.api.quotasentry.service;

import com.api.quotasentry.model.User;
import com.api.quotasentry.repository.UserElasticDataInMemoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Elastic's implementation of the DB
 */
@Service
class ElasticDataService implements DataService, AdminDataService, SyncDataService {

    private final UserElasticDataInMemoryRepository userElasticDataInMemoryRepository;
    private final UserInitialDataService userInitialDataService;

    @Autowired
    ElasticDataService(UserElasticDataInMemoryRepository userElasticDataInMemoryRepository, UserInitialDataService userInitialDataService) {
        this.userElasticDataInMemoryRepository = userElasticDataInMemoryRepository;
        this.userInitialDataService = userInitialDataService;
    }

    @Override
    public void createUser(User user) {
        userElasticDataInMemoryRepository.createUser(user);
    }

    @Override
    public User getUser(String id) {
        return userElasticDataInMemoryRepository.getUser(id);
    }

    @Override
    public void updateUser(String id, User updatedUser) {
        userElasticDataInMemoryRepository.updateUser(id, updatedUser);
    }

    @Override
    public void deleteUser(String id) {
        userElasticDataInMemoryRepository.deleteUser(id);
    }

    @Override
    public void consumeQuota(String id) {
        userElasticDataInMemoryRepository.consumeQuota(id);
    }

    @Override
    public List<User> getUsersQuota() {
        return userElasticDataInMemoryRepository.getUsersQuota();
    }

    @Override
    public void deleteDataFromDb() {
        userElasticDataInMemoryRepository.deleteDataFromDb();
    }

    @Override
    public void seedDataToDb(List<User> users) {
        users.forEach(user -> userElasticDataInMemoryRepository.createUser(user));
    }

    @Override
    public List<User> getAllUsers() {
        return userElasticDataInMemoryRepository.getAllUsers();
    }

    @Override
    public void saveUsers(List<User> users) {
        userElasticDataInMemoryRepository.saveUsers(users);
    }

    @Override
    public void removeAllSoftDeletedUsers() {
        userElasticDataInMemoryRepository.removeAllSoftDeletedUsers();
    }
}
