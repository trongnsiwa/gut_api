package com.ecommerce.gut.repository;

import com.ecommerce.gut.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {
  
  @Query(
    value = "SELECT EXISTS(SELECT 1 FROM categories pc WHERE pc.parent_id = ?1)",
    nativeQuery = true
  )
  boolean existsByParentId(Long parentId);

  boolean existsByName(String name);

}
