package com.library.model;

public record Member(String id, String firstName, String lastName, String email) {

    public Member {
        id = Validation.requireNorBlankOrNull(id, "id");
        firstName = Validation.requireNotNull(firstName, "firstName");
        lastName = Validation.requireNotNull(lastName, "lastName");
        email = Validation.requireNorBlankOrNull(email, "email");
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Invalid email address");
        }
    }
}
