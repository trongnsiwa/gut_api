package com.ecommerce.gut.controller;

import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import com.ecommerce.gut.converters.ColorConverter;
import com.ecommerce.gut.dto.ColorDTO;
import com.ecommerce.gut.entity.Color;
import com.ecommerce.gut.exception.CreateDataFailException;
import com.ecommerce.gut.exception.DataNotFoundException;
import com.ecommerce.gut.exception.DeleteDataFailException;
import com.ecommerce.gut.exception.DuplicateDataException;
import com.ecommerce.gut.exception.RestrictDataException;
import com.ecommerce.gut.exception.UpdateDataFailException;
import com.ecommerce.gut.payload.response.ErrorCode;
import com.ecommerce.gut.payload.response.ResponseDTO;
import com.ecommerce.gut.payload.response.SuccessCode;
import com.ecommerce.gut.service.ColorService;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/color")
@Tag(name = "color")
@Validated
public class ColorController {

  @Autowired
  ColorService colorService;

  @Autowired
  ColorConverter converter;

  @Operation(summary = "Get all colors")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Found the colors even if empty",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
      @ApiResponse(responseCode = "404", description = "Not found color", content = @Content),
  })

  @GetMapping("/all")
  public ResponseEntity<ResponseDTO> getAllColors() {
    ResponseDTO response = new ResponseDTO();
    List<ColorDTO> colors = colorService.getAllColors().stream()
        .map(color -> converter.convertToDto(color))
        .collect(Collectors.toList());
    response.setData(colors);
    response.setSuccessCode(SuccessCode.COLOR_LOADED_SUCCESS);

    return ResponseEntity.ok().body(response);
  }

  @Operation(summary = "Get the color per page")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Found the colors even if empty",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
      @ApiResponse(responseCode = "404", description = "Not found color", content = @Content),
  })

  @GetMapping("/page")
  public ResponseEntity<ResponseDTO> getColorsPerPage(
      @RequestParam("num") @Min(1) Integer pageNumber,
      @RequestParam("size") @Min(1) Integer pageSize,
      @RequestParam("sortBy") @NotNull @NotBlank String sortBy) {
    ResponseDTO response = new ResponseDTO();
    List<ColorDTO> colors = null;
    colors = colorService.getColorsPerPage(pageNumber, pageSize, sortBy).stream()
        .map(color -> converter.convertToDto(color))
        .collect(Collectors.toList());
    response.setData(colors);
    response.setSuccessCode(SuccessCode.COLOR_LOADED_SUCCESS);

    return ResponseEntity.ok().body(response);
  }

  @Operation(summary = "Search colors by name")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Found the colors even if empty",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
      @ApiResponse(responseCode = "404", description = "Not found color", content = @Content),
  })
  @GetMapping("/search")
  public ResponseEntity<ResponseDTO> searchByName(@RequestParam("num") @Min(1) Integer pageNumber,
      @RequestParam("size") @Min(1) Integer pageSize,
      @RequestParam("sortBy") @NotNull @NotBlank String sortBy,
      @RequestParam("name") @NotNull @NotBlank String name)
      throws DataNotFoundException {
    ResponseDTO response = new ResponseDTO();
    List<ColorDTO> colors = null;
    colors = colorService.searchByName(pageNumber, pageSize, sortBy, name).stream()
        .map(color -> converter.convertToDto(color))
        .collect(Collectors.toList());
    response.setData(colors);
    response.setSuccessCode(SuccessCode.COLOR_LOADED_SUCCESS);

    return ResponseEntity.ok().body(response);
  }

  @Operation(summary = "Get the color by its id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Found the color", content = @Content),
      @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
      @ApiResponse(responseCode = "404", description = "Not found color", content = @Content),
  })
  @GetMapping("/{id}")
  public ResponseEntity<ResponseDTO> getColorById(@PathVariable(name = "id") @Min(1) Long id)
      throws DataNotFoundException {
    ResponseDTO response = new ResponseDTO();
    try {
      Color foundColor = colorService.getColorById(id);
      ColorDTO responseColor = converter.convertToDto(foundColor);
      response.setData(responseColor);
      response.setSuccessCode(SuccessCode.COLOR_LOADED_SUCCESS);
    } catch (Exception e) {
      response.setErrorCode(ErrorCode.ERR_COLOR_NOT_FOUND);
      throw new DataNotFoundException(ErrorCode.ERR_COLOR_NOT_FOUND);
    }

    return ResponseEntity.ok().body(response);
  }

  @Operation(summary = "Count colors")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Countable", content = @Content),
      @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
      @ApiResponse(responseCode = "404", description = "Not found color", content = @Content),
  })
  @GetMapping("/count")
  public ResponseEntity<ResponseDTO> countColors() {
    ResponseDTO response = new ResponseDTO();
    Long countColors = colorService.countColors();
    response.setData(countColors);
    response.setSuccessCode(SuccessCode.COLOR_LOADED_SUCCESS);

    return ResponseEntity.ok().body(response);
  }

  @Operation(summary = "Count colors by name")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Countable", content = @Content),
      @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
      @ApiResponse(responseCode = "404", description = "Not found color", content = @Content),
  })
  @GetMapping("/count-name")
  public ResponseEntity<ResponseDTO> countColorsByName(@RequestParam("name") @NotNull @NotBlank String name) {
    ResponseDTO response = new ResponseDTO();
    Long countColors = colorService.countColorsByName(name);
    response.setData(countColors);
    response.setSuccessCode(SuccessCode.COLOR_LOADED_SUCCESS);

    return ResponseEntity.ok().body(response);
  }

  @Operation(summary = "Add new color",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Color object to be added"))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Create new color successful",
          content = @Content),
      @ApiResponse(responseCode = "409", description = "Color id or name is already taken",
          content = @Content),
  })
  @PostMapping
  public ResponseEntity<ResponseDTO> addColor(@Valid @RequestBody ColorDTO colorDTO)
      throws CreateDataFailException, DuplicateDataException {
    ResponseDTO response = new ResponseDTO();
    try {
      Color color = converter.convertToEntity(colorDTO);
      boolean added = colorService.createColor(color);
      if (added) {
        response.setData(null);
        response.setSuccessCode(SuccessCode.COLOR_CREATED_SUCCESS);
      }
    } catch (DuplicateDataException e) {
      if (e.getMessage().equals(ErrorCode.ERR_COLOR_NAME_ALREADY_TAKEN)) {
        response.setErrorCode(ErrorCode.ERR_COLOR_NAME_ALREADY_TAKEN);
        throw new DuplicateDataException(ErrorCode.ERR_COLOR_NAME_ALREADY_TAKEN);
      } else {
        response.setErrorCode(ErrorCode.ERR_COLOR_SOURCE_ALREADY_TAKEN);
        throw new DuplicateDataException(ErrorCode.ERR_COLOR_SOURCE_ALREADY_TAKEN);
      }
    } catch (Exception e) {
      response.setErrorCode(ErrorCode.ERR_COLOR_CREATED_FAIL);
      throw new CreateDataFailException(ErrorCode.ERR_COLOR_CREATED_FAIL);
    }

    return ResponseEntity.ok().body(response);
  }

  @Operation(summary = "Update a color by its id",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Color object to be updated"))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Update the color successful",
          content = @Content),
      @ApiResponse(responseCode = "400",
          description = "Need to provide color Id and enter valid data", content = @Content),
      @ApiResponse(responseCode = "404", description = "Color is not found", content = @Content),
      @ApiResponse(responseCode = "409", description = "Color name is already taken",
          content = @Content),
  })
  @PutMapping
  public ResponseEntity<ResponseDTO> updateColor(@Valid @RequestBody ColorDTO colorDTO)
      throws UpdateDataFailException, DuplicateDataException, DataNotFoundException {
    ResponseDTO response = new ResponseDTO();
    try {
      Color color = converter.convertToEntity(colorDTO);
      Color updatedColor = colorService.updateColor(color, colorDTO.getId());
      ColorDTO responseColor = converter.convertToDto(updatedColor);
      response.setData(responseColor);
      response.setSuccessCode(SuccessCode.COLOR_UPDATED_SUCCESS);

    } catch (DataNotFoundException e) {
      response.setErrorCode(ErrorCode.ERR_COLOR_NOT_FOUND);
      throw new DataNotFoundException(ErrorCode.ERR_COLOR_NOT_FOUND);
    } catch (DuplicateDataException e) {
      if (e.getMessage().equals(ErrorCode.ERR_COLOR_NAME_ALREADY_TAKEN)) {
        response.setErrorCode(ErrorCode.ERR_COLOR_NAME_ALREADY_TAKEN);
        throw new DuplicateDataException(ErrorCode.ERR_COLOR_NAME_ALREADY_TAKEN);
      } else {
        response.setErrorCode(ErrorCode.ERR_COLOR_SOURCE_ALREADY_TAKEN);
        throw new DuplicateDataException(ErrorCode.ERR_COLOR_SOURCE_ALREADY_TAKEN);
      }
    } catch (Exception e) {
      response.setErrorCode(ErrorCode.ERR_COLOR_UPDATED_FAIL);
      throw new UpdateDataFailException(ErrorCode.ERR_COLOR_UPDATED_FAIL);
    }

    return ResponseEntity.ok().body(response);
  }

  @Operation(summary = "Delete a color by its id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Delete the color successful",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Need to provide color Id",
          content = @Content),
      @ApiResponse(responseCode = "404", description = "Color is not found", content = @Content),
      @ApiResponse(responseCode = "409",
          description = "There are some products still have this color", content = @Content),
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<ResponseDTO> deleteColor(@PathVariable(name = "id") @Min(1) Long id)
      throws DeleteDataFailException, RestrictDataException, DataNotFoundException {
    ResponseDTO response = new ResponseDTO();
    try {
      boolean deleted = colorService.deleteColor(id);
      if (deleted) {
        response.setData(null);
        response.setSuccessCode(SuccessCode.COLOR_DELETED_SUCCESS);
      }
    } catch (DataNotFoundException e) {
      response.setErrorCode(ErrorCode.ERR_COLOR_NOT_FOUND);
      throw new DataNotFoundException(ErrorCode.ERR_COLOR_NOT_FOUND);
    } catch (RestrictDataException e) {
      response.setErrorCode(ErrorCode.ERR_PRODUCT_STILL_HAVE_COLOR);
      throw new RestrictDataException(ErrorCode.ERR_PRODUCT_STILL_HAVE_COLOR);
    } catch (Exception e) {
      response.setErrorCode(ErrorCode.ERR_COLOR_DELETED_FAIL);
      throw new DeleteDataFailException(ErrorCode.ERR_COLOR_DELETED_FAIL);
    }

    return ResponseEntity.ok().body(response);
  }

}
