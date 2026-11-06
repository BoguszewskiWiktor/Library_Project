# Library Management System

A console application for managing a small library, written in plain Java as a learning project. It lets a librarian manage books and members, lend and return books, search the catalogue and see overdue loans.

The project is built step by step, with a clear separation into layers and no frameworks.

## Features

- Add books and list the whole catalogue with availability status
- Search books by title or author (case-insensitive, partial match)
- Register library members and list them
- Borrow and return books
- Show a member's active loans
- Show all overdue loans

### Menu

```
1. Add Book
2. List all books
3. Search books
4. Register member
5. List members
6. Borrow book
7. Return book
8. Show member's active loans
9. Show overdue loans
0. Exit
```

## Business rules

- A book is identified by its ISBN, a member by a member ID. Both must be unique.
- A loan lasts 14 days.
- A member can have at most 5 active loans at the same time.
- A book is available when there is no active loan for it. Availability is calculated from loans and is not stored in the book.
- A loan is overdue when it is still active and its due date has passed.
- Invalid input (blank values, malformed numbers, unknown ISBN or member) results in a clear error message, and the program returns to the menu.

## Tech stack

- Java 21
- Maven
- Plain Java, no frameworks

## Architecture

The code is split into layers. Each layer depends only on the one below it:

```
ui  ->  service  ->  repository (interfaces)
                         ^
                   implementations (in-memory)
```

| Package | Responsibility |
|---|---|
| `model` | Data classes: `Book` and `Member` (records), `Loan` |
| `exception` | Custom business exceptions based on `LibraryException` |
| `repository` | Repository interfaces and their in-memory implementations |
| `service` | Business logic and rules (`LibraryService`) |
| `ui` | Console menu and input handling (`ConsoleUI`, `ConsoleInput`) |

Key design decisions:

- Repositories are used through interfaces, so the storage can be replaced (file, database) without changing the service or the UI.
- Dependencies are passed through constructors and assembled manually in `Main`.
- Business rules live only in the service. The UI only reads input, calls the service and displays results.
- Data is validated in model constructors, and business errors are reported with custom exceptions.

## Getting started

1. Clone the repository.
2. Open the project in IntelliJ IDEA (JDK 21).
3. Run the `Main` class (`com.library.Main`).

The application starts with an empty library. To start with sample books, members and loans (including an overdue one), uncomment the `DemoData` line in `Main`.

## Status

Version `v0.1`: the console application works end to end. Data is stored in memory only and is lost when the program exits.

## Roadmap

- Unit tests with JUnit
- Optional file persistence
- Database persistence with JDBC
