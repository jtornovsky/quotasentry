package com.api.quotasentry.service;

import com.api.quotasentry.model.DbType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Factory to choose the appropriate DataService based on the current time
 */
@Component
class DataServiceFactory {

    private final int MY_SQL_DB_START_HOUR;
    private final int MY_SQL_DB_END_HOUR;

    private final MySqlDataService mySqlDataService;
    private final ElasticDataService elasticDataService;

    @Autowired
    DataServiceFactory(@Value("${quota.MY_SQL_DB_START_HOUR}") final int MY_SQL_DB_START_HOUR,
                       @Value("${quota.MY_SQL_DB_END_HOUR}") final int MY_SQL_DB_END_HOUR,
            MySqlDataService mySqlDataService, ElasticDataService elasticDataService) {
        this.MY_SQL_DB_START_HOUR = MY_SQL_DB_START_HOUR;
        this.MY_SQL_DB_END_HOUR = MY_SQL_DB_END_HOUR;
        this.mySqlDataService = mySqlDataService;
        this.elasticDataService = elasticDataService;
    }

    DataService getDataService() {
        DbType dbType = getWorkingDbType();
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

    private DbType getWorkingDbType() {
        // implements logic to check if the current time is within the MySQL time range (from 9:00 - 17:00 UTC)
        LocalDateTime currentTime = LocalDateTime.now(ZoneOffset.UTC);
        if (currentTime.getHour() >= MY_SQL_DB_START_HOUR && currentTime.getHour() <= MY_SQL_DB_END_HOUR) {
            return DbType.Mysql;
        }
        return DbType.Elastic;
    }
}
