package com.ecommerce.gut.temp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductTemp {
  
  private Long productId;
  private String productName;
  private Double price;
  private String shortDesc;

}
