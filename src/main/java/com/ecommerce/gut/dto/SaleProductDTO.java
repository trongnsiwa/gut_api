package com.ecommerce.gut.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SaleProductDTO extends ProductDTO {
  
  private Double salePrice;
  private LocalDateTime saleFromDate;
  private LocalDateTime saleToDate;

  public SaleProductDTO(Double salePrice, LocalDateTime saleFromDate, LocalDateTime saleToDate) {
    this.salePrice = salePrice;
    this.saleFromDate = saleFromDate;
    this.saleToDate = saleToDate;
  }

  public SaleProductDTO(Long id, String name, Double price, String shortDesc,
      List<ProductImageDTO> images, Set<ColorDTO> colors, Long categoryId, Long brandId,
      Double salePrice, LocalDateTime saleFromDate, LocalDateTime saleToDate) {
    super(id, name, price, shortDesc, images, colors, categoryId, brandId);
    this.salePrice = salePrice;
    this.saleFromDate = saleFromDate;
    this.saleToDate = saleToDate;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((saleFromDate == null) ? 0 : saleFromDate.hashCode());
    result = prime * result + ((salePrice == null) ? 0 : salePrice.hashCode());
    result = prime * result + ((saleToDate == null) ? 0 : saleToDate.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    SaleProductDTO other = (SaleProductDTO) obj;
    if (saleFromDate == null) {
      if (other.saleFromDate != null)
        return false;
    } else if (!saleFromDate.equals(other.saleFromDate))
      return false;
    if (salePrice == null) {
      if (other.salePrice != null)
        return false;
    } else if (!salePrice.equals(other.salePrice))
      return false;
    if (saleToDate == null) {
      if (other.saleToDate != null)
        return false;
    } else if (!saleToDate.equals(other.saleToDate))
      return false;
    return true;
  }

  

}
