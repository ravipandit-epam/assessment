package com.example.backend.service;

import com.example.backend.client.CacheClient;
import com.example.backend.exception.ValidationException;
import com.example.backend.model.Address;
import com.example.backend.model.User;
import com.example.backend.parser.InputParser;
import com.example.backend.parser.ParserFactory;
import com.example.backend.service.impl.UserServiceImpl;
import com.example.backend.validation.UserValidator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    @Test
    void shouldParseValidateAndCacheUser() {
        ParserFactory parserFactory = mock(ParserFactory.class);
        CacheClient cacheClient = mock(CacheClient.class);
        UserValidator userValidator = mock(UserValidator.class);
        InputParser parser = mock(InputParser.class);
        User user = new User("ravi", "ravi@example.com", new Address("Street", "Mumbai", "India"));

        when(parserFactory.getParser("application/json")).thenReturn(parser);
        when(parser.parse("payload")).thenReturn(user);

        UserServiceImpl service = new UserServiceImpl(parserFactory, cacheClient, userValidator);
        User result = service.process("payload", "application/json");

        assertEquals("ravi", result.getUsername());
        verify(cacheClient).saveUser(user);
    }

    @Test
    void shouldFailValidationWhenAddressMissing() {
        ParserFactory parserFactory = mock(ParserFactory.class);
        CacheClient cacheClient = mock(CacheClient.class);
        UserValidator userValidator = mock(UserValidator.class);
        InputParser parser = mock(InputParser.class);
        User user = new User("ravi", "ravi@example.com", null);

        when(parserFactory.getParser("application/json")).thenReturn(parser);
        when(parser.parse("payload")).thenReturn(user);

        doThrow(new ValidationException("Address is required.")).when(userValidator).validate(user);
        UserServiceImpl service = new UserServiceImpl(parserFactory, cacheClient, userValidator);

        assertThrows(ValidationException.class, () -> service.process("payload", "application/json"));
        verify(cacheClient, never()).saveUser(user);
    }

    @Test
    void shouldFailValidationWhenEmailMissing() {
        ParserFactory parserFactory = mock(ParserFactory.class);
        CacheClient cacheClient = mock(CacheClient.class);
        UserValidator userValidator = mock(UserValidator.class);
        InputParser parser = mock(InputParser.class);
        User user = new User("ravi", "   ", new Address("Street", "Mumbai", "India"));

        when(parserFactory.getParser("application/json")).thenReturn(parser);
        when(parser.parse("payload")).thenReturn(user);

        doThrow(new ValidationException("Email is required.")).when(userValidator).validate(user);
        UserServiceImpl service = new UserServiceImpl(parserFactory, cacheClient, userValidator);

        assertThrows(ValidationException.class, () -> service.process("payload", "application/json"));
        verify(cacheClient, never()).saveUser(user);
    }

    @Test
    void shouldGetAllUsersFromCacheClient() {
        ParserFactory parserFactory = mock(ParserFactory.class);
        CacheClient cacheClient = mock(CacheClient.class);
        UserValidator userValidator = mock(UserValidator.class);
        UserServiceImpl service = new UserServiceImpl(parserFactory, cacheClient, userValidator);
        List<User> users = List.of(new User("ravi", "ravi@example.com", new Address("Street", "Mumbai", "India")));

        when(cacheClient.getAllUsers()).thenReturn(users);

        List<User> result = service.getAllUsers();

        assertEquals(1, result.size());
        verify(cacheClient).getAllUsers();
    }
}
