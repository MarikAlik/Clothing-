package com.clothingstore.controller;

import com.clothingstore.model.ClothingItem;
import com.clothingstore.service.ClothingItemService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clothing")
public class ClothingItemController {
    private final ClothingItemService clothingItemService;

    public ClothingItemController(ClothingItemService clothingItemService) {
        this.clothingItemService = clothingItemService;
    }

    @GetMapping
    public List<ClothingItem> getAllItems() {
        return clothingItemService.getAllItems();
    }

    @GetMapping("/{id}")
    public Optional<ClothingItem> getItemById(@PathVariable Long id) {
        return clothingItemService.getItemById(id);
    }
}
