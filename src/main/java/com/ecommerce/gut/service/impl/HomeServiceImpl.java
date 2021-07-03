package com.ecommerce.gut.service.impl;

import com.ecommerce.gut.repository.ProductRepository;
import com.ecommerce.gut.service.HomeService;
import com.ecommerce.gut.util.CustomResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class HomeServiceImpl implements HomeService {

  @Autowired
  ProductRepository productRepository;

  @Autowired
  CustomResponseEntity customResponseEntity;

  @Override
  public ResponseEntity<?> getNewProducts(int size) {
    if (size < 0) {
      return customResponseEntity.generateMessageResponseEntity("Please give the size of products higher than 0", HttpStatus.BAD_REQUEST);
    }

    return ResponseEntity.ok(productRepository.getNewProducts(size));
  }

  @Override
  public ResponseEntity<?> getLimitedProducts(int size) {
    if (size < 0) {
      return customResponseEntity.generateMessageResponseEntity("Please give the size of products higher than 0", HttpStatus.BAD_REQUEST);
    }

    return ResponseEntity.ok(productRepository.getLimitedProducts(size));
  }
  
}
