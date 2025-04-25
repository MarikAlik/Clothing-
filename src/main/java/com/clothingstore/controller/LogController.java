package com.clothingstore.controller;

import com.clothingstore.service.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logs")
public class LogController {

    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    private static final String LOG_FILE = "logs/clothingstore.log";

    @Operation(
            summary = "Получить логи по дате",
            description = "Возвращает строки из файла логов, содержащие указанную дату. "
                    + "Формат даты должен соответствовать формату, используемому в логах "
                    + "(например, '2025-04-15')."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Лог найден и возвращён"),
        @ApiResponse(responseCode = "404",
                    description = "Файл логов не найден или нет записей на указанную дату"),
        @ApiResponse(responseCode = "500", description = "Ошибка при чтении файла логов")
    })
    @GetMapping("/{date}")
    public ResponseEntity<String> getLogsByDate(
            @Parameter(description = "Дата для фильтрации логов. Пример: 2025-04-15")
            @PathVariable String date
    ) {
        try {
            Path path = Paths.get(LOG_FILE);

            if (!Files.exists(path)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Log file not found.");
            }

            List<String> filteredLines;
            try (Stream<String> lines = Files.lines(path)) {
                filteredLines = lines
                        .filter(line -> line.contains(date))
                        .toList();
            }

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

    @Operation(summary = "Инициировать создание лог-файла")
    @ApiResponse(responseCode = "202", description = "Процесс создания лог-файла запущен")
    @PostMapping
    public ResponseEntity<String> createLogFile() {
        String taskId = logService.startLogFileCreation();
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body("Task started. ID: " + taskId);
    }

    @Operation(
            summary = "Проверить статус создания лог-файла",
            responses = {
                @ApiResponse(responseCode = "200", description = "Статус найден"),
                @ApiResponse(responseCode = "404", description = "ID не найден")
            }
    )
    @GetMapping("/{id}/status")
    public ResponseEntity<String> getStatus(@PathVariable String id) {
        Optional<String> status = logService.getTaskStatus(id);
        return status.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No task found with ID: " + id));
    }

    @Operation(
            summary = "Получить лог-файл по ID задачи",
            responses = {
                @ApiResponse(responseCode = "200", description = "Файл возвращён"),
                @ApiResponse(responseCode = "404", description = "Файл не найден или ещё не готов")
            }
    )
    public ResponseEntity<Resource> getLogFile(@PathVariable String id) {
        Optional<Path> filePath = logService.getLogFile(id);

        if (filePath.isEmpty() || !Files.exists(filePath.get())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Path path = filePath.get();
        Resource resource = new FileSystemResource(path);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + path.getFileName() + "\"")
                .body(resource);
    }

    @Operation(
            summary = "Получить логи по дате из сгенерированного файла",
            description = "Поиск логов по дате в файле, созданном по ID задачи",
            responses = {
                @ApiResponse(responseCode = "200", description = "Логи найдены"),
                @ApiResponse(responseCode = "404", description = "Файл не найден")
            }
    )
    @GetMapping("/{id}/logs/{date}")
    public ResponseEntity<String> getLogsFromGeneratedFile(
            @PathVariable String id,
            @PathVariable String date
    ) {
        Optional<String> result = logService.getLogsByDateFromGeneratedFile(id, date);

        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Log file not found for ID: " + id);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(result.get());
    }

}
