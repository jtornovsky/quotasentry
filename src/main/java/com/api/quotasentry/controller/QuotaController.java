package com.api.quotasentry.controller;

import com.api.quotasentry.dto.UserDTO;
import com.api.quotasentry.model.DbType;
import com.api.quotasentry.model.User;
import com.api.quotasentry.service.DbService;
import com.api.quotasentry.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("quota")
public class QuotaController {

    private final DbService dbService;

    @Autowired
    public QuotaController(DbService dbService) {
        this.dbService = dbService;
    }

    /**
     * url examples.
     *
     * "http://localhost:8080/quota/consume/179c3abc-bf63-4c34-aca6-7550195481ad"
     *
     * @return
     */
    @PutMapping("consume/{userId}")
    public ResponseEntity<String> consumeQuota(@PathVariable String userId) {
        dbService.consumeQuota(userId);
        return ResponseEntity.ok().body("Quota consuming request sent for user " + userId);
    }

    /**
     * url examples.
     *
     * "http://localhost:8080/quota/getdata"
     *
     * @return
     */
    @GetMapping("getdata")
    public ResponseEntity<String> getUsersQuota() {
        List<UserDTO> users = dbService.getUsersQuota();
        return ResponseEntity.ok().body(JsonUtils.toJson(users));
    }
}
