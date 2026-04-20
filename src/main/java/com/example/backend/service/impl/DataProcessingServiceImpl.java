package com.example.backend.service.impl;

import com.example.backend.client.ExternalDataClient;
import com.example.backend.dto.ParseResponse;
import com.example.backend.exception.UnsupportedFormatException;
import com.example.backend.model.DataRecord;
import com.example.backend.parser.CsvDataParser;
import com.example.backend.parser.JsonDataParser;
import com.example.backend.parser.XmlDataParser;
import com.example.backend.service.DataProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class DataProcessingServiceImpl implements DataProcessingService {

    private static final Logger log = LoggerFactory.getLogger(DataProcessingServiceImpl.class);

    private final JsonDataParser jsonDataParser;
    private final XmlDataParser xmlDataParser;
    private final CsvDataParser csvDataParser;
    private final ExternalDataClient externalDataClient;

    public DataProcessingServiceImpl(JsonDataParser jsonDataParser,
                                     XmlDataParser xmlDataParser,
                                     CsvDataParser csvDataParser,
                                     ExternalDataClient externalDataClient) {
        this.jsonDataParser = jsonDataParser;
        this.xmlDataParser = xmlDataParser;
        this.csvDataParser = csvDataParser;
        this.externalDataClient = externalDataClient;
    }

    @Override
    public ParseResponse process(String format, String payload) {
        log.info("Processing data payload with requested format: {}", format);
        String rawPayload = externalDataClient.fetchRawPayload(payload);
        String normalizedFormat = format.toLowerCase(Locale.ROOT);

        List<DataRecord> records = switch (normalizedFormat) {
            case "json" -> jsonDataParser.parse(rawPayload);
            case "xml" -> xmlDataParser.parse(rawPayload);
            case "csv" -> csvDataParser.parse(rawPayload);
            default -> throw new UnsupportedFormatException("Unsupported format: " + format);
        };

        log.debug("Parsed {} records using '{}' format.", records.size(), normalizedFormat);
        return new ParseResponse(normalizedFormat, records.size(), records);
    }
}
