package com.library.repository.jdbc;

import com.library.config.ConnectionFactory;
import com.library.exception.StorageException;
import com.library.model.Book;
import com.library.repository.BookRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcBookRepository implements BookRepository {

    private final ConnectionFactory connectionFactory;

    public JdbcBookRepository(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public void save(Book book) {
        String query = "INSERT INTO books (isbn, title, author, publication_year) VALUES (?, ?, ?, ?)";
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, book.isbn());
            statement.setString(2, book.title());
            statement.setString(3, book.author());
            statement.setInt(4, book.publicationYear());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new StorageException("Cannot save book: " + book.isbn(), e);
        }
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        String query = "SELECT isbn, title, author, publication_year FROM books WHERE isbn = ?";
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, isbn);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapBook(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new StorageException("Cannot read book: " + isbn, e);
        }
    }

    @Override
    public boolean existsByIsbn(String isbn) {
        String query = "SELECT 1 FROM books WHERE isbn = ?";
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, isbn);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new StorageException("Cannot check if book exists: " + isbn, e);
        }
    }

    @Override
    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        String query = "SELECT isbn, title, author, publication_year FROM books ORDER BY isbn";
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                books.add(mapBook(rs));
            }
            return List.copyOf(books);

        } catch (SQLException e) {
            throw new StorageException("Cannot read books. ", e);
        }
    }

    private Book mapBook(ResultSet rs) throws SQLException {
        return new Book(
                rs.getString("isbn"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getInt("publication_year"));
    }
}