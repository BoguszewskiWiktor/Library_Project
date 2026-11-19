package com.library.repository.jdbc;

import com.library.config.ConnectionFactory;
import com.library.exception.StorageException;
import com.library.model.Member;
import com.library.repository.MemberRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcMemberRepository implements MemberRepository {

    private final ConnectionFactory connectionFactory;

    public JdbcMemberRepository(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public void save(Member member) {
        String query = "INSERT INTO members (id, first_name, last_name, email) VALUES (?, ?, ?, ?)";
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, member.id());
            statement.setString(2, member.firstName());
            statement.setString(3, member.lastName());
            statement.setString(4, member.email());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new StorageException("Cannot save member: " + member.id(), e);
        }
    }

    @Override
    public Optional<Member> findById(String id) {
        String query = "SELECT id, first_name, last_name, email FROM members WHERE id = ?";
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapMember(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new StorageException("Cannot read member: " + id, e);
        }
    }

    @Override
    public boolean existsById(String id) {
        String query = "SELECT 1 FROM members WHERE id = ?";
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new StorageException("Cannot check if member exists: " + id, e);
        }
    }

    @Override
    public List<Member> findAll() {
        List<Member> members = new ArrayList<>();
        String query = "SELECT id, first_name, last_name, email FROM members ORDER BY id";
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                members.add(mapMember(rs));
            }
            return List.copyOf(members);

        } catch (SQLException e) {
            throw new StorageException("Cannot read members", e);
        }
    }

    private Member mapMember(ResultSet rs) throws SQLException {
        return new Member(
                rs.getString("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("email"));
    }
}