package com.library;

import com.library.config.ConnectionFactory;
import com.library.config.DatabaseConfig;
import com.library.repository.BookRepository;
import com.library.repository.LoanRepository;
import com.library.repository.MemberRepository;
import com.library.repository.inMemory.InMemoryLoanRepository;
import com.library.repository.inMemory.InMemoryMemberRepository;
import com.library.repository.jdbc.JdbcBookRepository;
import com.library.repository.jdbc.JdbcMemberRepository;
import com.library.service.LibraryService;
import com.library.ui.ConsoleInput;
import com.library.ui.ConsoleUI;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        DatabaseConfig config = DatabaseConfig.load();
        ConnectionFactory connectionFactory = new ConnectionFactory(config);

//        BookRepository bookRepository = new InMemoryBookRepository();
//        MemberRepository memberRepository = new InMemoryMemberRepository();
        BookRepository bookRepository = new JdbcBookRepository(connectionFactory);
        MemberRepository memberRepository = new JdbcMemberRepository(connectionFactory);
        LoanRepository loanRepository = new InMemoryLoanRepository();

        LibraryService libraryService = new LibraryService(bookRepository, loanRepository, memberRepository);

        ConsoleInput consoleInput = new ConsoleInput(new Scanner(System.in));
        ConsoleUI consoleUI = new ConsoleUI(libraryService, consoleInput);

//        new DemoData(main.bookRepository, main.memberRepository,  main.loanRepository).load();
        consoleUI.run();
    }
}