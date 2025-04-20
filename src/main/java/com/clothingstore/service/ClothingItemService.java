package com.clothingstore.service;

import com.clothingstore.cache.CacheService;
import com.clothingstore.model.ClothingItem;
import com.clothingstore.repository.ClothingItemRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ClothingItemService {

    private static final Logger logger = LoggerFactory.getLogger(ClothingItemService.class);

    private static final String CACHE_ALL_ITEMS = "all_items";
    private static final String FETCHING_FROM_CACHE = "Fetching '{}' from cache...";
    private static final String FETCHING_FROM_DB = "Fetching '{}' from database"
            + " and storing in cache...";

    private final ClothingItemRepository clothingItemRepository;
    private final CacheService cacheService;

    public ClothingItemService(ClothingItemRepository clothingItemRepository,
                               CacheService cacheService) {
        this.clothingItemRepository = clothingItemRepository;
        this.cacheService = cacheService;
    }

    public List<ClothingItem> saveBulkItems(List<ClothingItem> clothingItems) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        List<ClothingItem> validItems = clothingItems.stream()
                .filter(Objects::nonNull)
                .filter(item -> {
                    Set<ConstraintViolation<ClothingItem>> violations = validator.validate(item);
                    if (!violations.isEmpty()) {
                        logger.warn("Item validation failed: {}", violations);
                        return false;
                    }
                    return true;
                })
                .toList();  // Используем Stream.toList() вместо collect(Collectors.toList())

        List<ClothingItem> savedItems = clothingItemRepository.saveAll(validItems);
        cacheService.clear();

        logger.info("Saved {}/{} items ({} failed validation)",
                savedItems.size(),
                clothingItems.size(),
                clothingItems.size() - savedItems.size());

        return savedItems;
    }

    public List<ClothingItem> getAllItems() {
        if (cacheService.contains(CACHE_ALL_ITEMS)) {
            logger.info(FETCHING_FROM_CACHE, CACHE_ALL_ITEMS);
            return (List<ClothingItem>) cacheService.get(CACHE_ALL_ITEMS);
        }

        List<ClothingItem> items = clothingItemRepository.findAll();
        cacheService.put(CACHE_ALL_ITEMS, items);
        logger.info(FETCHING_FROM_DB, CACHE_ALL_ITEMS);
        return items;
    }

    public Optional<ClothingItem> getItemById(Long id) {
        String cacheKey = "item_" + id;
        if (cacheService.contains(cacheKey)) {
            logger.info(FETCHING_FROM_CACHE, cacheKey);
            return Optional.of((ClothingItem) cacheService.get(cacheKey));
        }

        Optional<ClothingItem> item = clothingItemRepository.findById(id);
        item.ifPresent(value -> {
            cacheService.put(cacheKey, value);
            logger.info(FETCHING_FROM_DB, cacheKey);
        });
        return item;
    }

    public List<ClothingItem> getItemsByName(String name) {
        String cacheKey = "items_by_name_" + name.toLowerCase();
        if (cacheService.contains(cacheKey)) {
            logger.info(FETCHING_FROM_CACHE, cacheKey);
            return (List<ClothingItem>) cacheService.get(cacheKey);
        }

        List<ClothingItem> items = clothingItemRepository.findByNameContainingIgnoreCase(name);
        cacheService.put(cacheKey, items);
        logger.info(FETCHING_FROM_DB, cacheKey);
        return items;
    }

    public List<ClothingItem> getItemsByNameAndRating(String name, int rating) {
        String cacheKey = "items_by_name_and_rating_" + name.toLowerCase() + "_" + rating;

        if (cacheService.contains(cacheKey)) {
            logger.info(FETCHING_FROM_CACHE, cacheKey);
            return (List<ClothingItem>) cacheService.get(cacheKey);
        }

        List<ClothingItem> items = clothingItemRepository
                .findAllByNameAndReviewRatingGreaterThanEqualNative(name, rating);
        cacheService.put(cacheKey, items);
        logger.info(FETCHING_FROM_DB, cacheKey);
        return items;
    }

    public List<ClothingItem> getItemsByRating(int rating) {
        String cacheKey = "items_by_rating_" + rating;
        if (cacheService.contains(cacheKey)) {
            logger.info(FETCHING_FROM_CACHE, cacheKey);
            return (List<ClothingItem>) cacheService.get(cacheKey);
        }

        List<ClothingItem> items = clothingItemRepository
                .findAllByReviewRatingGreaterThanEqual(rating);
        cacheService.put(cacheKey, items);
        logger.info(FETCHING_FROM_DB, cacheKey);
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
