package com.api.quotasentry.controller;

import com.api.quotasentry.dto.UserDTO;
import com.api.quotasentry.service.DbService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/quota")
public class QuotaController {

    private final DbService dbService;

    @Autowired
    public QuotaController(DbService dbService) {
        this.dbService = dbService;
    }

    /**
     * URL example:
     * "http://localhost:8080/quota/consume/179c3abc-bf63-4c34-aca6-7550195481ad"
     */
    @PutMapping("/consume/{userId}")
    public ResponseEntity<String> consumeQuota(@PathVariable String userId) {
        try {
            dbService.consumeQuota(userId);
            return ResponseEntity.ok().body("Quota consuming request sent for user " + userId);
        } catch (Exception e) {
            log.error("Error consuming quota for user with id {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error consuming quota: " + e.getMessage());
        }
    }

    /**
     * URL example:
     * "http://localhost:8080/quota/getdata"
     */
    @GetMapping("/getdata")
    public ResponseEntity<?> getUsersQuota() {
        try {
            List<UserDTO> users = dbService.getUsersQuota();
            return ResponseEntity.ok().body(users);
        } catch (Exception e) {
            log.error("Error retrieving user quotas", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving user quotas: " + e.getMessage());
        }
    }
}
