package com.clothingstore.service;

import com.clothingstore.cache.CacheService;
import com.clothingstore.model.ClothingItem;
import com.clothingstore.repository.ClothingItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClothingItemServiceTest {

    @Mock
    private ClothingItemRepository clothingItemRepository;

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private ClothingItemService clothingItemService;

    private ClothingItem validItem;
    private ClothingItem invalidItem;

    @BeforeEach
    void setUp() {
        validItem = new ClothingItem(1L, "Valid T-Shirt", "M", 19.99);
        invalidItem = new ClothingItem(2L, "", "L", -10.0); // Невалидный
    }

    @Test
    void saveBulkItems_shouldSaveOnlyValidItems() {
        // Arrange
        List<ClothingItem> inputItems = Arrays.asList(validItem, invalidItem, null);
        when(clothingItemRepository.saveAll(anyList())).thenReturn(Collections.singletonList(validItem));

        // Act
        List<ClothingItem> result = clothingItemService.saveBulkItems(inputItems);

        // Assert
        assertEquals(1, result.size());
        assertEquals(validItem, result.get(0));
        verify(clothingItemRepository).saveAll(anyList());
        verify(cacheService).clear();
    }

    @Test
    void getAllItems_shouldReturnFromCacheWhenAvailable() {
        // Arrange
        List<ClothingItem> cachedItems = Collections.singletonList(validItem);
        when(cacheService.contains("all_items")).thenReturn(true);
        when(cacheService.get("all_items")).thenReturn(cachedItems);

        // Act
        List<ClothingItem> result = clothingItemService.getAllItems();

        // Assert
        assertEquals(cachedItems, result);
        verify(clothingItemRepository, never()).findAll();
    }

    @Test
    void getAllItems_shouldFetchFromDbAndCacheWhenNotInCache() {
        // Arrange
        List<ClothingItem> dbItems = Collections.singletonList(validItem);
        when(cacheService.contains("all_items")).thenReturn(false);
        when(clothingItemRepository.findAll()).thenReturn(dbItems);

        // Act
        List<ClothingItem> result = clothingItemService.getAllItems();

        // Assert
        assertEquals(dbItems, result);
        verify(cacheService).put("all_items", dbItems);
    }

    @Test
    void getItemById_shouldReturnFromCacheWhenAvailable() {
        // Arrange
        String cacheKey = "item_1";
        when(cacheService.contains(cacheKey)).thenReturn(true);
        when(cacheService.get(cacheKey)).thenReturn(validItem);

        // Act
        Optional<ClothingItem> result = clothingItemService.getItemById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(validItem, result.get());
        verify(clothingItemRepository, never()).findById(any());
    }

    @Test
    void getItemById_shouldFetchFromDbAndCacheWhenNotInCache() {
        // Arrange
        String cacheKey = "item_1";
        when(cacheService.contains(cacheKey)).thenReturn(false);
        when(clothingItemRepository.findById(1L)).thenReturn(Optional.of(validItem));

        // Act
        Optional<ClothingItem> result = clothingItemService.getItemById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(validItem, result.get());
        verify(cacheService).put(cacheKey, validItem);
    }

    @Test
    void getItemById_shouldReturnEmptyWhenNotFound() {
        // Arrange
        when(cacheService.contains("item_1")).thenReturn(false);
        when(clothingItemRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<ClothingItem> result = clothingItemService.getItemById(1L);

        // Assert
        assertFalse(result.isPresent());
        verify(cacheService, never()).put(any(), any());
    }

    @Test
    void getItemsByName_shouldReturnCachedResults() {
        // Arrange
        String name = "shirt";
        String cacheKey = "items_by_name_" + name;
        List<ClothingItem> cachedItems = Collections.singletonList(validItem);

        when(cacheService.contains(cacheKey)).thenReturn(true);
        when(cacheService.get(cacheKey)).thenReturn(cachedItems);

        // Act
        List<ClothingItem> result = clothingItemService.getItemsByName(name);

        // Assert
        assertEquals(cachedItems, result);
        verify(clothingItemRepository, never()).findByNameContainingIgnoreCase(any());
    }

    @Test
    void saveItem_shouldSaveAndClearCache() {
        // Arrange
        when(clothingItemRepository.save(validItem)).thenReturn(validItem);

        // Act
        ClothingItem result = clothingItemService.saveItem(validItem);

        // Assert
        assertEquals(validItem, result);
        verify(cacheService).clear();
    }

    @Test
    void deleteItem_shouldDeleteAndClearCache() {
        // Act
        clothingItemService.deleteItem(1L);

        // Assert
        verify(clothingItemRepository).deleteById(1L);
        verify(cacheService).clear();
    }

    @Test
    void getItemsByRating_shouldReturnFromRepository() {
        // Arrange
        int rating = 4;
        String cacheKey = "items_by_rating_" + rating;
        List<ClothingItem> expectedItems = Collections.singletonList(validItem);

        when(cacheService.contains(cacheKey)).thenReturn(false);
        when(clothingItemRepository.findAllByReviewRatingGreaterThanEqual(rating))
                .thenReturn(expectedItems);

        // Act
        List<ClothingItem> result = clothingItemService.getItemsByRating(rating);

        // Assert
        assertEquals(expectedItems, result);
        verify(cacheService).put(cacheKey, expectedItems);
    }

    @Test
    void getItemsByName_shouldFetchFromDbAndPutToCacheWhenNotCached() {
        // Arrange
        String name = "jeans";
        String cacheKey = "items_by_name_" + name;
        List<ClothingItem> dbItems = Collections.singletonList(validItem);

        when(cacheService.contains(cacheKey)).thenReturn(false);
        when(clothingItemRepository.findByNameContainingIgnoreCase(name)).thenReturn(dbItems);

        // Act
        List<ClothingItem> result = clothingItemService.getItemsByName(name);

        // Assert
        assertEquals(dbItems, result);
        verify(cacheService).put(cacheKey, dbItems);
    }

    @Test
    void getItemsByRating_shouldReturnFromCacheWhenAvailable() {
        // Arrange
        int rating = 5;
        String cacheKey = "items_by_rating_" + rating;
        List<ClothingItem> cachedItems = Collections.singletonList(validItem);

        when(cacheService.contains(cacheKey)).thenReturn(true);
        when(cacheService.get(cacheKey)).thenReturn(cachedItems);

        // Act
        List<ClothingItem> result = clothingItemService.getItemsByRating(rating);

        // Assert
        assertEquals(cachedItems, result);
        verify(clothingItemRepository, never()).findAllByReviewRatingGreaterThanEqual(anyInt());
    }

}