package com.api.quotasentry.service;

import com.api.quotasentry.dto.UserDTO;
import com.api.quotasentry.model.User;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public UserDTO convertUserToUserDto(User user) {
        return UserDTO.builder().id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .lastLoginTimeUtc(user.getLastLoginTimeUtc())
                .requests(user.getRequests())
                .isLocked(user.isLocked())
                .build();
    }
}
