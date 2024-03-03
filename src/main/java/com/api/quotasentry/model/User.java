package com.api.quotasentry.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @Column(name = "id", updatable = false, unique = true)
    private String id;

    @Column(name = "firstName", nullable = false)
    protected String firstName;

    @Column(name = "firstName", nullable = false)
    protected String lastName;

    @Column(name = "lastLoginTimeUtc", nullable = false)
    protected LocalDateTime lastLoginTimeUtc;

    @Column(name = "requests", nullable = false)
    protected int requests; // added field: the executed number of requests of the user

    @Column(name = "isLocked", nullable = false)
    protected boolean isLocked; // added field: whether user is locked or not
}

