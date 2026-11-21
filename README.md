# Library Management System

A console application for managing a small library: books, members and loans.
Written in plain Java as a learning project, with a focus on clean layered
architecture, validation, unit testing and persistence with JDBC and PostgreSQL.

**Current version: v0.3** (console app with PostgreSQL persistence and JUnit tests)

## Tech stack

- Java 21
- Maven
- PostgreSQL + JDBC (`org.postgresql:postgresql`)
- JUnit 5 (unit tests)
- No frameworks yet (objects are wired manually in `Main`)

## Features

- Add books and register members
- Find a book by ISBN and search books by title or author
  (case-insensitive, surrounding spaces ignored)
- Check whether a book is available
- Borrow and return books
- List active loans and full loan history of a member
  (returned loans show their return date)
- List overdue loans
- List all books and all members
- Interactive console menu with readable error messages
- Data is stored in PostgreSQL and survives application restarts

## Console menu

```
1. Add book
2. List all books
3. Search books
4. Register member
5. List members
6. Borrow book
7. Return book
8. Show member's active loans
9. Show member's loan history
10. Show overdue loans
0. Exit
```

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
config       DatabaseConfig (loads database.properties), ConnectionFactory
model        Book, Member (records with validation), Loan
util         Validation helpers
exception    LibraryException and its subclasses (unchecked)
repository   BookRepository, MemberRepository, LoanRepository (interfaces)
             InMemory... implementations (used in unit tests)
             Jdbc... implementations (PostgreSQL)
service      LibraryService - all business rules
ui           ConsoleUI, ConsoleInput, DemoData (optional sample data)
Main         assembles the objects and starts the UI
```

Dependencies point one way: `ui` -> `service` -> `repository` -> `model`.
The service depends only on repository interfaces, so the storage can be
replaced (memory, database) without changing business logic.

Design decisions:

- `Book` and `Member` are immutable records; `Loan` is a class because only
  its return date changes.
- Book availability is derived from active loans, not stored on the book.
- A loan without a return date is active (`return_date IS NULL` in the
  database).
- Repositories never return `null` (they use `Optional` and empty lists) and
  return unmodifiable lists.
- Business errors are reported with custom unchecked exceptions
  (`BookNotFoundException`, `BookAlreadyExistsException`,
  `BookNotAvailableException`, `MemberNotFoundException`,
  `MemberAlreadyExistsException`, `LoanNotFoundException`,
  `LoanLimitExceededException`, `StorageException`). The UI catches them and
  prints a message. Database errors are wrapped in `StorageException`.
- JDBC repositories use `PreparedStatement` with parameters and
  try-with-resources. One connection is opened per operation; there is no
  connection pool and no transactions yet.
- The database also enforces the rules: foreign keys, `CHECK` constraints and
  a partial unique index that allows only one active loan per book.

## Database setup

1. Create a PostgreSQL database named `library`.
2. Run the schema script `src/main/resources/schema.sql` (creates the tables
   `books`, `members` and `loans`).
3. Copy `src/main/resources/database.properties.example` to
   `src/main/resources/database.properties` and fill in your values:

   ```
   db.url=jdbc:postgresql://localhost:5432/library
   db.username=your_user
   db.password=your_password
   ```

`database.properties` contains credentials and is ignored by Git. Only the
`.example` template is committed.

## Running the application

1. Open the project in IntelliJ IDEA (JDK 21).
2. Make sure the database is set up as described above.
3. Run the `Main` class.
4. Use the numbered menu in the console.

Optional sample data can be loaded with `DemoData` (see `Main`).

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
they are deterministic and do not need a database.

Run all tests (requires JDK 21):

```
mvn test
```

The JDBC repositories were verified with manual end-to-end scenarios.
Automated integration tests for them are planned.

## Roadmap

- [x] v0.1 - console application with in-memory storage, manual test scenarios
- [x] v0.2 - unit tests with JUnit 5
- [x] v0.3 - PostgreSQL persistence with JDBC
- [ ] Integration tests for JDBC repositories
- [ ] Later: Spring, logging, additional test libraries
