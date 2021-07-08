package com.ecommerce.gut.controller;

import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import com.ecommerce.gut.dto.ColorDTO;
import com.ecommerce.gut.dto.ImageListDTO;
import com.ecommerce.gut.dto.ProductColorSizeDTO;
import com.ecommerce.gut.dto.ProductDetailDTO;
import com.ecommerce.gut.dto.ProductImageDTO;
import com.ecommerce.gut.dto.SizeDTO;
import com.ecommerce.gut.entity.Color;
import com.ecommerce.gut.entity.PSize;
import com.ecommerce.gut.entity.Product;
import com.ecommerce.gut.entity.ProductColorSize;
import com.ecommerce.gut.entity.ProductImage;
import com.ecommerce.gut.payload.request.ProductRequest;
import com.ecommerce.gut.service.ProductService;
import org.modelmapper.ModelMapper;
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
  private ProductService productService;

  @Autowired
  private ModelMapper modelMapper;

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
  public ProductDetailDTO getProductDetail(@PathVariable("id") @Min(1) Long id) {
    return convertProductDetailToDto(productService.getProductDetail(id));
  }

  @Operation(summary = "Add a product to a category",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Product object to be added to category"))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Add a product successful", content = @Content),
      @ApiResponse(responseCode = "400", description = "Enter invalid data", content = @Content),
      @ApiResponse(responseCode = "404", description = "Product Id is not found", content = @Content),
      @ApiResponse(responseCode = "409", description = "Product Id is already taken", content = @Content),
  })
  @PostMapping("/add/{categoryId}")
  // @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> addProductToCategory(@Valid @RequestBody ProductRequest productRequest, @PathVariable("categoryId") @Min(1) Long categoryId) {
    return productService.addProductToCategory(productRequest, categoryId);
  }

  @Operation(summary = "Update a product by its Id",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Product object to to updated"))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Add a product successful", content = @Content),
      @ApiResponse(responseCode = "400", description = "Enter invalid data", content = @Content),
      @ApiResponse(responseCode = "404", description = "Product Id is not found", content = @Content),
      @ApiResponse(responseCode = "409", description = "Product Id is already taken", content = @Content),
  })
  @PutMapping("/update/{id}/{categoryId}")
  // @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> updateProduct(@Valid @RequestBody ProductRequest productRequest, @PathVariable("id") @Min(1) Long id, @PathVariable("categoryId") @Min(1) Long categoryId) {
    return productService.updateProduct(productRequest, id, categoryId);
  }

  @Operation(summary = "Delete a product by its Id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Delete a product successful", content = @Content),
      @ApiResponse(responseCode = "404", description = "Product Id is not found", content = @Content),
  })
  @DeleteMapping("/delete/{id}")
  // @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> deleteProduct(@PathVariable("id") @Min(1) Long id) {
    return productService.deleteProduct(id);
  }

  @Operation(summary = "Update the images of product",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Images list to be updated to product"))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Update images successful", content = @Content),
      @ApiResponse(responseCode = "400", description = "Enter invalid data", content = @Content),
      @ApiResponse(responseCode = "404", description = "Product Id is not found", content = @Content),
  })
  @PutMapping("/update/{id}/images")
  // @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> replaceImagesOfProduct(@Valid @RequestBody ImageListDTO imageListDTO, @PathVariable("id") @Min(1) Long id) {
    return productService.replaceImagesOfProduct(imageListDTO, id);
  }

  private ProductDetailDTO convertProductDetailToDto(Product product) {
    ProductDetailDTO productDetailDTO = modelMapper.map(product, ProductDetailDTO.class);
    productDetailDTO.setProductImages(
        product.getProductImages().stream()
            .map(this::convertProductImageToDto)
            .collect(Collectors.toList())
    );
    productDetailDTO.setColorSizes(
        product.getColorSizes().stream()
            .map(this::convertProductColorSizeToDto)
            .collect(Collectors.toSet())    
    );

    return productDetailDTO;
  }

  private ProductImageDTO convertProductImageToDto(ProductImage productImage) {
    return modelMapper.map(productImage, ProductImageDTO.class);
  }

  private ProductColorSizeDTO convertProductColorSizeToDto(ProductColorSize productColorSize) {
    ColorDTO colorDTO = convertColorToDto(productColorSize.getColor());
    SizeDTO sizeDTO = convertSizeToDto(productColorSize.getSize());
    return new ProductColorSizeDTO(colorDTO, sizeDTO);
  }

  private ColorDTO convertColorToDto(Color color) {
    return modelMapper.map(color, ColorDTO.class);
  }

  private SizeDTO convertSizeToDto(PSize pSize) {
    return modelMapper.map(pSize, SizeDTO.class);
  }
  
}
