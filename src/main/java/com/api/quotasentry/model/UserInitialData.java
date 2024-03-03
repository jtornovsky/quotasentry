package com.api.quotasentry.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "user_initial_data")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInitialData extends User {

    @Enumerated(EnumType.STRING)
    @Column(name = "targetDb", nullable = false)
    private DbType targetDb;
}

