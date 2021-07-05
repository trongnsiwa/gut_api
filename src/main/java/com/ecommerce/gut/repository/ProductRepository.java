package com.ecommerce.gut.repository;

import java.util.Collection;
import com.ecommerce.gut.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends PagingAndSortingRepository<Product, Long> {
  
  @Query(
    value = "SELECT product_id, product_name, price, quantity, "
          + "short_desc, updated_date " 
          + "FROM products "
          + "WHERE brand_new = true "
          + "ORDER BY updated_date DESC "
          + "LIMIT ?1",
    nativeQuery = true
  )
  Collection<Product> getNewProducts(int size);

  @Query(
    value = "SELECT product_id, product_name, price, quantity, "
          + "short_desc, updated_date " 
          + "FROM products "
          + "WHERE limited = true "
          + "ORDER BY updated_date DESC "
          + "LIMIT ?1",
    nativeQuery = true
  )
  Collection<Product> getLimitedProducts(int size);

  @Query(
    value = "SELECT product_id, product_name, price, quantity, "
          + "short_desc, updated_date " 
          + "FROM products "
          + "WHERE category_id = ?{categoryId} "
          + "ORDER BY ?{#pageable}",
    countQuery = "SELECT count(*) "
               + "FROM products "
               + "WHERE category_id = ?{categoryId}",
    nativeQuery = true
  )
  Page<Product> getProductsByCategoryId(Long categoryId, Pageable pageable);

}
