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
        return load("/database.properties");
    }

    public static DatabaseConfig load(String resourceName) {
        Properties props = new Properties();
        try (InputStream input = DatabaseConfig.class
                .getResourceAsStream(resourceName)) {
            if (input == null) {
                throw new IllegalStateException(resourceName + " file not found");
            }
            props.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read " + resourceName + " file", e);
        }
        return new DatabaseConfig(
                props.getProperty("db.url"),
                props.getProperty("db.username"),
                props.getProperty("db.password"));
    }
}