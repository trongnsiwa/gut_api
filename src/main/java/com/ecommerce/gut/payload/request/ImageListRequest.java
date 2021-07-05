package com.ecommerce.gut.payload.request;

import java.util.ArrayList;
import java.util.Collection;
import com.ecommerce.gut.dto.ProductImageDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImageListRequest {
  
  private Collection<ProductImageDto> images = new ArrayList<>();

}
