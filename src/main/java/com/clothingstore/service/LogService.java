package com.clothingstore.service;

import com.clothingstore.cache.CacheService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

@Service
@EnableAsync
public class LogService {
    private static final String SOURCE_LOG_FILE = "logs/clothingstore.log";
    private static final String GENERATED_LOGS_DIR = "generated_logs/";
    private final AtomicLong idGenerator = new AtomicLong(1);

    private final CacheService cacheService;
    private final ApplicationContext applicationContext;

    @Autowired
    public LogService(CacheService cacheService, ApplicationContext applicationContext) {
        this.cacheService = cacheService;
        this.applicationContext = applicationContext;
    }

    public String startLogFileCreation() {
        String taskId = String.valueOf(idGenerator.getAndIncrement());
        cacheService.put(taskId + "_status", "IN_PROGRESS");

        LogService proxy = applicationContext.getBean(LogService.class);
        proxy.createLogFileAsync(taskId);

        return taskId;
    }

    @Async
    public void createLogFileAsync(String taskId) {
        try {
            cacheService.put(taskId + "_status", "PROCESSING");
            Thread.sleep(5000);

            Path source = Paths.get(SOURCE_LOG_FILE);
            Path outputDir = Paths.get(GENERATED_LOGS_DIR);
            Files.createDirectories(outputDir);

            Path outputFile = outputDir.resolve("log_" + taskId + ".log");
            Files.copy(source, outputFile);

            cacheService.put(taskId + "_status", "DONE");
            cacheService.put(taskId + "_file", outputFile.toString());

        } catch (IOException | InterruptedException e) {
            cacheService.put(taskId + "_status", "FAILED");
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public Optional<String> getTaskStatus(String taskId) {
        Object status = cacheService.get(taskId + "_status");
        return status != null ? Optional.of(status.toString()) : Optional.empty();
    }

    public Optional<Path> getLogFile(String taskId) {
        Object filePath = cacheService.get(taskId + "_file");
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

