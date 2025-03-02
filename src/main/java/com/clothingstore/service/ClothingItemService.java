package com.clothingstore.service;

import com.clothingstore.dao.ClothingItemDao;
import com.clothingstore.model.ClothingItem;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClothingItemService {
    private final ClothingItemDao clothingItemDao;

    public ClothingItemService(ClothingItemDao clothingItemDao) {
        this.clothingItemDao = clothingItemDao;
    }

    public List<ClothingItem> getAllItems() {
        return clothingItemDao.getAllItems();
    }

    public Optional<ClothingItem> getItemById(Long id) {
        return clothingItemDao.getItemById(id);
    }
}
