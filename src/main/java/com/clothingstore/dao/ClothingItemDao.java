package com.clothingstore.dao;

import com.clothingstore.model.ClothingItem;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

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

    public List<ClothingItem> getItemsByName(String name) {
        return items.stream()
                .filter(item -> item.getName().toLowerCase().startsWith(name.toLowerCase()))
                .toList();
    }
}
