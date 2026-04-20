package com.example.backend.service;

import com.example.backend.dto.ParseResponse;

public interface DataProcessingService {
    ParseResponse process(String format, String payload);
}
