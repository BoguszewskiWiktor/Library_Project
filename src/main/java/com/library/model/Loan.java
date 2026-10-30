package com.library.model;

import java.time.LocalDate;

public class Loan {

    private final String isbn;
    private final String memberId;
    private final LocalDate loanDate;
    private final LocalDate dueDate;
    private LocalDate returnDate;

    public Loan(String isbn, String memberId, LocalDate loanDate, LocalDate dueDate) {
        this.isbn = Validation.requireNotBlank(isbn, "isbn");
        this.memberId = Validation.requireNotBlank(memberId,  "memberId");
        this.loanDate = Validation.requireNotNull(loanDate, "loanDate");
        this.dueDate = Validation.requireNotNull(dueDate,  "dueDate");
        if (!loanDate.isBefore(dueDate)) {
            throw new IllegalArgumentException("loanDate must be before dueDate");
        }
    }

    public boolean isActive() {
        return returnDate == null;
    }

    public boolean isOverdue(LocalDate today) {
        return isActive() && today.isAfter(dueDate);
    }

    public void makeReturnDate(LocalDate date) {
        if (!isActive()) {
            throw new IllegalStateException("Loan is already returned");
        }
        if (date == null || date.isBefore(loanDate)) {
            throw new IllegalArgumentException("returnDate must not be before loanDate");
        }
        this.returnDate = date;

    }

    public String getIsbn() {
        return isbn;
    }

    public String getMemberId() {
        return memberId;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    @Override
    public String toString() {
        return "Loan{" +
                "isbn='" + isbn + '\'' +
                ", memberId='" + memberId + '\'' +
                ", loanDate=" + loanDate +
                ", dueDate=" + dueDate +
                ", returnDate=" + returnDate +
                '}';
    }
}