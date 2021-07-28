package com.ecommerce.gut.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.ecommerce.gut.entity.Cart;
import com.ecommerce.gut.entity.CartItem;
import com.ecommerce.gut.entity.Product;
import com.ecommerce.gut.entity.User;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CartRepositoryTest {
  
  @Autowired
  private CartRepository cartRepository;

  @Autowired
  private CartItemRepository cartItemRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ProductRepository productRepository;

  @Test
  public void testAddItemToNotExistedCartSuccess() {
    User user = userRepository.findByEmail("trong@gmail.com").get();
    Product product = productRepository.findById(89L).get();
    Cart cart = new Cart();
    cart.setUser(user);

    CartItem cartItem = new CartItem();
    cartItem.setProduct(product);
    cartItem.setAmount(2);
    cartItem.setPrice(product.getPriceSale() > 0 ? product.getPriceSale() : product.getPrice());
    cartItem.setCart(cart);

    cart.getCartItems().add(cartItem);
    assertNotNull(cartRepository.save(cart));
  }

  @Test
  public void testAddExistedItemToExistedCartSuccess() {
    User user = userRepository.findByEmail("trong@gmail.com").get();
    Product product = productRepository.findById(89L).get();
    Cart cart = cartRepository.findByUser(user).get();

    List<CartItem> items = cart.getCartItems().stream().filter(item -> item.getProduct().getId() == product.getId()).collect(Collectors.toList());

    CartItem cartItem = items.get(0);
    cartItem.setProduct(product);
    cartItem.setAmount(cartItem.getAmount() + 1);
    cartItem.setPrice(product.getPriceSale() > 0 ? product.getPriceSale() : product.getPrice());

    cart.getCartItems().add(cartItem);
    cart.setUpdatedDate(LocalDateTime.now());

    assertNotNull(cartRepository.save(cart));
  }

  @Test
  public void testAddNewItemToExistedCartSuccess() {
    User user = userRepository.findByEmail("trong@gmail.com").get();
    Product product = productRepository.findById(90L).get();
    Cart cart = cartRepository.findByUser(user).get();

    CartItem cartItem = new CartItem();

    cartItem.setProduct(product);
    cartItem.setAmount(cartItem.getAmount() + 1);
    cartItem.setPrice(product.getPriceSale() > 0 ? product.getPriceSale() : product.getPrice());
    cartItem.setCart(cart);

    cart.getCartItems().add(cartItem);
    cart.setUpdatedDate(LocalDateTime.now());

    assertNotNull(cartRepository.save(cart));
  }

  @Test
  public void testFindCartByUserIdSuccess() {
    User user = userRepository.findById(UUID.fromString("628c6f73-459c-4c8d-afa5-6345b28f80ef")).get();
    Cart cart = cartRepository.findByUser(user).get();
    List<CartItem> cartItems = cart.getCartItems();

    assertNotNull(cart);
    assertEquals(true, cartItems.size() > 0);
  }

  @Test
  public void testUpdateItemQuantitySuccess() {
    User user = userRepository.findByEmail("trong@gmail.com").get();
    Cart cart = cartRepository.findByUser(user).get();
    Long productId = 89L;
    int quantity = 2;

    CartItem existedItem = cart.getCartItems().stream()
        .filter(item -> item.getProduct().getId() == productId)
        .collect(Collectors.toList())
        .get(0);
    existedItem.setAmount(quantity);

    cart.getCartItems().add(existedItem);
    cart.setUpdatedDate(LocalDateTime.now());

    assertNotNull(cartRepository.save(cart));
  }

  @Test
  public void testRemoveItemSuccess() {
    User user = userRepository.findByEmail("trong@gmail.com").get();
    Cart cart = cartRepository.findByUser(user).get();
    Long productId = 89L;

    CartItem existedItem = cart.getCartItems().stream()
        .filter(item -> item.getProduct().getId() == productId)
        .collect(Collectors.toList())
        .get(0);
    existedItem.setCart(null);

    cart.getCartItems().removeIf(item -> item.getProduct().getId() == productId);
    cart.setUpdatedDate(LocalDateTime.now());
    cartRepository.save(cart);

    assertNull(cartItemRepository.findById(existedItem.getId()).orElse(null));
  }

  @Test
  public void testClearCartSuccess() {
    User user = userRepository.findByEmail("trong@gmail.com").get();
    Cart cart = cartRepository.findByUser(user).get();
    cart.getCartItems().clear();
    cart.setUpdatedDate(LocalDateTime.now());
    
    assertNotNull(cartRepository.save(cart));
  }

}
