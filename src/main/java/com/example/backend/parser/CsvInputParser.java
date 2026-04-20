package com.example.backend.parser;

import com.example.backend.exception.ParsingException;
import com.example.backend.model.Address;
import com.example.backend.model.User;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.StringReader;
import java.util.List;

@Component
public class CsvInputParser implements InputParser {

    private static final Logger log = LoggerFactory.getLogger(CsvInputParser.class);

    @Override
    public String supportedContentType() {
        return "text/csv";
    }

    @Override
    public User parse(String input) {
        try {
            CSVParser csvParser = CSVFormat.DEFAULT.builder()
                    .setHeader("username", "email", "street", "city", "country")
                    .setSkipHeaderRecord(true)
                    .build()
                    .parse(new StringReader(input));

            List<CSVRecord> records = csvParser.getRecords();
            if (records.isEmpty()) {
                throw new ParsingException("CSV input is empty. Expected one user row.", null);
            }

            CSVRecord record = records.get(0);
            Address address = new Address(
                    record.get("street"),
                    record.get("city"),
                    record.get("country")
            );

            return new User(
                    record.get("username"),
                    record.get("email"),
                    address
            );
        } catch (ParsingException ex) {
            throw ex;
        } catch (Exception ex) {
            log.warn("Failed to parse CSV input: {}", ex.getMessage());
            throw new ParsingException("Invalid CSV input for User parsing.", ex);
        }
    }
}
