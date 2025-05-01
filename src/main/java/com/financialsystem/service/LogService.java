package com.financialsystem.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Service
public class LogService {

    private static final String LOG_FILE_PATH = "logs/user-activity.log";

    public byte[] getLogFile() {
        var logFile = new File(LOG_FILE_PATH);

        try (var fileInputStream = new FileInputStream(logFile)) {

            byte[] fileBytes = new byte[(int) logFile.length()];
            fileInputStream.read(fileBytes);

            return fileBytes;
        } catch (IOException e) {
            throw new RuntimeException("Error reading log file: " + e.getMessage());
        }
    }
}