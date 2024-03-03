package com.api.quotasentry.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    protected String firstName;
    protected String lastName;
    protected LocalDateTime lastLoginTimeUtc;
    protected int requests;
    protected boolean isLocked;
}
