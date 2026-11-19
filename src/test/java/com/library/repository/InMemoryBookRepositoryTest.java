package com.library.repository;

import com.library.model.Book;
import com.library.repository.inMemory.InMemoryBookRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryBookRepositoryTest {

    private final BookRepository bookRepository = new InMemoryBookRepository();
    private final Book book = new Book("111", "Harry Potter", "J.K. Rowling", 1999);
    private final Book book2 = new Book("222", "Harry Potter 2", "J.K. Rowling", 2000);

    @Test
    void save_whenSaveBookCorrectly_findByIsbnShowsThisBook() {
        bookRepository.save(book);

        assertEquals(Optional.of(book), bookRepository.findByIsbn("111"));
    }

    @Test
    void findByIsbn_whenIsbnUnknown_returnsEmptyOptional() {
        assertEquals(Optional.empty(), bookRepository.findByIsbn("111"));
    }

    @Test
    void existsByIsbn_whenExists_returnsTrue() {
        bookRepository.save(book);

        assertTrue(bookRepository.existsByIsbn("111"));
    }

    @Test
    void existsByIsbn_whenNotExists_returnsFalse() {
        assertFalse(bookRepository.existsByIsbn("111"));
    }

    @Test
    void findAll_whenNoBooks_returnsEmptyList() {
        assertEquals(List.of(), bookRepository.findAll());
    }

    @Test
    void findAll_whenBooksSaved_returnsThemInInsertionOrder() {
        bookRepository.save(book);
        bookRepository.save(book2);

        assertEquals(List.of(book, book2), bookRepository.findAll());
    }

    @Test
    void findAll_whenReturnedListIsModified_throwsUnsupportedOperationException() {
        bookRepository.save(book);
        List<Book> books = bookRepository.findAll();

        assertThrows(UnsupportedOperationException.class, () -> books.add(book2));
        assertEquals(List.of(book), bookRepository.findAll());
    }
}