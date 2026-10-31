package com.library.exception;

public class MemberNotFoundException extends LibraryException {
    public MemberNotFoundException(String memberId) {
        super("Member with ID " + memberId + " was not found");
    }
}
