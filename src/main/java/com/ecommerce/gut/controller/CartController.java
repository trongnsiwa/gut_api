package com.ecommerce.gut.controller;

import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.ecommerce.gut.dto.CartDTO;
import com.ecommerce.gut.dto.RemoveCartItemDTO;
import com.ecommerce.gut.dto.UpdateCartItemDTO;
import com.ecommerce.gut.converters.CartConverter;
import com.ecommerce.gut.dto.AddCartItemDTO;
import com.ecommerce.gut.entity.Cart;
import com.ecommerce.gut.exception.CreateDataFailException;
import com.ecommerce.gut.exception.DataNotFoundException;
import com.ecommerce.gut.exception.DeleteDataFailException;
import com.ecommerce.gut.exception.UpdateDataFailException;
import com.ecommerce.gut.payload.response.ErrorCode;
import com.ecommerce.gut.payload.response.ResponseDTO;
import com.ecommerce.gut.payload.response.SuccessCode;
import com.ecommerce.gut.service.CartService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
@RestController
@RequestMapping("/cart")
@Tag(name = "cart")
@Validated
public class CartController {

  @Autowired
  CartService cartService;

  @Autowired
  CartConverter converter;

  @Operation(summary = "Add item to cart of user",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Item cart object to be added"))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Add item to cart successful",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Enter invalid data",
          content = @Content),
      @ApiResponse(responseCode = "404", description = "Data not found",
          content = @Content),
  })
  @PostMapping("/addToCart")
  public ResponseEntity<ResponseDTO> addItemToCart(@Valid @RequestBody AddCartItemDTO dto)
      throws CreateDataFailException, DataNotFoundException {

    ResponseDTO response = new ResponseDTO();

    try {

      Cart cart =
          cartService.addItemToCart(dto.getUserId(), dto.getProductId(),
              dto.getColorId(), dto.getSizeId(), dto.getAmount());
      CartDTO resCart = converter.convertCartToDto(cart);
      response.setSuccessCode(SuccessCode.ADD_TO_CART_SUCCESS);
      response.setData(resCart);

    } catch (DataNotFoundException ex) {

      if (ex.getMessage().equals(ErrorCode.ERR_USER_NOT_FOUND)) {
        response.setErrorCode(ErrorCode.ERR_USER_NOT_FOUND);
        throw new DataNotFoundException(ErrorCode.ERR_USER_NOT_FOUND);
      } else {
        response.setErrorCode(ErrorCode.ERR_PRODUCT_NOT_FOUND);
        throw new DataNotFoundException(ErrorCode.ERR_PRODUCT_NOT_FOUND);
      }

    } catch (Exception ex) {

      String message = ex.getMessage();

      if (message.equals(ErrorCode.ERR_OUT_OF_STOCK)) {
        response.setErrorCode(ErrorCode.ERR_OUT_OF_STOCK);
        throw new CreateDataFailException(ErrorCode.ERR_OUT_OF_STOCK);
      } else if (message.equals(ErrorCode.ERR_CURRENT_QUANTITY_LOWER)) {
        response.setErrorCode(ErrorCode.ERR_CURRENT_QUANTITY_LOWER);
        throw new CreateDataFailException(ErrorCode.ERR_CURRENT_QUANTITY_LOWER);
      } else {
        response.setErrorCode(ErrorCode.ERR_ADD_TO_CART_FAIL);
        throw new CreateDataFailException(ErrorCode.ERR_ADD_TO_CART_FAIL);
      }

    }

    return ResponseEntity.ok().body(response);
  }

  @Operation(summary = "Update item in cart of user",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Item cart object to be updated"))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Update item successful",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Enter invalid data",
          content = @Content),
      @ApiResponse(responseCode = "404", description = "Data not found",
          content = @Content),
  })
  @PutMapping("/updateItem")
  public ResponseEntity<ResponseDTO> updateItemQuantity(@Valid @RequestBody UpdateCartItemDTO dto)
      throws UpdateDataFailException, DataNotFoundException {

    ResponseDTO response = new ResponseDTO();

    try {

      Cart cart = cartService.updateItemQuantity(dto.getUserId(),
          dto.getProductId(), dto.getAmount());
      CartDTO resCart = converter.convertCartToDto(cart);
      response.setSuccessCode(SuccessCode.UPDATE_ITEM_QUANTITY_SUCCESS);
      response.setData(resCart);

    } catch (DataNotFoundException ex) {

      String message = ex.getMessage();

      if (message.equals(ErrorCode.ERR_USER_NOT_FOUND)) {
        response.setErrorCode(ErrorCode.ERR_USER_NOT_FOUND);
        throw new DataNotFoundException(ErrorCode.ERR_USER_NOT_FOUND);
      } else if (message.equals(ErrorCode.ERR_CART_NOT_FOUND)) {
        response.setErrorCode(ErrorCode.ERR_CART_NOT_FOUND);
        throw new DataNotFoundException(ErrorCode.ERR_CART_NOT_FOUND);
      } else {
        response.setErrorCode(ErrorCode.ERR_ITEM_CART_NOT_FOUND);
        throw new DataNotFoundException(ErrorCode.ERR_ITEM_CART_NOT_FOUND);
      }

    } catch (Exception ex) {


      String message = ex.getMessage();

      if (message.equals(ErrorCode.ERR_OUT_OF_STOCK)) {
        response.setErrorCode(ErrorCode.ERR_OUT_OF_STOCK);
        throw new UpdateDataFailException(ErrorCode.ERR_OUT_OF_STOCK);
      } else if (message.equals(ErrorCode.ERR_CURRENT_QUANTITY_LOWER)) {
        response.setErrorCode(ErrorCode.ERR_CURRENT_QUANTITY_LOWER);
        throw new UpdateDataFailException(ErrorCode.ERR_CURRENT_QUANTITY_LOWER);
      } else {
        response.setErrorCode(ErrorCode.ERR_UPDATE_ITEM_QUANTITY_FAIL);
        throw new UpdateDataFailException(ErrorCode.ERR_UPDATE_ITEM_QUANTITY_FAIL);
      }
    }

    return ResponseEntity.ok().body(response);
  }

  @Operation(summary = "Remove item from cart of user",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Item cart object to be removed"))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Remove item successful",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Enter invalid data",
          content = @Content),
      @ApiResponse(responseCode = "404", description = "Data not found",
          content = @Content),
  })
  @DeleteMapping("/removeItem")
  public ResponseEntity<ResponseDTO> removeItem(@Valid @RequestBody RemoveCartItemDTO dto)
      throws DeleteDataFailException, DataNotFoundException {

    ResponseDTO response = new ResponseDTO();

    try {

      Cart cart =
          cartService.removeItem(dto.getUserId(), dto.getProductId());
      CartDTO resCart = converter.convertCartToDto(cart);
      response.setSuccessCode(SuccessCode.REMOVE_ITEM_SUCCESS);
      response.setData(resCart);

    } catch (DataNotFoundException ex) {

      String message = ex.getMessage();

      if (message.equals(ErrorCode.ERR_USER_NOT_FOUND)) {
        response.setErrorCode(ErrorCode.ERR_USER_NOT_FOUND);
        throw new DataNotFoundException(ErrorCode.ERR_USER_NOT_FOUND);
      } else if (message.equals(ErrorCode.ERR_CART_NOT_FOUND)) {
        response.setErrorCode(ErrorCode.ERR_CART_NOT_FOUND);
        throw new DataNotFoundException(ErrorCode.ERR_CART_NOT_FOUND);
      } else {
        response.setErrorCode(ErrorCode.ERR_ITEM_CART_NOT_FOUND);
        throw new DataNotFoundException(ErrorCode.ERR_ITEM_CART_NOT_FOUND);
      }

    } catch (Exception ex) {
      response.setErrorCode(ErrorCode.ERR_REMOVE_ITEM_FAIL);
      throw new DeleteDataFailException(ErrorCode.ERR_REMOVE_ITEM_FAIL);
    }

    return ResponseEntity.ok().body(response);
  }

  @Operation(summary = "Clear all items in cart of user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Clear all items in cart successful",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Enter invalid data",
          content = @Content),
      @ApiResponse(responseCode = "404", description = "Data not found",
          content = @Content),
  })
  @DeleteMapping("/clear/{userId}")
  public ResponseEntity<ResponseDTO> clearCart(@PathVariable("userId") @NotNull UUID userId)
      throws UpdateDataFailException, DataNotFoundException {

    ResponseDTO response = new ResponseDTO();

    try {

      boolean cleared = cartService.clearCart(userId);
      if (cleared) {
        response.setSuccessCode(SuccessCode.CLEAR_CART_SUCCESS);
        response.setData(null);
      }

    } catch (DataNotFoundException ex) {

      String message = ex.getMessage();

      if (message.equals(ErrorCode.ERR_USER_NOT_FOUND)) {
        response.setErrorCode(ErrorCode.ERR_USER_NOT_FOUND);
        throw new DataNotFoundException(ErrorCode.ERR_USER_NOT_FOUND);
      } else {
        response.setErrorCode(ErrorCode.ERR_CART_NOT_FOUND);
        throw new DataNotFoundException(ErrorCode.ERR_CART_NOT_FOUND);
      }
    } catch (Exception ex) {
      response.setErrorCode(ErrorCode.ERR_CLEAR_CART_FAIL);
      throw new UpdateDataFailException(ErrorCode.ERR_CLEAR_CART_FAIL);
    }

    return ResponseEntity.ok().body(response);
  }

  @Operation(summary = "Get cart of user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Found the cart", content = @Content),
      @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
      @ApiResponse(responseCode = "404", description = "Not found cart",
          content = @Content),
  })
  @GetMapping("/{userId}")
  public ResponseEntity<ResponseDTO> getCartByUserId(@PathVariable("userId") @NotNull UUID userId)
      throws DataNotFoundException {

    ResponseDTO response = new ResponseDTO();

    try {

      Cart cart = cartService.getCartByUserId(userId);
      CartDTO responseCart = converter.convertCartToDto(cart);
      response.setSuccessCode(SuccessCode.LOAD_CART_SUCCESS);
      response.setData(responseCart);

    } catch (Exception ex) {

      String message = ex.getMessage();
      if (message.equals(ErrorCode.ERR_USER_NOT_FOUND)) {
        response.setErrorCode(ErrorCode.ERR_USER_NOT_FOUND);
        throw new DataNotFoundException(ErrorCode.ERR_USER_NOT_FOUND);
      } else {
        response.setErrorCode(ErrorCode.ERR_CART_NOT_FOUND);
        throw new DataNotFoundException(ErrorCode.ERR_CART_NOT_FOUND);
      }

    }

    return ResponseEntity.ok().body(response);
  }

}
