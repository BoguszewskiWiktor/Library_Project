package com.library.repository;

import com.library.model.Book;

import java.util.List;
import java.util.Optional;

/**
 * Storage for books. Contains no business rules.
 */

public interface BookRepository {

    /** Stores the book. Does not check for duplicates (the service does). */
    void save(Book book);

    /** Returns the book with the given ISBN, or an empty Optional if there is none. */
    Optional<Book> findByIsbn(String isbn);

    /** Returns true if a book with the given ISBN is stored. */
    boolean existsByIsbn(String isbn);

    /** Returns all stored books. Never returns null. */
    List<Book> findAll();

}