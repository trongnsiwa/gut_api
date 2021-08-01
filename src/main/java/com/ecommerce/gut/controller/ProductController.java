package com.ecommerce.gut.controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.ecommerce.gut.converters.ProductConverter;
import com.ecommerce.gut.dto.AddReviewDTO;
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
import com.google.common.base.Strings;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
@RestController
@RequestMapping("/product")
@Tag(name = "product")
@Validated
public class ProductController {

  @Autowired
  ProductService productService;

  @Autowired
  ProductConverter converter;

  @Operation(summary = "Get products per page")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Found the page's products",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Enter invalid data", content = @Content),
  })
  @GetMapping("/page")
  public ResponseEntity<ResponseDTO> getProductsPerPage(
      @RequestParam("num") @Min(1) Integer pageNumber,
      @RequestParam("size") @Min(1) Integer pageSize,
      @RequestParam("sortBy") @NotNull @NotBlank String sortBy,
      @RequestParam(required = false, name = "saleTypes") String saleTypes,
      @RequestParam(required = false, name = "colorIds") String colorIds,
      @RequestParam(required = false, name = "sizeIds") String sizeIds,
      @RequestParam(required = false, name = "fromPrice") Double fromPrice,
      @RequestParam(required = false, name = "toPrice") Double toPrice) {
    ResponseDTO response = new ResponseDTO();

    List<PagingProductDTO> pagingProducts = productService
        .getProductsPerPage(
            pageNumber, pageSize, sortBy,
            getListTypesFromString(saleTypes), getListIdsFromString(colorIds),
            getListIdsFromString(sizeIds), fromPrice, toPrice)
        .stream()
        .map(product -> converter.convertPagingProductToDto(product))
        .collect(Collectors.toList());

    response.setData(pagingProducts);
    response.setSuccessCode(SuccessCode.PRODUCTS_LOADED_SUCCESS);

    return ResponseEntity.ok().body(response);
  }

  @Operation(summary = "Count products")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Countable products",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Enter invalid data", content = @Content),
  })
  @GetMapping("/count")
  public ResponseEntity<ResponseDTO> countProducts(
      @RequestParam(required = false, name = "saleTypes") String saleTypes,
      @RequestParam(required = false, name = "colorIds") String colorIds,
      @RequestParam(required = false, name = "sizeIds") String sizeIds,
      @RequestParam(required = false, name = "fromPrice") Double fromPrice,
      @RequestParam(required = false, name = "toPrice") Double toPrice) {
    ResponseDTO response = new ResponseDTO();

    Long countProducts = productService.countProducts(getListTypesFromString(saleTypes),
        getListIdsFromString(colorIds), getListIdsFromString(sizeIds), fromPrice, toPrice);

    response.setData(countProducts);
    response.setSuccessCode(SuccessCode.PRODUCTS_LOADED_SUCCESS);

    return ResponseEntity.ok().body(response);
  }

  @Operation(summary = "Count products by name")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Countable products",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Enter invalid data", content = @Content),
  })
  @GetMapping("/count-name")
  public ResponseEntity<ResponseDTO> countProductsByName(
      @RequestParam("name") @NotNull @NotBlank String name,
      @RequestParam(required = false, name = "saleTypes") String saleTypes,
      @RequestParam(required = false, name = "colorIds") String colorIds,
      @RequestParam(required = false, name = "sizeIds") String sizeIds,
      @RequestParam(required = false, name = "fromPrice") Double fromPrice,
      @RequestParam(required = false, name = "toPrice") Double toPrice) {
    ResponseDTO response = new ResponseDTO();

    Long countProducts = productService.countProductsByName(name, getListTypesFromString(saleTypes),
        getListIdsFromString(colorIds), getListIdsFromString(sizeIds), fromPrice, toPrice);

    response.setData(countProducts);
    response.setSuccessCode(SuccessCode.PRODUCTS_LOADED_SUCCESS);

    return ResponseEntity.ok().body(response);
  }

  @Operation(summary = "Search products by name")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Found the page's products",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Enter invalid data", content = @Content),
      @ApiResponse(responseCode = "404", description = "Category Id is not found",
          content = @Content),
  })
  @GetMapping("/search")
  public ResponseEntity<ResponseDTO> searchProductsByName(
      @RequestParam("num") @Min(1) Integer pageNumber,
      @RequestParam("size") @Min(1) Integer pageSize,
      @RequestParam("sortBy") @NotNull @NotBlank String sortBy,
      @RequestParam("name") @NotNull @NotBlank String name,
      @RequestParam(required = false, name = "saleTypes") String saleTypes,
      @RequestParam(required = false, name = "colorIds") String colorIds,
      @RequestParam(required = false, name = "sizeIds") String sizeIds,
      @RequestParam(required = false, name = "fromPrice") Double fromPrice,
      @RequestParam(required = false, name = "toPrice") Double toPrice) {
    ResponseDTO response = new ResponseDTO();

    List<PagingProductDTO> pagingProducts = productService
        .searchProductsByName(pageNumber, pageSize, sortBy, name, getListTypesFromString(saleTypes),
            getListIdsFromString(colorIds), getListIdsFromString(sizeIds), fromPrice, toPrice)
        .stream()
        .map(product -> converter.convertPagingProductToDto(product))
        .collect(Collectors.toList());

    response.setData(pagingProducts);
    response.setSuccessCode(SuccessCode.PRODUCTS_LOADED_SUCCESS);

    return ResponseEntity.ok().body(response);
  }

  @Operation(summary = "Get products by category Id per page")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Found the page's products",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Enter invalid data", content = @Content),
      @ApiResponse(responseCode = "404", description = "Category Id is not found",
          content = @Content),
  })
  @GetMapping("/category/page")
  public ResponseEntity<ResponseDTO> getProductsByCategoryPerPage(
      @RequestParam("category") @NotNull @Min(1) Long categoryId,
      @RequestParam("num") @Min(1) Integer pageNumber,
      @RequestParam("size") @Min(1) Integer pageSize,
      @RequestParam("sort") @NotNull @NotBlank String sortBy,
      @RequestParam(required = false, name = "saleTypes") String saleTypes,
      @RequestParam(required = false, name = "colorIds") String colorIds,
      @RequestParam(required = false, name = "sizeIds") String sizeIds,
      @RequestParam(required = false, name = "fromPrice") Double fromPrice,
      @RequestParam(required = false, name = "toPrice") Double toPrice)
      throws LoadDataFailException {

    ResponseDTO response = new ResponseDTO();

    try {

      List<PagingProductDTO> pagingProducts = productService
          .getProductsByCategoryPerPage(categoryId, pageNumber, pageSize, sortBy,
              getListTypesFromString(saleTypes), getListIdsFromString(colorIds),
              getListIdsFromString(sizeIds), fromPrice, toPrice)
          .stream()
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

  @Operation(summary = "Search products by category Id per page")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Found the page's products",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Enter invalid data", content = @Content),
      @ApiResponse(responseCode = "404", description = "Category Id is not found",
          content = @Content),
  })
  @GetMapping("/category/search")
  public ResponseEntity<ResponseDTO> searchProductsByCategoryAndName(
      @RequestParam("category") @NotNull @Min(1) Long categoryId,
      @RequestParam("num") @Min(1) Integer pageNumber,
      @RequestParam("size") @Min(1) Integer pageSize,
      @RequestParam("sort") @NotNull @NotBlank String sortBy,
      @RequestParam("name") @NotNull @NotBlank String name,
      @RequestParam(required = false, name = "saleTypes") String saleTypes,
      @RequestParam(required = false, name = "colorIds") String colorIds,
      @RequestParam(required = false, name = "sizeIds") String sizeIds,
      @RequestParam(required = false, name = "fromPrice") Double fromPrice,
      @RequestParam(required = false, name = "toPrice") Double toPrice)
      throws LoadDataFailException {

    ResponseDTO response = new ResponseDTO();

    try {

      List<PagingProductDTO> pagingProducts = productService
          .searchProductsByCategoryAndName(categoryId, pageNumber, pageSize, sortBy, name,
              getListTypesFromString(saleTypes), getListIdsFromString(colorIds),
              getListIdsFromString(sizeIds), fromPrice, toPrice)
          .stream()
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

  @Operation(summary = "Count products by category")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Countable products",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Enter invalid data", content = @Content),
  })
  @GetMapping("/category/count")
  public ResponseEntity<ResponseDTO> countProductsByCategory(
      @RequestParam("category") @NotNull @Min(1) Long categoryId,
      @RequestParam(required = false, name = "saleTypes") String saleTypes,
      @RequestParam(required = false, name = "colorIds") String colorIds,
      @RequestParam(required = false, name = "sizeIds") String sizeIds,
      @RequestParam(required = false, name = "fromPrice") Double fromPrice,
      @RequestParam(required = false, name = "toPrice") Double toPrice) {
    ResponseDTO response = new ResponseDTO();

    Long countProducts =
        productService.countProductsByCategory(categoryId, getListTypesFromString(saleTypes),
            getListIdsFromString(colorIds), getListIdsFromString(sizeIds), fromPrice, toPrice);

    response.setData(countProducts);
    response.setSuccessCode(SuccessCode.PRODUCTS_LOADED_SUCCESS);

    return ResponseEntity.ok().body(response);
  }

  @Operation(summary = "Count products by category and name")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Countable products",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Enter invalid data", content = @Content),
  })
  @GetMapping("/category/count-name")
  public ResponseEntity<ResponseDTO> countProductsByCategoryAndName(
      @RequestParam("category") @NotNull @Min(1) Long categoryId,
      @RequestParam("name") @NotNull @NotBlank String name,
      @RequestParam(required = false, name = "saleTypes") String saleTypes,
      @RequestParam(required = false, name = "colorIds") String colorIds,
      @RequestParam(required = false, name = "sizeIds") String sizeIds,
      @RequestParam(required = false, name = "fromPrice") Double fromPrice,
      @RequestParam(required = false, name = "toPrice") Double toPrice) {
    ResponseDTO response = new ResponseDTO();

    Long countProducts = productService.countProductsByCategoryAndName(categoryId, name,
        getListTypesFromString(saleTypes), getListIdsFromString(colorIds),
        getListIdsFromString(sizeIds), fromPrice, toPrice);

    response.setData(countProducts);
    response.setSuccessCode(SuccessCode.PRODUCTS_LOADED_SUCCESS);

    return ResponseEntity.ok().body(response);
  }

  @Operation(summary = "Get the detail of product")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Found the detail of product",
          content = @Content),
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
      response.setErrorCode(ErrorCode.ERR_PRODUCT_NOT_FOUND);
      throw new DataNotFoundException(ErrorCode.ERR_PRODUCT_NOT_FOUND);
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
  @PostMapping
  public ResponseEntity<ResponseDTO> addProductToCategory(
      @Valid @RequestBody CreateProductDTO productDTO)
      throws CreateDataFailException, DataNotFoundException {

    ResponseDTO response = new ResponseDTO();

    try {

      boolean added = productService.addProductToCategory(productDTO, productDTO.getCategoryId());

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
  @PutMapping
  public ResponseEntity<ResponseDTO> updateProduct(
      @Valid @RequestBody UpdateProductDTO productDTO)
      throws UpdateDataFailException, DataNotFoundException {

    ResponseDTO response = new ResponseDTO();

    try {

      Product updatedProduct =
          productService.updateProduct(productDTO, productDTO.getId(), productDTO.getCategoryId());

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
  @DeleteMapping("/{id}")
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
  @PutMapping("/images")
  public ResponseEntity<ResponseDTO> replaceImagesOfProduct(
      @Valid @RequestBody ImageListDTO imageListDTO)
      throws UpdateDataFailException, DuplicateDataException {

    ResponseDTO response = new ResponseDTO();

    try {

      Optional<Product> updatedProduct =
          productService.replaceImagesOfProduct(imageListDTO, imageListDTO.getProductId());

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
      response.setErrorCode(ErrorCode.ERR_PRODUCT_IMAGES_REPLACED_FAIL);
      throw new UpdateDataFailException(ErrorCode.ERR_PRODUCT_IMAGES_REPLACED_FAIL);
    }

    return ResponseEntity.ok().body(response);
  }

  @Operation(summary = "Add user review of product",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "User review object to add to product"))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Add review successful",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Enter invalid data", content = @Content),
      @ApiResponse(responseCode = "404", description = "Product Id or User Id is not found",
          content = @Content),
  })
  @PutMapping("/review")
  public ResponseEntity<ResponseDTO> addUserReviewOfProduct(
      @Valid @RequestBody AddReviewDTO reviewRequest)
      throws UpdateDataFailException, DataNotFoundException {

    ResponseDTO response = new ResponseDTO();

    try {

      Product updatedProduct = productService.addUserReviewOfProduct(reviewRequest);
      ProductDetailDTO responseProduct = converter.convertProductDetailToDto(updatedProduct);

      response.setData(responseProduct);
      response.setSuccessCode(SuccessCode.PRODUCT_REVIEW_ADDED_SUCCESS);
    } catch (DataNotFoundException e) {

      String message = e.getMessage();

      if (message.equals(ErrorCode.ERR_PRODUCT_NOT_FOUND)) {
        response.setErrorCode(ErrorCode.ERR_PRODUCT_NOT_FOUND);
        throw new DataNotFoundException(ErrorCode.ERR_PRODUCT_NOT_FOUND);
      } else {
        response.setErrorCode(ErrorCode.ERR_USER_NOT_FOUND);
        throw new DataNotFoundException(ErrorCode.ERR_USER_NOT_FOUND);
      }

    } catch (Exception e) {
      response.setErrorCode(ErrorCode.ERR_PRODUCT_DELETED_FAIL);
      throw new UpdateDataFailException(ErrorCode.ERR_PRODUCT_REVIEW_ADDED_FAIL);
    }

    return ResponseEntity.ok().body(response);
  }

  private Set<Long> getListIdsFromString(String ids) {
    if (Strings.isNullOrEmpty(ids)) {
      return Collections.emptySet();
    }

    return Arrays.asList(ids.split(",")).stream().map(s -> Long.parseLong(s.trim()))
        .collect(Collectors.toSet());
  }

  private Set<String> getListTypesFromString(String ids) {
    if (Strings.isNullOrEmpty(ids)) {
      return Collections.emptySet();
    }

    return Arrays.asList(ids.split(",")).stream().map(String::trim).collect(Collectors.toSet());
  }

}
