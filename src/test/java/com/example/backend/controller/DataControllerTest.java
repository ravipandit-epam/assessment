package com.example.backend.controller;

import com.example.backend.dto.ParseRequest;
import com.example.backend.dto.ParseResponse;
import com.example.backend.model.DataRecord;
import com.example.backend.service.DataProcessingService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DataControllerTest {

    @Test
    void shouldParseDataSuccessfully() {
        DataProcessingService dataProcessingService = mock(DataProcessingService.class);
        DataController dataController = new DataController(dataProcessingService);

        ParseRequest request = new ParseRequest();
        request.setFormat("json");
        request.setPayload("[{\"id\":\"1\",\"name\":\"Alice\",\"source\":\"json\"}]");

        ParseResponse response = new ParseResponse(
                "json",
                1,
                List.of(new DataRecord("1", "Alice", "json"))
        );

        when(dataProcessingService.process(anyString(), anyString())).thenReturn(response);

        ResponseEntity<ParseResponse> result = dataController.parse(request);

        org.junit.jupiter.api.Assertions.assertEquals(HttpStatusCode.valueOf(200), result.getStatusCode());
        org.junit.jupiter.api.Assertions.assertEquals("json", result.getBody().getFormat());
        org.junit.jupiter.api.Assertions.assertEquals(1, result.getBody().getCount());
    }
}
