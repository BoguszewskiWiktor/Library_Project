package com.library.exception;

public class LoanNotFoundException extends LibraryException {
    public LoanNotFoundException(String isbn) {
        super("No active loan for book with ISBN " + isbn);
    }
}
