package com.ecommerce.gut.exception;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDetails {
  
  private LocalDateTime datetime;
  private String message;
  private String description;

}
