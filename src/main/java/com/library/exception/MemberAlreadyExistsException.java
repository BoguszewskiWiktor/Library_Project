package com.library.exception;

public class MemberAlreadyExistsException extends LibraryException {
    public MemberAlreadyExistsException(String memberId) {
        super("Member with ID " + memberId + " already exists");
    }
}
