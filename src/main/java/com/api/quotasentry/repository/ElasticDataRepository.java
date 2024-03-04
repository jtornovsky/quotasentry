package com.api.quotasentry.repository;

import com.api.quotasentry.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ElasticDataRepository implements DataRepository {

    /**
     * simulates Elastic DB
     */
    private final Map<String, User> usersMap = new ConcurrentHashMap<>();

    @Override
    public void createUser(User user) {
        if (usersMap.containsKey(user.getId())) {
            log.info("User with id {} already exists, nothing to create", user.getId());
            return;
        }
        usersMap.put(user.getId(), user);
        log.info("User with id {} created", user.getId());
    }

    @Override
    public User getUser(String id) {
        if (usersMap.containsKey(id)) {
            User user = usersMap.get(id);
            if (!user.isDeleted()) {
                return user;
            } else {
                log.info("User with id {} found but is marked as deleted", id);
            }
        }
        log.info("User with id {} not found", id);
        return null;
    }

    @Override
    public void updateUser(String id, User updatedUser) {
        if (usersMap.containsKey(id)) {
            User currentUser = usersMap.get(id);
            if (!currentUser.isDeleted()) {
                if (currentUser.getModified().isBefore(updatedUser.getModified())) {
                    usersMap.put(id, updatedUser);
                    log.info("User with id {} updated", id);
                } else {
                    log.info("User with id {} not updated as the provided data is older than existing data", id);
                }
            } else {
                log.info("User with id {} not updated as it is marked as deleted", id);
            }
        } else {
            log.info("User with id {} not found, creating it.", id);
            createUser(updatedUser);
        }
    }

    @Override
    public void deleteUser(String id) {
        if (usersMap.containsKey(id)) {
            User user = usersMap.get(id);
            if (!user.isDeleted()) {
                user.setDeleted(true);
                log.info("User with id {} marked as deleted", id);
            } else {
                log.info("User with id {} already marked as deleted", id);
            }
        } else {
            log.info("User with id {} not found, so not marked as deleted", id);
        }
    }

    @Override
    public void consumeQuota(String id) {
        User user = getUser(id);
        if (user != null) {
            if (user.isDeleted()) {
                log.info("User with id {} is marked as deleted, quota not consumed", id);
                return;
            }
            int currentRequests = user.getRequests();
            user.setRequests(currentRequests + 1);
            user.setLastLoginTimeUtc(LocalDateTime.now(ZoneOffset.UTC));
            user.setModified(LocalDateTime.now(ZoneOffset.UTC));
            log.info("Quota consumed for the user with id {}", id);
        }
    }

    @Override
    public List<User> getUsersQuota() {
        return getUsersWithoutSoftDeletedOnes();
    }

    public List<User> getUsers() {
        return getUsersWithoutSoftDeletedOnes();
    }

    public void removeAllSoftDeletedUsers() {
        usersMap.entrySet().removeIf(entry -> entry.getValue().isDeleted());
        log.info("All soft deleted users removed");
    }

    private List<User> getUsersWithoutSoftDeletedOnes() {
        return usersMap.values().stream()
                .filter(user -> !user.isDeleted())
                .collect(Collectors.toList());
    }

    public List<User> getAllUsers() {
        return List.copyOf(usersMap.values());
    }

    public void deleteDataFromDb() {
        usersMap.clear();
    }

    public void saveUsers(List<User> users) {
        users.forEach(user -> updateUser(user.getId(), user));
    }
}
