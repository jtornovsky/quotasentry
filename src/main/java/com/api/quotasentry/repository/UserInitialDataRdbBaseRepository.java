package com.api.quotasentry.repository;

import com.api.quotasentry.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class UserInitialDataRdbBaseRepository extends UserDataRdbBaseRepository {

    public UserInitialDataRdbBaseRepository(ConnectionProvider connectionProvider) {
        super("user_initial_data", connectionProvider);
    }

    public List<User> getUserInitialData() {
        return getAllUsers();
    }
}