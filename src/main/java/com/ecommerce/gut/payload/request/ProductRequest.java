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

  @NotBlank(message = "Name must not be blank.")
  @Size(min = 1, max = 100)
  private String name;

  @NotNull(message = "Price must not be null.")
  @Min(value = 1000, message = "Price must be equal or greater than 1000.")
  private Double price;

  @Size(max = 1000, message = "Short description must lower than 1000 characters.")
  private String shortDesc;

  private String longDesc;

  @Size(max = 255, message = "Material must lower than 1000 characters.")
  private String material;

  @Size(max = 255, message = "Handling must lower than 1000 characters.")
  private String handling;

  @NotNull(message = "Brand new must not be null")
  private boolean brandNew;

  @NotNull(message = "Sale must not be null")
  private boolean sale;

  private Double priceSale;

  private Date saleFromDate;

  private Date saleToDate;
  
  @NotEmpty(message = "Please provide color and its sizes for this product.")
  private Set<ColorSizeDTO> colors = new HashSet<>();

}
