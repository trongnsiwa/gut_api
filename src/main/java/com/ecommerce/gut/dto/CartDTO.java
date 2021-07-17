package com.ecommerce.gut.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {

  private Long id;

  private List<CartItemDTO> cartItems = new ArrayList<>();

  private UUID userId;

}
