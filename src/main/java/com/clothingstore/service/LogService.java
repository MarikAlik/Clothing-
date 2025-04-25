package com.clothingstore.service;

import com.clothingstore.cache.CacheService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

@Service
@EnableAsync
public class LogService {
    private static final String SOURCE_LOG_FILE = "logs/clothingstore.log";
    private static final String GENERATED_LOGS_DIR = "generated_logs/";

    // Константа для статуса задачи
    private static final String STATUS_SUFFIX = "_status";
    private static final String FILE_SUFFIX = "_file";

    private final CacheService cacheService;
    private final LogService logFileService;

    @Autowired
    public LogService(CacheService cacheService, LogService logFileService) {
        this.cacheService = cacheService;
        this.logFileService = logFileService;
    }

    public String startLogFileCreation() {
        String taskId = UUID.randomUUID().toString();
        cacheService.put(taskId + STATUS_SUFFIX, "IN_PROGRESS");


        logFileService.createLogFileAsync(taskId);

        return taskId;
    }

    /**
     * Асинхронно создает копию лог-файла.
     */
    @Async
    public void createLogFileAsync(String taskId) {
        try {
            Thread.sleep(5000); // 5 секунд

            Path source = Paths.get(SOURCE_LOG_FILE);
            Path outputDir = Paths.get(GENERATED_LOGS_DIR);
            Files.createDirectories(outputDir);

            Path outputFile = outputDir.resolve("log_" + taskId + ".log");
            Files.copy(source, outputFile);

            cacheService.put(taskId + STATUS_SUFFIX, "DONE");
            cacheService.put(taskId + FILE_SUFFIX, outputFile.toString());
        } catch (IOException | InterruptedException e) {
            cacheService.put(taskId + STATUS_SUFFIX, "FAILED");

            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Получить статус задачи по ID.
     */
    public Optional<String> getTaskStatus(String taskId) {
        Object status = cacheService.get(taskId + STATUS_SUFFIX);
        return status != null ? Optional.of(status.toString()) : Optional.empty();
    }

    /**
     * Получить путь к файлу по ID задачи.
     */
    public Optional<Path> getLogFile(String taskId) {
        Object filePath = cacheService.get(taskId + FILE_SUFFIX);
        if (filePath != null) {
            return Optional.of(Paths.get(filePath.toString()));
        }
        return Optional.empty();
    }

    public Optional<String> getLogsByDateFromGeneratedFile(String taskId, String date) {
        Optional<Path> filePath = getLogFile(taskId);
        if (filePath.isEmpty() || !Files.exists(filePath.get())) {
            return Optional.empty();
        }

        try (Stream<String> lines = Files.lines(filePath.get())) {
            List<String> filteredLines = lines
                    .filter(line -> line.contains(date))
                    .toList();

            if (filteredLines.isEmpty()) {
                return Optional.of("No log entries found for date: " + date);
            }

            return Optional.of(String.join("\n", filteredLines));
        } catch (IOException e) {
            return Optional.of("Error reading log file: " + e.getMessage());
        }
    }
}


