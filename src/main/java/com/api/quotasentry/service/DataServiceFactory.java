package com.api.quotasentry.service;

import com.api.quotasentry.model.DbType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * A factory responsible for choosing the appropriate DataService based on the current time.
 * The factory has two main methods: getActiveDataService() and getNotActiveDataService().
 * getActiveDataService() returns the DataService that is currently active, based on the current time.
 * getNotActiveDataService() returns the DataService that is currently not active, based on the current time.
 */
@Component
class DataServiceFactory {

    /**
     * The starting hour for MySQL database activity.
     */
    private final int MY_SQL_DB_START_HOUR;

    /**
     * The ending hour for MySQL database activity.
     */
    private final int MY_SQL_DB_END_HOUR;

    /**
     * The MySQL data service.
     */
    private final MySqlDataService mySqlDataService;

    /**
     * The Elastic data service.
     */
    private final ElasticDataService elasticDataService;

    /**
     * Constructor to create a DataServiceFactory object.
     *
     * @param MY_SQL_DB_START_HOUR The starting hour for MySQL database activity.
     * @param MY_SQL_DB_END_HOUR   The ending hour for MySQL database activity.
     * @param mySqlDataService     The MySQL data service.
     * @param elasticDataService   The Elastic data service.
     */
    @Autowired
    DataServiceFactory(@Value("${quota.MY_SQL_DB_START_HOUR}") final int MY_SQL_DB_START_HOUR,
                       @Value("${quota.MY_SQL_DB_END_HOUR}") final int MY_SQL_DB_END_HOUR,
            MySqlDataService mySqlDataService, ElasticDataService elasticDataService) {
        this.MY_SQL_DB_START_HOUR = MY_SQL_DB_START_HOUR;
        this.MY_SQL_DB_END_HOUR = MY_SQL_DB_END_HOUR;
        this.mySqlDataService = mySqlDataService;
        this.elasticDataService = elasticDataService;
    }

    /**
     * Returns the DataService that should be active based on the current time.
     *
     * @return The active DataService.
     */
    DataService getActiveDataService() {
        return getDataService(true);
    }

    /**
     * Returns the DataService that should not be active based on the current time.
     *
     * @return The non-active DataService.
     */
    DataService getNotActiveDataService() {
        return getDataService(false);
    }

    /**
     * Returns the appropriate DataService based on the current time and whether it is an active service or not.
     *
     * @param isActiveService Whether the service is currently active or not.
     * @return The DataService based on the current time and service activity.
     */
    private DataService getDataService(boolean isActiveService) {
        LocalDateTime currentTime = LocalDateTime.now(ZoneOffset.UTC);
        DbType dbType = getDbType(currentTime, isActiveService);
        switch (dbType) {
            case Mysql -> {
                return mySqlDataService;
            }
            case Elastic -> {
                return elasticDataService;
            }
            default -> throw new RuntimeException("Unknown DB type " + dbType);
        }
    }

    /**
     * Determines the appropriate DbType based on the current time and service activity.
     *
     * @param currentTime    The current time.
     * @param isActiveService Whether the service is currently active or not.
     * @return The DbType based on the current time and service activity.
     */
    private DbType getDbType(LocalDateTime currentTime, boolean isActiveService) {
        // implements logic to check if the current time is within the MySQL time range (from 9:00 - 17:00 UTC)
        if (currentTime.getHour() >= MY_SQL_DB_START_HOUR && currentTime.getHour() < MY_SQL_DB_END_HOUR) {
            if (isActiveService) {
                return DbType.Mysql;
            }
            return DbType.Elastic;
        } else {
            if (isActiveService) {
                return DbType.Elastic;
            }
            return DbType.Mysql;
        }
    }
}
