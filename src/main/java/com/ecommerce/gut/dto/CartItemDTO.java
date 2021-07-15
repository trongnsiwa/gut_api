package com.ecommerce.gut.dto;

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
public class CartItemDTO {
  
  @NotNull(message = "{cart.userId.notNull}")
  private String userId;
  
  @Min(value = 1, message = "{cart.productId.min}")
  private Long productId;

  private Long colorId;

  private Long sizeId;
  
  @Min(value = 0, message = "{cart.quantity.min}")
  private Integer quantity;

}
