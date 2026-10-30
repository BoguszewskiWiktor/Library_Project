package com.library.model;

import java.time.LocalDate;

public record Book(String isbn, String title, String author, int publicationYear) {

    public Book {
        isbn = Validation.requireNotBlank(isbn, "isbn");
        title = Validation.requireNotBlank(title, "title");
        author = Validation.requireNotBlank(author, "author");
        if (publicationYear <= 0 || publicationYear > LocalDate.now().getYear()) {
            throw new IllegalArgumentException(
                    "Publication year must be in range [0 - " + LocalDate.now().getYear() + "]"
            );
        }
    }

}