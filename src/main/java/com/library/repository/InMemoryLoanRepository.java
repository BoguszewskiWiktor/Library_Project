package com.library.repository;

import com.library.model.Loan;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryLoanRepository implements LoanRepository {

    private final List<Loan> loans = new ArrayList<>();

    @Override
    public void save(Loan loan) {
        loans.add(loan);
    }

    @Override
    public void update(Loan loan) {
        // Nothing to do: the stored Loan is the same object the service modified.
    }

    @Override
    public Optional<Loan> findActiveByIsbn(String isbn) {

        for (Loan loan : loans) {
            if (loan.isActive() && loan.getIsbn().equals(isbn)) {
                return Optional.of(loan);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Loan> findActiveByMemberId(String memberId) {
        List<Loan> memberLoans = new ArrayList<>();

        for (Loan loan : loans) {
            if (loan.isActive() && loan.getMemberId().equals(memberId)) {
                memberLoans.add(loan);
            }
        }
        return memberLoans;
    }

    @Override
    public List<Loan> findAllByMemberId(String memberId) {
        List<Loan> memberLoans = new ArrayList<>();

        for (Loan loan : loans) {
            if (loan.getMemberId().equals(memberId)) {
                memberLoans.add(loan);
            }
        }

        return memberLoans;
    }

    @Override
    public List<Loan> findAllActive() {
        List<Loan> activeLoans = new ArrayList<>();

        for (Loan loan : loans) {
            if (loan.isActive()) {
                activeLoans.add(loan);
            }
        }
        return activeLoans;
    }
}