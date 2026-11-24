package com.library.repository.inMemory;

import com.library.model.Loan;
import com.library.repository.LoanRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


public class InMemoryLoanRepositoryTest {

    private static final LocalDate LOAN_DATE = LocalDate.of(2000, 1, 10);
    private static final LocalDate DUE_DATE = LocalDate.of(2000, 1, 24);
    private static final LocalDate RETURN_DATE = LocalDate.of(2000, 1, 22);

    private final LoanRepository loanRepository = new InMemoryLoanRepository();
    private final Loan loan1 = new Loan("111", "A-1", LOAN_DATE, DUE_DATE);
    private final Loan loan2 = new Loan("222", "A-2", LOAN_DATE, DUE_DATE);

    private Loan returnedLoan(String isbn, String memberId) {
        Loan loan = new Loan(isbn, memberId, LOAN_DATE, DUE_DATE);
        loan.makeReturn(RETURN_DATE);
        return loan;
    }

    @Test
    void save_whenSaveLoanCorrectly_findAllActiveShowsThisLoan() {
        loanRepository.save(loan1);

        assertEquals(List.of(loan1), loanRepository.findAllActive());
    }

    @Test
    void findAllActive_whenNoLoans_returnsEmptyList() {
        assertEquals(List.of(), loanRepository.findAllActive());
    }

    @Test
    void findAllActive_whenReturnedListIsModified_throwsUnsupportedOperationException() {
        loanRepository.save(loan1);
        List<Loan> loans = loanRepository.findAllActive();

        assertThrows(UnsupportedOperationException.class, () -> loans.add(loan2));
        assertEquals(List.of(loan1), loanRepository.findAllActive());
    }

    @Test
    void findAllActive_whenSomeLoansReturned_returnsOnlyActiveOnes() {
        loanRepository.save(loan1);
        loanRepository.save(returnedLoan("222", "A-2"));

        assertEquals(List.of(loan1), loanRepository.findAllActive());
    }

    @Test
    void findActiveByIsbn_whenActiveLoanExists_returnsIt() {
        loanRepository.save(loan1);

        assertEquals(Optional.of(loan1), loanRepository.findActiveByIsbn("111"));
    }

    @Test
    void findActiveByIsbn_whenLoanIsReturned_returnsEmptyOptional() {
        loanRepository.save(returnedLoan("111", "A-1"));

        assertEquals(Optional.empty(), loanRepository.findActiveByIsbn("111"));
    }

    @Test
    void findActiveByIsbn_whenIsbnIsUnknown_returnsEmptyOptional() {
        loanRepository.save(loan2);

        assertEquals(Optional.empty(), loanRepository.findActiveByIsbn("111"));
    }

    @Test
    void findActiveByMemberId_whenMemberHasActiveAndReturnedLoans_returnsOnlyActive() {
        loanRepository.save(loan1);
        loanRepository.save(returnedLoan("333", "A-1"));
        loanRepository.save(loan2);

        assertEquals(List.of(loan1), loanRepository.findActiveByMemberId("A-1"));
    }

    @Test
    void findAllByMemberId_whenMemberHasActiveAndReturnedLoans_returnsAllHisLoans() {
        loanRepository.save(loan1);
        Loan returned = returnedLoan("333", "A-1");
        loanRepository.save(returned);
        loanRepository.save(loan2);

        assertEquals(List.of(loan1, returned), loanRepository.findAllByMemberId("A-1"));
    }

    @Test
    void findAllByMemberId_whenMemberHasNoLoans_returnsEmptyList() {
        assertEquals(List.of(), loanRepository.findAllByMemberId("A-1"));
    }

    @Test
    void update_whenLoanReturned_keepsSingleRecordAndMarksItInactive() {
        Loan loan = new Loan("111", "A-1", LOAN_DATE, DUE_DATE);
        loanRepository.save(loan);

        loan.makeReturn(RETURN_DATE);
        loanRepository.update(loan);

        assertEquals(1, loanRepository.findAllByMemberId("A-1").size());
        assertEquals(Optional.empty(), loanRepository.findActiveByIsbn("111"));
    }
}