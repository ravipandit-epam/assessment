package com.example.backend.client;

import com.example.backend.exception.CacheClientException;
import com.example.backend.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
@Primary
public class GemfireRestClient implements CacheClient {

    private static final Logger log = LoggerFactory.getLogger(GemfireRestClient.class);

    private final RestTemplate restTemplate;
    private final GemfireClientProperties properties;

    public GemfireRestClient(RestTemplate restTemplate, GemfireClientProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    @Override
    public void saveUser(User user) {
        executeWithRetry(() -> {
            String url = buildRegionUrl() + "/" + user.getUsername();
            HttpEntity<User> request = new HttpEntity<>(user);
            restTemplate.exchange(url, HttpMethod.PUT, request, Void.class);
            log.debug("Saved user '{}' to GemFire region '{}'.", user.getUsername(), properties.getRegionName());
            return null;
        }, "Failed to save user to GemFire.");
    }

    @Override
    public List<User> getAllUsers() {
        return executeWithRetry(() -> {
            ResponseEntity<User[]> response = restTemplate.getForEntity(buildRegionUrl(), User[].class);
            User[] body = response.getBody();
            if (body == null) {
                return Collections.emptyList();
            }
            log.debug("Fetched {} user entries from GemFire region '{}'.", body.length, properties.getRegionName());
            return Arrays.asList(body);
        }, "Failed to retrieve users from GemFire.");
    }

    private String buildRegionUrl() {
        String baseUrl = properties.getBaseUrl().replaceAll("/+$", "");
        return baseUrl + "/geode/v1/" + properties.getRegionName();
    }

    private <T> T executeWithRetry(RetryOperation<T> operation, String errorMessage) {
        int attempts = Math.max(1, properties.getRetryAttempts());
        long retryDelayMs = Math.max(0, properties.getRetryDelayMs());
        RestClientException lastException = null;

        for (int currentAttempt = 1; currentAttempt <= attempts; currentAttempt++) {
            try {
                return operation.execute();
            } catch (RestClientException ex) {
                lastException = ex;
                log.warn("GemFire call failed on attempt {}/{}: {}", currentAttempt, attempts, ex.getMessage());
                if (currentAttempt == attempts) {
                    break;
                }
                sleepQuietly(retryDelayMs);
            }
        }

        log.error("GemFire call failed after {} attempts. Last error: {}", attempts,
                lastException != null ? lastException.getMessage() : "n/a");
        throw new CacheClientException(errorMessage, lastException);
    }

    private void sleepQuietly(long retryDelayMs) {
        if (retryDelayMs <= 0) {
            return;
        }
        try {
            Thread.sleep(retryDelayMs);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new CacheClientException("Retry interrupted while communicating with GemFire.", ex);
        }
    }

    @FunctionalInterface
    private interface RetryOperation<T> {
        T execute();
    }
}
