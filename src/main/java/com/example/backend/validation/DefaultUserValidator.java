package com.example.backend.validation;

import com.example.backend.exception.ValidationException;
import com.example.backend.model.Address;
import com.example.backend.model.User;
import org.springframework.stereotype.Component;

@Component
public class DefaultUserValidator implements UserValidator {

    @Override
    public void validate(User user) {
        if (user == null) {
            throw new ValidationException("Parsed user cannot be null.");
        }
        if (isBlank(user.getUsername())) {
            throw new ValidationException("Username is required.");
        }
        if (isBlank(user.getEmail())) {
            throw new ValidationException("Email is required.");
        }
        Address address = user.getAddress();
        if (address == null) {
            throw new ValidationException("Address is required.");
        }
        if (isBlank(address.getStreet()) || isBlank(address.getCity()) || isBlank(address.getCountry())) {
            throw new ValidationException("Address fields street, city, and country are required.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
