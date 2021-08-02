package com.ecommerce.gut.dto;

import java.util.UUID;
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
public class UpdateCartItemDTO {
  
  @NotNull(message = "{cart.userId.notNull}")
  private UUID userId;
  
  @NotNull(message = "{product.id.notNull}")
  @Min(value = 1, message = "{cart.productId.min}")
  private Long productId;

  @Min(value = 1, message = "{cart.amount.min}")
  private Integer amount;

}
