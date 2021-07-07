package com.ecommerce.gut.controller;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import com.ecommerce.gut.dto.ImageListDTO;
import com.ecommerce.gut.dto.ProductDTO;
import com.ecommerce.gut.entity.Product;
import com.ecommerce.gut.service.ProductService;
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
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/product")
@Tag(name = "product")
@Validated
public class ProductController {
  
  @Autowired
  ProductService productService;

  @Operation(summary = "Get the detail of product")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Found the detail of product",
          content = @Content(
              schema = @Schema(implementation = Product.class),
              mediaType = "application/json")),
              @ApiResponse(responseCode = "400", description = "Enter invalid data", content = @Content),
      @ApiResponse(responseCode = "404", description = "Product Id is not found", content = @Content),
  })
  @GetMapping("/{id}")
  public Product getProductDetail(@PathVariable("id") @Min(1) Long id) {
    return productService.getProductDetail(id);
  }

  @Operation(summary = "Add a product to a category")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Add a product successful", content = @Content),
      @ApiResponse(responseCode = "400", description = "Enter invalid data", content = @Content),
      @ApiResponse(responseCode = "404", description = "Product Id is not found", content = @Content),
      @ApiResponse(responseCode = "409", description = "Product Id is already taken", content = @Content),
  })
  @PostMapping("/add/{categoryId}")
  public ResponseEntity<?> addProductToCategory(@Valid @RequestBody ProductDTO productRequest, @PathVariable("categoryId") @Min(1) Long categoryId) {
    return productService.addProductToCategory(productRequest, categoryId);
  }

  @Operation(summary = "Update a product by its Id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Add a product successful", content = @Content),
      @ApiResponse(responseCode = "400", description = "Enter invalid data", content = @Content),
      @ApiResponse(responseCode = "404", description = "Product Id is not found", content = @Content),
      @ApiResponse(responseCode = "409", description = "Product Id is already taken", content = @Content),
  })
  @PutMapping("/update/{id}/{categoryId}")
  public ResponseEntity<?> updateProduct(@Valid @RequestBody ProductDTO productRequest, @PathVariable("id") @Min(1) Long id, @PathVariable("categoryId") @Min(1) Long categoryId) {
    return productService.updateProduct(productRequest, id, categoryId);
  }

  @Operation(summary = "Delete a product by its Id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Delete a product successful", content = @Content),
      @ApiResponse(responseCode = "404", description = "Product Id is not found", content = @Content),
  })
  @DeleteMapping("/delete/{id}")
  public ResponseEntity<?> deleteProduct(@PathVariable("id") @Min(1) Long id) {
    return productService.deleteProduct(id);
  }

  @Operation(summary = "Update the images of product")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Update images successful", content = @Content),
      @ApiResponse(responseCode = "400", description = "Enter invalid data", content = @Content),
      @ApiResponse(responseCode = "404", description = "Product Id is not found", content = @Content),
  })
  @PutMapping("/update/{id}/images")
  public ResponseEntity<?> replaceImagesOfProduct(@Valid @RequestBody ImageListDTO imageListRequest, @PathVariable("id") @Min(1) Long id) {
    return productService.replaceImagesOfProduct(imageListRequest, id);
  }
  
}
