package com.api.quotasentry.repository;

import com.api.quotasentry.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Repository
public class ElasticDataRepository implements DataRepository {

    private final Map<String, User> usersMap = new ConcurrentHashMap<>();

    @Override
    public void createUser(User user) {
        usersMap.put(user.getId(), user);
        log.info("User {} created", user);
    }

    @Override
    public User getUser(String id) {
        if (usersMap.containsKey(id)) {
            return usersMap.get(id);
        }
        log.info("User with id {} not found", id);
        return null;
    }

    @Override
    public void updateUser(String id, User updatedUser) {
        if (usersMap.containsKey(id)) {
            usersMap.put(id, updatedUser);
            log.info("User {} updated", id);
            return;
        }
        log.info("User with id {} not found, so not updated", id);
    }

    @Override
    public void deleteUser(String id) {
        if (usersMap.containsKey(id)) {
            usersMap.remove(id);
            log.info("User {} deleted", id);
            return;
        }
        log.info("User with id {} not found, so not deleted", id);
    }

    @Override
    public void consumeQuota(String id) {
        if (usersMap.containsKey(id)) {
            User user = usersMap.get(id);
            int currentRequests = user.getRequests();
            user.setRequests(currentRequests + 1);
            log.info("Quota consumed for the user {}", id);
            return;
        }
        log.info("User with id {} not found, so quota not consumed", id);
    }

    @Override
    public List<User> getUsersQuota() {
        return new ArrayList<>(usersMap.values());
    }

    public void deleteDataFromDb() {
        usersMap.clear();
    }
}
