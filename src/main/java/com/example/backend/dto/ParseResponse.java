package com.example.backend.dto;

import com.example.backend.model.DataRecord;

import java.util.List;

public class ParseResponse {

    private String format;
    private int count;
    private List<DataRecord> records;

    public ParseResponse() {
    }

    public ParseResponse(String format, int count, List<DataRecord> records) {
        this.format = format;
        this.count = count;
        this.records = records;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<DataRecord> getRecords() {
        return records;
    }

    public void setRecords(List<DataRecord> records) {
        this.records = records;
    }
}
