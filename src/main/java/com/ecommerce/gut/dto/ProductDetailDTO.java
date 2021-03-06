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
public class ProductDetailDTO {

  private Long id;
  private String name;
  private Double price;
  private String shortDesc;
  private String longDesc;
  private String material;
  private String handling;
  private boolean brandNew;
  private boolean sale;
  private Double priceSale;
  private LocalDateTime saleFromDate;
  private LocalDateTime saleToDate;

  @Builder.Default
  private List<ProductImageDTO> productImages = new ArrayList<>();

  @Builder.Default
  private Set<ProductColorSizeDTO> colorSizes = new HashSet<>();

  @Builder.Default
  private List<ReviewDTO> reviews = new ArrayList<>();
  private boolean deleted;
  private Long categoryId;
  private String categoryName;
  private Long brandId;
  private String brandName;

}
