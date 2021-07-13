package com.ecommerce.gut.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class RestrictDataException extends Exception {
 
  private static final long serialVersionUID = 1L;

  public RestrictDataException(String message) {
    super(message);
  }

}
