package com.library.repository;

import com.library.exception.BookNotFoundException;
import com.library.model.Book;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryBookRepository implements BookRepository {

    Map<String, Book> books = new LinkedHashMap<>();

    @Override
    public void save(Book book) {
        books.put(book.isbn(), book);
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        return Optional.of(books.get(isbn));
    }

    @Override
    public boolean existsByIsbn(String isbn) {
        return books.containsKey(isbn);
    }

    @Override
    public List<Book> findAll() {
        return List.copyOf(books.values());
    }
}