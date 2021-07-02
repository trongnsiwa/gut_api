package com.ecommerce.gut.exception;

public class ItemNotFoundException extends RuntimeException {
  
  private static final long serialVersionUID = 1L;

  public ItemNotFoundException(String item) {
    super(item + " is not found.");
  }

}
