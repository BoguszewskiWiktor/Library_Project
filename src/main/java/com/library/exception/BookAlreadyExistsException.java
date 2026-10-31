package com.library.exception;

public class BookAlreadyExistsException extends LibraryException {
    public BookAlreadyExistsException(String isbn) {
        super("Book with ISBN " + isbn + " already exists");
    }
}
