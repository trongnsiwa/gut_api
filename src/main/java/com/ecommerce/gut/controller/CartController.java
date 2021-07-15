// package com.ecommerce.gut.controller;

// import java.util.UUID;
// import javax.validation.Valid;
// import javax.validation.constraints.NotNull;
// import com.ecommerce.gut.dto.CartDTO;
// import com.ecommerce.gut.dto.ErrorCode;
// import com.ecommerce.gut.dto.CartItemDTO;
// import com.ecommerce.gut.dto.ResponseDTO;
// import com.ecommerce.gut.dto.SuccessCode;
// import com.ecommerce.gut.entity.Cart;
// import com.ecommerce.gut.exception.CreateDataFailException;
// import com.ecommerce.gut.exception.DataNotFoundException;
// import com.ecommerce.gut.exception.DeleteDataFailException;
// import com.ecommerce.gut.exception.UpdateDataFailException;
// import com.ecommerce.gut.service.CartService;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.validation.annotation.Validated;
// import org.springframework.web.bind.annotation.CrossOrigin;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.media.Content;
// import io.swagger.v3.oas.annotations.responses.ApiResponse;
// import io.swagger.v3.oas.annotations.responses.ApiResponses;
// import io.swagger.v3.oas.annotations.tags.Tag;
// import io.swagger.v3.oas.annotations.media.Schema;

// @CrossOrigin(origins = "*", maxAge = 3600)
// @RestController
// @RequestMapping("/cart")
// @Tag(name = "user")
// @Validated
// public class CartController {

//   @Autowired
//   private CartService cartService;

//   @Operation(summary = "Add item to cart of user",
//       requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
//           description = "Item cart object to be added"))
//   @ApiResponses(value = {
//       @ApiResponse(responseCode = "200", description = "Add item to cart successful",
//           content = @Content),
//       @ApiResponse(responseCode = "400", description = "Enter invalid data",
//           content = @Content),
//       @ApiResponse(responseCode = "404", description = "Data not found",
//           content = @Content),
//   })
//   @PostMapping("/addToCart")
//   public ResponseEntity<ResponseDTO> addItemToCart(@Valid @RequestBody CartItemDTO dto)
//       throws CreateDataFailException {
//     ResponseDTO response = new ResponseDTO();
//     try {
//       boolean added =
//           cartService.addItemToCart(convertStringToUUID(dto.getUserId()), dto.getProductId());
//       if (added) {
//         response.setSuccessCode(SuccessCode.ADD_TO_CART_SUCCESS);
//         response.setData(null);
//       }
//     } catch (Exception ex) {
//       response.setErrorCode(ErrorCode.ERR_ADD_TO_CART_FAIL);
//       throw new CreateDataFailException(ErrorCode.ERR_ADD_TO_CART_FAIL);
//     }

//     return ResponseEntity.ok().body(response);
//   }

//   @Operation(summary = "Update item in cart of user",
//       requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
//           description = "Item cart object to be updated"))
//   @ApiResponses(value = {
//       @ApiResponse(responseCode = "200", description = "Update item successful",
//           content = @Content),
//       @ApiResponse(responseCode = "400", description = "Enter invalid data",
//           content = @Content),
//       @ApiResponse(responseCode = "404", description = "Data not found",
//           content = @Content),
//   })
//   @PostMapping("/updateItem")
//   public ResponseEntity<ResponseDTO> updateItemQuantity(@Valid @RequestBody CartItemDTO dto)
//       throws UpdateDataFailException {
//     ResponseDTO response = new ResponseDTO();
//     try {
//       boolean updated = cartService.updateItemQuantity(convertStringToUUID(dto.getUserId()),
//           dto.getProductId(), dto.getQuantity());
//       if (updated) {
//         response.setSuccessCode(SuccessCode.UPDATE_ITEM_QUANTITY_SUCCESS);
//         response.setData(null);
//       }
//     } catch (Exception ex) {
//       response.setErrorCode(ErrorCode.ERR_UPDATE_ITEM_QUANTITY_FAIL);
//       throw new UpdateDataFailException(ErrorCode.ERR_UPDATE_ITEM_QUANTITY_FAIL);
//     }

//     return ResponseEntity.ok().body(response);
//   }

//   @Operation(summary = "Remove item from cart of user",
//       requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
//           description = "Item cart object to be removed"))
//   @ApiResponses(value = {
//       @ApiResponse(responseCode = "200", description = "Remove item successful",
//           content = @Content),
//       @ApiResponse(responseCode = "400", description = "Enter invalid data",
//           content = @Content),
//       @ApiResponse(responseCode = "404", description = "Data not found",
//           content = @Content),
//   })
//   @PostMapping("/removeItem")
//   public ResponseEntity<ResponseDTO> removeItem(@Valid @RequestBody CartItemDTO dto)
//       throws DeleteDataFailException {
//     ResponseDTO response = new ResponseDTO();
//     try {
//       boolean removed =
//           cartService.removeItem(convertStringToUUID(dto.getUserId()), dto.getProductId());
//       if (removed) {
//         response.setSuccessCode(SuccessCode.REMOVE_ITEM_SUCCESS);
//         response.setData(null);
//       }
//     } catch (Exception ex) {
//       response.setErrorCode(ErrorCode.ERR_REMOVE_ITEM_FAIL);
//       throw new DeleteDataFailException(ErrorCode.ERR_REMOVE_ITEM_FAIL);
//     }

//     return ResponseEntity.ok().body(response);
//   }

//   @Operation(summary = "Clear all items in cart of user")
//   @ApiResponses(value = {
//       @ApiResponse(responseCode = "200", description = "Clear all items in cart successful",
//           content = @Content),
//       @ApiResponse(responseCode = "400", description = "Enter invalid data",
//           content = @Content),
//       @ApiResponse(responseCode = "404", description = "Data not found",
//           content = @Content),
//   })
//   @PostMapping("/clear/{userId}")
//   public ResponseEntity<ResponseDTO> clearCart(@PathVariable("userId") @NotNull UUID userId)
//       throws UpdateDataFailException {
//     ResponseDTO response = new ResponseDTO();
//     try {
//       boolean cleared = cartService.clearCart(userId);
//       if (cleared) {
//         response.setSuccessCode(SuccessCode.CLEAR_CART_SUCCESS);
//         response.setData(null);
//       }
//     } catch (Exception ex) {
//       response.setErrorCode(ErrorCode.ERR_CLEAR_CART_FAIL);
//       throw new UpdateDataFailException(ErrorCode.ERR_CLEAR_CART_FAIL);
//     }

//     return ResponseEntity.ok().body(response);
//   }

//   @Operation(summary = "Get cart of user")
//   @ApiResponses(value = {
//       @ApiResponse(responseCode = "200", description = "Found the cart",
//           content = @Content(
//               schema = @Schema(implementation = CartDTO.class),
//               mediaType = "application/json")),
//       @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
//       @ApiResponse(responseCode = "404", description = "Not found cart",
//           content = @Content),
//   })
//   @GetMapping("/{userId}")
//   public ResponseEntity<ResponseDTO> getCartByUserId(@PathVariable("userId") @NotNull UUID userId)
//       throws DataNotFoundException {
//     ResponseDTO response = new ResponseDTO();
//     try {
//       Cart cart = cartService.getCartByUserId(userId);
//       response.setSuccessCode(SuccessCode.LOAD_CART_SUCCESS);
//       response.setData(cart);
//     } catch (Exception ex) {
//       response.setErrorCode(ErrorCode.ERR_CART_NOT_FOUND);
//       throw new DataNotFoundException(ErrorCode.ERR_CART_NOT_FOUND);
//     }

//     return ResponseEntity.ok().body(response);
//   }

//   private UUID convertStringToUUID(String uuidString) {
//     return UUID.fromString(uuidString);
//   }

// }
