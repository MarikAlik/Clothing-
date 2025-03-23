package com.clothingstore.controller;

import com.clothingstore.model.ClothingItem;
import com.clothingstore.service.ClothingItemService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    public ResponseEntity<List<ClothingItem>> getAllItems() {
        List<ClothingItem> items = clothingItemService.getAllItems();
        if (items.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No items found");
        }
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClothingItem> getItemById(@PathVariable Long id) {
        ClothingItem item = clothingItemService.getItemById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Item not found with id: " + id));
        return ResponseEntity.ok(item);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ClothingItem>> getItemsByName(
            @RequestParam String name) {
        List<ClothingItem> items = clothingItemService.getItemsByName(name);
        if (items.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "No items found with name: " + name);
        }
        return ResponseEntity.ok(items);
    }

    @PostMapping
    public ResponseEntity<ClothingItem> createItem(
            @RequestBody ClothingItem clothingItem) {
        ClothingItem createdItem = clothingItemService.saveItem(clothingItem);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClothingItem> updateItem(
            @PathVariable Long id, @RequestBody ClothingItem clothingItem) {
        clothingItemService.getItemById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Item not found with id  " + id));

        clothingItem.setId(id);
        ClothingItem updatedItem = clothingItemService.saveItem(clothingItem);
        return ResponseEntity.ok(updatedItem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        clothingItemService.getItemById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Item not found with id " + id));

        clothingItemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}
