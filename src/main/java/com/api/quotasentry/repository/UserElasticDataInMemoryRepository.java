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

/**
 * Repository class for accessing user data stored in a simulated in-memory Elasticsearch database.
 * This class implements the DataRepository interface and provides methods for CRUD operations on user data.
 * It simulates the behavior of a database by using a ConcurrentHashMap to store user objects.
 */
@Slf4j
@Repository
public class UserElasticDataInMemoryRepository implements DataRepository {

    /**
     * Map simulating the Elasticsearch database, with user IDs as keys and user objects as values.
     */
    private final Map<String, User> usersMap = new ConcurrentHashMap<>();

    /**
     * Creates a new user in the simulated Elasticsearch database.
     *
     * @param user the user object to be created
     */
    @Override
    public void createUser(User user) {
        if (usersMap.containsKey(user.getId())) {
            log.info("User with id {} already exists, nothing to create", user.getId());
            return;
        }
        usersMap.put(user.getId(), user);
        log.info("User with id {} created", user.getId());
    }

    /**
     * Retrieves a user from the simulated Elasticsearch database based on the user's ID.
     *
     * @param id the ID of the user to retrieve
     * @return the user object if found, null otherwise
     */
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

    /**
     * Updates a user's information in the simulated Elasticsearch database.
     *
     * @param id           the ID of the user to update
     * @param updatedUser  the updated user object
     */
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

    /**
     * Soft-deletes a user from the simulated Elasticsearch database.
     *
     * @param id the ID of the user to delete
     */
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

    /**
     * Method to simulate quota consumption for a user in the in-memory map.
     * @param id
     */
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

    /**
     * Method to retrieve all users with active quotas from the in-memory map.
     * @return
     */
    @Override
    public List<User> getUsersQuota() {
        return getUsersWithoutSoftDeletedOnes();
    }

    public List<User> getUsers() {
        return getUsersWithoutSoftDeletedOnes();
    }

    /**
     * Removes all soft-deleted users from the in-memory map.
     */
    public void removeAllSoftDeletedUsers() {
        usersMap.entrySet().removeIf(entry -> entry.getValue().isDeleted());
        log.info("All soft deleted users removed");
    }

    private List<User> getUsersWithoutSoftDeletedOnes() {
        return usersMap.values().stream()
                .filter(user -> !user.isDeleted())
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all users from the in-memory map, including soft-deleted ones.
     *
     * @return a list of User objects representing all users, including soft-deleted ones
     */
    public List<User> getAllUsers() {
        return List.copyOf(usersMap.values());
    }

    /**
     * Clears all user data from the in-memory map.
     */
    public void deleteDataFromDb() {
        usersMap.clear();
    }

    /**
     * Saves a list of users to the in-memory map, updating existing users or creating new ones.
     *
     * @param users the list of User objects to save
     */
    public void saveUsers(List<User> users) {
        users.forEach(user -> updateUser(user.getId(), user));
    }
}
