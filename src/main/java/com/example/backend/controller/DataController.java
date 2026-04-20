package com.example.backend.controller;

import com.example.backend.dto.ParseRequest;
import com.example.backend.dto.ParseResponse;
import com.example.backend.service.DataProcessingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/data")
public class DataController {

    private final DataProcessingService dataProcessingService;

    public DataController(DataProcessingService dataProcessingService) {
        this.dataProcessingService = dataProcessingService;
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Backend service is running");
    }

    @PostMapping("/parse")
    public ResponseEntity<ParseResponse> parse(@Valid @RequestBody ParseRequest request) {
        ParseResponse response = dataProcessingService.process(request.getFormat(), request.getPayload());
        return ResponseEntity.ok(response);
    }
}
