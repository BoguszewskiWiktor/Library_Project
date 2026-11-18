package com.library;

import com.library.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
//        System.out.println("Starting library");
//
//        BookRepository bookRepository = new InMemoryBookRepository();
//        MemberRepository memberRepository = new InMemoryMemberRepository();
//        LoanRepository loanRepository = new InMemoryLoanRepository();
//
//        LibraryService libraryService = new LibraryService(bookRepository, loanRepository, memberRepository);
//
//        ConsoleInput consoleInput = new ConsoleInput(new Scanner(System.in));
//        ConsoleUI consoleUI = new ConsoleUI(libraryService, consoleInput);
//
////        new DemoData(main.bookRepository, main.memberRepository,  main.loanRepository).load();
//        consoleUI.run();

        DatabaseConfig config = DatabaseConfig.load();
        try (Connection connection = DriverManager.getConnection(config.url(), config.username(), config.password())){
            System.out.println("Connected to database successfully");
        } catch (SQLException e) {
            System.err.println("Exception caught: " + e.getMessage());
        }
    }
}