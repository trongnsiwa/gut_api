package com.ecommerce.gut.service;

import java.util.UUID;
import com.ecommerce.gut.entity.Cart;
import com.ecommerce.gut.exception.CreateDataFailException;
import com.ecommerce.gut.exception.DeleteDataFailException;
import com.ecommerce.gut.exception.UpdateDataFailException;

public interface CartService {
  
  boolean addItemToCart(UUID userId, Long productId, Long colorId, Long sizeId) throws CreateDataFailException;
  
  boolean updateItemQuantity(UUID userId, Long productId, Integer amount) throws UpdateDataFailException;

  boolean removeItem(UUID userId, Long productId) throws DeleteDataFailException;

  boolean clearCart(UUID userId) throws UpdateDataFailException;

  Cart getCartByUserId(UUID userId);

}
