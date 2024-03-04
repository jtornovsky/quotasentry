package com.api.quotasentry.service;

import com.api.quotasentry.model.User;

import java.util.List;

public interface SyncDataService {
    List<User> getAllUsers();
    void saveUsers(List<User> users);
    void removeAllSoftDeletedUsers();
}
