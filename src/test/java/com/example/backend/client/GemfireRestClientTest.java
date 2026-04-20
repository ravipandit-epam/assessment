package com.example.backend.client;

import com.example.backend.exception.CacheClientException;
import com.example.backend.model.Address;
import com.example.backend.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GemfireRestClientTest {

    @Test
    void shouldSaveUserWithRetry() {
        RestTemplate restTemplate = mock(RestTemplate.class);
        GemfireClientProperties properties = new GemfireClientProperties();
        properties.setBaseUrl("http://localhost:7070");
        properties.setRegionName("users");
        properties.setRetryAttempts(2);
        properties.setRetryDelayMs(0);

        User user = new User("ravi", "ravi@example.com", new Address("Street", "Mumbai", "India"));

        when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(), eq(Void.class)))
                .thenThrow(new RestClientException("temporary"))
                .thenReturn(ResponseEntity.ok().build());

        GemfireRestClient client = new GemfireRestClient(restTemplate, properties);
        client.saveUser(user);

        verify(restTemplate, times(2)).exchange(anyString(), eq(HttpMethod.PUT), any(), eq(Void.class));
    }

    @Test
    void shouldReturnUsersFromGemfireRegion() {
        RestTemplate restTemplate = mock(RestTemplate.class);
        GemfireClientProperties properties = new GemfireClientProperties();
        properties.setRetryAttempts(1);

        User[] users = {
                new User("ravi", "ravi@example.com", new Address("Street", "Mumbai", "India"))
        };
        when(restTemplate.getForEntity(anyString(), eq(User[].class))).thenReturn(ResponseEntity.ok(users));

        GemfireRestClient client = new GemfireRestClient(restTemplate, properties);
        List<User> result = client.getAllUsers();

        assertEquals(1, result.size());
        assertEquals("ravi", result.get(0).getUsername());
    }

    @Test
    void shouldThrowCacheClientExceptionAfterRetryExhausted() {
        RestTemplate restTemplate = mock(RestTemplate.class);
        GemfireClientProperties properties = new GemfireClientProperties();
        properties.setRetryAttempts(2);
        properties.setRetryDelayMs(0);

        when(restTemplate.getForEntity(anyString(), eq(User[].class)))
                .thenThrow(new RestClientException("failure"));

        GemfireRestClient client = new GemfireRestClient(restTemplate, properties);

        assertThrows(CacheClientException.class, client::getAllUsers);
        verify(restTemplate, times(2)).getForEntity(anyString(), eq(User[].class));
    }
}
