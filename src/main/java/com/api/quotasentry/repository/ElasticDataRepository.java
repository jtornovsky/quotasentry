package com.api.quotasentry.repository;

import com.api.quotasentry.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
        if (usersMap.containsKey(user.getId())) {
            log.info("User {} already exists, nothing to create", user.getId());
            return;
        }
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
        if (!usersMap.containsKey(id)) {
            log.info("User with id {} not found, so not updated", id);
            return;
        }
        User currentUser = usersMap.get(id);
        if (currentUser.getModified().isBefore(updatedUser.getModified())) {
            usersMap.put(id, updatedUser);
            log.info("User {} updated", id);
        } else {
            log.info("User {} not updated as a data to update is older than user has", id);
        }
    }

    @Override
    public void deleteUser(String id) {
        if (!usersMap.containsKey(id)) {
            log.info("User with id {} not found, so not deleted", id);
            return;
        }
        usersMap.remove(id);
        log.info("User {} deleted", id);
    }

    @Override
    public void consumeQuota(String id) {
        if (!usersMap.containsKey(id)) {
            log.info("User with id {} not found, so quota not consumed", id);
            return;
        }
        User user = usersMap.get(id);
        int currentRequests = user.getRequests();
        user.setRequests(currentRequests + 1);
        user.setLastLoginTimeUtc(LocalDateTime.now(ZoneOffset.UTC));
        user.setModified(LocalDateTime.now(ZoneOffset.UTC));
        log.info("Quota consumed for the user {}", id);
    }

    @Override
    public List<User> getUsersQuota() {
        return new ArrayList<>(usersMap.values());
    }

    public void deleteDataFromDb() {
        usersMap.clear();
    }

    public List<User> getUsers() {
        return new ArrayList<>(usersMap.values());
    }

    public void saveUsers(List<User> users) {
        users.stream().forEach(user -> updateUser(user.getId(), user));
    }
}
