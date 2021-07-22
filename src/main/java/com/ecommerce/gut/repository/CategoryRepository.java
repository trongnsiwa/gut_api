package com.ecommerce.gut.repository;

import java.util.Optional;
import com.ecommerce.gut.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
  
  @Query(
    value = "SELECT EXISTS(SELECT 1 FROM categories pc WHERE pc.parent_id = ?1)",
    nativeQuery = true
  )
  boolean existsByParentId(Long parentId);

  boolean existsByName(String name);

  Optional<Category> findByName(String name);

  @Query(
    value = "SELECT COUNT(c) "
          + "FROM Category c "
          + "WHERE c.parent IS NULL"
  )
  long countParentCategory();
  @Query(
    value = "SELECT COUNT(c) "
          + "FROM Category c "
          + "WHERE c.parent IS NOT NULL"
  )
  long countChildCategory();

  @Query(
    value = "SELECT COUNT(c) "
          + "FROM Category c "
          + "WHERE c.parent IS NOT NULL AND UPPER(c.name) LIKE CONCAT('%',UPPER(:name),'%')"
  )
  long countChildCategoryByName(@Param("name") String name);

  @Query(
    value = "SELECT COUNT(c) "
          + "FROM Category c "
          + "WHERE c.parent IS NULL AND UPPER(c.name) LIKE CONCAT('%',UPPER(:name),'%')"
  )
  long countParentCategoryByName(@Param("name") String name);

  @Query(
    value = "SELECT c "
          + "FROM Category c "
          + "WHERE c.parent IS NULL"
  )
  Page<Category> getParentCategoryPerPage(Pageable pageable);

  @Query(
    value = "SELECT c "
          + "FROM Category c "
          + "WHERE c.parent IS NOT NULL"
  )
  Page<Category> getChildCategoryPerPage(Pageable pageable);

  @Query(
    value = "SELECT c "
          + "FROM Category c "
          + "WHERE c.parent IS NULL AND UPPER(c.name) LIKE CONCAT('%',UPPER(:name),'%')"
  )
  Page<Category> findByParentName(@Param("name") String name, Pageable pageable);

  @Query(
    value = "SELECT c "
          + "FROM Category c "
          + "WHERE c.parent IS NOT NULL AND UPPER(c.name) LIKE CONCAT('%',UPPER(:name),'%')"
  )
  Page<Category> findByChildName(@Param("name") String name, Pageable pageable);

}
