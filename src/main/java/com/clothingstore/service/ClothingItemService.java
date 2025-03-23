package com.clothingstore.service;

import com.clothingstore.model.ClothingItem;
import com.clothingstore.repository.ClothingItemRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ClothingItemService {

    private final ClothingItemRepository clothingItemRepository;

    public ClothingItemService(ClothingItemRepository clothingItemRepository) {
        this.clothingItemRepository = clothingItemRepository;
    }

    public List<ClothingItem> getAllItems() {
        return clothingItemRepository.findAll();
    }

    public Optional<ClothingItem> getItemById(Long id) {
        return clothingItemRepository.findById(id);
    }

    public List<ClothingItem> getItemsByName(String name) {
        return clothingItemRepository.findByNameContainingIgnoreCase(name);
    }

    public ClothingItem saveItem(ClothingItem item) {
        return clothingItemRepository.save(item);
    }

    public void deleteItem(Long id) {
        clothingItemRepository.deleteById(id);
    }
}
