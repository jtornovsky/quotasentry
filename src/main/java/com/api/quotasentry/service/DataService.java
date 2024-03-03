package com.api.quotasentry.service;

import com.api.quotasentry.model.User;

import java.util.List;

interface DataService {
    void createUser(User user);
    User getUser(String id);
    void updateUser(String id, User updatedUser);
    void deleteUser(String id);
    void consumeQuota(String id);
    List<User> getUsersQuota();
}
