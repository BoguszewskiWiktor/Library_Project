# Library Management System

A console application for managing a small library: books, members and loans.
Written in plain Java as a learning project, with a focus on clean layered
architecture, validation and unit testing.

**Current version: v0.2** (console app with in-memory storage and JUnit tests)

## Tech stack

- Java 21
- Maven
- JUnit 5 (unit tests)
- No frameworks yet (objects are wired manually in `Main`)

## Features

- Add books and register members
- Find a book by ISBN and search books by title or author
  (case-insensitive, surrounding spaces ignored)
- Check whether a book is available
- Borrow and return books
- List active loans and full loan history of a member
- List overdue loans
- List all books and all members
- Interactive console menu with readable error messages

## Business rules

- One ISBN identifies exactly one copy of a book
- A loan lasts 14 days
- A member can have at most 5 active loans (returned loans do not count)
- A book can be borrowed only if it has no active loan
- ISBN and member id must be unique
- Required fields cannot be null or blank; the email must contain `@`
- A loan is overdue when it is still active and the due date has passed

## Architecture

```
model        Book, Member (records with validation), Loan, Validation helpers
exception    LibraryException and its subclasses (unchecked)
repository   BookRepository, MemberRepository, LoanRepository (interfaces)
             InMemory... implementations
service      LibraryService - all business rules
ui           ConsoleUI, ConsoleInput, DemoData (optional sample data)
Main         assembles the objects and starts the UI
```

Dependencies point one way: `ui` -> `service` -> `repository` -> `model`.
The service depends only on repository interfaces, so the storage can be
replaced (file, database) without changing business logic.

Design decisions:

- `Book` and `Member` are immutable records; `Loan` is a class because only
  its return date changes.
- Book availability is derived from active loans, not stored on the book.
- Repositories never return `null` (they use `Optional` and empty lists) and
  return unmodifiable lists.
- Business errors are reported with custom unchecked exceptions
  (`BookNotFoundException`, `BookAlreadyExistsException`,
  `BookNotAvailableException`, `MemberNotFoundException`,
  `MemberAlreadyExistsException`, `LoanNotFoundException`,
  `LoanLimitExceededException`, ...). The UI catches them and prints a message.

## Tests

Unit tests are written with JUnit 5 and cover:

- `Loan`, `Book`, `Member`: construction, validation, `isActive`, `isOverdue`,
  `makeReturn`
- `InMemoryBookRepository`, `InMemoryMemberRepository`,
  `InMemoryLoanRepository`: saving, searching, filtering active loans,
  `update`, unmodifiable results
- `LibraryService`: registration, search, availability, borrowing (loan limit,
  returned loans, unavailable books), returning, reports, error cases

Tests use fresh in-memory repositories for every test and fixed dates, so
they are deterministic.

Run all tests (requires JDK 21):

```
mvn test
```

## Running the application

1. Open the project in IntelliJ IDEA (JDK 21).
2. Run the `Main` class.
3. Use the numbered menu in the console.

Optional sample data can be loaded with `DemoData` (see `Main`).

## Roadmap

- [x] v0.1 - console application with in-memory storage, manual test scenarios
- [x] v0.2 - unit tests with JUnit 5
- [ ] Optional: file persistence
- [ ] JDBC database persistence
- [ ] Later: Spring, logging, additional test libraries
