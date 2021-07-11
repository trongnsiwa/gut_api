package com.ecommerce.gut.dto;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ColorSizeDTO {

  @Positive
  @Min(value = 1, message = "{colorSize.colorId.min}")
  @NotNull(message = "{colorSize.colorId.notNull}")
  private Long colorId;

  @NotEmpty(message = "{colorSize.sizes.notEmpty}")
  private Map<Long, Integer> sizes = new HashMap<>();
  
}
