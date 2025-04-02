package com.clothingstore.repository;

import com.clothingstore.model.ClothingItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClothingItemRepository extends JpaRepository<ClothingItem, Long> {

    // Метод для поиска по имени (без учета регистра)
    List<ClothingItem> findByNameContainingIgnoreCase(String name);

    // JPQL запрос для фильтрации по рейтингу отзыва
    @Query("SELECT c FROM ClothingItem c JOIN c.reviews r WHERE r.rating >= :rating")
    List<ClothingItem> findAllByReviewRatingGreaterThanEqual(int rating);

    // JPQL запрос для фильтрации по имени товара и минимальному рейтингу отзыва
    @Query("SELECT c FROM ClothingItem c JOIN c.reviews r WHERE c.name LIKE %:name% AND r.rating >= :rating")
    List<ClothingItem> findAllByNameAndReviewRatingGreaterThanEqual(String name, int rating);

    // Native SQL запрос для фильтрации по имени товара и минимальному рейтингу отзыва
    @Query(value = "SELECT * FROM product p INNER JOIN review r ON p.id = r.product_id WHERE p.name LIKE %:name% AND r.rating >= :rating", nativeQuery = true)
    List<ClothingItem> findAllByNameAndReviewRatingGreaterThanEqualNative(String name, int rating);
}
