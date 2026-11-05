package com.library.ui;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class ConsoleInput {

    private final Scanner scanner;

    public ConsoleInput(Scanner scanner) {
        this.scanner = new Scanner(System.in);
    }

    public String readNonBlankString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine();

            if (!line.isEmpty()) {
                return line.strip();
            }

            System.out.println("Value must not be empty. Try again.");
        }
    }

    public int readInteger(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine();

            try {
                return Integer.parseInt(line.strip());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number. Try again.");
            }
        }
    }

    public LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine();

            try {
                return LocalDate.parse(line.strip());
            } catch (DateTimeParseException e) {
                System.out.println("Please enter a valid date in the format yyyy-MM-dd. Try again.");
            }
        }
    }

}