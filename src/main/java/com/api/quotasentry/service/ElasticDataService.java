package com.api.quotasentry.service;

import com.api.quotasentry.model.DbType;
import com.api.quotasentry.model.User;
import com.api.quotasentry.model.UserInitialData;
import com.api.quotasentry.repository.ElasticDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Elastic's implementation of the DataService and AdminDataService interfaces
 */
@Service
class ElasticDataService implements DataService, AdminDataService {

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
    public void seedDataToDb() {
        List<UserInitialData> userInitialDataList = userInitialDataService.getUserInitialDataByTargetDb(DbType.Elastic);
        for (UserInitialData userInitialData : userInitialDataList) {
            User user = new User();
            user.setId(UUID.randomUUID().toString());
            user.setFirstName(userInitialData.getFirstName());
            user.setLastName(userInitialData.getLastName());
            user.setLastLoginTimeUtc(userInitialData.getLastLoginTimeUtc());
            user.setLocked(false);
            user.setRequests(0);
            elasticDataRepository.createUser(user);
        }
    }
}
