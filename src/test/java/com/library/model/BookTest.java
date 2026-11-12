package com.library.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class BookTest {

    @Test
    void constructor_withValidData_storesValues() {
        Book book = new Book("111", "Harry Potter", "J.K. Rowling", 1999);

        assertEquals("111", book.isbn());
        assertEquals("Harry Potter", book.title());
        assertEquals("J.K. Rowling", book.author());
        assertEquals(1999, book.publicationYear());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   ", "\t"})
    void constructor_whenIsbnIsBlank_throwsIllegalArgumentException(String isbn) {
        assertThrows(IllegalArgumentException.class,
                () -> new Book(isbn, "Harry Potter", "J.K. Rowling", 1999));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   ", "\t"})
    void constructor_whenTitleIsBlank_throwsIllegalArgumentException(String title) {
        assertThrows(IllegalArgumentException.class,
                () -> new Book("111", title, "J.K. Rowling", 1999));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   ", "\t"})
    void constructor_whenAuthorIsBlank_throwsIllegalArgumentException(String author) {
        assertThrows(IllegalArgumentException.class,
                () -> new Book("111", "Harry Potter", author, 1999));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -111})
    void constructor_whenPublicationYearIsZeroOrNegative_throwsIllegalArgumentException(int publicationYear) {
        assertThrows(IllegalArgumentException.class,
                () -> new Book("111", "Harry Potter", "J.K. Rowling", publicationYear));
    }

    @Test
    void constructor_whenTextHasSurroundingSpaces_stripsThem() {
        Book book =
                new Book("  111  ", "  Harry Potter  ", "  J.K. Rowling  ", 1999);

        assertEquals("111", book.isbn());
        assertEquals("Harry Potter", book.title());
        assertEquals("J.K. Rowling", book.author());
    }

    @Test
    void equals_whenBooksHaveSameData_returnsTrue() {
        Book book1 = new Book("111", "Harry Potter", "J.K. Rowling", 1999);
        Book book2 = new Book("111", "Harry Potter", "J.K. Rowling", 1999);

        assertEquals(book1, book2);
    }

    @Test
    void equals_whenBooksHaveDifferentIsbn_returnsFalse() {
        Book book1 = new Book("111", "Harry Potter", "J.K. Rowling", 1999);
        Book book2 = new Book("222", "Harry Potter", "J.K. Rowling", 1999);

        assertNotEquals(book1, book2);
    }
}