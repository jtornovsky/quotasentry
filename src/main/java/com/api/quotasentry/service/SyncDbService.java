package com.api.quotasentry.service;

import com.api.quotasentry.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for synchronizing data between active and non-active databases.
 * The service runs on a scheduled basis and performs the following steps:
 * <ul>
 *     <li>Retrieve all users from the active database.</li>
 *     <li>Write the retrieved users to the non-active database.</li>
 *     <li>Remove soft-deleted entries from both databases.</li>
 * </ul>
 * <p>
 * The service logs the progress of each step and any errors encountered.
 * <p>
 * This service is scheduled to run every 10 minutes with an initial delay of 10 minutes.
 * <p>
 * Note: This class assumes that the {@link DataServiceFactory} provides the correct {@link SyncDataService}
 * implementations for the active and non-active databases.
 */
@Slf4j
@Service
public class SyncDbService {
    private final DataServiceFactory dataServiceFactory;

    @Autowired
    public SyncDbService(DataServiceFactory dataServiceFactory) {
        this.dataServiceFactory = dataServiceFactory;
    }

    /**
     * Synchronize databases method. Retrieves all users from the active database, writes them to the non-active
     * database, and removes soft-deleted entries from both databases.
     */
    @Scheduled(initialDelay = 600000, fixedRate = 600000) // runs every 10 minutes
    public void synchronizeDatabases() {
        log.info("Sync job started.");
        SyncDataService activeDataService = (SyncDataService)dataServiceFactory.getActiveDataService();
        SyncDataService notActiveDataService = (SyncDataService)dataServiceFactory.getNotActiveDataService();

        try {
            // Read data from the active database
            List<User> users = activeDataService.getAllUsers();
            log.info("Found {} users in the active database.", users.size());

            // Write data to the non-active database
            notActiveDataService.saveUsers(users);
            log.info("Synced {} users to the non-active database.", users.size());

            // Remove previously soft-removed entries
            activeDataService.removeAllSoftDeletedUsers();
            notActiveDataService.removeAllSoftDeletedUsers();
            log.info("Soft-deleted entries removed from both databases.");

        } catch (Exception e) {
            log.error("Error occurred during database synchronization: {}", e.getMessage(), e);
        }

        log.info("Sync job ended.");
    }
}
