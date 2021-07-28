package com.ecommerce.gut.dto;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImageListDTO {
  
  @NotNull(message = "{product.id.notNull}")
  @Min(value = 1, message = "{product.id.min}")
  private Long productId;
  
  private List<ProductImageDTO> images = new ArrayList<>();

}
