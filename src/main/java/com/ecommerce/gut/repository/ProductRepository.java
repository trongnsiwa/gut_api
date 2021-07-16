package com.ecommerce.gut.repository;

import java.util.List;
import com.ecommerce.gut.entity.Category;
import com.ecommerce.gut.entity.Product;
import com.ecommerce.gut.temp.ProductTemp;
import com.ecommerce.gut.temp.SaleProductTemp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends PagingAndSortingRepository<Product, Long> {

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

    @Query(
            value = "SELECT NEW Product(p.id, p.name, p.price, p.shortDesc, p.priceSale, p.saleFromDate, p.saleToDate) "
                    + "FROM Product p "
                    + "INNER JOIN p.category c "
                    + "WHERE c = :category")
    Page<Product> getProductsByCategoryId(@Param("category") Category category, Pageable pageable);

}
