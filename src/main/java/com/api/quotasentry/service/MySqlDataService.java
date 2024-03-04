package com.api.quotasentry.service;

import com.api.quotasentry.model.User;
import com.api.quotasentry.repository.UserMySqlDataRdbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class providing data manipulation functionalities specific to the MySQL database.
 * This class implements the DataService, AdminDataService, and SyncDataService interfaces.
 * It delegates the actual database operations to the UserMySqlDataRdbRepository.
 * <p>
 * Methods in this class provide CRUD operations for users, along with specialized operations for handling quotas,
 * soft deletes, and synchronization between active and non-active databases.
 */
@Service
class MySqlDataService implements DataService, AdminDataService, SyncDataService {

    private final UserMySqlDataRdbRepository mySqlDataRepository;

    /**
     * Constructor for the MySqlDataService class.
     *
     * @param mySqlDataRepository The repository handling data operations for the MySQL database.
     */
    @Autowired
    MySqlDataService(UserMySqlDataRdbRepository mySqlDataRepository) {
        this.mySqlDataRepository = mySqlDataRepository;
    }

    /**
     * Creates a new user in the MySQL database.
     *
     * @param user The user object to be created.
     */
    @Override
    public void createUser(User user) {
        mySqlDataRepository.createUser(user);
    }

    /**
     * Retrieves a user from the MySQL database based on the provided ID.
     *
     * @param id The ID of the user to retrieve.
     * @return The user object corresponding to the provided ID, or null if no user is found.
     */
    @Override
    public User getUser(String id) {
        return mySqlDataRepository.getUser(id);
    }

    /**
     * Updates an existing user in the MySQL database.
     *
     * @param id           The ID of the user to update.
     * @param updatedUser  The updated user object with new data.
     */
    @Override
    public void updateUser(String id, User updatedUser) {
        mySqlDataRepository.updateUser(id, updatedUser);
    }

    /**
     * Soft-deletes a user from the MySQL database based on the provided ID.
     *
     * @param id The ID of the user to delete.
     */
    @Override
    public void deleteUser(String id) {
        mySqlDataRepository.deleteUser(id);
    }

    /**
     * Increases the quota of requests for a user in the MySQL database based on the provided ID.
     *
     * @param id The ID of the user whose quota to increase.
     */
    @Override
    public void consumeQuota(String id) {
        mySqlDataRepository.consumeQuota(id);
    }

    /**
     * Retrieves the list of users with their quotas from the MySQL database.
     *
     * @return The list of users with quotas.
     */
    @Override
    public List<User> getUsersQuota() {
        return mySqlDataRepository.getUsersQuota();
    }

    /**
     * Physically deletes all data from the MySQL database.
     */
    @Override
    public void deleteDataFromDb() {
        mySqlDataRepository.deleteDataFromDb();
    }

    /**
     * Seeds the MySQL database with the provided list of users.
     *
     * @param users The list of users to seed the database with.
     */
    @Override
    public void seedDataToDb(List<User> users) {
        mySqlDataRepository.seedDataToDb(users);
    }

    /**
     * Retrieves all users from the MySQL database.
     *
     * @return The list of all users in the database.
     */
    @Override
    public List<User> getAllUsers() {
        return mySqlDataRepository.getAllUsers();
    }

    /**
     * Saves the provided list of users to the MySQL database.
     *
     * @param users The list of users to save to the database.
     */
    @Override
    public void saveUsers(List<User> users) {
        mySqlDataRepository.saveUsers(users);
    }

    /**
     * Physically removes all soft-deleted users from the MySQL database.
     */
    @Override
    public void removeAllSoftDeletedUsers() {
        mySqlDataRepository.removeAllSoftDeletedUsers();
    }
}
