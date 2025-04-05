package com.clothingstore.service;

import com.clothingstore.cache.CacheService;
import com.clothingstore.model.ClothingItem;
import com.clothingstore.repository.ClothingItemRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ClothingItemService {

    private final ClothingItemRepository clothingItemRepository;
    private final CacheService cacheService;

    public ClothingItemService(ClothingItemRepository clothingItemRepository,
                               CacheService cacheService) {
        this.clothingItemRepository = clothingItemRepository;
        this.cacheService = cacheService;
    }

    public List<ClothingItem> getAllItems() {
        String cacheKey = "all_items";
        if (cacheService.contains(cacheKey)) {
            System.out.println("Fetching 'all_items' from cache...");
            return (List<ClothingItem>) cacheService.get(cacheKey);
        }


        List<ClothingItem> items = clothingItemRepository.findAll();
        cacheService.put(cacheKey, items);
        System.out.println("Fetching 'all_items' from database and storing in cache...");
        return items;
    }

    public Optional<ClothingItem> getItemById(Long id) {
        String cacheKey = "item_" + id;
        if (cacheService.contains(cacheKey)) {
            System.out.println("Fetching 'item_" + id + "' from cache...");
            return Optional.of((ClothingItem) cacheService.get(cacheKey));
        }

        Optional<ClothingItem> item = clothingItemRepository.findById(id);
        item.ifPresent(value -> {
            cacheService.put(cacheKey, value);
            System.out.println("Fetching 'item_" + id + "' from database and storing in cache...");
        });
        return item;
    }

    public List<ClothingItem> getItemsByName(String name) {
        String cacheKey = "items_by_name_" + name.toLowerCase();
        if (cacheService.contains(cacheKey)) {
            System.out.println("Fetching 'items_by_name_" + name.toLowerCase() + "' from cache...");
            return (List<ClothingItem>) cacheService.get(cacheKey);
        }

        List<ClothingItem> items = clothingItemRepository.findByNameContainingIgnoreCase(name);
        cacheService.put(cacheKey, items);
        System.out.println("Fetching 'items_by_name_" + name.toLowerCase()
                + "' from database and storing in cache...");
        return items;
    }

    public List<ClothingItem> getItemsByNameAndRating(String name, int rating) {
        String cacheKey = "items_by_name_and_rating_" + name.toLowerCase() + "_" + rating;
        if (cacheService.contains(cacheKey)) {
            System.out.println("Fetching 'items_by_name_and_rating_" + name.toLowerCase()
                    + "_" + rating + "' from cache...");
            return (List<ClothingItem>) cacheService.get(cacheKey);
        }

        List<ClothingItem> items = clothingItemRepository
                .findAllByNameAndReviewRatingGreaterThanEqual(name, rating);
        cacheService.put(cacheKey, items);
        System.out.println("Fetching 'items_by_name_and_rating_" + name.toLowerCase()
                + "_" + rating + "' from database and storing in cache...");
        return items;
    }

    public List<ClothingItem> getItemsByRating(int rating) {
        String cacheKey = "items_by_rating_" + rating;
        if (cacheService.contains(cacheKey)) {
            System.out.println("Fetching 'items_by_rating_" + rating + "' from cache...");
            return (List<ClothingItem>) cacheService.get(cacheKey);
        }

        List<ClothingItem> items = clothingItemRepository
                .findAllByReviewRatingGreaterThanEqual(rating);
        cacheService.put(cacheKey, items);
        System.out.println("Fetching 'items_by_rating_" + rating
                + "' from database and storing in cache...");
        return items;
    }

    public ClothingItem saveItem(ClothingItem item) {
        ClothingItem savedItem = clothingItemRepository.save(item);
        cacheService.clear();
        return savedItem;
    }

    public void deleteItem(Long id) {
        clothingItemRepository.deleteById(id);
        cacheService.clear();
    }
}
