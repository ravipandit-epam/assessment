package com.example.backend.service;

import com.example.backend.client.ExternalDataClient;
import com.example.backend.dto.ParseResponse;
import com.example.backend.exception.UnsupportedFormatException;
import com.example.backend.model.DataRecord;
import com.example.backend.parser.CsvDataParser;
import com.example.backend.parser.JsonDataParser;
import com.example.backend.parser.XmlDataParser;
import com.example.backend.service.impl.DataProcessingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DataProcessingServiceImplTest {

    @Mock
    private JsonDataParser jsonDataParser;

    @Mock
    private XmlDataParser xmlDataParser;

    @Mock
    private CsvDataParser csvDataParser;

    @Mock
    private ExternalDataClient externalDataClient;

    @InjectMocks
    private DataProcessingServiceImpl service;

    @BeforeEach
    void setUp() {
        when(externalDataClient.fetchRawPayload(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void shouldProcessJsonPayload() {
        when(jsonDataParser.parse(anyString())).thenReturn(List.of(new DataRecord("1", "Alice", "json")));

        ParseResponse response = service.process("json", "[{\"id\":\"1\",\"name\":\"Alice\",\"source\":\"json\"}]");

        assertEquals("json", response.getFormat());
        assertEquals(1, response.getCount());
    }

    @Test
    void shouldThrowForUnsupportedFormat() {
        assertThrows(UnsupportedFormatException.class, () -> service.process("yaml", "test"));
    }
}
