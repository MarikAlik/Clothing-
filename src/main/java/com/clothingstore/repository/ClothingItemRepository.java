package com.clothingstore.repository;

import com.clothingstore.model.ClothingItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ClothingItemRepository extends JpaRepository<ClothingItem, Long> {
    List<ClothingItem> findByNameContainingIgnoreCase(String name);
}
