package com.example.backend.controller;

import com.example.backend.model.Address;
import com.example.backend.model.User;
import com.example.backend.service.UserService;
import com.example.backend.service.support.ContentTypeResolver;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserControllerTest {

    @Test
    void shouldCreateUserAndReturnCreatedStatus() {
        UserService userService = mock(UserService.class);
        ContentTypeResolver contentTypeResolver = mock(ContentTypeResolver.class);
        UserController controller = new UserController(userService, contentTypeResolver);
        User user = new User("ravi", "ravi@example.com", new Address("Street", "Mumbai", "India"));

        when(contentTypeResolver.normalize("application/json; charset=UTF-8")).thenReturn("application/json");
        when(userService.process("payload", "application/json")).thenReturn(user);

        ResponseEntity<User> response = controller.createUser("application/json; charset=UTF-8", "payload");

        assertEquals(HttpStatusCode.valueOf(201), response.getStatusCode());
        assertEquals("ravi", response.getBody().getUsername());
        verify(userService).process("payload", "application/json");
    }

    @Test
    void shouldReturnAllUsers() {
        UserService userService = mock(UserService.class);
        ContentTypeResolver contentTypeResolver = mock(ContentTypeResolver.class);
        UserController controller = new UserController(userService, contentTypeResolver);
        List<User> users = List.of(
                new User("ravi", "ravi@example.com", new Address("Street", "Mumbai", "India"))
        );
        when(userService.getAllUsers()).thenReturn(users);

        ResponseEntity<List<User>> response = controller.getAllUsers();

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(userService).getAllUsers();
    }
}
