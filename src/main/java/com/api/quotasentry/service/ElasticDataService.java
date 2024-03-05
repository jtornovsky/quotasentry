package com.api.quotasentry.service;

import com.api.quotasentry.model.User;
import com.api.quotasentry.repository.UserElasticDataInMemoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class providing data manipulation functionalities specific to the simulated Elastic database.
 * This class implements the DataService, AdminDataService, and SyncDataService interfaces.
 * It delegates the actual database operations to the UserElasticDataInMemoryRepository.
 * <p>
 * Methods in this class provide CRUD operations for users, along with specialized operations for handling quotas,
 * soft deletes, and synchronization between active and non-active databases.
 */
@Service
class ElasticDataService implements DataService, AdminDataService, SyncDataService {

    private final UserElasticDataInMemoryRepository userElasticDataInMemoryRepository;

    @Autowired
    ElasticDataService(UserElasticDataInMemoryRepository userElasticDataInMemoryRepository) {
        this.userElasticDataInMemoryRepository = userElasticDataInMemoryRepository;
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
