package com.library.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;


public class LoanTest {

    private static final LocalDate LOAN_DATE = LocalDate.of(2000, 1, 10);
    private static final LocalDate DUE_DATE = LocalDate.of(2000, 1, 24);

    @Test
    void constructor_withValidData_storesValuesAndIsActive() {
        Loan loan = new Loan("111", "A-1", LOAN_DATE, DUE_DATE);

        assertEquals("111", loan.getIsbn());
        assertEquals("A-1", loan.getMemberId());
        assertEquals(LOAN_DATE, loan.getLoanDate());
        assertEquals(DUE_DATE, loan.getDueDate());
        assertNull(loan.getReturnDate());
        assertTrue(loan.isActive());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   ", "\t"})
    void constructor_whenIsbnIsBlank_throwsIllegalArgumentException(String isbn) {
        assertThrows(IllegalArgumentException.class, () -> new Loan(isbn, "A-1", LOAN_DATE, DUE_DATE));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   ", "\t"})
    void constructor_whenMemberIdIsBlank_throwsIllegalArgumentException(String memberId) {
        assertThrows(IllegalArgumentException.class, () -> new Loan("111", memberId, LOAN_DATE, DUE_DATE));
    }

    @Test
    void constructor_whenLoanDateIsNull_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Loan("111", "A-1", null, DUE_DATE));
    }

    @Test
    void constructor_whenDueDateIsNull_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Loan("111", "A-1", LOAN_DATE, null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2000-01-10", "2000-01-09"})
    void constructor_whenDueDateIsNotAfterLoanDate_throwsIllegalArgumentException(LocalDate dueDate) {
        assertThrows(IllegalArgumentException.class, () -> new Loan("111", "A-1", LOAN_DATE, dueDate));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2000-01-23", "2000-01-24"})
    void isOverdue_whenDateIsBeforeOrSameAsDueDate_returnsFalse(LocalDate date) {
        Loan loan = new Loan("111", "A-1", LOAN_DATE, DUE_DATE);

        assertFalse(loan.isOverdue(date));
    }

    @Test
    void isOverdue_whenDateIsAfterDueDate_returnsTrue() {
        Loan loan = new Loan("111", "A-1", LOAN_DATE, DUE_DATE);

        assertTrue(loan.isOverdue(DUE_DATE.plusDays(1)));
    }

    @Test
    void isOverdue_whenReturnDateIsAfterDueDate_returnsFalse() {
        Loan loan = new Loan("111", "A-1", LOAN_DATE, DUE_DATE);
        loan.makeReturn(LocalDate.now());

        assertFalse(loan.isOverdue(DUE_DATE.plusDays(2)));
    }

    @Test
    void markReturn_whenReturnedBeforeDueDate_setsReturnDateAndDeactivatesLoan() {
        LocalDate returnDate = LocalDate.of(2000, 1, 20);
        Loan loan = new Loan("111", "A-1", LOAN_DATE, DUE_DATE);

        loan.makeReturn(returnDate);

        assertEquals(returnDate, loan.getReturnDate());
        assertFalse(loan.isActive());
    }

    @Test
    void markReturn_whenReturnedOnLoanDate_marksLoanAsReturned() {
        Loan loan = new Loan("111", "A-1", LOAN_DATE, DUE_DATE);
        loan.makeReturn(LocalDate.of(2000, 1, 10));

        assertFalse(loan.isActive());
        assertEquals(LOAN_DATE, loan.getReturnDate());
    }

    @Test
    void markReturn_whenAlreadyReturned_throwsIllegalStateException() {
        Loan loan = new Loan("111", "A-1", LOAN_DATE, DUE_DATE);
        loan.makeReturn(LOAN_DATE.plusDays(1));

        assertThrows(IllegalStateException.class, () -> loan.makeReturn(DUE_DATE));
    }

    @Test
    void markReturn_whenDateIsNull_throwsIllegalArgumentException() {
        Loan loan = new Loan("111", "A-1", LOAN_DATE, DUE_DATE);

        assertThrows(IllegalArgumentException.class, () -> loan.makeReturn(null));
    }

    @Test
    void markReturn_whenDateIsBeforeLoanDate_throwsIllegalArgumentException() {
        Loan loan = new Loan("111", "A-1", LOAN_DATE, DUE_DATE);

        assertThrows(IllegalArgumentException.class, () -> loan.makeReturn(LOAN_DATE.minusDays(1)));
    }

    @Test
    void overdueDays_whenActiveAndBeforeDueDate_returnsZero() {
        Loan loan = new Loan("111", "A-1", LOAN_DATE, DUE_DATE);

        int days = loan.overdueDays(DUE_DATE.minusDays(1));

        assertEquals(0, days);
    }

    @Test
    void overdueDays_whenActiveAndOnDueDate_returnsZero() {
        Loan loan = new Loan("111", "A-1", LOAN_DATE, DUE_DATE);

        int days = loan.overdueDays(DUE_DATE);

        assertEquals(0, days);
    }

    @Test
    void overdueDays_whenActiveOneDayAfterDueDate_returnsOne() {
        Loan loan = new Loan("111", "A-1", LOAN_DATE, DUE_DATE);

        int days = loan.overdueDays(DUE_DATE.plusDays(1));

        assertEquals(1, days);
    }

    @Test
    void overdueDays_whenActiveSeveralDaysAfterDueDate_returnsThatNumberOfDays() {
        Loan loan = new Loan("111", "A-1", LOAN_DATE, DUE_DATE);

        int days = loan.overdueDays(DUE_DATE.plusDays(100));

        assertEquals(100, days);
    }

    @Test
    void overdueDays_whenReturnedBeforeDueDate_returnsZero() {
        Loan loan = new Loan("111", "A-1", LOAN_DATE, DUE_DATE);
        loan.makeReturn(DUE_DATE.minusDays(1));

        int days = loan.overdueDays(DUE_DATE.plusDays(30));

        assertEquals(0, days);
    }

    @Test
    void overdueDays_whenReturnedOnDueDate_returnsZero() {
        Loan loan = new Loan("111", "A-1", LOAN_DATE, DUE_DATE);
        loan.makeReturn(DUE_DATE);

        int days = loan.overdueDays(DUE_DATE.plusDays(30));

        assertEquals(0, days);
    }

    @Test
    void overdueDays_whenReturnedAfterDueDate_returnsDaysUpToReturnDate() {
        Loan loan = new Loan("111", "A-1", LOAN_DATE, DUE_DATE);
        loan.makeReturn(DUE_DATE.plusDays(3));

        int days = loan.overdueDays(DUE_DATE.plusDays(30));

        assertEquals(3, days);
    }

    @Test
    void overdueDays_whenReturned_doesNotDependOnGivenDate() {
        Loan loan = new Loan("111", "A-1", LOAN_DATE, DUE_DATE);
        loan.makeReturn(DUE_DATE.plusDays(5));

        int days = loan.overdueDays(DUE_DATE.plusDays(15));

        assertEquals(5, days);
    }

    @Test
    void overdueDays_whenDateIsNull_throwsIllegalArgumentException() {
        Loan loan = new Loan("111", "A-1", LOAN_DATE, DUE_DATE);

        assertThrows(IllegalArgumentException.class, () -> loan.overdueDays(null));
    }

    @ParameterizedTest
    @ValueSource(ints = {-5, -1, 0, 1, 2, 15})
    void overdueDays_whenActive_isPositiveExactlyWhenLoanIsOverdue(int daysAfterDueDate) {
        Loan loan = new Loan("111", "A-1", LOAN_DATE, DUE_DATE);
        LocalDate today = DUE_DATE.plusDays(daysAfterDueDate);

        assertEquals(loan.isOverdue(today), loan.overdueDays(today) > 0);
    }
}