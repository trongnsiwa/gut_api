package com.ecommerce.gut.temp;

import java.util.Date;
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
  private Date saleFromDate;
  private Date saleToDate;

}
