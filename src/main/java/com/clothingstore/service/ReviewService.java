package com.clothingstore.service;

import com.clothingstore.model.Review;
import com.clothingstore.repository.ReviewRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public Review saveReview(Review review) {
        return reviewRepository.save(review);
    }

    public List<Review> getReviewsByClothingItemId(Long clothingItemId) {
        return reviewRepository.findByClothingItemId(clothingItemId);
    }

    public Optional<Review> getReviewById(Long id) {
        return reviewRepository.findById(id);
    }

    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }
}
