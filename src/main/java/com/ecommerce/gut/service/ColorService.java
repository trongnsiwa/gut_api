package com.ecommerce.gut.service;

import com.ecommerce.gut.entity.Color;
import org.springframework.http.ResponseEntity;

public interface ColorService {

  Color getColorById(Long id);
  
  ResponseEntity<?> addColor(Color color);

  ResponseEntity<?> updateColor(Color color, Long id);

  ResponseEntity<?> deleteColor(Long id);

}
