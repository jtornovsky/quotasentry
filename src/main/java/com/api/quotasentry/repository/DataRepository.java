package com.api.quotasentry.repository;

import com.api.quotasentry.model.User;

import java.util.List;

/**
 * contract for all repositories
 * wasn't used JpaRepository due to assignment constraints
 */
interface DataRepository {
    void createUser(User user);
    User getUser(String id);
    void updateUser(String id, User updatedUser);
    void deleteUser(String id);
    void consumeQuota(String id);
    List<User> getUsersQuota();
}
