package com.ecommerce.gut.repository;

import java.util.List;
import java.util.Set;
import com.ecommerce.gut.entity.Product;
import com.ecommerce.gut.entity.ColorSize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ColorSizeRepository extends JpaRepository<ColorSize, Long> {
  
  @Query(
    value = "SELECT DISTINCT(color_id) " 
          + "FROM color_sizes "
          + "WHERE product_id = ?1",
    nativeQuery = true
  )
  List<Long> findColorsByProductId(Long productId); 

  @Query(
    value = "SELECT c " 
          + "FROM ColorSize c "
          + "WHERE c.product = :product"
  )
  Set<ColorSize> findColorSizesByProductId(@Param("product") Product product); 

  @Query(
    value = "SELECT EXISTS(SELECT 1 "
                        + "FROM color_sizes "
                        + "WHERE color_id = ?1)",
    nativeQuery = true
  )
  boolean existsJoiningColor(Long colorId);

}
