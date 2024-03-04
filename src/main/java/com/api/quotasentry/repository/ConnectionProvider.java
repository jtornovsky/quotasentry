package com.api.quotasentry.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Provides JDBC connections from a data source.
 */
@Component
class ConnectionProvider {

    private final DataSource dataSource;

    /**
     * Constructs a ConnectionProvider with the given DataSource.
     *
     * @param dataSource the data source to obtain connections from
     */
    @Autowired
    ConnectionProvider(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Returns a JDBC connection from the data source.
     *
     * @return a JDBC connection
     * @throws SQLException if a database access error occurs
     */
    public Connection getConnection() throws SQLException {
        return DataSourceUtils.getConnection(dataSource);
    }
}

