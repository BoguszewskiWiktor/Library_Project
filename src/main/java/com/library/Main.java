package com.library;

import com.library.repository.InMemoryBookRepository;
import com.library.repository.InMemoryLoanRepository;
import com.library.repository.InMemoryMemberRepository;
import com.library.service.LibraryService;
import com.library.ui.ConsoleInput;
import com.library.ui.ConsoleUI;

import java.util.Scanner;

public class Main {

    private final InMemoryBookRepository bookRepository = new InMemoryBookRepository();
    private final InMemoryLoanRepository loanRepository = new InMemoryLoanRepository();
    private final InMemoryMemberRepository memberRepository = new InMemoryMemberRepository();

    private final LibraryService libraryService = new LibraryService(bookRepository, loanRepository, memberRepository);

    Scanner scanner = new Scanner(System.in);
    private final ConsoleInput consoleInput = new ConsoleInput(scanner);
    private final ConsoleUI consoleUI = new ConsoleUI(libraryService, consoleInput);

    public static void main(String[] args) {
        System.out.println("Starting library");
        Main main = new Main();
        main.consoleUI.run();

    }
}