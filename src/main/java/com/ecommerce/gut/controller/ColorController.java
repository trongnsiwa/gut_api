// package com.ecommerce.gut.controller;

// import javax.validation.Valid;
// import javax.validation.constraints.Min;
// import com.ecommerce.gut.converters.ColorConverter;
// import com.ecommerce.gut.dto.ColorDTO;
// import com.ecommerce.gut.entity.Color;
// import com.ecommerce.gut.exception.CreateDataFailException;
// import com.ecommerce.gut.exception.DataNotFoundException;
// import com.ecommerce.gut.exception.DeleteDataFailException;
// import com.ecommerce.gut.exception.UpdateDataFailException;
// import com.ecommerce.gut.payload.response.ErrorCode;
// import com.ecommerce.gut.payload.response.ResponseDTO;
// import com.ecommerce.gut.payload.response.SuccessCode;
// import com.ecommerce.gut.service.ColorService;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.validation.annotation.Validated;
// import org.springframework.web.bind.annotation.CrossOrigin;
// import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.PutMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.media.Content;
// import io.swagger.v3.oas.annotations.responses.*;
// import io.swagger.v3.oas.annotations.tags.Tag;
// import io.swagger.v3.oas.annotations.media.Schema;

// @CrossOrigin(origins = "*", maxAge = 3600)
// @RestController
// @RequestMapping("/color")
// @Tag(name = "color")
// @Validated
// public class ColorController {

//   @Autowired
//   private ColorService colorService;

//   @Autowired
//   private ColorConverter converter;

//   @Operation(summary = "Get the color by its id")
//   @ApiResponses(value = {
//       @ApiResponse(responseCode = "200", description = "Found the color",
//           content = @Content(
//               schema = @Schema(implementation = Color.class),
//               mediaType = "application/json")),
//       @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
//       @ApiResponse(responseCode = "404", description = "Not found color", content = @Content),
//   })
//   @GetMapping("/{id}")
//   public ResponseEntity<ResponseDTO> getColorById(@PathVariable(name = "id") @Min(1) Long id) {
//     ResponseDTO response = new ResponseDTO();
//     try {
//       Color foundColor = colorService.getColorById(id);
//       ColorDTO responseColor = converter.convertToDto(foundColor);
//       response.setData(responseColor);
//       response.setSuccessCode(SuccessCode.COLOR_LOADED_SUCCESS);
//     } catch (Exception e) {
//       response.setErrorCode(ErrorCode.ERR_COLOR_NOT_FOUND);
//       throw new DataNotFoundException(ErrorCode.ERR_COLOR_NOT_FOUND);
//     }

//     return ResponseEntity.ok().body(response);
//   }

//   @Operation(summary = "Add new color",
//       requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
//           description = "Color object to be added"))
//   @ApiResponses(value = {
//       @ApiResponse(responseCode = "201", description = "Create new color successful",
//           content = @Content(
//               schema = @Schema(implementation = Color.class),
//               mediaType = "application/json")),
//       @ApiResponse(responseCode = "409", description = "Color id or name is already taken",
//           content = @Content),
//   })
//   @PostMapping("/add")
//   // @PreAuthorize("hasRole('ADMIN')")
//   public ResponseEntity<ResponseDTO> addColor(@Valid @RequestBody ColorDTO colorDTO)
//       throws CreateDataFailException {
//     ResponseDTO response = new ResponseDTO();
//     try {
//       Color color = converter.convertToEntity(colorDTO);
//       boolean added = colorService.createColor(color);
//       if (added) {
//         response.setData(null);
//         response.setSuccessCode(SuccessCode.COLOR_CREATED_SUCCESS);
//       }
//     } catch (Exception e) {
//       response.setErrorCode(ErrorCode.ERR_COLOR_CREATED_FAIL);
//       throw new CreateDataFailException(ErrorCode.ERR_COLOR_CREATED_FAIL);
//     }

//     return ResponseEntity.ok().body(response);
//   }

//   @Operation(summary = "Update a color by its id",
//       requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
//           description = "Color object to be updated"))
//   @ApiResponses(value = {
//       @ApiResponse(responseCode = "200", description = "Update the color successful",
//           content = @Content(
//               schema = @Schema(implementation = Color.class),
//               mediaType = "application/json")),
//       @ApiResponse(responseCode = "400",
//           description = "Need to provide color Id and enter valid data", content = @Content),
//       @ApiResponse(responseCode = "404", description = "Color is not found", content = @Content),
//       @ApiResponse(responseCode = "409", description = "Color name is already taken",
//           content = @Content),
//   })
//   @PutMapping("/update/{id}")
//   // @PreAuthorize("hasRole('ADMIN')")
//   public ResponseEntity<ResponseDTO> updateColor(@Valid @RequestBody ColorDTO colorDTO,
//       @PathVariable(name = "id") @Min(1) Long id) throws UpdateDataFailException {
//     ResponseDTO response = new ResponseDTO();
//     try {
//       Color color = converter.convertToEntity(colorDTO);
//       Color updatedColor = colorService.updateColor(color, id);
//       response.setData(updatedColor);
//       response.setSuccessCode(SuccessCode.COLOR_UPDATED_SUCCESS);
//     } catch (Exception e) {
//       response.setErrorCode(ErrorCode.ERR_COLOR_UPDATED_FAIL);
//       throw new UpdateDataFailException(ErrorCode.ERR_COLOR_UPDATED_FAIL);
//     }

//     return ResponseEntity.ok().body(response);
//   }

//   @Operation(summary = "Delete a color by its id")
//   @ApiResponses(value = {
//       @ApiResponse(responseCode = "200", description = "Delete the color successful",
//           content = @Content(
//               schema = @Schema(implementation = Color.class),
//               mediaType = "application/json")),
//       @ApiResponse(responseCode = "400", description = "Need to provide color Id",
//           content = @Content),
//       @ApiResponse(responseCode = "404", description = "Color is not found", content = @Content),
//       @ApiResponse(responseCode = "409",
//           description = "There are some products still have this color", content = @Content),
//   })
//   @DeleteMapping("/delete/{id}")
//   // @PreAuthorize("hasRole('ADMIN')")
//   public ResponseEntity<ResponseDTO> deleteColor(@PathVariable(name = "id") @Min(1) Long id) throws DeleteDataFailException {
//     ResponseDTO response = new ResponseDTO();
//     try {
//       boolean deleted = colorService.deleteColor(id);
//       if (deleted) {
//         response.setData(null);
//         response.setSuccessCode(SuccessCode.COLOR_DELETED_SUCCESS);
//       }
//     } catch (Exception e) {
//       response.setErrorCode(ErrorCode.ERR_COLOR_DELETED_FAIL);
//       throw new DeleteDataFailException(ErrorCode.ERR_COLOR_DELETED_FAIL);
//     }

//     return ResponseEntity.ok().body(response);
//   }

// }
