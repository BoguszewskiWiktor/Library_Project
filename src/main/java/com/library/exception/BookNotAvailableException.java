package com.library.exception;

public class BookNotAvailableException extends LibraryException {
    public BookNotAvailableException(String isbn) {
        super("Book with ISBN " + isbn + " is already borrowed");
    }
}
