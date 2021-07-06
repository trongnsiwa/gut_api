package com.ecommerce.gut.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
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
@Entity
@Table(name = "product_colors")
public class ProductColor {
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "product_color_id")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "product_id")
  private Product product;

  @ManyToOne
  @JoinColumn(name = "color_id")
  private Color color;

  @OneToMany(
      mappedBy = "productColor",
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private List<ProductColorSize> colorSizes = new ArrayList<>();


  public ProductColor(Product product, Color color) {
    this.product = product;
    this.color = color;
  }

  public void addColorSize(PSize size, int quantity) {
    ProductColorSize colorSize = new ProductColorSize(this, size);
    colorSize.setQuantity(quantity);
    colorSizes.add(colorSize);
    size.getColorSizes().add(colorSize);
  }

  public void removeColorSize(PSize size) {
    for (Iterator<ProductColorSize> iterator = colorSizes.iterator(); iterator.hasNext();) {
      ProductColorSize colorSize = iterator.next();

      if (colorSize.getProductColor().equals(this) &&
          colorSize.getSize().equals(size)) {
        iterator.remove();
        colorSize.getSize().getColorSizes().remove(colorSize);
        colorSize.setProductColor(null);
        colorSize.setSize(null);
      }
    }
  }

  public void updateColorSizeQuantity(PSize size, int quantity) {
    for (Iterator<ProductColorSize> iterator = colorSizes.iterator(); iterator.hasNext();) {
      ProductColorSize colorSize = iterator.next();

      if (colorSize.getProductColor().equals(this) &&
      colorSize.getSize().equals(size)) {
        colorSize.setQuantity(quantity);
      }
    }
  }

}
