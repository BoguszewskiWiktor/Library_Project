package com.library;

import com.library.repository.*;
import com.library.service.LibraryService;
import com.library.ui.ConsoleInput;
import com.library.ui.ConsoleUI;
import com.library.ui.DemoData;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        System.out.println("Starting library");

        BookRepository bookRepository = new InMemoryBookRepository();
        MemberRepository memberRepository = new InMemoryMemberRepository();
        LoanRepository loanRepository = new InMemoryLoanRepository();

        LibraryService libraryService = new LibraryService(bookRepository, loanRepository, memberRepository);

        ConsoleInput consoleInput = new ConsoleInput(new Scanner(System.in));
        ConsoleUI consoleUI = new ConsoleUI(libraryService, consoleInput);

//        new DemoData(main.bookRepository, main.memberRepository,  main.loanRepository).load();
        consoleUI.run();
    }
}