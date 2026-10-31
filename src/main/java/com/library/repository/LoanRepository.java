package com.library.repository;

import com.library.model.Loan;

import java.util.List;
import java.util.Optional;

/**
 * Storage for loans. Contains no business rules.
 * A loan is active when it has not been returned yet.
 */
public interface LoanRepository {

    /** Stores a new loan. */
    void save(Loan loan);

    /** Stores changes made to an existing loan (e.g. after the book is returned). */
    void update(Loan loan);

    /** Returns the active loan for the given book, or an empty Optional if the book is not borrowed. */
    Optional<Loan> findActiveByIsbn(String isbn);

    /** Returns the member's active loans. Returns an empty list if there are none. */
    List<Loan> findActiveByMemberId(String memberId);

    /** Returns the member's whole loan history, active and returned. Returns an empty list if there are none. */
    List<Loan> findAllByMemberId(String memberId);

    /** Returns all active loans. Returns an empty list if there are none. */
    List<Loan> findAllActive();

}