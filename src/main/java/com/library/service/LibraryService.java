package com.library.service;

import com.library.exception.*;
import com.library.model.Book;
import com.library.model.Loan;
import com.library.model.Member;
import com.library.repository.BookRepository;
import com.library.repository.LoanRepository;
import com.library.repository.MemberRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LibraryService {

    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final MemberRepository memberRepository;
    private final int MAX_ACTIVE_LOANS_PER_MEMBER = 5;
    private final int LOAN_PERIOD_DAYS = 14;


    public LibraryService
            (
                    BookRepository bookRepository,
                    LoanRepository loanRepository,
                    MemberRepository memberRepository
            ) {
        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
        this.memberRepository = memberRepository;
    }

    public void addBook(Book book) {
        Objects.requireNonNull(book, "Book must not be null");

        if (bookRepository.existsByIsbn(book.isbn())) {
            throw new BookAlreadyExistsException(book.isbn());
        }

        bookRepository.save(book);
    }

    public void registerMember(Member member) {
        Objects.requireNonNull(member, "Member must not be null");

        if (memberRepository.existsById(member.id())) {
            throw new MemberAlreadyExistsException(member.id());
        }

        memberRepository.save(member);
    }

    public Book findBook(String isbn) {
        return bookRepository.findByIsbn(isbn).orElseThrow(() -> new BookNotFoundException(isbn));
    }

    public void getAllBooks() {
        List<Book> books = bookRepository.findAll();
    }

    public void getAllMembers() {
        List<Member> members = memberRepository.findAll();
    }

    public List<Book> searchBooks(String phrase) {
        if (phrase == null || phrase.isBlank()) {
            throw new IllegalArgumentException("Phrase must not be null or blank");
        }

        String normalized = phrase.strip().toLowerCase();
        List<Book> result = new ArrayList<>();

            for (Book book : bookRepository.findAll()) {
                if (
                        book.title().toLowerCase().contains(normalized)
                                || book.author().toLowerCase().contains(normalized)
                ) {
                    result.add(book);
                }
            }

        return result;
    }

    public boolean isBookAvailable(String isbn) {
        findBook(isbn);
        return loanRepository.findActiveByIsbn(isbn).isEmpty();
    }

    public Loan borrowBook(String isbn, String memberId) {

        findBook(isbn);                 // throw BookNotFoundException
        getMemberOrThrow(memberId);     // throw MemberNotFoundException

        if (loanRepository.findActiveByIsbn(isbn).isPresent()) {
            throw new BookNotAvailableException(isbn);
        }

        if (loanRepository.findActiveByMemberId(memberId).size() >= MAX_ACTIVE_LOANS_PER_MEMBER) {
            throw new LoanLimitExceededException(memberId);
        }

        LocalDate today = LocalDate.now();
        Loan loan = new Loan(isbn, memberId, today, today.plusDays(LOAN_PERIOD_DAYS));
        loanRepository.save(loan);
        return loan;
    }

    public Loan returnBook(String isbn) {

        findBook(isbn);

        Loan loan = loanRepository.findActiveByIsbn(isbn)
                .orElseThrow(() -> new LoanNotFoundException(isbn));
        loan.makeReturn(LocalDate.now());
        loanRepository.update(loan);
        return loan;

    }

    public List<Loan> getActiveLoansForMember(String memberId) {
        getMemberOrThrow(memberId);
        return loanRepository.findActiveByMemberId(memberId);
    }

    public List<Loan> getLoanHistoryForMember(String memberId) {
        getMemberOrThrow(memberId);
        return loanRepository.findAllByMemberId(memberId);
    }

    public List<Loan> getOverdueLoans() {
        List<Loan> overdueLoans = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (Loan loan : loanRepository.findAllActive()) {
            if (loan.isOverdue(today)) {
                overdueLoans.add(loan);
            }
        }
        return overdueLoans;
    }

    private Member getMemberOrThrow(String memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
    }
}