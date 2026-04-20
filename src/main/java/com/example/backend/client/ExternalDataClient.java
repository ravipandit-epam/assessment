package com.example.backend.client;

import org.springframework.stereotype.Component;

@Component
public class ExternalDataClient {

    public String fetchRawPayload(String payload) {
        return payload;
    }
}
