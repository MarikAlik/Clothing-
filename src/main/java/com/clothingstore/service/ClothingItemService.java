package com.clothingstore.service;

import com.clothingstore.dao.ClothingItemDao;
import com.clothingstore.model.ClothingItem;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;


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

    public List<ClothingItem> getItemsByName(String name) {
        return clothingItemDao.getItemsByName(name);
    }
}
