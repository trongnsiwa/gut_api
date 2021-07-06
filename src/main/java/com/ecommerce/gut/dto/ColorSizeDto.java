package com.ecommerce.gut.dto;

import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ColorSizeDto {

  @Positive
  @Min(1)
  @NotNull(message = "Color Id must not be null.")
  private Integer colorId;

  @NotEmpty(message = "Please give size Id and quantity of its size.")
  private Map<Integer, Integer> sizes = new HashMap<>();
  
}
