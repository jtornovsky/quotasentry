package com.api.quotasentry.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Represents a user in the application.
 */
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
    private String firstName;

    @Column(name = "lastName", nullable = false)
    private String lastName;

    @Column(name = "lastLoginTimeUtc")
    private LocalDateTime lastLoginTimeUtc;

    @Column(name = "requests", nullable = false)
    private int requests; // added field: the executed number of requests of the user

    @Column(name = "locked", nullable = false)
    private boolean locked; // added field: whether user is locked or not

    @Column(name = "deleted", nullable = false, columnDefinition = "boolean default false")
    private boolean deleted;    // added field: whether user is deleted or not

    @Column(name = "created", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime created = LocalDateTime.now(ZoneOffset.UTC);

    @Column(name = "modified", nullable = false)
    @LastModifiedDate
    private LocalDateTime modified = LocalDateTime.now(ZoneOffset.UTC);
}

