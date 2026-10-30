package com.library;

import com.library.model.Book;
import com.library.model.Loan;
import com.library.model.Member;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting library");
        LocalDate today = LocalDate.of(2026, 10, 30);

        Loan overdueLoan = new Loan(
                "1242345", "123",
                LocalDate.of(2026, 10, 10), LocalDate.of(2026, 10 ,24));
        System.out.println("Loan 1 - Active, past due:  " + overdueLoan.isOverdue(today));

        Loan futureLoan = new Loan(
                "1242345", "123",
                LocalDate.of(2026, 10, 25), LocalDate.of(2026, 11 ,8));
        System.out.println("Loan 2 - Active, not due yet: " + futureLoan.isOverdue(today));

        overdueLoan.makeReturnDate(LocalDate.of(2026, 10, 28));
        System.out.println("Loan 3 - Returned:  " + overdueLoan.isOverdue(today));

    }
}