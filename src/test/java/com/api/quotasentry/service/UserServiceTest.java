package com.api.quotasentry.service;

import com.api.quotasentry.dto.UserDTO;
import com.api.quotasentry.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserServiceTest {

    @Test
    public void testConvertUserToUserDto() {

        User user = new User();
        user.setId("123");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setLastLoginTimeUtc(LocalDateTime.of(2022, 1, 1, 12, 0)); // Example date and time
        user.setRequests(5);
        user.setLocked(true);

        UserService userService = new UserService();

        UserDTO userDTO = userService.convertUserToUserDto(user);

        // Assert the values in the UserDTO
        assertEquals(user.getId(), userDTO.getId());
        assertEquals(user.getFirstName(), userDTO.getFirstName());
        assertEquals(user.getLastName(), userDTO.getLastName());
        assertEquals(user.getLastLoginTimeUtc(), userDTO.getLastLoginTimeUtc());
        assertEquals(user.getRequests(), userDTO.getRequests());
        assertEquals(user.isLocked(), userDTO.isLocked());
    }
}
