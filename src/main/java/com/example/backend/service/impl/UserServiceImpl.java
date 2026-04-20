package com.example.backend.service.impl;

import com.example.backend.client.CacheClient;
import com.example.backend.model.User;
import com.example.backend.parser.InputParser;
import com.example.backend.parser.ParserFactory;
import com.example.backend.service.UserService;
import com.example.backend.validation.UserValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final ParserFactory parserFactory;
    private final CacheClient cacheClient;
    private final UserValidator userValidator;

    public UserServiceImpl(ParserFactory parserFactory, CacheClient cacheClient, UserValidator userValidator) {
        this.parserFactory = parserFactory;
        this.cacheClient = cacheClient;
        this.userValidator = userValidator;
    }

    @Override
    public User process(String rawInput, String contentType) {
        log.info("Processing user input for content type: {}", contentType);
        InputParser parser = parserFactory.getParser(contentType);
        User user = parser.parse(rawInput);
        userValidator.validate(user);
        cacheClient.saveUser(user);
        log.info("User '{}' validated and persisted to cache.", user.getUsername());
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        log.debug("Fetching all users from cache.");
        return cacheClient.getAllUsers();
    }
}
