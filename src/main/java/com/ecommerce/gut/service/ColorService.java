package com.ecommerce.gut.service;

import java.util.Optional;
import com.ecommerce.gut.entity.Color;
import org.springframework.http.ResponseEntity;

public interface ColorService {

  Color getColorById(Integer id);
  
  ResponseEntity<?> addColor(Color color);

  ResponseEntity<?> updateColor(Color color, Optional<Integer> id);

  ResponseEntity<?> deleteColor(Optional<Integer> id);

}
