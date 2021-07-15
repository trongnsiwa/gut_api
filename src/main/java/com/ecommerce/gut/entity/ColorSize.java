package com.ecommerce.gut.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "color_sizes")
public class ColorSize {
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "color_size_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
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


}
