package com.example.backend.parser;

import com.example.backend.exception.ParsingException;
import com.example.backend.model.User;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.StringReader;

@Component
public class XmlInputParser implements InputParser {

    private static final Logger log = LoggerFactory.getLogger(XmlInputParser.class);

    @Override
    public String supportedContentType() {
        return "application/xml";
    }

    @Override
    public User parse(String input) {
        try {
            JAXBContext context = JAXBContext.newInstance(User.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            Object object = unmarshaller.unmarshal(new StringReader(input));
            return (User) object;
        } catch (Exception ex) {
            log.warn("Failed to parse XML input: {}", ex.getMessage());
            throw new ParsingException("Invalid XML input for User parsing.", ex);
        }
    }
}
