package com.library.config;


import com.library.util.Validation;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public record DatabaseConfig(String url, String username, String password) {

    public DatabaseConfig {
        url = Validation.requireNotBlank(url, "url");
        username = Validation.requireNotBlank(username, "username");
        password = Validation.requireNotBlank(password, "password");
    }

    public static DatabaseConfig load() {
        Properties props = new Properties();
        try (InputStream input = DatabaseConfig.class
                .getResourceAsStream("/database.properties")) {
            if (input == null) {
                throw new IllegalStateException("database.properties file not found");
            }
            props.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read database.properties file", e);
        }
        return new DatabaseConfig(
                props.getProperty("db.url"),
                props.getProperty("db.username"),
                props.getProperty("db.password"));
    }
}