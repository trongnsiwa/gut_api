package com.ecommerce.gut.repository;

import java.util.Collection;
import com.ecommerce.gut.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
  
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

}
