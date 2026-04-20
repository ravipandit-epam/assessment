package com.example.backend.service;

import com.example.backend.model.User;

import java.util.List;

public interface UserService {
    User process(String rawInput, String contentType);

    List<User> getAllUsers();
}
