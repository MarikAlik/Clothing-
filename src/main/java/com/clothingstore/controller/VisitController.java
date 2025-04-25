package com.clothingstore.controller;

import com.clothingstore.service.VisitCountService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class VisitController {
    private final VisitCountService visitCountService;

    @Autowired
    public VisitController(VisitCountService visitCountService) {
        this.visitCountService = visitCountService;
    }

    @Operation(summary = "Увеличить счетчик посещений на сайте")
    @GetMapping("/visit")
    public String visit() {
        visitCountService.incrementVisitCount();
        return "Visit counted. Total visits: " + visitCountService.getVisitCount();
    }

    @Operation(summary = "Получить количество посещений")
    @GetMapping("/visit/count")
    public String getVisitCount() {
        return "Total visits: " + visitCountService.getVisitCount();
    }
}
