package com.clothingstore.controller;

import com.clothingstore.model.ClothingItem;
import com.clothingstore.service.ClothingItemService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;


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
    public ResponseEntity<ClothingItem> getItemById(@PathVariable Long id) {
        ClothingItem item = clothingItemService.getItemById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "not found"));
        return ResponseEntity.ok(item);
    }

    @GetMapping("/search")
    public List<ClothingItem> getItemsByName(@RequestParam String name) {
        List<ClothingItem> items = clothingItemService.getItemsByName(name);
        if (items.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No items found " + name);
        }
        return items;
    }
}
