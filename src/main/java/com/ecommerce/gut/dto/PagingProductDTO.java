package com.ecommerce.gut.dto;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagingProductDTO {

  private Long id;
  private String name;
  private Double price;
  private String shortDesc;
  private boolean brandNew;
  private boolean sale;
  private Double salePrice;
  private LocalDateTime saleFromDate;
  private LocalDateTime saleToDate;
  private Double rate;

  @Builder.Default
  private List<ProductImageDTO> images = new ArrayList<>();

  @Builder.Default
  private Set<ColorDTO> colors = new HashSet<>();
  
  private boolean deleted;
  private Long categoryId;
  private String categoryName;
  private Long brandId;
  private String brandName;

}
