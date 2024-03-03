package com.api.quotasentry.service;

import com.api.quotasentry.model.User;

import java.util.List;

/**
 * for Admin purposes only
 */
interface AdminDataService {
    void deleteDataFromDb();
    void seedDataToDb(List<User> users);
}
