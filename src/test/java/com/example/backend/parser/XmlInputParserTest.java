package com.example.backend.parser;

import com.example.backend.exception.ParsingException;
import com.example.backend.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class XmlInputParserTest {

    private final XmlInputParser parser = new XmlInputParser();

    @Test
    void shouldParseValidXmlInput() {
        String input = """
                <user>
                  <username>ravi</username>
                  <email>ravi@example.com</email>
                  <address>
                    <street>Street</street>
                    <city>Mumbai</city>
                    <country>India</country>
                  </address>
                </user>
                """;

        User user = parser.parse(input);

        assertEquals("ravi", user.getUsername());
        assertEquals("India", user.getAddress().getCountry());
    }

    @Test
    void shouldThrowWhenXmlIsInvalid() {
        String input = "<user><username>ravi</username>";

        assertThrows(ParsingException.class, () -> parser.parse(input));
    }
}
