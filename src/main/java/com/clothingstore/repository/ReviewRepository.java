package com.clothingstore.repository;

import com.clothingstore.model.Review;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Получить все отзывы для конкретного товара
    List<Review> findByClothingItemId(Long clothingItemId);

    // Проверка: существует ли уже отзыв от пользователя на товар
    boolean existsByUsernameAndClothingItemId(String username, Long clothingItemId);
}
