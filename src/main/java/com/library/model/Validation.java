package com.library.model;

import java.time.LocalDate;

public class Validation {

    private Validation() {}

    static String requireNotBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.strip();
    }

    static String requireNotNull(String value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value;
    }

    static LocalDate requireNotNull(LocalDate value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value;
    }

}