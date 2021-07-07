package com.ecommerce.gut.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ProductColorSizeId implements Serializable {

  @Column(name = "product_id")
  private Long productId;

  @Column(name = "color_id")
  private Integer colorId;

  @Column(name = "size_id")
  private Integer sizeId;

}
