package com.ecommerce.gut.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
  
  private Long id;
  private Long productId;
  private String productName;
  private Double price;
  private ProductImageDTO image;
  private ColorDTO color;
  private SizeDTO size;
  private Integer amount;
  
}
