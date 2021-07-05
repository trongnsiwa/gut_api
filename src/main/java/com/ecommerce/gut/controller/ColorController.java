package com.ecommerce.gut.controller;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import com.ecommerce.gut.entity.Color;
import com.ecommerce.gut.service.ColorService;
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
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Schema;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/color")
@Tag(name = "color")
@Validated
public class ColorController {
  
  @Autowired
  ColorService colorService;

  
  @Operation(summary = "Get the color by its id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Found the color",
          content = @Content(
              schema = @Schema(implementation = Color.class),
              mediaType = "application/json")),
      @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
      @ApiResponse(responseCode = "404", description = "Not found color", content = @Content),
  })
  @GetMapping("/{id}")
  public Color getColorById(@PathVariable(name = "id") @Min(1) Integer id) {
    return colorService.getColorById(id);
  }
  
  @Operation(summary = "Add new color")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Create new color successful",
          content = @Content(
              schema = @Schema(implementation = Color.class),
              mediaType = "application/json")),
      @ApiResponse(responseCode = "409", description = "Color id or name is already taken", content = @Content),
  })
  @PostMapping("/add")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> addColor(@Valid @RequestBody Color color) {
    return colorService.addColor(color);
  }

  @Operation(summary = "Update a color by its id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Update the color successful",
          content = @Content(
              schema = @Schema(implementation = Color.class),
              mediaType = "application/json")),
      @ApiResponse(responseCode = "400", description = "Need to provide color Id and enter valid data", content = @Content),
      @ApiResponse(responseCode = "404", description = "Color is not found", content = @Content),
      @ApiResponse(responseCode = "409", description = "Color name is already taken", content = @Content),
  })
  @PutMapping("/update/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> updateColor(@Valid @RequestBody Color color, @PathVariable(name = "id") @Min(1) Integer id) {
    return colorService.updateColor(color, id);
  }

  @Operation(summary = "Delete a color by its id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Delete the color successful",
          content = @Content(
              schema = @Schema(implementation = Color.class),
              mediaType = "application/json")),
      @ApiResponse(responseCode = "400", description = "Need to provide color Id", content = @Content),
      @ApiResponse(responseCode = "404", description = "Color is not found", content = @Content),
      @ApiResponse(responseCode = "409", description = "There are some products still have this color", content = @Content),
  })
  @DeleteMapping("/delete/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> deleteColor(@PathVariable(name = "id") @Min(1) Integer id) {
    return colorService.deleteColor(id);
  }

}
