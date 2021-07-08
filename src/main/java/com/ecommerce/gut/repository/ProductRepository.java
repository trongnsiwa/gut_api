package com.ecommerce.gut.repository;

import java.util.List;
import com.ecommerce.gut.entity.Product;
import com.ecommerce.gut.temp.ProductTemp;
import com.ecommerce.gut.temp.SaleProductTemp;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

  @Query(
      value = "SELECT NEW com.ecommerce.gut.temp.ProductTemp(p.id, p.name, p.price, p.shortDesc) "
          + "FROM Product p "
          + "WHERE p.brandNew = TRUE "
          + "ORDER BY p.updatedDate DESC")
  List<ProductTemp> getNewProducts(Pageable pageable);

  @Query(
      value = "SELECT NEW com.ecommerce.gut.temp.SaleProductTemp(p.id, p.name, p.price, p.shortDesc, p.priceSale, p.saleFromDate, p.saleToDate) "
          + "FROM Product p "
          + "WHERE p.sale = TRUE "
          + "ORDER BY p.updatedDate DESC")
  List<SaleProductTemp> getSaleProducts(Pageable pageable);

//   @Query(
//       value = "SELECT p.product_id, p.product_name, p.price, p.short_desc "
//           + "FROM products p "
//           + "WHERE p.category_id = ?1 "
//           + "ORDER BY ?#{#pageable}",
//       countQuery = "SELECT count(*) "
//           + "FROM products p "
//           + "WHERE p.category_id = ?1",
//       nativeQuery = true)
//   Page<ProductTemp> getProductsByCategoryId(Long categoryId, Pageable pageable);

}
