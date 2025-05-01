package com.financialsystem.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ActivityLogger {

    private static final String LOG_FILE_PATH = "logs/user-activity.log";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void log(String message) {
        try (FileWriter fileWriter = new FileWriter(LOG_FILE_PATH, true);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {

            String timestamp = LocalDateTime.now().format(FORMATTER);
            printWriter.println(timestamp + " - " + message);

        } catch (IOException e) {
            System.err.println("Failed to write to user activity log: " + e.getMessage());
        }
    }
}