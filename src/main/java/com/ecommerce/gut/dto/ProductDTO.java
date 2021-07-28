package com.ecommerce.gut.dto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
  
  private Long id;
  private String name;
  private Double price;
  private String shortDesc;
  private List<ProductImageDTO> images = new ArrayList<>();
  private Set<ColorDTO> colors = new HashSet<>();
  private Long categoryId;
  private Long brandId;

}
