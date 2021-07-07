package com.ecommerce.gut.dto;

import java.util.ArrayList;
import java.util.Collection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImageListDTO {
  
  private Collection<ProductImageDTO> images = new ArrayList<>();

}
