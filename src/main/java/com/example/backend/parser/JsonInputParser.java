package com.example.backend.parser;

import com.example.backend.exception.ParsingException;
import com.example.backend.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class JsonInputParser implements InputParser {

    private static final Logger log = LoggerFactory.getLogger(JsonInputParser.class);

    private final ObjectMapper objectMapper;

    public JsonInputParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String supportedContentType() {
        return "application/json";
    }

    @Override
    public User parse(String input) {
        try {
            return objectMapper.readValue(input, User.class);
        } catch (Exception ex) {
            log.warn("Failed to parse JSON input: {}", ex.getMessage());
            throw new ParsingException("Invalid JSON input for User parsing.", ex);
        }
    }
}
