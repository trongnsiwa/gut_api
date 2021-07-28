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
public class RemoveCartItemDTO {
  
  @NotNull(message = "{cart.userId.notNull}")
  private String userId;
  
  @NotNull(message = "{product.id.notNull}")
  @Min(value = 1, message = "{cart.productId.min}")
  private Long productId;

}
