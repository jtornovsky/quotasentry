package com.api.quotasentry.service;

import com.api.quotasentry.model.DbType;
import com.api.quotasentry.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * A service for managing database operations at the admin level.
 * This includes functions such as deleting data from databases, seeding initial data to databases,
 * and retrieving data from databases.
 */
@Slf4j
@Service
public class AdminDbService {

    private final MySqlDataService mySqlDataService;
    private final ElasticDataService elasticDataService;
    private final UserInitialDataService userInitialDataService;

    /**
     * Constructor to create an AdminDbService object.
     *
     * @param mySqlDataService         The MySQL data service.
     * @param elasticDataService       The Elastic data service.
     * @param userInitialDataService  The initial user data service.
     */
    @Autowired
    public AdminDbService(MySqlDataService mySqlDataService, ElasticDataService elasticDataService, UserInitialDataService userInitialDataService) {
        this.mySqlDataService = mySqlDataService;
        this.elasticDataService = elasticDataService;
        this.userInitialDataService = userInitialDataService;
    }

    /**
     * Physically deletes data from both the MySQL and Elastic databases.
     */
    public void deleteDataFromDbs() {
        mySqlDataService.deleteDataFromDb();
        elasticDataService.deleteDataFromDb();
    }

    /**
     * Seeds initial data to both the MySQL and Elastic databases.
     */
    public void seedDataToDbs() {
        deleteDataFromDbs();
        List<User> users = userInitialDataService.getUserInitialData();
        mySqlDataService.seedDataToDb(users);
        elasticDataService.seedDataToDb(users);
    }

    /**
     * Retrieves data from the specified database type.
     *
     * @param dbType The database type (MySQL or Elastic).
     * @return The list of users retrieved from the specified database type.
     */
    public List<User> getDataFromDb(DbType dbType) {
        switch (dbType) {
            case Elastic -> {
                return elasticDataService.getUsersQuota();
            }
            case Mysql -> {
                return mySqlDataService.getUsersQuota();
            }
            default -> {
                return null;
            }
        }
    }

    public List<User> getDataFromDbs() {
        List<User> users = getDataFromDb(DbType.Elastic);
        users.addAll(getDataFromDb(DbType.Mysql));
        return users;
    }
}
