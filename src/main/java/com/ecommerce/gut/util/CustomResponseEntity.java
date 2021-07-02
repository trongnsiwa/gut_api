package com.ecommerce.gut.util;

import java.util.Map;
import com.ecommerce.gut.payload.response.MessageResponse;
import com.ecommerce.gut.payload.response.MessagesResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class CustomResponseEntity {

  public ResponseEntity<?> generateMessagesResponseEntity(Map<String, String> messages, HttpStatus status, String title, String message) {
    messages.put(title, message);
    return ResponseEntity.status(status).body(new MessagesResponse(messages));
  }

  public ResponseEntity<?> generateMessageResponseEntity(String message, HttpStatus status) {
    return ResponseEntity.status(status).body(new MessageResponse(message));
  } 

}
