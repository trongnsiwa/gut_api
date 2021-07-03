package com.ecommerce.gut.service;

import org.springframework.http.ResponseEntity;

public interface HomeService {
  
  ResponseEntity<?> getNewProducts(int size);

  ResponseEntity<?> getLimitedProducts(int size);

}
