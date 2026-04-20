package com.example.backend.controller;

import com.example.backend.model.User;
import com.example.backend.service.support.ContentTypeResolver;
import com.example.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final ContentTypeResolver contentTypeResolver;

    public UserController(UserService userService, ContentTypeResolver contentTypeResolver) {
        this.userService = userService;
        this.contentTypeResolver = contentTypeResolver;
    }

    @PostMapping(consumes = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            "text/csv",
            MediaType.TEXT_PLAIN_VALUE
    })
    public ResponseEntity<User> createUser(
            @RequestHeader(HttpHeaders.CONTENT_TYPE) String contentType,
            @RequestBody String rawInput
    ) {
        String normalizedContentType = contentTypeResolver.normalize(contentType);
        log.info("Received create user request with content type: {}", normalizedContentType);
        User user = userService.process(rawInput, normalizedContentType);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<User>> getAllUsers() {
        log.debug("Received request to fetch all users.");
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
