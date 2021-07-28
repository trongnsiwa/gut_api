package com.ecommerce.gut.dto;

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
public class SizeDTO {
  
  private Long id;

  @Size(max = 10, message = "{size.name.size}")
  private String name;

}
