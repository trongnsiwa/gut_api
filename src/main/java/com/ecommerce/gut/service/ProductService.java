package com.ecommerce.gut.service;

import com.ecommerce.gut.dto.ImageListDTO;
import com.ecommerce.gut.dto.ProductDTO;
import com.ecommerce.gut.entity.Product;
import org.springframework.http.ResponseEntity;

public interface ProductService {
  
  ResponseEntity<?> getProductsByCategoryIdPerPage(Long categoryId, int pageNumber, int pageSize, String sortBy);

  Product getProductDetail(Long id);

  ResponseEntity<?> addProductToCategory(ProductDTO productRequest, Long categoryId);

  ResponseEntity<?> updateProduct(ProductDTO productRequest, Long id, Long categoryId);

  ResponseEntity<?> deleteProduct(Long id);

  ResponseEntity<?> replaceImagesOfProduct(ImageListDTO imageListRequest, Long id);
}
