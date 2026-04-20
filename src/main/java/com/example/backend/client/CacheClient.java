package com.example.backend.client;

import com.example.backend.model.User;

import java.util.List;

public interface CacheClient {
    void saveUser(User user);

    List<User> getAllUsers();
}
