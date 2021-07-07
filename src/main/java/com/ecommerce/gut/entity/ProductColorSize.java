package com.ecommerce.gut.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
  @JsonIgnore
  private ProductColorSizeId id;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("productId")
  @JsonIgnore
  private Product product;

  @ManyToOne
  @MapsId("colorId")
  private Color color;

  @ManyToOne
  @MapsId("sizeId")
  private PSize size;

  private Integer quantity;

  public ProductColorSize(Product product, Color color, PSize size) {
    this.product = product;
    this.color = color;
    this.size = size;
    this.id = new ProductColorSizeId(product.getId(), color.getId(), size.getId());
  }

}
