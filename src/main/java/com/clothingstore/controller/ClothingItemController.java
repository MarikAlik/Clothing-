package com.clothingstore.controller;

import com.clothingstore.exception.ResourceNotFoundException;
import com.clothingstore.model.ClothingItem;
import com.clothingstore.service.ClothingItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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


@RestController
@RequestMapping("/api/clothing")
@Tag(name = "Clothing Items", description = "Операции с товарами одежды")
public class ClothingItemController {

    private static final String ITEM_NOT_FOUND = "Item not found with id: ";

    private final ClothingItemService clothingItemService;

    public ClothingItemController(ClothingItemService clothingItemService) {
        this.clothingItemService = clothingItemService;
    }

    @GetMapping
    @Operation(summary = "Получить все товары",
            description = "Возвращает список всех товаров одежды")
    public ResponseEntity<List<ClothingItem>> getAllItems() {
        List<ClothingItem> items = clothingItemService.getAllItems();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить товар по ID",
            description = "Возвращает товар по его уникальному идентификатору")
    @ApiResponse(responseCode = "200", description = "Товар найден")
    @ApiResponse(responseCode = "404", description = "Товар не найден")
    public ResponseEntity<ClothingItem> getItemById(
            @Parameter(description = "ID товара") @PathVariable Long id) {
        return ResponseEntity.ok(
                clothingItemService.getItemById(id)
                        .orElseThrow(() -> new ResourceNotFoundException(ITEM_NOT_FOUND + id))
        );
    }

    @GetMapping("/search")
    @Operation(summary = "Поиск товара по имени",
            description = "Возвращает товары, содержащие указанное имя")
    public ResponseEntity<List<ClothingItem>> getItemsByName(
            @Parameter(description = "Название товара") @RequestParam @NotBlank String name) {
        List<ClothingItem> items = clothingItemService.getItemsByName(name);
        if (items.isEmpty()) {
            throw new ResourceNotFoundException("No items found with name: " + name);
        }
        return ResponseEntity.ok(items);
    }

    @GetMapping("/searchByNameAndRating")
    @Operation(summary = "Поиск товара по имени и рейтингу",
            description = "Возвращает товары по совпадению имени и заданного рейтинга")
    public ResponseEntity<List<ClothingItem>> getItemsByNameAndRating(
            @Parameter(description = "Название товара") @RequestParam @NotBlank String name,
            @Parameter(description = "Рейтинг от 1 до 5")
            @RequestParam @Min(1) @Max(5) int rating) {
        List<ClothingItem> items = clothingItemService.getItemsByNameAndRating(name, rating);
        if (items.isEmpty()) {
            throw new ResourceNotFoundException("No items found with name: "
                    + name + " and rating: " + rating);
        }
        return ResponseEntity.ok(items);
    }

    @GetMapping("/searchByRating")
    @Operation(summary = "Поиск товара по рейтингу",
            description = "Возвращает товары с заданным рейтингом")
    public ResponseEntity<List<ClothingItem>> getItemsByRating(
            @Parameter(description = "Рейтинг от 1 до 5")
            @RequestParam @Min(1) @Max(5) int rating) {
        List<ClothingItem> items = clothingItemService.getItemsByRating(rating);
        if (items.isEmpty()) {
            throw new ResourceNotFoundException("No items found with rating: " + rating);
        }
        return ResponseEntity.ok(items);
    }

    @PostMapping
    @Operation(summary = "Создать новый товар",
            description = "Создает новый товар одежды с заданными параметрами")
    @ApiResponse(responseCode = "201", description = "Товар успешно создан")
    public ResponseEntity<ClothingItem> createItem(
            @Valid @RequestBody ClothingItem clothingItem) {
        ClothingItem createdItem = clothingItemService.saveItem(clothingItem);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить товар", description = "Обновляет существующий товар по ID")
    @ApiResponse(responseCode = "200", description = "Товар успешно обновлен")
    @ApiResponse(responseCode = "404", description = "Товар не найден")
    public ResponseEntity<ClothingItem> updateItem(
            @Parameter(description = "ID товара") @PathVariable Long id,
            @Valid @RequestBody ClothingItem clothingItem) {

        clothingItemService.getItemById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ITEM_NOT_FOUND + id));

        clothingItem.setId(id);
        ClothingItem updatedItem = clothingItemService.saveItem(clothingItem);
        return ResponseEntity.ok(updatedItem);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить товар", description = "Удаляет товар по заданному ID")
    @ApiResponse(responseCode = "204", description = "Товар успешно удален")
    @ApiResponse(responseCode = "404", description = "Товар не найден")
    public ResponseEntity<Void> deleteItem(
            @Parameter(description = "ID товара") @PathVariable Long id) {

        clothingItemService.getItemById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ITEM_NOT_FOUND + id));

        clothingItemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}
