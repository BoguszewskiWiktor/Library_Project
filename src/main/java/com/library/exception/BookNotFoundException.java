package com.library.exception;

public class BookNotFoundException extends LibraryException {
    public BookNotFoundException(String isbn) {
        super("Book with ISBN " + isbn + " was not found");
    }
}
