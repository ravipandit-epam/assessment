package com.example.backend.parser;

import com.example.backend.exception.UnsupportedFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ParserFactory {

    private static final Logger log = LoggerFactory.getLogger(ParserFactory.class);

    private final Map<String, InputParser> parserRegistry;

    public ParserFactory(List<InputParser> inputParsers) {
        // New parser strategies are auto-registered via Spring bean scanning.
        this.parserRegistry = inputParsers.stream()
                .collect(Collectors.toUnmodifiableMap(
                        parser -> parser.supportedContentType().toLowerCase(Locale.ROOT),
                        Function.identity()
                ));
    }

    public InputParser getParser(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            throw new UnsupportedFormatException("Content type must be provided.");
        }
        InputParser parser = parserRegistry.get(contentType.toLowerCase(Locale.ROOT));
        if (parser == null) {
            log.warn("No parser registered for content type: {}", contentType);
            throw new UnsupportedFormatException("Unsupported content type: " + contentType);
        }
        log.debug("Resolved parser '{}' for content type '{}'.", parser.getClass().getSimpleName(), contentType);
        return parser;
    }
}
