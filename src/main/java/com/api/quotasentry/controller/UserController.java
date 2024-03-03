package com.api.quotasentry.controller;

import com.api.quotasentry.dto.UserDTO;
import com.api.quotasentry.service.DbService;
import com.api.quotasentry.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("user")
public class UserController {

    private final DbService dbService;

    public UserController(DbService dbService) {
        this.dbService = dbService;
    }

    /**
     * url examples.
     *
     * "http://localhost:8080/user/delete/179c3abc-bf63-4c34-aca6-7550195481ad"
     *
     * @return
     */
    @DeleteMapping("delete/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable String userId) {
        dbService.deleteUser(userId);
        return ResponseEntity.ok().body("User deleted " + userId);
    }

    /**
     * url examples.
     *
     * "http://localhost:8080/user/get/179c3abc-bf63-4c34-aca6-7550195481ad"
     *
     * @return
     */
    @GetMapping("get/{userId}")
    public ResponseEntity<String> getUser(@PathVariable String userId) {
        UserDTO userDTO = dbService.getUser(userId);
        return ResponseEntity.ok().body(JsonUtils.toJson(userDTO));
    }
}
