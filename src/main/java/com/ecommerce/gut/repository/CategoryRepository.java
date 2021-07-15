package com.ecommerce.gut.repository;

import com.ecommerce.gut.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
  
  @Query(
    value = "SELECT EXISTS(SELECT 1 FROM categories pc WHERE pc.parent_id = ?1)",
    nativeQuery = true
  )
  boolean existsByParentId(Long parentId);

  boolean existsByName(String name);

  @Query(
    value = "SELECT pc.parent_id " +
            "FROM categories pc " +
            "WHERE pc.category_id = ?1",
    nativeQuery = true
  )
  Long getParentIdbyId(Long id);

  @Query(
    value = "SELECT c "
          + "FROM Category c "
          + "WHERE c.parent IS NULL"
  )
  Page<Category> getParentCategoryPerPage(Pageable pageable);

}
