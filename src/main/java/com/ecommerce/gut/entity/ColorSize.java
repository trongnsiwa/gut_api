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
  name = "color_sizes",
  indexes = {
    @Index(name = "idx_colorsize_product_id", columnList = "product_id"),
    @Index(name = "idx_colorsize_color_id", columnList = "color_id"),
    @Index(name = "idx_colorsize_size_id", columnList = "size_id")
  }
)
public class ColorSize {
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "color_size_id")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "product_id")  
  private Product product;

  @ManyToOne
  @JoinColumn(name = "color_id")
  private Color color;

  @ManyToOne
  @JoinColumn(name = "size_id")
  private PSize size;

  private Integer quantity;

  public ColorSize(Product product, Color color, PSize size) {
    this.product = product;
    this.color = color;
    this.size = size;
    this.quantity = 0;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((color == null) ? 0 : color.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((size == null) ? 0 : size.hashCode());
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
    ColorSize other = (ColorSize) obj;
    if (color == null) {
      if (other.color != null)
        return false;
    } else if (!color.equals(other.color))
      return false;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (size == null) {
      if (other.size != null)
        return false;
    } else if (!size.equals(other.size))
      return false;
    return true;
  }

  

}
