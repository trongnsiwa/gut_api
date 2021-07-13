package com.ecommerce.gut.temp;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaleProductTemp {
  
  private Long productId;
  private String productName;
  private Double price;
  private String shortDesc;
  private Double priceSale;
  private LocalDateTime saleFromDate;
  private LocalDateTime saleToDate;

}
