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
public class SyncDbService {
    private final DataServiceFactory dataServiceFactory;

    @Autowired
    public SyncDbService(DataServiceFactory dataServiceFactory) {
        this.dataServiceFactory = dataServiceFactory;
    }

    @Scheduled(initialDelay = 60, fixedRate = 60) // run every 10 minutes
    public void synchronizeDatabases() {
        log.info("Sync job started.");
        SyncDataService activeDataService = (SyncDataService)dataServiceFactory.getActiveDataService();
        SyncDataService notActiveDataService = (SyncDataService)dataServiceFactory.getNotActiveDataService();

        // Read data from the active database
        List<User> users = activeDataService.getUsers();

        // Write data to the non-active database
        notActiveDataService.saveUsers(users);
        log.info("Sync job ended.");
    }
}
