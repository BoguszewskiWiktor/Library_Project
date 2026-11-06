package com.library.ui;

import com.library.exception.LibraryException;
import com.library.model.Book;
import com.library.model.Loan;
import com.library.model.Member;
import com.library.service.LibraryService;

import java.util.List;

public class ConsoleUI {

    private final LibraryService libraryService;
    private final ConsoleInput consoleInput;

    public ConsoleUI(LibraryService libraryService, ConsoleInput consoleInput) {
        this.libraryService = libraryService;
        this.consoleInput = consoleInput;
    }

    public void run() {
        boolean running = true;

        while (running) {
            printMenu();
            int choice = consoleInput.readInteger("Please enter your choice: ");

            try {
                switch (choice) {
                    case 1 -> System.out.println(addBook());
                    case 2 -> listAllBooks();
                    case 3 -> searchBooks();
                    case 4 -> System.out.println(registerMember());
                    case 5 -> listAllMembers();
                    case 6 -> System.out.println(borrowBook());
                    case 7 -> System.out.println(returnBook());
                    case 8 -> showActiveLoansForMember();
                    case 9 -> showOverdueLoans();
                    case 0 -> running = false;
                    default -> System.out.println("Unknown option. Try again.");
                }
            } catch (LibraryException | IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
            System.out.println();
        }
        System.out.println("Goodbye!");
    }

    private void printMenu() {
        System.out.println("1. Add book");
        System.out.println("2. List all books");
        System.out.println("3. Search books");
        System.out.println("4. Register member");
        System.out.println("5. List members");
        System.out.println("6. Borrow book");
        System.out.println("7. Return book");
        System.out.println("8. Show member's active loans");
        System.out.println("9. Show overdue loans");
        System.out.println("0. Exit");
    }

    private String addBook() {
        String isbn = consoleInput.readNonBlankString("Please enter book ISBN: ");
        String title = consoleInput.readNonBlankString("Please enter book title: ");
        String author = consoleInput.readNonBlankString("Please enter book author: ");
        int publicationYear = consoleInput.readInteger("Please enter book publication year: ");
        Book book = new Book(isbn, title, author, publicationYear);

        libraryService.addBook(book);
        return "Book added successfully!";
    }

    private void listAllBooks() {
        displayBooks(libraryService.getAllBooks());
    }

    private void searchBooks() {
        String phrase = consoleInput.readNonBlankString("Please enter phrase: ");
        displayBooks(libraryService.searchBooks(phrase));
    }

    private String registerMember() {
        String id = consoleInput.readNonBlankString("Please enter member ID: ");
        String firstName = consoleInput.readNonBlankString("Please enter member first name: ");
        String lastName = consoleInput.readNonBlankString("Please enter member last name: ");
        String email = consoleInput.readNonBlankString("Please enter member email: ");
        Member member = new Member(id, firstName, lastName, email);

        libraryService.registerMember(member);
        return "Member registered successfully!";
    }

    private void listAllMembers() {
        displayMembers(libraryService.getAllMembers());
    }

    private String borrowBook() {
        String isbn = consoleInput.readNonBlankString("Please enter the book ISBN: ");
        String memberId = consoleInput.readNonBlankString("Please enter the member ID: ");

        Loan loan = libraryService.borrowBook(isbn, memberId);
        Book book = libraryService.findBook(isbn);

        return book.title() + " borrowed successfully! " + "Due date: " + loan.getDueDate();
    }

    private String returnBook() {
        String isbn = consoleInput.readNonBlankString("Please enter the book ISBN: ");
        libraryService.returnBook(isbn);

        Book book = libraryService.findBook(isbn);
        return book.title() + " returned successfully.";
    }

    private void showActiveLoansForMember() {
        String memberId = consoleInput.readNonBlankString("Please enter the member id: ");
        displayLoans(libraryService.getActiveLoansForMember(memberId));
    }

    private void showOverdueLoans() {
        displayLoans(libraryService.getOverdueLoans());
    }

    private void displayMembers(List<Member> allMembers) {
        if (allMembers.isEmpty()) {
            System.out.println("No members found.");
            return;
        }

        for (Member member : allMembers) {
            System.out.println(formatMember(member));
        }
    }

    private String formatMember(Member member) {
        return String.format("%s %s (ID: %s), %s",
                member.lastName(), member.firstName(), member.id(), member.email());
    }

    private void displayBooks(List<Book> books) {
        if (books.isEmpty()) {
            System.out.println("No books found.");
            return;
        }

        for (Book book : books) {
            boolean available = libraryService.isBookAvailable(book.isbn());
            System.out.println(formatBook(book, available));
        }
    }

    private String formatBook(Book book, boolean available) {
        String status = available ? "Available" : "Not available";

        return String.format("%s by %s (%d), ISBN: %s - %s",
                book.title(), book.author(), book.publicationYear(), book.isbn(), status);
    }

    private void displayLoans(List<Loan> loans) {
        if (loans.isEmpty()) {
            System.out.println("No loans found.");
            return;
        }

        for (Loan loan : loans) {
            System.out.println(formatLoan(loan));
        }
    }

    private String formatLoan(Loan loan) {
        return String.format("%s, ISBN: %s, MemberID: %s, Loan Date: %s, Due Date: %s",
                libraryService.findBook(loan.getIsbn()).title(), loan.getIsbn(),
                loan.getMemberId(), loan.getLoanDate(), loan.getDueDate());
    }
}