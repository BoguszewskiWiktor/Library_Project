package com.library.repository.jdbc;

import com.library.config.ConnectionFactory;
import com.library.exception.LoanNotFoundException;
import com.library.exception.StorageException;
import com.library.model.Loan;
import com.library.repository.LoanRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcLoanRepository implements LoanRepository {

    private final ConnectionFactory connectionFactory;

    public JdbcLoanRepository(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public void save(Loan loan) {
        String query = "INSERT INTO loans (isbn, member_id, loan_date, due_date, return_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, loan.getIsbn());
            statement.setString(2, loan.getMemberId());
            statement.setDate(3, Date.valueOf(loan.getLoanDate()));
            statement.setDate(4, Date.valueOf(loan.getDueDate()));

            LocalDate returnDate = loan.getReturnDate();
            if (returnDate == null) {
                statement.setNull(5, Types.DATE);
            } else {
                statement.setDate(5, Date.valueOf(returnDate));
            }

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new StorageException(
                    "Cannot save loan for book " + loan.getIsbn() + " and member " + loan.getMemberId(), e);
        }
    }

    @Override
    public void update(Loan loan) {
        String query = "UPDATE loans SET return_date = ? WHERE isbn = ? AND return_date IS NULL";
        if (loan.getReturnDate() == null) {
            throw new IllegalArgumentException("Loan must have a return date to be updated");
        }

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setDate(1, Date.valueOf(loan.getReturnDate()));
            statement.setString(2, loan.getIsbn());
            int updated = statement.executeUpdate();
            if (updated == 0) {
                throw new LoanNotFoundException("No active loan for book " + loan.getIsbn());
            }
        } catch (SQLException e) {
            throw new StorageException("Cannot update loan for book " + loan.getIsbn(), e);
        }
    }

    @Override
    public Optional<Loan> findActiveByIsbn(String isbn) {
        String query = """
                SELECT isbn, member_id, loan_date, due_date, return_date
                FROM loans
                WHERE isbn = ? AND return_date IS NULL
                """;
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, isbn);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapLoan(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new StorageException("Cannot find loan for book: " + isbn, e);
        }
    }

    @Override
    public List<Loan> findActiveByMemberId(String memberId) {
        List<Loan> loans = new ArrayList<>();
        String query = """
                SELECT isbn, member_id, loan_date, due_date, return_date
                FROM loans
                WHERE member_id = ? AND return_date IS NULL
                ORDER BY loan_date, isbn
                """;
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, memberId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    loans.add(mapLoan(rs));
                }
                return List.copyOf(loans);
            }

        } catch (SQLException e) {
            throw new StorageException("Cannot find loans for member: " + memberId, e);
        }
    }

    @Override
    public List<Loan> findAllByMemberId(String memberId) {
        List<Loan> loans = new ArrayList<>();
        String query = """
                SELECT isbn, member_id, loan_date, due_date, return_date
                FROM loans
                WHERE member_id = ?
                ORDER BY loan_date, isbn""";
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, memberId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    loans.add(mapLoan(rs));
                }
                return List.copyOf(loans);
            }

        } catch (SQLException e) {
            throw new StorageException("Cannot find loans for member: " + memberId, e);
        }
    }

    @Override
    public List<Loan> findAllActive() {
        List<Loan> loans = new ArrayList<>();
        String query = """
                SELECT isbn, member_id, loan_date, due_date, return_date
                FROM loans
                WHERE return_date IS NULL
                ORDER BY loan_date, isbn
                """;
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                loans.add(mapLoan(rs));
            }
            return List.copyOf(loans);

        } catch (SQLException e) {
            throw new StorageException("Cannot find active loans", e);
        }
    }

    private Loan mapLoan(ResultSet rs) throws SQLException {
        Loan loan = new Loan(
                rs.getString("isbn"),
                rs.getString("member_id"),
                rs.getDate("loan_date").toLocalDate(),
                rs.getDate("due_date").toLocalDate());

        Date returnDate = rs.getDate("return_date");
        if (returnDate != null) {
            loan.makeReturn(returnDate.toLocalDate());
        }
        return loan;
    }
}