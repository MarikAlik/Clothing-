package com.clothingstore.controller;

import com.clothingstore.service.RequestCounterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/requests")
@Tag(name = "Request Counter", description = "Операции со счетчиком запросов")
public class RequestCounterController {
    

    private final RequestCounterService requestCounterService;

    public RequestCounterController(RequestCounterService requestCounterService) {
        this.requestCounterService = requestCounterService;
    }

    @GetMapping("/count")
    @Operation(summary = "Получить количество запросов",
            description = "Возвращает текущее количество обращений к GET /api/clothing")
    public ResponseEntity<String> getRequestCount() {
        int count = requestCounterService.getCount();
        return ResponseEntity.ok("Total requests: " + count);
    }

    @PostMapping("/reset")
    @Operation(summary = "Сбросить счетчик",
            description = "Устанавливает счетчик запросов в 0")
    public ResponseEntity<String> resetRequestCount() {
        requestCounterService.reset();
        return ResponseEntity.ok("Request count has been reset.");
    }
}
