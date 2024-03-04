package com.api.quotasentry.controller;

import com.api.quotasentry.dto.UserDTO;
import com.api.quotasentry.model.User;
import com.api.quotasentry.service.DbService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
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
        try {
            dbService.deleteUser(userId);
            return ResponseEntity.ok().body("User deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting user with id {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting user: " + e.getMessage());
        }
    }

    /**
     * url examples.
     *
     * "http://localhost:8080/user/get/179c3abc-bf63-4c34-aca6-7550195481ad"
     *
     * @return
     */
    @GetMapping("/get/{userId}")
    public ResponseEntity<?> getUser(@PathVariable String userId) {
        try {
            UserDTO userDTO = dbService.getUser(userId);
            if (userDTO != null) {
                return ResponseEntity.ok().body(userDTO);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error retrieving user with id {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving user: " + e.getMessage());
        }
    }

    /**
     * url examples.
     *
     * "http://localhost:8080/user/create"
     * body:
         {
         "id": "179c3abc-0000-1111-aca6-7550195481ad",
         "firstName": "AAAA",
         "lastName": "BBBB",
         "requests": 0,
         "isLocked": false,
         "isDeleted": false,
         "created": "2024-03-01T00:00:00Z",
         "modified": "2024-03-01T00:00:00Z"
         }
     *
     * @return
     */
    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            dbService.createUser(user);
            return ResponseEntity.ok("User created successfully");
        } catch (Exception e) {
            log.error("Error creating user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating user: " + e.getMessage());
        }
    }

    /**
     * url examples.
     *
     * "http://localhost:8080/user/update/179c3abc-0000-1111-aca6-7550195481ad"
     * body:
     {
     "id": "179c3abc-0000-1111-aca6-7550195481ad",
     "firstName": "AAAAGGGG",
     "lastName": "BBBBVVVVV",
     "lastLoginTimeUtc": "2024-03-01T00:00:00Z",
     "requests": 3,
     "isLocked": true,
     "isDeleted": true,
     "created": "2024-03-01T00:00:00Z",
     "modified": "2025-03-01T00:00:00Z"
     }
     *
     * @return
     */
    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody User user) {
        try {
            dbService.updateUser(id, user);
            return ResponseEntity.ok("User updated successfully");
        } catch (Exception e) {
            log.error("Error updating user with id {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating user: " + e.getMessage());
        }
    }
}
