package com.ecommerce.gut.repository;

import java.util.List;
import java.util.Optional;
import com.ecommerce.gut.entity.Product;
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

  @Query(
    value = "SELECT i "
          + "FROM ProductImage i "
          + "WHERE i.product = :product"
  )
  List<ProductImage> findImagesByProduct(@Param("product") Product product);

  @Query(
    value = "SELECT i "
          + "FROM ProductImage i "
          + "WHERE i.product = :product AND i.colorCode = :code"
  )
  Optional<ProductImage> findImageByProductIdAndColorCode(@Param("product") Product product, @Param("code") Long colorCode);

}
