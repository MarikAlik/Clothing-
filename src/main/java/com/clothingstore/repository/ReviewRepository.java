package com.clothingstore.repository;

import com.clothingstore.model.Review;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByClothingItemId(Long clothingItemId);

    boolean existsByUsernameAndClothingItemId(String username, Long clothingItemId);
}
