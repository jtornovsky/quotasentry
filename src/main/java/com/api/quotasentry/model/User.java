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

    @Column(name = "lastLoginTimeUtc")
    protected LocalDateTime lastLoginTimeUtc;

    @Column(name = "requests", nullable = false)
    protected int requests; // added field: the executed number of requests of the user

    @Column(name = "isLocked", nullable = false)
    protected boolean isLocked; // added field: whether user is locked or not

    @Column(name = "isDeleted", nullable = false, columnDefinition = "boolean default false") // added field: whether user is deleted or not
    private boolean isDeleted;

    @Column(name = "created", nullable = false, updatable = false)
    @CreatedDate
    protected LocalDateTime created = LocalDateTime.now(ZoneOffset.UTC);

    @Column(name = "modified", nullable = false)
    @LastModifiedDate
    protected LocalDateTime modified = LocalDateTime.now(ZoneOffset.UTC);
}

