package com.api.quotasentry.repository;

import com.api.quotasentry.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class UserMySqlDataRdbRepository extends UserDataRdbBaseRepository implements DataRepository {

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

    public void saveUsers(List<User> users) {
        List<User> currentlyResideUsers = super.getAllUsersWithoutDeleted();
        if (CollectionUtils.isEmpty(currentlyResideUsers)) {
            // no resident users in db, just batch insert all new ones
            super.insertMultipleUsers(users);
            return;
        }
        List<User> newUsers = users.stream()
                .filter(user -> currentlyResideUsers.stream().noneMatch(currUser -> currUser.getId().equals(user.getId())))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(newUsers)) {
            // if no new users, so no need to calculate the old ones, just batch update all of them
            super.updateMultipleUsers(users);
            return;
        }

        // in case of new users, we need to collect the old ones to the different collection
        List<User> oldUsers = users.stream()
                .filter(user -> currentlyResideUsers.stream().anyMatch(currUser -> currUser.getId().equals(user.getId())))
                .collect(Collectors.toList());
        super.insertMultipleUsers(newUsers);
        super.updateMultipleUsers(oldUsers);
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

    public void removeAllSoftDeletedUsers() {
        executeSql(
                userSqlQueriesHolder.getRemoveAllSoftDeletedUsersSql(),
                "All soft deleted users removed",
                "Failed to remove the soft delete users"
        );
    }

    public void deleteDataFromDb() {
        executeSql(
                userSqlQueriesHolder.getDeleteAllUsersSql(),
                "All users deleted",
                "Failed to delete users"
        );
    }

    public void seedDataToDb(List<User> users) {
        super.insertMultipleUsers(users);
    }

    public List<User> getAllUsers() {
        return super.getAllUsers();
    }
}
