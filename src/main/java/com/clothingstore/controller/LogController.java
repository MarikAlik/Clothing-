package com.clothingstore.controller;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/logs")
public class LogController {

    private static final String LOG_FILE = "logs/clothingstore.log";

    @GetMapping("/{date}")
    public ResponseEntity<String> getLogsByDate(@PathVariable String date) {
        try {
            Path path = Paths.get(LOG_FILE);

            if (!Files.exists(path)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Log file not found.");
            }

            List<String> filteredLines = Files.lines(path)
                    .filter(line -> line.contains(date))
                    .collect(Collectors.toList());

            if (filteredLines.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No log entries found for date: " + date);
            }

            String content = String.join("\n", filteredLines);

            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(content);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error reading log file: " + e.getMessage());
        }
    }
}
