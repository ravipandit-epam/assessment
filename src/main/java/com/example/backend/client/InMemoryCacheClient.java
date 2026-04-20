package com.example.backend.client;

import com.example.backend.model.User;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Primary
@ConditionalOnProperty(name = "gemfire.enabled", havingValue = "false", matchIfMissing = true)
public class InMemoryCacheClient implements CacheClient {

    private final Map<String, User> cache = new ConcurrentHashMap<>();

    @Override
    public void saveUser(User user) {
        cache.put(user.getUsername(), user);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(cache.values());
    }
}
