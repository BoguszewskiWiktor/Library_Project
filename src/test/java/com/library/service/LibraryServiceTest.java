package com.library.service;

import com.library.exception.*;
import com.library.model.Book;
import com.library.model.Loan;
import com.library.model.Member;
import com.library.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class LibraryServiceTest {

    private BookRepository bookRepository;
    private MemberRepository memberRepository;
    private LoanRepository loanRepository;
    private LibraryService libraryService;

    private static final LocalDate LOAN_DATE = LocalDate.of(2000, 1, 10);
    private static final LocalDate DUE_DATE = LocalDate.of(2000, 1, 24);
    private static final LocalDate RETURN_DATE = LocalDate.of(2000, 1, 22);


    private final Book book =
            new Book("111", "Harry Potter", "J.K. Rowling", 1999);
    private final Book book2 =
            new Book("222", "Harry Potter 2", "J.K. Rowling", 2001);
    private final Book book3 =
            new Book("333", "Lalka", "Bolesław Prus", 1975);
    private final Book book4 =
            new Book("444", "Lalka 2", "Bolesław Prus", 1976);
    private final Book book5 =
            new Book("555", "Lalka 3", "Bolesław Prus", 1977);
    private final Book book6 =
            new Book("666", "Lalka 4", "Bolesław Prus", 1978);
    private final Member member =
            new Member("A-1", "Jan", "Kowalski", "kowalski@gmail.com");
    private final Member member2 =
            new Member("A-2", "Adam", "Nowak", "nowak@gmail.com");

    private Loan returnedLoan(String isbn, String memberId) {
        Loan loan = new Loan(isbn, memberId, LOAN_DATE, DUE_DATE);
        loan.makeReturn(RETURN_DATE);
        return loan;
    }

    @BeforeEach
    public void setUp() {
        bookRepository = new InMemoryBookRepository();
        memberRepository = new InMemoryMemberRepository();
        loanRepository = new InMemoryLoanRepository();
        libraryService = new LibraryService(bookRepository, loanRepository, memberRepository);
    }

    @Test
    void addBook_whenBookIsNew_savesBook() {
        libraryService.addBook(book);

        assertEquals(Optional.of(book), bookRepository.findByIsbn("111"));
    }

    @Test
    void addBook_whenIsbnAlreadyExists_throwsBookAlreadyExistsException() {
        bookRepository.save(book);

        assertThrows(BookAlreadyExistsException.class, () -> libraryService.addBook(book));
    }

    @Test
    void addBook_whenBookIsNull_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> libraryService.addBook(null));
    }

    @Test
    void registerMember_whenMemberIsNew_savesMember() {
        libraryService.registerMember(member);

        assertEquals(Optional.of(member), memberRepository.findById("A-1"));
    }

    @Test
    void registerMember_whenIdAlreadyExists_throwsMemberAlreadyExistsException() {
        memberRepository.save(member);

        assertThrows(MemberAlreadyExistsException.class, () -> libraryService.registerMember(member));
    }

    @Test
    void registerMember_whenMemberIsNull_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> libraryService.registerMember(null));
    }

    @Test
    void findBook_whenBookExists_returnsBook() {
        bookRepository.save(book);

        assertEquals(book, libraryService.findBook("111"));
    }

    @Test
    void findBook_whenBookNotExists_throwsBookNotFoundException() {
        assertThrows(BookNotFoundException.class,  () -> libraryService.findBook("111"));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   ", "\t"})
    void searchBooks_whenPhraseIsBlank_throwsIllegalArgumentException(String phrase) {
        assertThrows(IllegalArgumentException.class, () -> libraryService.searchBooks(phrase));
    }

    @Test
    void searchBooks_whenPhraseHasDifferentCase_findsBook() {
        bookRepository.save(book);

        assertEquals(List.of(book), libraryService.searchBooks("HaRrY"));
    }

    @Test
    void searchBooks_whenPhraseHasSurroundingSpaces_findsBook() {
        bookRepository.save(book);

        assertEquals(List.of(book), libraryService.searchBooks(" Harry "));
    }

    @Test
    void searchBooks_whenPhraseMatchesAuthor_returnsBook() {
        bookRepository.save(book);

        assertEquals(List.of(book), libraryService.searchBooks("J.K"));
    }

    @Test
    void searchBooks_whenNoBookMatches_returnsEmptyList() {
        bookRepository.save(book);

        assertEquals(List.of(), libraryService.searchBooks("Anna"));
    }

    @Test
    void searchBooks_whenSeveralBooksMatch_returnsOnlyMatchingBooks() {
        bookRepository.save(book);
        bookRepository.save(book2);
        bookRepository.save(book3);

        assertEquals(List.of(book, book2),  libraryService.searchBooks("Harry"));
    }

    @Test
    void isBookAvailable_whenBookIsAvailable_returnsTrue() {
        bookRepository.save(book);

        assertTrue(libraryService.isBookAvailable("111"));
    }

    @Test
    void isBookAvailable_whenBookIsNotAvailable_returnsFalse() {
        bookRepository.save(book);
        Loan loan = new Loan("111", "A-1", LOAN_DATE, DUE_DATE);
        loanRepository.save(loan);

        assertFalse(libraryService.isBookAvailable("111"));
    }

    @Test
    void isBookAvailable_whenIsbnIsUnknown_throwsBookNotFoundException() {
        assertThrows(BookNotFoundException.class, () -> libraryService.isBookAvailable("111"));
    }

    @Test
    void borrowBook_whenValidData_returnsActiveLoanWithDueDateAfter14Days() {
        bookRepository.save(book);
        memberRepository.save(member);

        Loan loan = libraryService.borrowBook("111", "A-1");

        assertEquals("111", loan.getIsbn());
        assertEquals("A-1", loan.getMemberId());
        assertEquals(LocalDate.now(), loan.getLoanDate());
        assertEquals(LocalDate.now().plusDays(14), loan.getDueDate());
        assertEquals(1, libraryService.getActiveLoansForMember("A-1").size());
    }

    @Test
    void borrowBook_whenBookIsUnknown_throwsBookNotFoundException() {
        bookRepository.save(book);

        assertThrows(BookNotFoundException.class, () -> libraryService.borrowBook("222", "A-1"));
    }

    @Test
    void borrowBook_whenMemberIsUnknown_throwsMemberNotFoundException() {
        bookRepository.save(book);

        assertThrows(MemberNotFoundException.class, () -> libraryService.borrowBook("111", "A-2"));
    }

    @Test
    void borrowBook_whenBookIsNotAvailable_throwsBookNotAvailableException() {
        bookRepository.save(book);
        memberRepository.save(member);
        loanRepository.save(new Loan("111", "A-1", LOAN_DATE, DUE_DATE));

        assertThrows(BookNotAvailableException.class, () -> libraryService.borrowBook("111", "A-1"));
    }

    @Test
    void borrowBook_whenMemberHasReachedLoanLimit_throwsLoanLimitExceededException() {
        bookRepository.save(book);
        bookRepository.save(book2);
        bookRepository.save(book3);
        bookRepository.save(book4);
        bookRepository.save(book5);
        bookRepository.save(book6);
        memberRepository.save(member);

        loanRepository.save(new Loan("111", "A-1", LOAN_DATE, DUE_DATE));
        loanRepository.save(new Loan("222", "A-1", LOAN_DATE, DUE_DATE));
        loanRepository.save(new Loan("333", "A-1", LOAN_DATE, DUE_DATE));
        loanRepository.save(new Loan("444", "A-1", LOAN_DATE, DUE_DATE));
        loanRepository.save(new Loan("555", "A-1", LOAN_DATE, DUE_DATE));

        assertThrows(LoanLimitExceededException.class, () -> libraryService.borrowBook("666", "A-1"));
    }

    @Test
    void borrowBook_whenMemberHasFourActiveLoans_allowsFifthLoan() {
        bookRepository.save(book);
        bookRepository.save(book2);
        bookRepository.save(book3);
        bookRepository.save(book4);
        bookRepository.save(book5);
        memberRepository.save(member);

        loanRepository.save(new Loan("111", "A-1", LOAN_DATE, DUE_DATE));
        loanRepository.save(new Loan("222", "A-1", LOAN_DATE, DUE_DATE));
        loanRepository.save(new Loan("333", "A-1", LOAN_DATE, DUE_DATE));
        loanRepository.save(new Loan("444", "A-1", LOAN_DATE, DUE_DATE));

        Loan loan = libraryService.borrowBook("555", "A-1");

        assertEquals("555", loan.getIsbn());
        assertEquals("A-1", loan.getMemberId());
        assertEquals(LocalDate.now(), loan.getLoanDate());
        assertEquals(LocalDate.now().plusDays(14), loan.getDueDate());
        assertEquals(5, libraryService.getActiveLoansForMember("A-1").size());
    }

    @Test
    void borrowBook_whenMemberHasOnlyReturnedLoans_doesNotCountThemToLimit() {
        bookRepository.save(book);
        bookRepository.save(book2);
        bookRepository.save(book3);
        bookRepository.save(book4);
        bookRepository.save(book5);
        bookRepository.save(book6);
        memberRepository.save(member2);

        loanRepository.save(returnedLoan("111",  "A-2"));
        loanRepository.save(returnedLoan("222",  "A-2"));
        loanRepository.save(returnedLoan("333",  "A-2"));
        loanRepository.save(returnedLoan("444",  "A-2"));
        loanRepository.save(returnedLoan("555",  "A-2"));

        Loan loan = libraryService.borrowBook("666", "A-2");

        assertEquals("666", loan.getIsbn());
        assertEquals("A-2", loan.getMemberId());
        assertEquals(LocalDate.now(), loan.getLoanDate());
        assertEquals(LocalDate.now().plusDays(14), loan.getDueDate());
        assertEquals(1, libraryService.getActiveLoansForMember("A-2").size());
    }

    @Test
    void borrowBook_whenBookWasPreviouslyReturned_returnsNewActiveLoan() {
        bookRepository.save(book);
        memberRepository.save(member);

        loanRepository.save(returnedLoan("111",  "A-1"));

        Loan loan = libraryService.borrowBook("111", "A-1");

        assertEquals("111", loan.getIsbn());
        assertEquals("A-1", loan.getMemberId());
        assertEquals(LocalDate.now(), loan.getLoanDate());
        assertEquals(LocalDate.now().plusDays(14), loan.getDueDate());
        assertEquals(1, libraryService.getActiveLoansForMember("A-1").size());
        assertEquals(2, libraryService.getLoanHistoryForMember("A-1").size());
    }

    @Test
    void returnBook_whenBookIsUnknown_throwsBookNotFoundException() {
        bookRepository.save(book);
        memberRepository.save(member);
        loanRepository.save(new Loan("111", "A-1", LOAN_DATE, DUE_DATE));

        assertThrows(BookNotFoundException.class, () -> libraryService.returnBook("222"));
    }

    @Test
    void returnBook_whenBookIsNotBorrowed_throwsLoanNotFoundException() {
        bookRepository.save(book);
        bookRepository.save(book2);
        memberRepository.save(member);
        loanRepository.save(new Loan("111", "A-1", LOAN_DATE, DUE_DATE));

        assertThrows(LoanNotFoundException.class, () -> libraryService.returnBook("222"));
    }

    @Test
    void returnBook_whenBookIsBorrowed_setsReturnDateAndKeepsSingleLoan() {
        bookRepository.save(book);
        memberRepository.save(member);
        loanRepository.save(new Loan("111", "A-1", LOAN_DATE, DUE_DATE));

        Loan returned = libraryService.returnBook("111");

        assertEquals(LocalDate.now(), returned.getReturnDate());
        assertEquals(1, libraryService.getLoanHistoryForMember("A-1").size());
        assertTrue(libraryService.isBookAvailable("111"));
    }

    @Test
    void returnBook_whenBookIsReturned_throwsLoanNotFoundException() {
        bookRepository.save(book);
        memberRepository.save(member);

        loanRepository.save(returnedLoan("111",  "A-1"));

        assertThrows(LoanNotFoundException.class, () -> libraryService.returnBook("111"));
    }

    @Test
    void getActiveLoansForMember_whenMemberHasActiveAndReturnedLoans_returnsOnlyActive() {
        bookRepository.save(book);
        bookRepository.save(book2);
        bookRepository.save(book3);
        memberRepository.save(member);

        Loan loan1 = new Loan("111", "A-1", LOAN_DATE, DUE_DATE);
        Loan loan2 = new Loan("333", "A-1", LOAN_DATE, DUE_DATE);
        loanRepository.save(loan1);
        loanRepository.save(loan2);
        loanRepository.save(returnedLoan("222",  "A-1"));

        List<Loan> activeLoansForMember = libraryService.getActiveLoansForMember("A-1");

        assertEquals(List.of(loan1, loan2), activeLoansForMember);
    }

    @Test
    void getActiveLoansForMember_whenMemberHasNoLoans_returnsEmptyList() {
        memberRepository.save(member);

        assertEquals(List.of(), libraryService.getActiveLoansForMember("A-1"));
    }

    @Test
    void getActiveLoansForMember_whenMemberUnknown_throwsMemberNotFoundException() {
        assertThrows(MemberNotFoundException.class, () -> libraryService.getActiveLoansForMember("111"));
    }

    @Test
    void getLoanHistoryForMember_whenMemberHasActiveAndReturnedLoans_returnsAllHisLoans() {
        bookRepository.save(book);
        bookRepository.save(book2);
        memberRepository.save(member);

        Loan loan1 = new Loan("111", "A-1", LOAN_DATE, DUE_DATE);
        Loan loan2 = new Loan("333", "A-1", LOAN_DATE, DUE_DATE);
        loanRepository.save(loan1);
        loanRepository.save(loan2);

        Loan returnedLoan = new Loan("111", "A-1", LOAN_DATE, DUE_DATE);
        returnedLoan.makeReturn(DUE_DATE.plusDays(10));
        loanRepository.save(returnedLoan);

        List<Loan> loanHistoryForMember = libraryService.getLoanHistoryForMember("A-1");

        assertEquals(List.of(loan1, loan2, returnedLoan), loanHistoryForMember);
    }

    @Test
    void getLoanHistoryForMember_whenMemberHasNoLoans_returnsEmptyList() {
        memberRepository.save(member);

        assertEquals(List.of(), libraryService.getLoanHistoryForMember("A-1"));
    }

    @Test
    void getLoanHistoryForMember_whenMemberUnknown_throwsMemberNotFoundException() {
        assertThrows(MemberNotFoundException.class, () -> libraryService.getLoanHistoryForMember("111"));
    }

    @Test
    void getOverdueLoans_whenActiveLoanIsPastDueDate_returnsAllOfThem() {
        bookRepository.save(book);
        bookRepository.save(book2);
        memberRepository.save(member);

        Loan loan1 = new Loan("111", "A-1", LOAN_DATE, DUE_DATE);
        Loan loan2 = new Loan("222", "A-1", LOAN_DATE, DUE_DATE);

        loanRepository.save(loan1);
        loanRepository.save(loan2);

        assertEquals(List.of(loan1, loan2), libraryService.getOverdueLoans());
    }

    @Test
    void getOverdueLoans_whenLoanIsNotYetDue_returnsEmptyList() {
        bookRepository.save(book);
        memberRepository.save(member);

        loanRepository.save(new Loan(
                "111",
                "A-1",
                LocalDate.of(2999, 1, 1),
                LocalDate.of(2999, 1, 15)
        ));

        assertEquals(List.of(), libraryService.getOverdueLoans());
    }

    @Test
    void getOverdueLoans_whenOverdueLoanWasReturned_isNotIncluded() {
        bookRepository.save(book);
        memberRepository.save(member);

        Loan loan = new Loan("111", "A-1", LOAN_DATE, DUE_DATE);
        loan.makeReturn(DUE_DATE.plusDays(10));
        loanRepository.save(loan);

        assertEquals(0, libraryService.getOverdueLoans().size());
    }

    @Test
    void getOverdueLoans_whenNoLoans_returnsEmptyList() {
        assertEquals(0, libraryService.getOverdueLoans().size());
    }

    @Test
    void getAllBooks_whenNoBooks_returnsEmptyList() {
        assertEquals(List.of(), libraryService.getAllBooks());
    }

    @Test
    void getAllBooks_whenBooksSaved_returnsAllOfThem() {
        bookRepository.save(book);
        bookRepository.save(book2);
        bookRepository.save(book3);
        bookRepository.save(book4);
        bookRepository.save(book5);
        bookRepository.save(book6);

        assertEquals(List.of(book, book2, book3, book4, book5, book6), libraryService.getAllBooks());
    }

    @Test
    void getAllMembers_whenNoMembers_returnsEmptyList() {
        assertEquals(List.of(), libraryService.getAllMembers());
    }

    @Test
    void getAllMembers_whenMembersSaved_returnsAllOfThem() {
        memberRepository.save(member);
        memberRepository.save(member2);

        assertEquals(List.of(member, member2), libraryService.getAllMembers());
    }
}