package com.ecommerce.gut.repository;

import java.util.List;
import com.ecommerce.gut.entity.Category;
import com.ecommerce.gut.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends PagingAndSortingRepository<Product, Long> {

        @Query("SELECT p "
                + "FROM Product p "
                + "WHERE p.brandNew = TRUE "
                + "ORDER BY p.updatedDate DESC")
        List<Product> getNewProducts(Pageable pageable);

        @Query("SELECT p "                        
                + "FROM Product p "
                + "WHERE p.sale = TRUE "
                + "ORDER BY p.updatedDate DESC")
        List<Product> getSaleProducts(Pageable pageable);

        @Query(
                "SELECT p "
                + "FROM Product p "
                + "INNER JOIN p.category c "
                + "WHERE c = :category")
        Page<Product> getProductsByCategory(@Param("category") Category category, Pageable pageable);

        @Query(
                "SELECT p "
                + "FROM Product p "
                + "INNER JOIN p.category c "
                + "WHERE c.parent = :parent")
        Page<Product> getProductsByParent(@Param("parent") Category parent, Pageable pageable);

}
