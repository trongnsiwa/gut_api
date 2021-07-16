package com.ecommerce.gut.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import com.ecommerce.gut.entity.Product;
import com.ecommerce.gut.entity.Color;
import com.ecommerce.gut.entity.ColorSize;
import com.ecommerce.gut.entity.PSize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
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
    value = "SELECT c " 
          + "FROM ColorSize c "
          + "WHERE c.product = :product AND c.color = :color AND c.size = :size"
  )
  Optional<ColorSize> getColorSizeOfProduct(@Param("product") Product product, @Param("color") Color color, @Param("size") PSize size);

  @Modifying
  @Query(
    value = "DELETE FROM ColorSize c "
          + "WHERE c.product = :product AND c.color = :color AND c.size = :size"
  )
  int deleteColorSizeOfProduct(@Param("product") Product product, @Param("color") Color color, @Param("size") PSize size);

  @Query(
    value = "SELECT EXISTS(SELECT 1 "
                        + "FROM color_sizes "
                        + "WHERE color_id = ?1)",
    nativeQuery = true
  )
  boolean existsJoiningColor(Long colorId);

}
