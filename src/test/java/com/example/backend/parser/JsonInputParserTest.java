package com.example.backend.parser;

import com.example.backend.exception.ParsingException;
import com.example.backend.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonInputParserTest {

    private final JsonInputParser parser = new JsonInputParser(new ObjectMapper());

    @Test
    void shouldParseValidJsonInput() {
        String input = """
                {
                  "username": "ravi",
                  "email": "ravi@example.com",
                  "address": {
                    "street": "Street",
                    "city": "Mumbai",
                    "country": "India"
                  }
                }
                """;

        User user = parser.parse(input);

        assertEquals("ravi", user.getUsername());
        assertEquals("Mumbai", user.getAddress().getCity());
    }

    @Test
    void shouldThrowWhenJsonIsInvalid() {
        String input = "{ invalid-json }";

        assertThrows(ParsingException.class, () -> parser.parse(input));
    }
}
