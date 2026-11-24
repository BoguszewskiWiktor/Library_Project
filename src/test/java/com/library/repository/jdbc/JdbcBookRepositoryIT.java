package com.library.repository.jdbc;

import com.library.exception.StorageException;
import com.library.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class JdbcBookRepositoryIT {

    private final TestDatabase testDatabase = new TestDatabase();
    private JdbcBookRepository bookRepository;
    private final Book book = new Book("111", "Harry Potter", "J.K. Rowling", 1999);
    private final Book book2 = new Book("222", "Harry Potter 2", "J.K. Rowling", 2000);

    @BeforeEach
    void setUp() {
        testDatabase.cleanDatabase();
        bookRepository = new JdbcBookRepository(testDatabase.connectionFactory());
    }

    @Test
    void save_whenNewBook_canBeReadBackByIsbn() {
        bookRepository.save(book);

        assertEquals(Optional.of(book), bookRepository.findByIsbn("111"));
    }

    @Test
    void save_whenIsbnAlreadyExists_throwStorageException() {
        Book duplicate = new Book("111", "Other title", "Other author", 2005);
        bookRepository.save(book);

        assertThrows(StorageException.class, () -> bookRepository.save(duplicate));
        assertEquals(Optional.of(book), bookRepository.findByIsbn("111"));
    }

    @Test
    void save_whenSeveralBooks_keepsThemSeparate() {
        bookRepository.save(book);
        bookRepository.save(book2);

        assertEquals(Optional.of(book), bookRepository.findByIsbn("111"));
        assertEquals(Optional.of(book2), bookRepository.findByIsbn("222"));
    }

    @Test
    void save_whenTextHasPolishCharacters_canBeReadBackUnchanged() {
        Book polishBook = new Book(
                "333", "Pan Tadeusz", "Adam Mickiewicz Zażółć gęślą jaźń", 1834);
        bookRepository.save(polishBook);

        assertEquals(Optional.of(polishBook), bookRepository.findByIsbn("333"));
    }

    @Test
    void findByIsbn_whenIsbnUnknown_returnsEmptyOptional() {
        assertEquals(Optional.empty(), bookRepository.findByIsbn("111"));
    }

    @Test
    void findByIsbn_whenOtherBookSaved_returnsEmptyOptional() {
        bookRepository.save(book);

        assertEquals(Optional.empty(), bookRepository.findByIsbn("222"));
    }

    @Test
    void existsByIsbn_whenBookSaved_returnsTrue() {
        bookRepository.save(book);

        assertTrue(bookRepository.existsByIsbn("111"));
    }

    @Test
    void existsByIsbn_whenBookNotSaved_returnsFalse() {
        assertFalse(bookRepository.existsByIsbn("222"));

        bookRepository.save(book);
        assertFalse(bookRepository.existsByIsbn("222"));
    }

    @Test
    void findAll_whenNoBooks_returnsEmptyList() {
        assertEquals(List.of(), bookRepository.findAll());
    }

    @Test
    void findAll_whenBooksSaved_returnsThemOrderedByIsbn() {
        bookRepository.save(book2);
        bookRepository.save(book);

        assertEquals(List.of(book,  book2), bookRepository.findAll());
    }

    @Test
    void findAll_whenReturnedListIsModified_throwsUnsupportedOperationException() {
        bookRepository.save(book);

        assertThrows(UnsupportedOperationException.class, () -> bookRepository.findAll().add(book2));
        assertEquals(List.of(book), bookRepository.findAll());
    }
}