package com.ecommerce.gut.controller;

import java.util.List;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import com.ecommerce.gut.dto.ProductDTO;
import com.ecommerce.gut.dto.SaleProductDTO;
import com.ecommerce.gut.exception.LoadDataFailException;
import com.ecommerce.gut.payload.response.ErrorCode;
import com.ecommerce.gut.payload.response.ResponseDTO;
import com.ecommerce.gut.payload.response.SuccessCode;
import com.ecommerce.gut.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/home")
@Tag(name = "home")
@Validated
public class HomeController {
  
  @Autowired
  HomeService homeService;

  @Operation(summary = "Get all new products")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Found all new products", content = @Content),
      @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
  })
  @GetMapping("/new")
  public ResponseEntity<ResponseDTO> getNewProducts(@RequestParam @NotNull @Min(1) Integer size) throws LoadDataFailException {
    ResponseDTO response = new ResponseDTO();
    try {
      List<ProductDTO> newProducts = homeService.getNewProducts(size);
      response.setData(newProducts);
      response.setSuccessCode(SuccessCode.PRODUCT_LOADED_SUCCESS);
    } catch (Exception e) {
      response.setErrorCode(ErrorCode.ERR_PRODUCT_LOADED_FAIL);
      throw new LoadDataFailException(ErrorCode.ERR_PRODUCT_LOADED_FAIL);
    }

    return ResponseEntity.ok().body(response);
  }

  @Operation(summary = "Get all sale products")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Found all sale products", content = @Content),
      @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
  })
  @GetMapping("/sale")
  public ResponseEntity<ResponseDTO> getSaleProducts(@RequestParam @NotNull @Min(1) Integer size) throws LoadDataFailException {
    ResponseDTO response = new ResponseDTO();
    try {
      List<SaleProductDTO> newProducts = homeService.getSaleProducts(size);
      response.setData(newProducts);
      response.setSuccessCode(SuccessCode.PRODUCT_LOADED_SUCCESS);
    } catch (Exception e) {
      response.setErrorCode(ErrorCode.ERR_PRODUCT_LOADED_FAIL);
      throw new LoadDataFailException(ErrorCode.ERR_PRODUCT_LOADED_FAIL);
    }

    return ResponseEntity.ok().body(response);
    
  }

}
