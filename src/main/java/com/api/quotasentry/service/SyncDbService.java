package com.api.quotasentry.service;

import com.api.quotasentry.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service syncs the Data bases: updates the currently not active db with the data of the active one.
 */
@Slf4j
@Service
public class SyncDbService {
    private final DataServiceFactory dataServiceFactory;

    @Autowired
    public SyncDbService(DataServiceFactory dataServiceFactory) {
        this.dataServiceFactory = dataServiceFactory;
    }

    @Scheduled(initialDelay = 600000, fixedRate = 600000) // runs every 10 minutes
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
