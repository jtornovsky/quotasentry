package com.api.quotasentry.repository;

import com.api.quotasentry.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

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
        updateMultipleUsers(users);
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
