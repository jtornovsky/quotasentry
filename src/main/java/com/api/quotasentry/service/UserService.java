package com.api.quotasentry.service;

import com.api.quotasentry.dto.UserDTO;
import com.api.quotasentry.model.User;
import org.springframework.stereotype.Service;

/**
 * A service class responsible for converting User objects to UserDTO objects.
 */
@Service
public class UserService {

    /**
     * Converts a User object to a UserDTO object.
     *
     * @param user The User object to be converted.
     * @return A UserDTO object representing the converted User.
     */
    public UserDTO convertUserToUserDto(User user) {
        return UserDTO.builder().id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .lastLoginTimeUtc(user.getLastLoginTimeUtc())
                .requests(user.getRequests())
                .locked(user.isLocked())
                .build();
    }
}
