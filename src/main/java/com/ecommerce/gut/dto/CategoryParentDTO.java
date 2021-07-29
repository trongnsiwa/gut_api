package com.ecommerce.gut.dto;

import java.util.HashSet;
import java.util.Set;

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
public class CategoryParentDTO {
  
  private Long id;
  private String name;
  private Set<CategoryDTO> subCategories = new HashSet<>();
  private boolean deleted;

}
