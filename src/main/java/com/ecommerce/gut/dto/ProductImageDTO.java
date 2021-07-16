package com.ecommerce.gut.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
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
public class ProductImageDTO {
  private Long id;

  @NotBlank(message = "{image.url.notBlank}")
  private String imageUrl;

  private String title;

  @Min(value = 0, message = "{productImage.colorCode.min}")
  private Long colorCode;

  public ProductImageDTO(@NotBlank(message = "{image.url.notBlank}") String imageUrl, String title,
      @Min(value = 0, message = "{productImage.colorCode.min}") Long colorCode) {
    this.imageUrl = imageUrl;
    this.title = title;
    this.colorCode = colorCode;
  }

}
