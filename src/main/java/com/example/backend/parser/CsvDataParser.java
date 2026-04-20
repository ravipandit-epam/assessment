package com.example.backend.parser;

import com.example.backend.model.DataRecord;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class CsvDataParser implements DataParser {

    @Override
    public List<DataRecord> parse(String payload) {
        try {
            CSVParser parser = CSVFormat.DEFAULT.builder()
                    .setHeader("id", "name", "source")
                    .setSkipHeaderRecord(true)
                    .build()
                    .parse(new StringReader(payload));

            List<DataRecord> records = new ArrayList<>();
            for (CSVRecord record : parser) {
                records.add(new DataRecord(
                        record.get("id"),
                        record.get("name"),
                        record.get("source")
                ));
            }
            return records;
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }
}
