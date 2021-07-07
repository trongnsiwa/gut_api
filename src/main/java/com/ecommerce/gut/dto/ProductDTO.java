package com.ecommerce.gut.dto;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ProductDTO {
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
  
  public ProductDTO() {
  }

  public ProductDTO(Long id, String name, Double price, String shortDesc, String longDesc, String material, String handling, boolean brandNew, boolean sale, Double priceSale, Date saleFromDate, Date saleToDate) {
    this.id = id;
    this.name = name;
    this.price = price;
    this.shortDesc = shortDesc;
    this.longDesc = longDesc;
    this.material = material;
    this.handling = handling;
    this.brandNew = brandNew;
    this.sale = sale;
    this.priceSale = priceSale;
    this.saleFromDate = saleFromDate;
    this.saleToDate = saleToDate;
  }

  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Double getPrice() {
    return this.price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public String getShortDesc() {
    return this.shortDesc;
  }

  public void setShortDesc(String shortDesc) {
    this.shortDesc = shortDesc;
  }

  public String getLongDesc() {
    return this.longDesc;
  }

  public void setLongDesc(String longDesc) {
    this.longDesc = longDesc;
  }

  public String getMaterial() {
    return this.material;
  }

  public void setMaterial(String material) {
    this.material = material;
  }

  public String getHandling() {
    return this.handling;
  }

  public void setHandling(String handling) {
    this.handling = handling;
  }

  public boolean isBrandNew() {
    return this.brandNew;
  }

  public boolean getBrandNew() {
    return this.brandNew;
  }

  public void setBrandNew(boolean brandNew) {
    this.brandNew = brandNew;
  }

  public boolean isSale() {
    return this.sale;
  }

  public boolean getSale() {
    return this.sale;
  }

  public void setSale(boolean sale) {
    this.sale = sale;
  }

  public Double getPriceSale() {
    return this.priceSale;
  }

  public void setPriceSale(Double priceSale) {
    this.priceSale = priceSale;
  }

  public Date getSaleFromDate() {
    return this.saleFromDate;
  }

  public void setSaleFromDate(Date saleFromDate) {
    this.saleFromDate = saleFromDate;
  }

  public Date getSaleToDate() {
    return this.saleToDate;
  }

  public void setSaleToDate(Date saleToDate) {
    this.saleToDate = saleToDate;
  }

  public Set<ColorSizeDTO> getColors() {
    return this.colors;
  }

  public void setColors(Set<ColorSizeDTO> colors) {
    this.colors = colors;
  }

}
