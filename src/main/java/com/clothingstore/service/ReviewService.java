package com.clothingstore.service;

import com.clothingstore.exception.ResourceNotFoundException;
import com.clothingstore.model.Review;
import com.clothingstore.repository.ClothingItemRepository;
import com.clothingstore.repository.ReviewRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ClothingItemRepository clothingItemRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         ClothingItemRepository clothingItemRepository) {
        this.reviewRepository = reviewRepository;
        this.clothingItemRepository = clothingItemRepository;
    }

    public Review saveReview(Review review) {
        if (review.getClothingItem() == null
                || review.getClothingItem().getId() == null
                || !clothingItemRepository.existsById(review.getClothingItem().getId())) {
            throw new ResourceNotFoundException("Товар с ID "
                    +
                    (review.getClothingItem() != null ? review.getClothingItem().getId() : "null")
                    +
                    " не найден.");
        }
        return reviewRepository.save(review);
    }

    public List<Review> getReviewsByClothingItemId(Long clothingItemId) {
        return reviewRepository.findByClothingItemId(clothingItemId);
    }

    public Optional<Review> getReviewById(Long id) {
        return reviewRepository.findById(id);
    }

    public void deleteReview(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new ResourceNotFoundException("Review not found with id: " + id);
        }
        reviewRepository.deleteById(id);
    }
}