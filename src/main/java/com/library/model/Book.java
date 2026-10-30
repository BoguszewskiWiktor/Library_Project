package com.library.model;

import java.time.LocalDate;

public record Book(String isbn, String title, String author, int publicationYear) {

    public Book {
        isbn = Validation.requireNorBlankOrNull(isbn, "isbn");
        title = Validation.requireNorBlankOrNull(title, "title");
        author = Validation.requireNorBlankOrNull(author, "author");
        if (publicationYear <= 0 || publicationYear > LocalDate.now().getYear()) {
            throw new IllegalArgumentException(
                    "Publication year must be in range [0 - " + LocalDate.now().getYear() + "]"
            );
        }
    }

}