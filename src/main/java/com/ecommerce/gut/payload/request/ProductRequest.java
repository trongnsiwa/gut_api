package com.ecommerce.gut.payload.request;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.ecommerce.gut.dto.ColorSizeDTO;

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
public class ProductRequest {
  private Long id;

  @NotBlank(message = "{product.name.notBlank}")
  @Size(min = 1, max = 100, message = "{product.name.size}")
  private String name;

  @NotNull(message = "{product.price.notNull}")
  @Min(value = 1000, message = "{product.price.min}")
  private Double price;

  @Size(max = 1000, message = "{product.shortDesc.size}")
  private String shortDesc;

  private String longDesc;

  @Size(max = 255, message = "{product.material.size}")
  private String material;

  @Size(max = 255, message = "{product.handling.size}")
  private String handling;

  @NotNull(message = "{product.brandNew.notNull}")
  private boolean brandNew;

  @NotNull(message = "{product.sale.notNull}")
  private boolean sale;

  private Double priceSale;

  private Date saleFromDate;

  private Date saleToDate;
  
  @NotEmpty(message = "{product.colors.notEmpty}")
  private Set<ColorSizeDTO> colors = new HashSet<>();

}
