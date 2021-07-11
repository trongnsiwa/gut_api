package com.ecommerce.gut.exception;

public class CustomNotFoundException extends RuntimeException {
  
  private static final long serialVersionUID = 1L;

  public CustomNotFoundException(String resource) {
    super(resource);
  }

}
