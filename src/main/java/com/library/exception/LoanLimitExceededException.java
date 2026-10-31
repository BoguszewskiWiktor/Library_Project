package com.library.exception;

public class LoanLimitExceededException extends LibraryException {
    public LoanLimitExceededException(String memberId) {
        super("Member " + memberId + " reached the limit of 5 active loans." );
    }
}