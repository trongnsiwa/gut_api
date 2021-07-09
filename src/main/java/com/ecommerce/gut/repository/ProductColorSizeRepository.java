package com.ecommerce.gut.repository;

import java.util.List;
import java.util.Set;
import com.ecommerce.gut.entity.Product;
import com.ecommerce.gut.entity.ProductColorSize;
import com.ecommerce.gut.entity.ProductColorSizeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductColorSizeRepository extends JpaRepository<ProductColorSize, ProductColorSizeId> {
  
  @Query(
    value = "SELECT DISTINCT(color_color_id) " 
          + "FROM product_color_sizes "
          + "WHERE product_product_id = ?1",
    nativeQuery = true
  )
  List<Long> findColorsByProductId(Long productId); 

  @Query(
    value = "SELECT p " 
          + "FROM ProductColorSize p "
          + "WHERE p.product = :product"
  )
  Set<ProductColorSize> findColorSizesByProductId(@Param("product") Product product); 

  @Query(
    value = "SELECT EXISTS(SELECT 1 "
                        + "FROM product_color_sizes "
                        + "WHERE color_color_id = ?1)",
    nativeQuery = true
  )
  boolean existsJoiningColor(Long colorId);

}
