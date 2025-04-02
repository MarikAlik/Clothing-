package com.clothingstore.service;

import com.clothingstore.cache.CacheService;
import com.clothingstore.model.ClothingItem;
import com.clothingstore.repository.ClothingItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClothingItemService {

    private final ClothingItemRepository clothingItemRepository;
    private final CacheService cacheService;

    // Конструктор с инъекцией зависимостей
    public ClothingItemService(ClothingItemRepository clothingItemRepository, CacheService cacheService) {
        this.clothingItemRepository = clothingItemRepository;
        this.cacheService = cacheService;
    }

    public List<ClothingItem> getAllItems() {
        String cacheKey = "all_items";
        // Проверка наличия данных в кэше
        if (cacheService.contains(cacheKey)) {
            return (List<ClothingItem>) cacheService.get(cacheKey);
        }

        // Если данных нет в кэше, получаем их из базы данных и сохраняем в кэш
        List<ClothingItem> items = clothingItemRepository.findAll();
        cacheService.put(cacheKey, items);
        return items;
    }

    public Optional<ClothingItem> getItemById(Long id) {
        String cacheKey = "item_" + id;
        // Проверка наличия данных в кэше
        if (cacheService.contains(cacheKey)) {
            return Optional.of((ClothingItem) cacheService.get(cacheKey));
        }

        // Если данных нет в кэше, получаем их из базы данных и сохраняем в кэш
        Optional<ClothingItem> item = clothingItemRepository.findById(id);
        item.ifPresent(value -> cacheService.put(cacheKey, value));
        return item;
    }

    // Метод для поиска товаров по имени
    public List<ClothingItem> getItemsByName(String name) {
        String cacheKey = "items_by_name_" + name.toLowerCase();
        // Проверка наличия данных в кэше
        if (cacheService.contains(cacheKey)) {
            return (List<ClothingItem>) cacheService.get(cacheKey);
        }

        // Если данных нет в кэше, получаем их из базы данных и сохраняем в кэш
        List<ClothingItem> items = clothingItemRepository.findByNameContainingIgnoreCase(name);
        cacheService.put(cacheKey, items);
        return items;
    }

    // Метод для поиска товаров по имени и минимальному рейтингу отзыва
    public List<ClothingItem> getItemsByNameAndRating(String name, int rating) {
        String cacheKey = "items_by_name_and_rating_" + name.toLowerCase() + "_" + rating;
        // Проверка наличия данных в кэше
        if (cacheService.contains(cacheKey)) {
            return (List<ClothingItem>) cacheService.get(cacheKey);
        }

        // Если данных нет в кэше, получаем их из базы данных и сохраняем в кэш
        List<ClothingItem> items = clothingItemRepository.findAllByNameAndReviewRatingGreaterThanEqual(name, rating);
        cacheService.put(cacheKey, items);
        return items;
    }

    // Метод для поиска товаров по рейтингу отзыва
    public List<ClothingItem> getItemsByRating(int rating) {
        String cacheKey = "items_by_rating_" + rating;
        // Проверка наличия данных в кэше
        if (cacheService.contains(cacheKey)) {
            return (List<ClothingItem>) cacheService.get(cacheKey);
        }

        // Если данных нет в кэше, получаем их из базы данных и сохраняем в кэш
        List<ClothingItem> items = clothingItemRepository.findAllByReviewRatingGreaterThanEqual(rating);
        cacheService.put(cacheKey, items);
        return items;
    }

    public ClothingItem saveItem(ClothingItem item) {
        ClothingItem savedItem = clothingItemRepository.save(item);
        // Очищаем кэш, так как добавили новый товар
        cacheService.clear();
        return savedItem;
    }

    public void deleteItem(Long id) {
        clothingItemRepository.deleteById(id);
        // Очищаем кэш, так как удалили товар
        cacheService.clear();
    }
}
