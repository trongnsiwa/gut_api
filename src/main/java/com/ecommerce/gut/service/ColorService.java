package com.ecommerce.gut.service;

import com.ecommerce.gut.entity.Color;
import org.springframework.http.ResponseEntity;

public interface ColorService {

  Color getColorById(Integer id);
  
  ResponseEntity<?> addColor(Color color);

  ResponseEntity<?> updateColor(Color color, Integer id);

  ResponseEntity<?> deleteColor(Integer id);

}
