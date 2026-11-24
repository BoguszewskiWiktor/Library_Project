package com.library.repository.jdbc;

import com.library.exception.LoanNotFoundException;
import com.library.exception.StorageException;
import com.library.model.Book;
import com.library.model.Loan;
import com.library.model.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class JdbcLoanRepositoryIT {

    private final TestDatabase testDatabase = new TestDatabase();
    private JdbcLoanRepository loanRepository;
    private static final LocalDate LOAN_DATE = LocalDate.of(2000, 1, 10);
    private static final LocalDate DUE_DATE = LocalDate.of(2000, 1, 24);
    private static final LocalDate RETURN_DATE = LocalDate.of(2000, 1, 22);

    private final Loan loan1 = new Loan("111", "A-1", LOAN_DATE, DUE_DATE);
    private final Loan loan2 = new Loan("222", "A-2", LOAN_DATE, DUE_DATE);

    @BeforeEach
    void setUp() {
        testDatabase.cleanDatabase();
        loanRepository = new JdbcLoanRepository(testDatabase.connectionFactory());

        JdbcMemberRepository memberRepository = new JdbcMemberRepository(testDatabase.connectionFactory());
        JdbcBookRepository bookRepository = new JdbcBookRepository(testDatabase.connectionFactory());

        Book book = new Book(
                "111", "Harry Potter", "J.K. Rowling", 1999);
        Book book2 = new Book(
                "222", "Harry Potter 2", "J.K. Rowling", 2000);
        Book polishBook = new Book(
                "333", "Pan Tadeusz", "Adam Mickiewicz Zażółć gęślą jaźń", 1834);

        Member member1 = new Member("A-1", "Jan", "Kowalski", "kowalski@gmail.com");
        Member member2 = new Member("A-2", "Krzysztof", "Żółć", "zolc@gmail.com");

        bookRepository.save(book);
        bookRepository.save(book2);
        bookRepository.save(polishBook);

        memberRepository.save(member1);
        memberRepository.save(member2);
    }

    @Test
    void save_whenLoanIsActive_storesItWithoutReturnDate() {
        loanRepository.save(loan1);

        assertEquals(Optional.of(loan1), loanRepository.findActiveByIsbn(loan1.getIsbn()));
    }

    @Test
    void save_whenLoanIsReturned_storesReturnDate() {
        Loan returnedLoan = returnedLoan("111", "A-1");
        loanRepository.save(returnedLoan);

        assertEquals(List.of(returnedLoan), loanRepository.findAllByMemberId("A-1"));
    }

    @Test
    void save_whenBookDoesNotExist_throwsStorageException() {
        assertThrows(StorageException.class,
                () -> loanRepository.save(new Loan("555", "A-1", LOAN_DATE, DUE_DATE)));
    }

    @Test
    void save_whenMemberDoesNotExist_throwsStorageException() {
        assertThrows(StorageException.class,
                () -> loanRepository.save(new Loan("111", "A-5", LOAN_DATE, DUE_DATE)));
    }

    @Test
    void save_whenBookAlreadyHasActiveLoan_throwsStorageException() {
        loanRepository.save(loan2);
        Loan other = new Loan("222", "A-1",
                LOAN_DATE.plusDays(30), DUE_DATE.plusDays(30));

        assertThrows(StorageException.class, () -> loanRepository.save(other));
        assertEquals(Optional.of(loan2), loanRepository.findActiveByIsbn("222"));
    }

    @Test
    void save_whenBookHasReturnedAndNewLoan_storesBoth() {
        Loan returned = returnedLoan("222", "A-2");
        Loan again = new Loan("222", "A-2",
                LOAN_DATE.plusDays(30), DUE_DATE.plusDays(30));

        loanRepository.save(again);
        loanRepository.save(returned);

        assertEquals(Optional.of(again), loanRepository.findActiveByIsbn("222"));
        assertEquals(List.of(returned, again), loanRepository.findAllByMemberId("A-2"));
    }

    @Test
    void findActiveByIsbn_whenActiveLoanExists_returnsIt() {
        loanRepository.save(loan1);

        assertEquals(Optional.of(loan1), loanRepository.findActiveByIsbn("111"));
    }

    @Test
    void findActiveByIsbn_whenLoanReturned_returnsEmptyOptional() {
        Loan returned = returnedLoan("222", "A-2");
        loanRepository.save(returned);

        assertEquals(Optional.empty(), loanRepository.findActiveByIsbn("222"));
    }

    @Test
    void findActiveByIsbn_whenIsbnUnknown_returnsEmptyOptional() {
        loanRepository.save(loan1);

        assertEquals(Optional.empty(), loanRepository.findActiveByIsbn("222"));
    }

    @Test
    void findActiveByMemberId_whenMemberHasActiveAndReturnedLoans_returnsOnlyActive() {
        loanRepository.save(loan1);
        Loan returned = returnedLoan("222", "A-1");
        loanRepository.save(returned);
        loanRepository.save(loan2);

        assertEquals(List.of(loan1), loanRepository.findActiveByMemberId("A-1"));
    }

    @Test
    void findAllByMemberId_whenMemberHasActiveAndReturnedLoans_returnsAllHisLoans() {
        loanRepository.save(loan1);
        Loan returned = returnedLoan("222", "A-1");
        loanRepository.save(returned);
        loanRepository.save(loan2);

        assertEquals(List.of(loan1, returned), loanRepository.findAllByMemberId("A-1"));
    }

    @Test
    void findAllByMemberId_whenMemberHasNoLoans_returnsEmptyList() {
        assertEquals(List.of(), loanRepository.findAllByMemberId("A-1"));
    }

    @Test
    void findAllActive_whenSomeLoansReturned_returnsOnlyActiveOnes() {
        loanRepository.save(loan2);

        Loan returned = returnedLoan("111", "A-1");
        loanRepository.save(returned);

        assertEquals(List.of(loan2), loanRepository.findAllActive());
    }

    @Test
    void findAllActive_whenNoLoans_returnsEmptyList() {
        assertEquals(List.of(), loanRepository.findAllActive());
    }

    @Test
    void findAllActive_whenReturnedListIsModified_throwsUnsupportedOperationException() {
        loanRepository.save(loan1);

        assertThrows(UnsupportedOperationException.class, () -> loanRepository.findAllActive().add(loan2));
        assertEquals(List.of(loan1), loanRepository.findAllActive());
    }

    @Test
    void update_whenLoanReturned_keepsSingleRowAndSetsReturnDate() {
        loanRepository.save(loan1);
        loan1.makeReturn(RETURN_DATE);
        loanRepository.update(loan1);

        assertEquals(Optional.empty(), loanRepository.findActiveByIsbn("111"));
        assertEquals(List.of(loan1), loanRepository.findAllByMemberId("A-1"));
    }

    @Test
    void update_whenNoActiveLoan_throwsLoanNotFoundException() {
        loanRepository.save(loan1);
        loan1.makeReturn(RETURN_DATE);
        loanRepository.update(loan1);

        assertThrows(LoanNotFoundException.class, () -> loanRepository.update(loan1));
    }

    private Loan returnedLoan(String isbn, String memberId) {
        Loan loan = new Loan(isbn, memberId, LOAN_DATE, DUE_DATE);
        loan.makeReturn(RETURN_DATE);
        return loan;
    }
}