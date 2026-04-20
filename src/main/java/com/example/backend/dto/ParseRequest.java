package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class ParseRequest {

    @NotBlank
    private String format;

    @NotBlank
    private String payload;

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
