package com.library.ui;

import com.library.model.Book;
import com.library.model.Loan;
import com.library.model.Member;
import com.library.repository.BookRepository;
import com.library.repository.LoanRepository;
import com.library.repository.MemberRepository;

import java.time.LocalDate;

public class DemoData {

    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final LoanRepository loanRepository;

    public DemoData(BookRepository bookRepository,
                    MemberRepository memberRepository,
                    LoanRepository loanRepository) {
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.loanRepository = loanRepository;
    }

    public void load() {
        loadBooks();
        loadMembers();
        loadLoans();
    }

    private void loadBooks() {
        bookRepository.save(new Book("111", "Clean Code", "Robert C. Martin", 2008));
        bookRepository.save(new Book("222", "Effective Java", "Joshua Bloch", 2018));
        bookRepository.save(new Book("333", "Refactoring", "Martin Fowler", 2018));
        bookRepository.save(new Book("444", "Head First Java", "Kathy Sierra", 2005));
        bookRepository.save(new Book("555", "The Pragmatic Programmer", "Andrew Hunt", 1999));
        bookRepository.save(new Book("666", "Design Patterns", "Erich Gamma", 1994));
    }

    private void loadMembers() {
        memberRepository.save(new Member("1", "Anna", "Nowak", "anna.nowak@example.com"));
        memberRepository.save(new Member("2", "Jan", "Kowalski", "jan.kowalski@example.com"));
        memberRepository.save(new Member("3", "Ewa", "Wisniewska", "ewa.wisniewska@example.com"));
    }

    private void loadLoans() {
        LocalDate today = LocalDate.now();

        // Active and overdue: due date was 16 days ago
        loanRepository.save(new Loan("111", "1", today.minusDays(30), today.minusDays(16)));

        // Active, not overdue yet: due in 11 days
        loanRepository.save(new Loan("222", "1", today.minusDays(3), today.plusDays(11)));

        // Returned after the due date: must NOT show up as overdue
        Loan returnedLate = new Loan("333", "2", today.minusDays(40), today.minusDays(26));
        returnedLate.makeReturn(today.minusDays(20));
        loanRepository.save(returnedLate);
    }
}