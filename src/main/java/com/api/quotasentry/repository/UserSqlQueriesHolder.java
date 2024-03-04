package com.api.quotasentry.repository;

import lombok.Getter;

/**
 * This class encapsulates SQL queries related to the `user` and `user_initial_data` tables.
 * It is used to store and retrieve these queries in a centralized and
 * reusable manner, making them more manageable and maintainable.
 */
@Getter
class UserSqlQueriesHolder {

    /**
     * SQL query for inserting a new user into the `user` table.
     */
    private final String insertUserSql;

    /**
     * SQL query for deleting a user by ID from the `user` table.
     */
    private final String deleteUserSql;

    /**
     * SQL query for deleting all users from the `user` table.
     */
    private final String deleteAllUsersSql;

    /**
     * SQL query for softly deleting a user by ID from the `user` table
     * (marking the user as deleted).
     */
    private final String deleteUserSoftlySql;

    /**
     * SQL query for removing all softly deleted users from the `user` table.
     */
    private final String removeAllSoftDeletedUsersSql;

    /**
     * SQL query for updating the quota of a user by ID in the `user` table.
     */
    private final String updateUserQuotaSql;

    /**
     * SQL query for selecting all users from the `user` table where the user
     * is not marked as deleted.
     */
    private final String selectAllUsersWithoutDeletedSql;

    /**
     * SQL query for selecting all users from the `user` and `user_initial_data` tables.
     */
    private final String selectAllUsersSql;

    /**
     * SQL query for selecting a user by ID from the `user` table where the user
     * is not marked as deleted.
     */
    private final String selectUserSql;

    /**
     * SQL query for updating user details (first name, last name, last login time, requests,
     * locked, deleted) by ID in the `user` table.
     */
    private final String updateUserSql;

    /**
     * Constructs a new instance of `UserSqlQueriesHolder` with the specified user table name.
     * The provided user table name is used to dynamically generate the SQL queries.
     *
     * @param userTableName the name of the user table in the database.
     */
    UserSqlQueriesHolder(String userTableName) {
        this.insertUserSql = "insert into " + userTableName + " (id, firstName, lastName, lastLoginTimeUtc, requests, locked, deleted, created, modified) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        this.deleteUserSql = "delete from " + userTableName + " where id = ?";
        this.deleteAllUsersSql = "delete from " + userTableName;
        this.deleteUserSoftlySql = "update " + userTableName + " set deleted = 1, modified = NOW() where id = ?";
        this.removeAllSoftDeletedUsersSql = "delete from " + userTableName + " where deleted = 1";
        this.updateUserQuotaSql = "update " + userTableName + " set requests = requests + 1, lastLoginTimeUtc = NOW(), modified = NOW() where id = ?";
        this.selectAllUsersWithoutDeletedSql = "select * from " + userTableName + " where deleted = 0";
        this.selectAllUsersSql = "select * from " + userTableName;
        this.selectUserSql = "select * from " + userTableName + " where id = ? and deleted = 0";
        this.updateUserSql = "update " + userTableName + " set firstName = ?, lastName = ?, lastLoginTimeUtc = ?, requests = ?, locked = ?, deleted = ?, modified = NOW() WHERE id = ?";
    }
}
