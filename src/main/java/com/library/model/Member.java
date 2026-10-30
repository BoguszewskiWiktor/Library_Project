package com.library.model;

public record Member(String id, String firstName, String lastName, String email) {

    public Member {
        id = Validation.requireNotBlank(id, "id");
        firstName = Validation.requireNotNull(firstName, "firstName");
        lastName = Validation.requireNotNull(lastName, "lastName");
        email = Validation.requireNotBlank(email, "email");
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Invalid email address");
        }
    }
}
