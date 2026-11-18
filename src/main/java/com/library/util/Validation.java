package com.library.util;

import java.time.LocalDate;

public class Validation {

    private Validation() {}

    public static String requireNotBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.strip();
    }

    public static LocalDate requireNotNull(LocalDate value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value;
    }

}