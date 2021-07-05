package com.ecommerce.gut.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
public class ProductImageDto {
  private Long id;

  @NotBlank(message = "Image URL must not be blank.")
  private String imageUrl;

  private String title;

  @NotNull(message = "Color code is not null")
  @Min(1)
  private Integer colorCode;

}
