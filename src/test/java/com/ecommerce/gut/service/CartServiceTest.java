// package com.ecommerce.gut.service;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import java.util.UUID;
// import com.ecommerce.gut.exception.CreateDataFailException;
// import com.ecommerce.gut.exception.DeleteDataFailException;
// import com.ecommerce.gut.exception.UpdateDataFailException;
// import org.junit.jupiter.api.Test;
// import org.junit.runner.RunWith;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.test.context.junit4.SpringRunner;

// @RunWith(SpringRunner.class)
// @SpringBootTest
// public class CartServiceTest {
  
//   @Autowired
//   private CartService cartService;

//   @Test
//   public void testAddNewItemToCartSuccess() throws CreateDataFailException {
//     assertEquals(true, cartService.addItemToCart(UUID.fromString("628c6f73-459c-4c8d-afa5-6345b28f80ef"), 89L)); 
//   }

//   @Test
//   public void testAddExistedItemToCartSuccess() throws CreateDataFailException {
//     assertEquals(true, cartService.addItemToCart(UUID.fromString("628c6f73-459c-4c8d-afa5-6345b28f80ef"), 90L)); 
//   }

//   @Test
//   public void testUpdateItemQuantitySuccess() throws UpdateDataFailException {
//     assertEquals(true, cartService.updateItemQuantity(UUID.fromString("628c6f73-459c-4c8d-afa5-6345b28f80ef"), 90L, 3)); 
//   }

//   @Test
//   public void testRemoveItemSuccess() throws DeleteDataFailException {
//     assertEquals(true, cartService.removeItem(UUID.fromString("628c6f73-459c-4c8d-afa5-6345b28f80ef"), 90L)); 
//   }

//   @Test
//   public void testClearCartSuccess() throws UpdateDataFailException {
//     assertEquals(true, cartService.clearCart(UUID.fromString("628c6f73-459c-4c8d-afa5-6345b28f80ef"))); 
//   }

//   @Test
//   public void testGetCartByUserIdSuccess() throws UpdateDataFailException {
//     assertNotNull(cartService.getCartByUserId(UUID.fromString("628c6f73-459c-4c8d-afa5-6345b28f80ef"))); 
//   }

// }
