package com.ecommerce.gut.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
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
public class PagingProductDTO {
  private Long id;
  private String name;
  private Double price;
  private String shortDesc;
  private Double salePrice;
  private Date saleFromDate;
  private Date saleToDate;
  private List<ProductImageDTO> images = new ArrayList<>();
  private Set<ColorDTO> colors = new HashSet<>();
}