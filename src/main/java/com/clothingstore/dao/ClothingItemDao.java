package com.clothingstore.dao;

import com.clothingstore.model.ClothingItem;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ClothingItemDao {
    private final List<ClothingItem> items = List.of(
            new ClothingItem(1L, "T-Shirt", "M", 19.99),
            new ClothingItem(2L, "Jeans", "L", 49.99)
    );

    public List<ClothingItem> getAllItems() {
        return items;
    }

    public Optional<ClothingItem> getItemById(Long id) {
        return items.stream().filter(item -> item.getId().equals(id)).findFirst();
    }
}
