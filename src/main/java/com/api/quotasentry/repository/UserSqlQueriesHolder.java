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
        this.insertUserSql = "insert into " + userTableName + " (id, firstName, lastName, lastLoginTimeUtc, requests, isLocked, isDeleted, created, modified) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        this.deleteUserSql = "delete from " + userTableName + " where id = ?";
        this.deleteAllUsersSql = "delete from " + userTableName;
        this.deleteUserSoftlySql = "update " + userTableName + " set isDeleted = 1, modified = NOW() where id = ?";
        this.removeAllSoftDeletedUsersSql = "delete from " + userTableName + " where isDeleted = 1";
        this.updateUserQuotaSql = "update " + userTableName + " set requests = requests + 1, lastLoginTimeUtc = NOW(), modified = NOW() where id = ?";
        this.selectAllUsersWithoutDeletedSql = "select * from " + userTableName + " where isDeleted = 0";
        this.selectAllUsersSql = "select * from " + userTableName;
        this.selectUserSql = "select * from " + userTableName + " where id = ? and isDeleted = 0";
        this.updateUserSql = "update " + userTableName + " set firstName = ?, lastName = ?, lastLoginTimeUtc = ?, requests = ?, isLocked = ?, isDeleted = ?, modified = NOW() WHERE id = ?";
    }
}
