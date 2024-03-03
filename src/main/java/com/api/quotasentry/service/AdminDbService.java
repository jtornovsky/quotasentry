package com.api.quotasentry.service;

import com.api.quotasentry.model.DbType;
import com.api.quotasentry.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AdminDbService {

    private final MySqlDataService mySqlDataService;
    private final ElasticDataService elasticDataService;
    private final UserInitialDataService userInitialDataService;

    @Autowired
    public AdminDbService(MySqlDataService mySqlDataService, ElasticDataService elasticDataService, UserInitialDataService userInitialDataService) {
        this.mySqlDataService = mySqlDataService;
        this.elasticDataService = elasticDataService;
        this.userInitialDataService = userInitialDataService;
    }

    public void deleteDataFromDbs() {
        mySqlDataService.deleteDataFromDb();
        elasticDataService.deleteDataFromDb();
    }

    public void seedDataToDbs() {
        deleteDataFromDbs();
        List<User> users = userInitialDataService.getUserInitialDataByTargetDb();
        mySqlDataService.seedDataToDb(users);
        elasticDataService.seedDataToDb(users);
    }

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
