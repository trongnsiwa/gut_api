package com.ecommerce.gut.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
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
public class CategoryDTO {
  
  private Long id;

  @NotBlank(message = "Name must not be blank.")
  @Size(max = 50, message = "Name must not be higher than 50 characters.")
  private String name;

}
