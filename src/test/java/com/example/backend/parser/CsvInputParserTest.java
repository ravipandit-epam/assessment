package com.example.backend.parser;

import com.example.backend.exception.ParsingException;
import com.example.backend.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CsvInputParserTest {

    private final CsvInputParser parser = new CsvInputParser();

    @Test
    void shouldParseValidCsvInput() {
        String input = """
                username,email,street,city,country
                ravi,ravi@example.com,Street,Mumbai,India
                """;

        User user = parser.parse(input);

        assertEquals("ravi", user.getUsername());
        assertEquals("Street", user.getAddress().getStreet());
    }

    @Test
    void shouldThrowWhenCsvHasNoDataRows() {
        String input = "username,email,street,city,country";

        assertThrows(ParsingException.class, () -> parser.parse(input));
    }
}
