package com.clothingstore.controller;

import com.clothingstore.exception.ResourceNotFoundException;
import com.clothingstore.model.Review;
import com.clothingstore.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reviews")
@Tag(name = "Reviews", description = "Управление отзывами на товары")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    @Operation(
            summary = "Создать отзыв",
            description = "Создает новый отзыв для товара. "
                    + "Требует имя пользователя, комментарий и рейтинг."
    )
    @ApiResponse(responseCode = "201", description = "Отзыв успешно создан")
    public ResponseEntity<Review> createReview(@Valid @RequestBody Review review) {
        Review savedReview = reviewService.saveReview(review);
        return new ResponseEntity<>(savedReview, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Получить отзыв по ID",
            description = "Возвращает отзыв по заданному ID, если он существует"
    )
    @ApiResponse(responseCode = "200", description = "Отзыв найден")
    @ApiResponse(responseCode = "404", description = "Отзыв не найден")
    public ResponseEntity<Review> getReviewById(@PathVariable Long id) {
        Review review = reviewService.getReviewById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: "
                        + id));
        return ResponseEntity.ok(review);
    }

    @GetMapping("/by-item/{clothingItemId}")
    @Operation(
            summary = "Получить отзывы по товару",
            description = "Возвращает список всех отзывов, относящихся к определенному товару"
    )
    @ApiResponse(responseCode = "200",
            description = "Отзывы успешно получены")
    public ResponseEntity<List<Review>> getReviewsByClothingItemId(
            @PathVariable Long clothingItemId) {
        List<Review> reviews = reviewService.getReviewsByClothingItemId(clothingItemId);
        return ResponseEntity.ok(reviews);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Обновить отзыв",
            description = "Обновляет существующий отзыв по ID. "
                    + "Требуется имя пользователя, комментарий и рейтинг."
    )
    @ApiResponse(responseCode = "200",
            description = "Отзыв успешно обновлён")
    @ApiResponse(responseCode = "404",
            description = "Отзыв не найден")
    public ResponseEntity<Review> updateReview(@PathVariable Long id,
                                               @Valid @RequestBody Review reviewDetails) {
        Review existingReview = reviewService.getReviewById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: "
                        + id));

        existingReview.setUsername(reviewDetails.getUsername());
        existingReview.setComment(reviewDetails.getComment());
        existingReview.setRating(reviewDetails.getRating());

        Review updatedReview = reviewService.saveReview(existingReview);
        return ResponseEntity.ok(updatedReview);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Удалить отзыв",
            description = "Удаляет отзыв по ID, если он существует"
    )
    @ApiResponse(responseCode = "204", description = "Отзыв успешно удалён")
    @ApiResponse(responseCode = "404", description = "Отзыв не найден")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        Review review = reviewService.getReviewById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: "
                        + id));
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}
