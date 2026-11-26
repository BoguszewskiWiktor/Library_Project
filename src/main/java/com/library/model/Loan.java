package com.library.model;

import com.library.util.Validation;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

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

    public void makeReturn(LocalDate date) {
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

    /**
     * Returns the number of days this loan is (or was) past its due date.
     * For an active loan the days are counted up to {@code today}; for a returned
     * loan they are counted up to the return date, so the value no longer grows
     * after the book is returned. Never negative.
     */
    public int overdueDays(LocalDate today) {
        Validation.requireNotNull(today, "today");

        // A returned loan stops accumulating overdue days on the return date.
        LocalDate endDate = isActive() ? today : returnDate;
        long days = ChronoUnit.DAYS.between(dueDate, endDate);

        // Returning early or on time means 0, never a negative number.
        return (int) Math.max(0, days);
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Loan loan = (Loan) o;
        return Objects.equals(isbn, loan.isbn) && Objects.equals(memberId, loan.memberId) && Objects.equals(loanDate, loan.loanDate) && Objects.equals(dueDate, loan.dueDate) && Objects.equals(returnDate, loan.returnDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn, memberId, loanDate, dueDate, returnDate);
    }
}