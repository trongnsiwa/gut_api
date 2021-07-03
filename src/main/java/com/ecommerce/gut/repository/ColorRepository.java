package com.ecommerce.gut.repository;

import com.ecommerce.gut.entity.Color;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ColorRepository extends JpaRepository<Color, Integer> {
  
  boolean existsByName(String name);

  @Query(
    value = "SELECT EXISTS(SELECT 1 "
                        + "FROM product_colors pc "
                        + "WHERE pc.color_id = ?1)",
    nativeQuery = true
  )
  boolean existsJoiningColor(Integer colorId);

}
