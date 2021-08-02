package com.ecommerce.gut.service.impl;

import static com.ecommerce.gut.specification.ColorSizeSpecification.colorEquals;
import static com.ecommerce.gut.specification.ColorSizeSpecification.productEquals;
import static com.ecommerce.gut.specification.ColorSizeSpecification.sizeEquals;
import static org.springframework.data.jpa.domain.Specification.where;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.ecommerce.gut.entity.Cart;
import com.ecommerce.gut.entity.CartItem;
import com.ecommerce.gut.entity.Color;
import com.ecommerce.gut.entity.ColorSize;
import com.ecommerce.gut.entity.PSize;
import com.ecommerce.gut.entity.Product;
import com.ecommerce.gut.entity.User;
import com.ecommerce.gut.exception.CreateDataFailException;
import com.ecommerce.gut.exception.DataNotFoundException;
import com.ecommerce.gut.exception.DeleteDataFailException;
import com.ecommerce.gut.exception.UpdateDataFailException;
import com.ecommerce.gut.payload.response.ErrorCode;
import com.ecommerce.gut.repository.CartRepository;
import com.ecommerce.gut.repository.ColorRepository;
import com.ecommerce.gut.repository.ColorSizeRepository;
import com.ecommerce.gut.repository.PSizeRepository;
import com.ecommerce.gut.repository.ProductRepository;
import com.ecommerce.gut.repository.UserRepository;
import com.ecommerce.gut.service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImpl implements CartService {

  private static final Logger LOGGER = LoggerFactory.getLogger(CartServiceImpl.class);

  @Autowired
  CartRepository cartRepository;

  @Autowired
  UserRepository userRepository;

  @Autowired
  ProductRepository productRepository;

  @Autowired
  ColorRepository colorRepository;

  @Autowired
  PSizeRepository pSizeRepository;

  @Autowired
  ColorSizeRepository colorSizeRepository;

  @Override
  public Cart addItemToCart(UUID userId, Long productId, Long colorId, Long sizeId,
      Integer amount)
      throws CreateDataFailException {
    try {
      Optional<User> user = userRepository.findById(userId);

      if (!user.isPresent()) {
        LOGGER.info("User {} is not found", userId);
        throw new DataNotFoundException(ErrorCode.ERR_USER_NOT_FOUND);
      }

      Product selectedProduct = productRepository.findById(productId)
          .orElseThrow(() -> {
            LOGGER.info("Product {} is not found", productId);
            return new DataNotFoundException(ErrorCode.ERR_PRODUCT_NOT_FOUND);
          });

      Cart cart = cartRepository.findByUser(user.get()).orElse(new Cart());

      List<CartItem> cartItems = cart.getCartItems()
          .stream()
          .filter(item -> item.getProduct().getId().equals(productId))
          .collect(Collectors.toList());

      CartItem cartItem = null;

      if (cartItems.isEmpty()) {
        cartItem = new CartItem();
      } else {
        cartItem = cartItems.get(0);
      }

      Color color = colorRepository.findById(colorId).get();
      PSize size = pSizeRepository.findById(sizeId).get();

      ColorSize colorSize = colorSizeRepository
          .findOne(
              where(productEquals(selectedProduct).and(colorEquals(color)).and(sizeEquals(size))))
          .get();
      int currentQuantity = colorSize.getQuantity();

      if (currentQuantity == 0) {
        LOGGER.info("This item is out of stock.");
        throw new CreateDataFailException(ErrorCode.ERR_OUT_OF_STOCK);
      } else if (currentQuantity < amount) {
        LOGGER.info("Not enough quantity to add to cart.");
        throw new CreateDataFailException(ErrorCode.ERR_CURRENT_QUANTITY_LOWER);
      }

      cartItem.setProduct(selectedProduct);
      cartItem.setAmount(cartItem.getAmount() + amount);
      cartItem.setPrice(selectedProduct.getPriceSale() > 0 ? selectedProduct.getPriceSale()
          : selectedProduct.getPrice());
      cartItem.setColorSize(colorSize);
      cartItem.setCart(cart);

      cart.getCartItems().add(cartItem);
      cart.setUser(user.get());
      cart.setUpdatedDate(LocalDateTime.now());

      Cart savedCart = cartRepository.save(cart);

      if (Objects.nonNull(savedCart)) {
        colorSize.setQuantity(currentQuantity - amount);
        colorSizeRepository.save(colorSize);
      }

      return savedCart;
    } catch (DataNotFoundException ex) {
      if (ex.getMessage().equals(ErrorCode.ERR_USER_NOT_FOUND)) {
        throw new DataNotFoundException(ErrorCode.ERR_USER_NOT_FOUND);
      } else {
        throw new DataNotFoundException(ErrorCode.ERR_PRODUCT_NOT_FOUND);
      }
    } catch (Exception ex) {
      
      String message = ex.getMessage();

      if (message.equals(ErrorCode.ERR_OUT_OF_STOCK)) {
        throw new CreateDataFailException(ErrorCode.ERR_OUT_OF_STOCK);
      } else if (message.equals(ErrorCode.ERR_CURRENT_QUANTITY_LOWER)) {
        throw new CreateDataFailException(ErrorCode.ERR_CURRENT_QUANTITY_LOWER);
      } else {
        LOGGER.info("Fail to add product {} into the cart of user {}", productId, userId);
        throw new CreateDataFailException(ErrorCode.ERR_ADD_TO_CART_FAIL);
      }
      
    }
  }

  @Override
  public Cart updateItemQuantity(UUID userId, Long productId, Integer amount)
      throws UpdateDataFailException {
    try {
      Optional<User> user = userRepository.findById(userId);

      if (!user.isPresent()) {
        LOGGER.info("User {} is not found", userId);
        throw new DataNotFoundException(ErrorCode.ERR_USER_NOT_FOUND);
      }

      Cart cart = cartRepository.findByUser(user.get())
          .orElseThrow(() -> {
            LOGGER.info("Cart of user {} is not found", userId);
            return new DataNotFoundException(ErrorCode.ERR_CART_NOT_FOUND);
          });

      if (!checkExistedItemInCart(cart, productId)) {
        LOGGER.info("Item {} in cart {} is not found", productId, cart.getId());
        throw new DataNotFoundException(ErrorCode.ERR_ITEM_CART_NOT_FOUND);
      }

      CartItem existedItem = cart.getCartItems()
          .stream()
          .filter(item -> item.getProduct().getId().equals(productId))
          .collect(Collectors.toList())
          .get(0);

      if (amount > 0) {
        ColorSize colorSize = existedItem.getColorSize();
        int currentQuantity = colorSize.getQuantity();

      if (currentQuantity == 0) {
        LOGGER.info("This item is out of stock.");
        throw new UpdateDataFailException(ErrorCode.ERR_OUT_OF_STOCK);
      } else if (currentQuantity < amount) {
        LOGGER.info("Not enough quantity to add to cart.");
        throw new UpdateDataFailException(ErrorCode.ERR_CURRENT_QUANTITY_LOWER);
      }
        
        existedItem.setAmount(amount);
        cart.getCartItems().add(existedItem);
      } else {
        cart.getCartItems().removeIf(item -> item.getProduct().getId().equals(productId));
      }

      cart.setUpdatedDate(LocalDateTime.now());

      return cartRepository.save(cart);

    } catch (DataNotFoundException ex) {

      String message = ex.getMessage();

      if (message.equals(ErrorCode.ERR_USER_NOT_FOUND)) {
        throw new DataNotFoundException(ErrorCode.ERR_USER_NOT_FOUND);
      } else if (message.equals(ErrorCode.ERR_CART_NOT_FOUND)) {
        throw new DataNotFoundException(ErrorCode.ERR_CART_NOT_FOUND);
      } else {
        throw new DataNotFoundException(ErrorCode.ERR_ITEM_CART_NOT_FOUND);
      }

    } catch (Exception ex) {

      String message = ex.getMessage();

      if (message.equals(ErrorCode.ERR_OUT_OF_STOCK)) {
        throw new UpdateDataFailException(ErrorCode.ERR_OUT_OF_STOCK);
      } else if (message.equals(ErrorCode.ERR_CURRENT_QUANTITY_LOWER)) {
        throw new UpdateDataFailException(ErrorCode.ERR_CURRENT_QUANTITY_LOWER);
      } else {
        LOGGER.info("Fail to update quantity of product {} into the cart of user {}", productId,
            userId);
        throw new UpdateDataFailException(ErrorCode.ERR_UPDATE_ITEM_QUANTITY_FAIL);
      }
    }
  }

  @Override
  public Cart removeItem(UUID userId, Long productId) throws DeleteDataFailException {
    try {
      Optional<User> user = userRepository.findById(userId);

      if (!user.isPresent()) {
        LOGGER.info("User {} is not found", userId);
        throw new DataNotFoundException(ErrorCode.ERR_USER_NOT_FOUND);
      }

      Cart cart = cartRepository.findByUser(user.get())
          .orElseThrow(() -> {
            LOGGER.info("Cart of user {} is not found", userId);
            return new DataNotFoundException(ErrorCode.ERR_CART_NOT_FOUND);
          });

      if (!checkExistedItemInCart(cart, productId)) {
        LOGGER.info("Item {} in cart {} is not found", productId, cart.getId());
        throw new DataNotFoundException(ErrorCode.ERR_ITEM_CART_NOT_FOUND);
      }

      CartItem existedItem = cart.getCartItems()
          .stream()
          .filter(item -> item.getProduct().getId().equals(productId))
          .collect(Collectors.toList())
          .get(0);

      existedItem.setCart(null);

      cart.getCartItems().removeIf(item -> item.getProduct().getId().equals(productId));

      cart.setUpdatedDate(LocalDateTime.now());

      return cartRepository.save(cart);

    } catch (DataNotFoundException ex) {

      String message = ex.getMessage();

      if (message.equals(ErrorCode.ERR_USER_NOT_FOUND)) {
        throw new DataNotFoundException(ErrorCode.ERR_USER_NOT_FOUND);
      } else if (message.equals(ErrorCode.ERR_CART_NOT_FOUND)) {
        throw new DataNotFoundException(ErrorCode.ERR_CART_NOT_FOUND);
      } else {
        throw new DataNotFoundException(ErrorCode.ERR_ITEM_CART_NOT_FOUND);
      }

    } catch (Exception ex) {
      LOGGER.info("Fail to remove product {} from the cart of user {}", productId, userId);
      throw new DeleteDataFailException(ErrorCode.ERR_ADD_TO_CART_FAIL);
    }
  }

  @Override
  public boolean clearCart(UUID userId) throws UpdateDataFailException {
    try {
      Optional<User> user = userRepository.findById(userId);

      if (!user.isPresent()) {
        LOGGER.info("User {} is not found", userId);
        throw new DataNotFoundException(ErrorCode.ERR_USER_NOT_FOUND);
      }

      Cart cart = cartRepository.findByUser(user.get())
          .orElseThrow(() -> {
            LOGGER.info("Cart of user {} is not found", userId);
            return new DataNotFoundException(ErrorCode.ERR_CART_NOT_FOUND);
          });

      cart.getCartItems().clear();
      cart.setUpdatedDate(LocalDateTime.now());

      cartRepository.save(cart);
    } catch (DataNotFoundException ex) {

      String message = ex.getMessage();

      if (message.equals(ErrorCode.ERR_USER_NOT_FOUND)) {
        throw new DataNotFoundException(ErrorCode.ERR_USER_NOT_FOUND);
      } else {
        throw new DataNotFoundException(ErrorCode.ERR_CART_NOT_FOUND);
      }

    } catch (Exception ex) {
      LOGGER.info("Fail to clear the cart of user {}", userId);
      throw new UpdateDataFailException(ErrorCode.ERR_CLEAR_CART_FAIL);
    }

    return true;
  }

  @Override
  public Cart getCartByUserId(UUID userId) {
    try {
      Optional<User> user = userRepository.findById(userId);

      if (!user.isPresent()) {
        LOGGER.info("User {} is not found", userId);
        throw new DataNotFoundException(ErrorCode.ERR_USER_NOT_FOUND);
      }

      return cartRepository.findByUser(user.get())
          .orElseThrow(() -> {
            LOGGER.info("Cart of user {} is not found", userId);
            return new DataNotFoundException(ErrorCode.ERR_CART_NOT_FOUND);
          });
    } catch (Exception ex) {
      String message = ex.getMessage();
      if (message.equals(ErrorCode.ERR_USER_NOT_FOUND)) {
        throw new DataNotFoundException(ErrorCode.ERR_USER_NOT_FOUND);
      } else {
        throw new DataNotFoundException(ErrorCode.ERR_CART_NOT_FOUND);
      }
    }

  }

  boolean checkExistedItemInCart(Cart cart, Long productId) {
    List<CartItem> cartItems = cart.getCartItems()
        .stream()
        .filter(item -> item.getProduct().getId().equals(productId))
        .collect(Collectors.toList());

    return !cartItems.isEmpty();
  }

}
