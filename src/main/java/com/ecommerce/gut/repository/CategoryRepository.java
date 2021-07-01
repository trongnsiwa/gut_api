package com.ecommerce.gut.repository;

import com.ecommerce.gut.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
  
  @Query(
    value = "SELECT EXISTS(SELECT 1 FROM product_categories pc WHERE pc.groupId = ?1)"
  )
  boolean existsByGroupId(Long groupId);

  boolean existsByName(String name);

}
