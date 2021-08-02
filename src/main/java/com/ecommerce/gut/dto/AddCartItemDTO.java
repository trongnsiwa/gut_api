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
public class AddCartItemDTO {
  
  @NotNull(message = "{cart.userId.notNull}")
  private UUID userId;
  
  @NotNull(message = "{product.id.notNull}")
  @Min(value = 1, message = "{cart.productId.min}")
  private Long productId;

  @NotNull(message = "{cart.colorId.notNull}")
  @Min(value = 0, message = "{cart.colorId.min}")
  private Long colorId;

  @NotNull(message = "{cart.sizeId.notNull}")
  @Min(value = 1, message = "{cart.sizeId.min}")
  private Long sizeId;

  @Min(value = 1, message = "{cart.amount.min}")
  private Integer amount;

}
