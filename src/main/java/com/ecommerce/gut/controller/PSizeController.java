package com.ecommerce.gut.controller;

import java.util.List;
import java.util.stream.Collectors;
import com.ecommerce.gut.dto.SizeDTO;
import com.ecommerce.gut.payload.response.ResponseDTO;
import com.ecommerce.gut.payload.response.SuccessCode;
import com.ecommerce.gut.service.PSizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/size")
@Tag(name = "size")
@Validated
public class PSizeController {

  @Autowired
  PSizeService pSizeService;

  @Operation(summary = "Get all sizes")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Found the sizes even if empty",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
      @ApiResponse(responseCode = "404", description = "Not found color", content = @Content),
  })

  @GetMapping("/all")
  public ResponseEntity<ResponseDTO> getAllSizes() {
    ResponseDTO response = new ResponseDTO();
    List<SizeDTO> sizes = pSizeService.getAllSizes().stream()
        .map(size -> new SizeDTO(size.getId(), size.getName()))
        .collect(Collectors.toList());
    response.setData(sizes);
    response.setSuccessCode(SuccessCode.COLOR_LOADED_SUCCESS);

    return ResponseEntity.ok().body(response);
  }
  
}
