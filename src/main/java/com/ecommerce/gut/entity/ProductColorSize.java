package com.ecommerce.gut.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "product_color_sizes")
public class ProductColorSize {
  
  @EmbeddedId
  private ProductColorSizeId id;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("productColorId")
  private ProductColor productColor;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("sizeId")
  private PSize size;

  private Integer quantity;

  public ProductColorSize(ProductColor productColor, PSize size) {
    this.productColor = productColor;
    this.id = new ProductColorSizeId(productColor.getId(), size.getId());
  }

}
