package com.api.quotasentry.service;

import com.api.quotasentry.model.DbType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class DataServiceFactoryTest {

    private MySqlDataService mySqlDataService = mock(MySqlDataService.class);
    private ElasticDataService elasticDataService = mock(ElasticDataService.class);
    private DataServiceFactory dataServiceFactory;

    @BeforeEach
    void setUp() {
        dataServiceFactory = new DataServiceFactory(9, 17, mySqlDataService, elasticDataService);
    }

    @Test
    void testGetActiveDataService_MySqlActive() {
        // Mock the current time to be within the MySQL activity hours
        LocalDateTime fixedTime = LocalDateTime.of(2024, 2, 29, 12, 0);
        dataServiceFactory = spy(dataServiceFactory);
        doReturn(fixedTime).when(dataServiceFactory).getCurrentTime();

        // Call the method under test
        DataService activeDataService = dataServiceFactory.getActiveDataService();

        // Assert that the active data service is MySQL
        assertEquals(DbType.Mysql, dataServiceFactory.getDbType(fixedTime, true));
        assertTrue(activeDataService instanceof MySqlDataService);
    }

    @Test
    void testGetActiveDataService_ElasticActive() {
        // Mock the current time to be outside the MySQL activity hours
        LocalDateTime fixedTime = LocalDateTime.of(2024, 2, 29, 18, 0);
        dataServiceFactory = spy(dataServiceFactory);
        doReturn(fixedTime).when(dataServiceFactory).getCurrentTime();

        // Call the method under test
        DataService activeDataService = dataServiceFactory.getActiveDataService();

        // Assert that the active data service is MySQL
        assertEquals(DbType.Elastic, dataServiceFactory.getDbType(fixedTime, true));
        assertTrue(activeDataService instanceof ElasticDataService);
    }

    @Test
    void testGetNotActiveDataService_MySqlInactive() {
        // Mock the current time to be outside the MySQL activity hours
        LocalDateTime fixedTime = LocalDateTime.of(2024, 2, 29, 18, 0);
        dataServiceFactory = spy(dataServiceFactory);
        doReturn(fixedTime).when(dataServiceFactory).getCurrentTime();

        // Call the method under test
        DataService notActiveDataService = dataServiceFactory.getNotActiveDataService();

        // Assert that the non-active data service is Mysql
        assertEquals(DbType.Mysql, dataServiceFactory.getDbType(fixedTime, false));
        assertTrue(notActiveDataService instanceof MySqlDataService);
    }

    @Test
    void testGetNotActiveDataService_ElasticInactive() {
        // Mock the current time to be within the MySQL activity hours
        LocalDateTime fixedTime = LocalDateTime.of(2024, 2, 29, 12, 0);
        dataServiceFactory = spy(dataServiceFactory);
        doReturn(fixedTime).when(dataServiceFactory).getCurrentTime();

        // Call the method under test
        DataService notActiveDataService = dataServiceFactory.getNotActiveDataService();

        // Assert that the non-active data service is Elastic
        assertEquals(DbType.Elastic, dataServiceFactory.getDbType(fixedTime, false));
        assertTrue(notActiveDataService instanceof ElasticDataService);
    }
}
