package com.library;

import com.library.config.DatabaseConfig;
import com.library.repository.BookRepository;
import com.library.repository.LoanRepository;
import com.library.repository.MemberRepository;
import com.library.repository.inMemory.InMemoryBookRepository;
import com.library.repository.inMemory.InMemoryLoanRepository;
import com.library.repository.inMemory.InMemoryMemberRepository;
import com.library.repository.jdbc.JdbcBookRepository;
import com.library.service.LibraryService;
import com.library.ui.ConsoleInput;
import com.library.ui.ConsoleUI;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
//        DatabaseConfig config = DatabaseConfig.load();
//        try (Connection connection = DriverManager.getConnection(config.url(), config.username(), config.password())){
//            System.out.println("Connected to database successfully");
//        } catch (SQLException e) {
//            System.err.println("Exception caught: " + e.getMessage());
//        }

        System.out.println("Starting library");

        BookRepository bookRepository = new InMemoryBookRepository();
//            BookRepository bookRepository = new JdbcBookRepository(config);
        MemberRepository memberRepository = new InMemoryMemberRepository();
        LoanRepository loanRepository = new InMemoryLoanRepository();

        LibraryService libraryService = new LibraryService(bookRepository, loanRepository, memberRepository);

        ConsoleInput consoleInput = new ConsoleInput(new Scanner(System.in));
        ConsoleUI consoleUI = new ConsoleUI(libraryService, consoleInput);

//        new DemoData(main.bookRepository, main.memberRepository,  main.loanRepository).load();
        consoleUI.run();
    }
}