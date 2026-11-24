package com.library.repository.jdbc;

import com.library.config.ConnectionFactory;
import com.library.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class TestDatabase {

    private final DatabaseConfig config = DatabaseConfig.load("/database-test.properties");
    private final ConnectionFactory connectionFactory;

    public TestDatabase() {
        if (!config.url().endsWith("_test")) {
            throw new IllegalStateException("Refusing to run integration tests on non-test database: " + config.url());
        }
        this.connectionFactory = new ConnectionFactory(config);
    }

    public ConnectionFactory connectionFactory() {
        return connectionFactory;
    }

    public void cleanDatabase() {
        try (Connection connection = connectionFactory.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM loans");
            statement.executeUpdate("DELETE FROM members");
            statement.executeUpdate("DELETE FROM books");
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot clean test database.", e);
        }
    }
}