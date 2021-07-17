package com.ecommerce.gut.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import com.ecommerce.gut.converters.ProductConverter;
import com.ecommerce.gut.dto.CreateProductDTO;
import com.ecommerce.gut.dto.ImageListDTO;
import com.ecommerce.gut.dto.PagingProductDTO;
import com.ecommerce.gut.dto.ProductDetailDTO;
import com.ecommerce.gut.dto.UpdateProductDTO;
import com.ecommerce.gut.entity.Product;
import com.ecommerce.gut.exception.CreateDataFailException;
import com.ecommerce.gut.exception.DataNotFoundException;
import com.ecommerce.gut.exception.DeleteDataFailException;
import com.ecommerce.gut.exception.DuplicateDataException;
import com.ecommerce.gut.exception.LoadDataFailException;
import com.ecommerce.gut.exception.UpdateDataFailException;
import com.ecommerce.gut.payload.response.ErrorCode;
import com.ecommerce.gut.payload.response.ResponseDTO;
import com.ecommerce.gut.payload.response.SuccessCode;
import com.ecommerce.gut.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/product")
@Tag(name = "product")
@Validated
public class ProductController {

  @Autowired
  private ProductService productService;

  @Autowired
  private ProductConverter converter;

  @Operation(summary = "Get products by category Id per page")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Found the page's products", content = @Content),
      @ApiResponse(responseCode = "400", description = "Enter invalid data", content = @Content),
      @ApiResponse(responseCode = "404", description = "Category Id is not found",
          content = @Content),
  })
  @GetMapping("/page")
  public ResponseEntity<ResponseDTO> getProductsByCategoryIdPerPage(
      @RequestParam("category") @NotNull @Min(1) Long categoryId,
      @RequestParam("num") @Min(1) Integer pageNumber,
      @RequestParam("size") @Min(1) Integer pageSize,
      @RequestParam("sort") @NotNull @NotBlank String sortBy) throws LoadDataFailException {
    ResponseDTO response = new ResponseDTO();
    try {

      List<PagingProductDTO> pagingProducts = productService
          .getProductsByCategoryIdPerPage(categoryId, pageNumber, pageSize, sortBy).stream()
          .map(product -> converter.convertPagingProductToDto(product))
          .collect(Collectors.toList());
      response.setData(pagingProducts);
      response.setSuccessCode(SuccessCode.PRODUCTS_LOADED_SUCCESS);

    } catch (DataNotFoundException e) {
      response.setErrorCode(ErrorCode.ERR_CATEGORY_NOT_FOUND);
      throw new DataNotFoundException(ErrorCode.ERR_CATEGORY_NOT_FOUND);
    } catch (Exception e) {
      response.setErrorCode(ErrorCode.ERR_PRODUCT_LOADED_FAIL);
      throw new LoadDataFailException(ErrorCode.ERR_PRODUCT_LOADED_FAIL);
    }

    return ResponseEntity.ok().body(response);
  }

  @Operation(summary = "Get the detail of product")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Found the detail of product", content = @Content),
      @ApiResponse(responseCode = "400", description = "Enter invalid data", content = @Content),
      @ApiResponse(responseCode = "404", description = "Product Id is not found",
          content = @Content),
  })
  @GetMapping("/{id}")
  public ResponseEntity<ResponseDTO> getProductDetail(@PathVariable("id") @Min(1) Long id)
      throws DataNotFoundException {
    ResponseDTO response = new ResponseDTO();
    try {
      Product foundProduct = productService.getProductDetail(id);
      ProductDetailDTO responseProduct = converter.convertProductDetailToDto(foundProduct);
      response.setData(responseProduct);
      response.setSuccessCode(SuccessCode.PRODUCT_LOADED_SUCCESS);
    } catch (Exception e) {
      response.setErrorCode(ErrorCode.ERR_CATEGORY_NOT_FOUND);
      throw new DataNotFoundException(ErrorCode.ERR_CATEGORY_NOT_FOUND);
    }

    return ResponseEntity.ok().body(response);
  }

  @Operation(summary = "Add a product to a category",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Product object to be added to category"))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Add a product successful",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Enter invalid data", content = @Content),
      @ApiResponse(responseCode = "404", description = "Product Id is not found",
          content = @Content),
      @ApiResponse(responseCode = "409", description = "Product Id is already taken",
          content = @Content),
  })
  @PostMapping("/add/{categoryId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ResponseDTO> addProductToCategory(
      @Valid @RequestBody CreateProductDTO productDTO,
      @PathVariable("categoryId") @Min(1) Long categoryId)
      throws CreateDataFailException, DataNotFoundException {
    ResponseDTO response = new ResponseDTO();
    try {
      boolean added = productService.addProductToCategory(productDTO, categoryId);
      if (added) {
        response.setData(null);
        response.setSuccessCode(SuccessCode.PRODUCT_CREATED_SUCCESS);
      }
    } catch (DataNotFoundException e) {
      String message = e.getMessage();
      if (message.equals(ErrorCode.ERR_CATEGORY_NOT_FOUND)) {
        response.setErrorCode(ErrorCode.ERR_CATEGORY_NOT_FOUND);
        throw new DataNotFoundException(ErrorCode.ERR_CATEGORY_NOT_FOUND);
      } else if (message.equals(ErrorCode.ERR_COLOR_NOT_FOUND)) {
        response.setErrorCode(ErrorCode.ERR_COLOR_NOT_FOUND);
        throw new DataNotFoundException(ErrorCode.ERR_COLOR_NOT_FOUND);
      } else {
        response.setErrorCode(ErrorCode.ERR_SIZE_NOT_FOUND);
        throw new DataNotFoundException(ErrorCode.ERR_SIZE_NOT_FOUND);
      }

    } catch (Exception e) {
      response.setErrorCode(ErrorCode.ERR_PRODUCT_CREATED_FAIL);
      throw new CreateDataFailException(ErrorCode.ERR_PRODUCT_CREATED_FAIL);
    }

    return ResponseEntity.ok().body(response);
  }

  @Operation(summary = "Update a product by its Id",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Product object to updated"))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Update a product successful",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Enter invalid data", content = @Content),
      @ApiResponse(responseCode = "404", description = "Product Id is not found",
          content = @Content),
  })
  @PutMapping("/update/{id}/{categoryId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ResponseDTO> updateProduct(
      @Valid @RequestBody UpdateProductDTO productDTO, @PathVariable("id") @Min(1) Long id,
      @PathVariable("categoryId") @Min(1) Long categoryId)
      throws UpdateDataFailException, DataNotFoundException {
    ResponseDTO response = new ResponseDTO();
    try {
      Product updatedProduct = productService.updateProduct(productDTO, id, categoryId);
      ProductDetailDTO responseProduct = converter.convertProductDetailToDto(updatedProduct);
      response.setData(responseProduct);
      response.setSuccessCode(SuccessCode.PRODUCT_UPDATED_SUCCESS);
    } catch (DataNotFoundException e) {
      String message = e.getMessage();

      if (message.equals(ErrorCode.ERR_SIZE_NOT_FOUND)) {
        response.setErrorCode(ErrorCode.ERR_SIZE_NOT_FOUND);
        throw new DataNotFoundException(ErrorCode.ERR_SIZE_NOT_FOUND);
      } else if (message.equals(ErrorCode.ERR_COLOR_NOT_FOUND)) {
        response.setErrorCode(ErrorCode.ERR_COLOR_NOT_FOUND);
        throw new DataNotFoundException(ErrorCode.ERR_COLOR_NOT_FOUND);
      } else if (message.equals(ErrorCode.ERR_PRODUCT_NOT_FOUND)) {
        response.setErrorCode(ErrorCode.ERR_PRODUCT_NOT_FOUND);
        throw new DataNotFoundException(ErrorCode.ERR_PRODUCT_NOT_FOUND);
      } else {
        response.setErrorCode(ErrorCode.ERR_CATEGORY_NOT_FOUND);
        throw new DataNotFoundException(ErrorCode.ERR_CATEGORY_NOT_FOUND);
      }
    } catch (Exception e) {
      response.setErrorCode(ErrorCode.ERR_PRODUCT_UPDATED_FAIL);
      throw new UpdateDataFailException(ErrorCode.ERR_PRODUCT_UPDATED_FAIL);
    }

    return ResponseEntity.ok().body(response);
  }

  @Operation(summary = "Delete a product by its Id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Delete a product successful",
          content = @Content),
      @ApiResponse(responseCode = "404", description = "Product Id is not found",
          content = @Content),
  })
  @DeleteMapping("/delete/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ResponseDTO> deleteProduct(@PathVariable("id") @Min(1) Long id)
      throws DeleteDataFailException, DataNotFoundException {
    ResponseDTO response = new ResponseDTO();
    try {
      boolean deleted = productService.deleteProduct(id);
      if (deleted) {
        response.setData(null);
        response.setSuccessCode(SuccessCode.PRODUCT_DELETED_SUCCESS);
      }
    } catch (DataNotFoundException e) {
      response.setErrorCode(ErrorCode.ERR_PRODUCT_NOT_FOUND);
      throw new DataNotFoundException(ErrorCode.ERR_PRODUCT_NOT_FOUND);
    } catch (Exception e) {
      response.setErrorCode(ErrorCode.ERR_PRODUCT_DELETED_FAIL);
      throw new DeleteDataFailException(ErrorCode.ERR_PRODUCT_DELETED_FAIL);
    }

    return ResponseEntity.ok().body(response);
  }

  @Operation(summary = "Update the images of product",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Images list to be updated to product"))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Update images successful",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Enter invalid data", content = @Content),
      @ApiResponse(responseCode = "404", description = "Product Id is not found",
          content = @Content),
  })
  @PutMapping("/update/{id}/images")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ResponseDTO> replaceImagesOfProduct(
      @Valid @RequestBody ImageListDTO imageListDTO, @PathVariable("id") @Min(1) Long id)
      throws UpdateDataFailException, DuplicateDataException {
    ResponseDTO response = new ResponseDTO();
    try {
      Optional<Product> updatedProduct = productService.replaceImagesOfProduct(imageListDTO, id);
      if (updatedProduct.isPresent()) {
        ProductDetailDTO responseProduct =
            converter.convertProductDetailToDto(updatedProduct.get());
        response.setData(responseProduct);
        if (responseProduct.getProductImages().isEmpty()) {
          response.setSuccessCode(SuccessCode.PRODUCT_IMAGES_DELETED_SUCCESS);
        } else {
          response.setSuccessCode(SuccessCode.PRODUCT_IMAGES_UPDATED_SUCCESS);
        }
      }
    } catch (DuplicateDataException e) {
      response.setErrorCode(ErrorCode.ERR_NOT_EXIST_TWO_SAME_COLORS);
      throw new DuplicateDataException(ErrorCode.ERR_NOT_EXIST_TWO_SAME_COLORS);
    } catch (DataNotFoundException e) {
      String message = e.getMessage();
      if (message.equals(ErrorCode.ERR_PRODUCT_NOT_FOUND)) {
        response.setErrorCode(ErrorCode.ERR_PRODUCT_NOT_FOUND);
        throw new DataNotFoundException(ErrorCode.ERR_PRODUCT_NOT_FOUND);
      } else {
        response.setErrorCode(ErrorCode.ERR_COLOR_NOT_FOUND);
        throw new DataNotFoundException(ErrorCode.ERR_COLOR_NOT_FOUND);
      }
    } catch (Exception e) {
      response.setErrorCode(ErrorCode.ERR_PRODUCT_DELETED_FAIL);
      throw new UpdateDataFailException(ErrorCode.ERR_PRODUCT_IMAGES_REPLACED_FAIL);
    }

    return ResponseEntity.ok().body(response);
  }

}
