package com.api.quotasentry.service;

import com.api.quotasentry.model.User;
import com.api.quotasentry.repository.ElasticDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Elastic's implementation of the DB
 */
@Service
class ElasticDataService implements DataService, AdminDataService, SyncDataService {

    private final ElasticDataRepository elasticDataRepository;
    private final UserInitialDataService userInitialDataService;

    @Autowired
    ElasticDataService(ElasticDataRepository elasticDataRepository, UserInitialDataService userInitialDataService) {
        this.elasticDataRepository = elasticDataRepository;
        this.userInitialDataService = userInitialDataService;
    }

    @Override
    public void createUser(User user) {
        elasticDataRepository.createUser(user);
    }

    @Override
    public User getUser(String id) {
        return elasticDataRepository.getUser(id);
    }

    @Override
    public void updateUser(String id, User updatedUser) {
        elasticDataRepository.updateUser(id, updatedUser);
    }

    @Override
    public void deleteUser(String id) {
        elasticDataRepository.deleteUser(id);
    }

    @Override
    public void consumeQuota(String id) {
        elasticDataRepository.consumeQuota(id);
    }

    @Override
    public List<User> getUsersQuota() {
        return elasticDataRepository.getUsersQuota();
    }

    @Override
    public void deleteDataFromDb() {
        elasticDataRepository.deleteDataFromDb();
    }

    @Override
    public void seedDataToDb(List<User> users) {
        users.forEach(user -> elasticDataRepository.createUser(user));
    }

    @Override
    public List<User> getAllUsers() {
        return elasticDataRepository.getAllUsers();
    }

    @Override
    public void saveUsers(List<User> users) {
        elasticDataRepository.saveUsers(users);
    }

    @Override
    public void removeAllSoftDeletedUsers() {
        elasticDataRepository.removeAllSoftDeletedUsers();
    }
}
