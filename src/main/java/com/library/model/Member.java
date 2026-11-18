package com.library.model;

import com.library.util.Validation;

public record Member(String id, String firstName, String lastName, String email) {

    public Member {
        id = Validation.requireNotBlank(id, "id");
        firstName = Validation.requireNotBlank(firstName, "firstName");
        lastName = Validation.requireNotBlank(lastName, "lastName");
        email = Validation.requireNotBlank(email, "email");
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Invalid email address. Email must contain ‘@’");
        }
    }
}
