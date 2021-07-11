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

  @NotBlank(message = "{color.name.notBlank}")
  @Size(max = 50, message = "{color.name.lowerThan50Chars}")
  private String name;

  @NotBlank(message = "{color.source.notBlank}")
  private String source;

}
