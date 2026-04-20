package com.example.backend.parser;

import com.example.backend.exception.UnsupportedFormatException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ParserFactoryTest {

    @Test
    void shouldReturnJsonParserForApplicationJson() {
        InputParser jsonParser = mock(InputParser.class);
        when(jsonParser.supportedContentType()).thenReturn("application/json");

        ParserFactory parserFactory = new ParserFactory(List.of(jsonParser));

        InputParser parser = parserFactory.getParser("application/json");
        assertSame(jsonParser, parser);
    }

    @Test
    void shouldThrowForUnsupportedContentType() {
        InputParser jsonParser = mock(InputParser.class);
        when(jsonParser.supportedContentType()).thenReturn("application/json");

        ParserFactory parserFactory = new ParserFactory(List.of(jsonParser));

        assertThrows(UnsupportedFormatException.class, () -> parserFactory.getParser("application/yaml"));
    }
}
