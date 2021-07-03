package com.ecommerce.gut.service.impl;

import java.util.Collection;
import java.util.Set;
import com.ecommerce.gut.entity.Product;
import com.ecommerce.gut.entity.ProductImage;
import com.ecommerce.gut.service.ProductService;
import org.springframework.http.ResponseEntity;

public class ProductServiceImpl implements ProductService  {

  @Override
  public ResponseEntity<?> getProductsByCategoryIdPerPage(Long categoryId, int pageNumber,
      int pageSize) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Product getProductDetail(Long id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ResponseEntity<?> addProductToCategory(Product product, Long categoryId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ResponseEntity<?> updateProduct(Product product, Long id, Long categoryId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ResponseEntity<?> deleteProduct(Long id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ResponseEntity<?> replaceImagesOfProduct(Collection<ProductImage> images, Long id,
      Long productId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ResponseEntity<?> replaceColorsOfProduct(Long productId, Set<Integer> colors) {
    // TODO Auto-generated method stub
    return null;
  }
  
}
