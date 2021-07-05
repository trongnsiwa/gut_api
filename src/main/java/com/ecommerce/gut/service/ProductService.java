package com.ecommerce.gut.service;

import com.ecommerce.gut.entity.Product;
import com.ecommerce.gut.payload.request.ImageListRequest;
import com.ecommerce.gut.payload.request.ProductRequest;
import org.springframework.http.ResponseEntity;

public interface ProductService {
  
  ResponseEntity<?> getProductsByCategoryIdPerPage(Long categoryId, int pageNumber, int pageSize, String sortBy);

  Product getProductDetail(Long id);

  ResponseEntity<?> addProductToCategory(ProductRequest productRequest, Long categoryId);

  ResponseEntity<?> updateProduct(ProductRequest productRequest, Long id, Long categoryId);

  ResponseEntity<?> deleteProduct(Long id);

  ResponseEntity<?> replaceImagesOfProduct(ImageListRequest imageListRequest, Long id);
}