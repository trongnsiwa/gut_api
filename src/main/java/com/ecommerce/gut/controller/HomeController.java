package com.ecommerce.gut.controller;

import java.util.Collection;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import com.ecommerce.gut.dto.ProductDTO;
import com.ecommerce.gut.dto.SaleProductDTO;
import com.ecommerce.gut.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
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
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/home")
@Tag(name = "home")
@Validated
public class HomeController {
  
  @Autowired
  private HomeService homeService;

  @Operation(summary = "Get all new products")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Found all new products",
          content = @Content(
              array = @ArraySchema(schema = @Schema(implementation = ProductDTO.class)),
              mediaType = "application/json")),
      @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
  })
  @GetMapping("/new")
  // @PreAuthorize("hasRole('USER')")
  public  Collection<ProductDTO> getNewProducts(@RequestParam @NotNull @Min(1) Integer size) {
    return homeService.getNewProducts(size);
  }

  @Operation(summary = "Get all sale products")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Found all sale products",
          content = @Content(
              array = @ArraySchema(schema = @Schema(implementation = SaleProductDTO.class)),
              mediaType = "application/json")),
      @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
  })
  @GetMapping("/sale")
  // @PreAuthorize("hasRole('USER')")
  public Collection<SaleProductDTO> getSaleProducts(@RequestParam @NotNull @Min(1) Integer size) {
    return homeService.getSaleProducts(size);
  }

}
