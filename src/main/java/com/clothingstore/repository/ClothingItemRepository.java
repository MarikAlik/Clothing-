package com.clothingstore.repository;

import com.clothingstore.model.ClothingItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ClothingItemRepository extends JpaRepository<ClothingItem, Long> {

    List<ClothingItem> findByNameContainingIgnoreCase(String name);

    @Query("SELECT c FROM ClothingItem c "
            + "JOIN c.reviews r "
            + "WHERE r.rating >= :rating")
    List<ClothingItem> findAllByReviewRatingGreaterThanEqual(int rating);

    @Query(value = "SELECT p.* FROM product p "
            + "INNER JOIN review r ON p.id = r.product_id "
            + "WHERE r.rating >= :rating", nativeQuery = true)
    List<ClothingItem> findAllByReviewRatingGreaterThanEqualNative(int rating);

    @Query("SELECT c FROM ClothingItem c "
            + "JOIN c.reviews r "
            + "WHERE c.name LIKE %:name% "
            + "AND r.rating >= :rating")
    List<ClothingItem> findAllByNameAndReviewRatingGreaterThanEqual(String name, int rating);

    @Query(value = "SELECT p.* FROM product p "
            + "INNER JOIN review r ON p.id = r.product_id "
            + "WHERE p.name LIKE %:name% "
            + "AND r.rating >= :rating", nativeQuery = true)
    List<ClothingItem> findAllByNameAndReviewRatingGreaterThanEqualNative(String name, int rating);
}
