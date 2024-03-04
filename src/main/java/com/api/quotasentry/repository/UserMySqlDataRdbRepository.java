package com.api.quotasentry.repository;

import com.api.quotasentry.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository class for interacting with MySQL data for the 'User' entity.
 */
@Slf4j
@Repository
public class UserMySqlDataRdbRepository extends UserDataRdbBaseRepository implements DataRepository {

    /**
     * Constructor that injects the connection provider.
     *
     * @param connectionProvider the connection provider for the MySQL database.
     */
    @Autowired
    public UserMySqlDataRdbRepository(ConnectionProvider connectionProvider) {
        super("user", connectionProvider);
    }

    @Override
    public void createUser(User user) {
        saveSingleUser(user.getId(), user);
    }

    @Override
    public User getUser(String id) {
        return super.getSingleUser(id);
    }

    /**
     * Saves a list of users to the database. If a user already exists in the database, it is updated.
     *
     * @param users the list of users to save.
     */
    public void saveUsers(List<User> users) {
        List<User> currentlyResideUsers = super.getAllUsersWithoutDeleted();
        if (CollectionUtils.isEmpty(currentlyResideUsers)) {
            // No resident users in db, just batch insert all new ones
            super.insertMultipleUsers(users);
            return;
        }

        List<User> newUsers = new ArrayList<>();
        List<User> oldUsers = new ArrayList<>();

        for (User user : users) {
            if (currentlyResideUsers.stream().noneMatch(currUser -> currUser.getId().equals(user.getId()))) {
                newUsers.add(user);
            } else {
                oldUsers.add(user);
            }
        }

        if (!CollectionUtils.isEmpty(newUsers)) {
            super.insertMultipleUsers(newUsers);
        }

        if (!CollectionUtils.isEmpty(oldUsers)) {
            super.updateMultipleUsers(oldUsers);
        }
    }

    @Override
    public void updateUser(String id, User updatedUser) {
        saveSingleUser(id, updatedUser);
    }

    @Override
    public void deleteUser(String id) {
        User usr = getUser(id);
        if (usr == null) {
            log.error("User {} doesn't exist, nothing to delete", id);
            return;
        }
        executeUpdateSql(id, userSqlQueriesHolder.getDeleteUserSoftlySql(), "User {} deleted", "Failed to delete user ");
    }

    @Override
    public void consumeQuota(String id) {
        User usr = getUser(id);
        if (usr == null) {
            log.error("User {} doesn't exist, nothing to consume", id);
            return;
        }
        executeUpdateSql(id, userSqlQueriesHolder.getUpdateUserQuotaSql(), "Quota consumed for the user {}", "Failed to set quota for the user ");
    }

    @Override
    public List<User> getUsersQuota() {
        return super.getAllUsersWithoutDeleted();
    }

    /**
     * Removes all softly deleted users from the database.
     */
    public void removeAllSoftDeletedUsers() {
        executeSql(
                userSqlQueriesHolder.getRemoveAllSoftDeletedUsersSql(),
                "All soft deleted users removed",
                "Failed to remove the soft delete users"
        );
    }

    /**
     * Deletes all users from the database.
     */
    public void deleteDataFromDb() {
        executeSql(
                userSqlQueriesHolder.getDeleteAllUsersSql(),
                "All users deleted",
                "Failed to delete users"
        );
    }

    /**
     * Seeds the database with a list of users.
     *
     * @param users the list of users to seed the database with.
     */
    public void seedDataToDb(List<User> users) {
        super.insertMultipleUsers(users);
    }

    /**
     * Retrieves all users from the database.
     *
     * @return the list of all users.
     */
    public List<User> getAllUsers() {
        return super.getAllUsers();
    }
}
