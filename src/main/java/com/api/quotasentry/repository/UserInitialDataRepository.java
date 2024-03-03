package com.api.quotasentry.repository;

import com.api.quotasentry.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class UserInitialDataRepository extends RdbDataRepository {

    private final ConnectionProvider connectionProvider;

    public UserInitialDataRepository(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public List<User> getUserInitialData() {
        return getUsers("user_initial_data", connectionProvider);
    }

}