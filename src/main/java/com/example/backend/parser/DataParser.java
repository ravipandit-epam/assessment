package com.example.backend.parser;

import com.example.backend.model.DataRecord;

import java.util.List;

public interface DataParser {
    List<DataRecord> parse(String payload);
}
