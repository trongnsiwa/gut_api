package com.ecommerce.gut.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "product_images",
    indexes = {
      @Index(name = "idx_img_product_id", columnList = "product_id"),
      @Index(name = "idx_p_image_id", columnList = "image_id"),
      @Index(name = "idx_color_code", columnList = "color_code")
    }
)
public class ProductImage {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "product_id")
  private Product product;

  @ManyToOne
  @JoinColumn(name = "image_id")
  private Image image;

  @Column(name = "color_code")
  private Long colorCode;

  public ProductImage(Product product, Image image) {
    this.product = product;
    this.image = image;
  }

  public ProductImage(Long id, Image image, Long colorCode) {
    this.id = id;
    this.image = image;
    this.colorCode = colorCode;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((colorCode == null) ? 0 : colorCode.hashCode());
    result = prime * result + ((image == null) ? 0 : image.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ProductImage other = (ProductImage) obj;
    if (colorCode == null) {
      if (other.colorCode != null)
        return false;
    } else if (!colorCode.equals(other.colorCode))
      return false;
    if (image == null) {
      if (other.image != null)
        return false;
    } else if (!image.equals(other.image))
      return false;
    return true;
  }

}
