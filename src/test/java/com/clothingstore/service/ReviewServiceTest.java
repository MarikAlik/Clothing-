package com.clothingstore.service;

import com.clothingstore.exception.ResourceNotFoundException;
import com.clothingstore.model.ClothingItem;
import com.clothingstore.model.Review;
import com.clothingstore.repository.ClothingItemRepository;
import com.clothingstore.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ClothingItemRepository clothingItemRepository;

    @InjectMocks
    private ReviewService reviewService;

    private ClothingItem clothingItem;
    private Review validReview;
    private Review invalidReview;

    @BeforeEach
    void setUp() {
        clothingItem = new ClothingItem(1L, "T-Shirt", "M", 19.99);

        validReview = new Review("testUser", "Great product", 5);
        validReview.setClothingItem(clothingItem);

        invalidReview = new Review();
    }

    @Test
    void saveReview_shouldSaveWhenClothingItemExists() {
        // Arrange
        when(clothingItemRepository.existsById(1L)).thenReturn(true);
        when(reviewRepository.save(validReview)).thenReturn(validReview);

        // Act
        Review result = reviewService.saveReview(validReview);

        // Assert
        assertEquals(validReview, result);
        verify(reviewRepository).save(validReview);
    }

    @Test
    void saveReview_shouldThrowExceptionWhenClothingItemIsNull() {
        // Arrange
        invalidReview.setClothingItem(null);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            reviewService.saveReview(invalidReview);
        });

        verify(reviewRepository, never()).save(any());
    }

    @Test
    void saveReview_shouldThrowExceptionWhenClothingItemIdIsNull() {
        // Arrange
        clothingItem.setId(null);
        invalidReview.setClothingItem(clothingItem);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            reviewService.saveReview(invalidReview);
        });

        verify(reviewRepository, never()).save(any());
    }

    @Test
    void saveReview_shouldThrowExceptionWhenClothingItemNotFound() {
        // Arrange
        when(clothingItemRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            reviewService.saveReview(validReview);
        });

        verify(reviewRepository, never()).save(any());
    }

    @Test
    void getReviewsByClothingItemId_shouldReturnReviews() {
        // Arrange
        List<Review> expectedReviews = Collections.singletonList(validReview);
        when(reviewRepository.findByClothingItemId(1L)).thenReturn(expectedReviews);

        // Act
        List<Review> result = reviewService.getReviewsByClothingItemId(1L);

        // Assert
        assertEquals(expectedReviews, result);
        assertEquals(1, result.size());
    }

    @Test
    void getReviewsByClothingItemId_shouldReturnEmptyListWhenNoReviews() {
        // Arrange
        when(reviewRepository.findByClothingItemId(1L)).thenReturn(Collections.emptyList());

        // Act
        List<Review> result = reviewService.getReviewsByClothingItemId(1L);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void getReviewById_shouldReturnReviewWhenExists() {
        // Arrange
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(validReview));

        // Act
        Optional<Review> result = reviewService.getReviewById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(validReview, result.get());
    }

    @Test
    void getReviewById_shouldReturnEmptyWhenNotFound() {
        // Arrange
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<Review> result = reviewService.getReviewById(1L);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void deleteReview_shouldDeleteReview() {
        // Arrange
        doNothing().when(reviewRepository).deleteById(1L);

        // Act
        reviewService.deleteReview(1L);

        // Assert
        verify(reviewRepository).deleteById(1L);
    }

    @Test
    void saveReview_shouldThrowExceptionWhenClothingItemIsMissing() {
        // Arrange
        Review review = new Review("user", "Nice", 4);
        review.setClothingItem(null);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> reviewService.saveReview(review));
    } // add

}