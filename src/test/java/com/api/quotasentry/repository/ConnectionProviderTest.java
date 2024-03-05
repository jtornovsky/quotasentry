package com.api.quotasentry.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.jdbc.DataSourceBuilder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class ConnectionProviderTest {

    private ConnectionProvider connectionProvider;

    @BeforeEach
    void setUp() {
        // Use H2 in-memory database for testing
        DataSource dataSource = DataSourceBuilder
                .create()
                .url("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false")
                .username("sa")
                .password("")
                .driverClassName("org.h2.Driver")
                .build();

        // Initialize connectionProvider with the DataSource
        this.connectionProvider = new ConnectionProvider(dataSource);
    }

    @Test
    void testGetConnectionNotNull() throws SQLException {
        Connection connection = connectionProvider.getConnection();
        assertNotNull(connection);
    }

    @Test
    void testGetConnectionIsConnection() throws SQLException {
        Connection connection = connectionProvider.getConnection();
        assertTrue(connection instanceof Connection);
    }

    @Test
    void testGetConnectionNotClosed() throws SQLException {
        Connection connection = connectionProvider.getConnection();
        assertFalse(connection.isClosed());
    }
}
