package com.ecommerce.gut.service;

import java.util.Collection;
import java.util.Set;

import com.ecommerce.gut.entity.Product;
import com.ecommerce.gut.entity.ProductImage;
import org.springframework.http.ResponseEntity;

public interface ProductService {
  
  ResponseEntity<?> getProductsByCategoryIdPerPage(Long categoryId, int pageNumber, int pageSize);

  Product getProductDetail(Long id);

  ResponseEntity<?> addProductToCategory(Product product, Long categoryId);

  ResponseEntity<?> updateProduct(Product product, Long id, Long categoryId);

  ResponseEntity<?> deleteProduct(Long id);

  ResponseEntity<?> replaceImagesOfProduct(Collection<ProductImage> images, Long id, Long productId);

  ResponseEntity<?> replaceColorsOfProduct(Long productId, Set<Integer> colors);

}
