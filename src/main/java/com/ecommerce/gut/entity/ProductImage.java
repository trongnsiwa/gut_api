package com.ecommerce.gut.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Table(name = "product_images")
public class ProductImage {
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "image_id")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "product_id", nullable = false)
  @JsonBackReference
  @Schema(hidden = true)
  private Product product;

  @Column(name = "image_url", columnDefinition = "TEXT", nullable = false)
  private String imageUrl;

  @Column(name = "title", length = 255)
  private String title;

  @Column(name = "color_code")
  private Long colorCode;

  public ProductImage(Long id, String imageUrl, String title, Long colorCode) {
    this.id = id;
    this.imageUrl = imageUrl;
    this.title = title;
    this.colorCode = colorCode;
  }

  

}
