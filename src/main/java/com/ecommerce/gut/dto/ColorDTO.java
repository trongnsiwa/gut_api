package com.ecommerce.gut.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ColorDTO {
  
  private Long id;

  @NotBlank(message = "Color name must not be blank.")
  @Size(max = 50, message = "Color name must be lower than 50 characters.")
  private String name;

  @NotBlank(message = "Source must not be blank.")
  private String source;

}
