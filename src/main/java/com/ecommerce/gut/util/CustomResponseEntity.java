package com.ecommerce.gut.util;

import java.util.Map;
import com.ecommerce.gut.payload.response.MessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class CustomResponseEntity {

  public ResponseEntity<?> generateResponseEntity(Map<String, String> messages, HttpStatus status, boolean onlyOne, String title, String message) {
    if (!onlyOne && messages.size() > 0) {
      messages.clear();
    }
    messages.put(title, message);
    return ResponseEntity.status(status).body(new MessageResponse(messages));
  }

}
