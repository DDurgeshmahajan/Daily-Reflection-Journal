/*
 * Author: Durgesh Mahajan
 * Date: 2023-10-05
 * Project: Daily Reflection Journal
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final String JOURNAL_FILE = "journal.txt";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            displayMenu();
            String choice = scanner.nextLine().trim().toLowerCase();

            switch (choice) {
                case "1":
                    addEntry(scanner);
                    break;
                case "2":
                    viewAllEntries();
                    break;
                case "3":
                    viewEntriesByDate(scanner);
                    break;
                case "4":
                    searchEntries(scanner);
                    break;
                case "5":
                    running = false;
                    System.out.println("Exiting the journal application. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please select a valid option.");
            }
        }
    }

    private static void displayMenu() {
        System.out.println("\
Daily Reflection Journal\
" +
                "1. Add New Entry\
" +
                "2. View All Entries\
" +
                "3. View Entries by Date\
" +
                "4. Search Entries\
" +
                "5. Exit\
" +
                "Choose an option: ");
    }

    private static void addEntry(Scanner scanner) {
        LocalDate date = LocalDate.now();
        System.out.println("Today's Date: " + date);
        System.out.println("Write your entry (type 'END_ENTRY' on a new line to finish): ");

        StringBuilder content = new StringBuilder();
        String line;
        while (true) {
            line = scanner.nextLine();
            if (line.equalsIgnoreCase("END_ENTRY")) {
                break;
            }
            content.append(line).append("\
");
        }

        JournalEntry entry = new JournalEntry(date, content.toString().trim());

        try (FileWriter fileWriter = new FileWriter(JOURNAL_FILE, true);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            printWriter.println(entry.toString());
            System.out.println("Entry added successfully!");
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    private static void viewAllEntries() {
        List<JournalEntry> entries = loadEntries();
        if (entries.isEmpty()) {
            System.out.println("No entries found.");
        } else {
            Collections.sort(entries);
            for (JournalEntry entry : entries) {
                System.out.println(entry.toString());
            }
        }
    }

    private static void viewEntriesByDate(Scanner scanner) {
        System.out.print("Enter a date (YYYY-MM-DD): ");
        String dateString = scanner.nextLine().trim();

        try {
            LocalDate date = LocalDate.parse(dateString);
            List<JournalEntry> entries = loadEntries();
            List<JournalEntry> filteredEntries = new ArrayList<>();

            for (JournalEntry entry : entries) {
                if (entry.getDate().equals(date)) {
                    filteredEntries.add(entry);
                }
            }

            if (filteredEntries.isEmpty()) {
                System.out.println("No entries found for the date: " + dateString);
            } else {
                for (JournalEntry entry : filteredEntries) {
                    System.out.println(entry.toString());
                }
            }
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Please use YYYY-MM-DD.");
        }
    }

    private static void searchEntries(Scanner scanner) {
        System.out.print("Enter a keyword or phrase to search: ");
        String keyword = scanner.nextLine().trim().toLowerCase();
        List<JournalEntry> entries = loadEntries();
        List<JournalEntry> matchingEntries = new ArrayList<>();

        for (JournalEntry entry : entries) {
            if (entry.getContent().toLowerCase().contains(keyword)) {
                matchingEntries.add(entry);
            }
        }

        if (matchingEntries.isEmpty()) {
            System.out.println("No entries found containing the keyword: " + keyword);
        } else {
            for (JournalEntry entry : matchingEntries) {
                System.out.println(entry.toString());
            }
        }
    }

    private static List<JournalEntry> loadEntries() {
        List<JournalEntry> entries = new ArrayList<>();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(JOURNAL_FILE))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                JournalEntry entry = JournalEntry.fromString(line);
                if (entry != null) {
                    entries.add(entry);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading from file: " + e.getMessage());
        }

        return entries;
    }
}