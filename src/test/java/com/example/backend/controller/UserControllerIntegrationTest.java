package com.example.backend.controller;

import com.example.backend.exception.GlobalExceptionHandler;
import com.example.backend.exception.ValidationException;
import com.example.backend.model.Address;
import com.example.backend.model.User;
import com.example.backend.service.UserService;
import com.example.backend.service.support.ContentTypeResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerIntegrationTest {

    private MockMvc mockMvc;
    private UserService userService;
    private ContentTypeResolver contentTypeResolver;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        contentTypeResolver = mock(ContentTypeResolver.class);
        when(contentTypeResolver.normalize(anyString())).thenReturn("application/json");
        UserController controller = new UserController(userService, contentTypeResolver);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldCreateUserFromRawInput() throws Exception {
        User user = new User("ravi", "ravi@example.com", new Address("Street", "Mumbai", "India"));
        when(userService.process(anyString(), anyString())).thenReturn(user);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"ravi\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("ravi"))
                .andExpect(jsonPath("$.address.city").value("Mumbai"));
    }

    @Test
    void shouldReturnAllUsersAsJson() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(
                new User("ravi", "ravi@example.com", new Address("Street", "Mumbai", "India"))
        ));

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("ravi"))
                .andExpect(jsonPath("$[0].email").value("ravi@example.com"));
    }

    @Test
    void shouldReturnBadRequestForValidationFailure() throws Exception {
        when(userService.process(anyString(), anyString()))
                .thenThrow(new ValidationException("Email is required."));

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"ravi\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Email is required."));
    }

    @Test
    void shouldReturnUnsupportedMediaTypeForTextHtml() throws Exception {
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.TEXT_HTML)
                        .content("<html></html>"))
                .andExpect(status().isUnsupportedMediaType());
    }
}
