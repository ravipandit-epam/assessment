package com.example.backend.parser;

import com.example.backend.model.User;

public interface InputParser {
    String supportedContentType();

    User parse(String input);
}
