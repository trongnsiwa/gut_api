package com.ecommerce.gut.repository;

import java.util.List;
import com.ecommerce.gut.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

  @Modifying
  @Query(
    value = "DELETE FROM product_images "
          + "WHERE product_id = :productId",
    nativeQuery = true
  )
  int deleteAllByProductId(@Param("productId") Long productId);

  @Modifying
  @Query(
    value = "DELETE FROM product_images "
          + "WHERE image_id = :id AND product_id = :productId",
    nativeQuery = true
  )
  int deleteByIdAndProductId(@Param("id") Long id, @Param("productId") Long productId);

  @Query(
    value = "SELECT image_id, product_id, image_url, title, color_code "
          + "FROM product_images "
          + "WHERE product_id = ?1",
    nativeQuery = true
  )
  List<ProductImage> findAllImageIdsByProductId(Long productId);
}
