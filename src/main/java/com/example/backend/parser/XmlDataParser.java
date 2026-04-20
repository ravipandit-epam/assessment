package com.example.backend.parser;

import com.example.backend.model.DataRecord;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class XmlDataParser implements DataParser {

    private final XmlMapper xmlMapper;

    public XmlDataParser() {
        this.xmlMapper = new XmlMapper();
    }

    @Override
    public List<DataRecord> parse(String payload) {
        try {
            return xmlMapper.readValue(payload, new TypeReference<>() {
            });
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }
}
