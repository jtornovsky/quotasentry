package com.api.quotasentry.repository;

import lombok.Getter;

@Getter
class UserSqlQueriesHolder {
    private final String insertUserSql;
    private final String deleteUserSql;
    private final String deleteUserSoftlySql;
    private final String deleteAllUsersSql;
    private final String removeAllSoftDeletedUsersSql;
    private final String updateUserQuotaSql;
    private final String updateUserSql;
    private final String selectAllUsersWithoutDeletedSql;
    private final String selectAllUsersSql;
    private final String selectUserSql;

    UserSqlQueriesHolder(String userTableName) {
        this.insertUserSql = "INSERT INTO " + userTableName + " (id, firstName, lastName, lastLoginTimeUtc, requests, isLocked, isDeleted, created, modified) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        this.deleteUserSql = "DELETE FROM " + userTableName + " WHERE id = ?";
        this.deleteAllUsersSql = "DELETE FROM " + userTableName;
        this.deleteUserSoftlySql = "UPDATE " + userTableName + " SET isDeleted = 1, modified = NOW() WHERE id = ?";
        this.removeAllSoftDeletedUsersSql = "DELETE FROM " + userTableName + " WHERE isDeleted = 'true'";
        this.updateUserQuotaSql = "UPDATE " + userTableName + " SET requests = requests + 1, lastLoginTimeUtc = NOW(), modified = NOW() WHERE id = ?";
        this.selectAllUsersWithoutDeletedSql = "SELECT * FROM " + userTableName + " WHERE isDeleted = 'false'";
        this.selectAllUsersSql = "SELECT * FROM " + userTableName;
        this.selectUserSql = "SELECT * FROM " + userTableName + " WHERE id = ? and isDeleted = 'false'";
        this.updateUserSql = "UPDATE " + userTableName + " SET firstName = ?, lastName = ?, lastLoginTimeUtc = ?, requests = ?, isLocked = ?, isDeleted = ?, modified = NOW() WHERE id = ?";
    }
}
