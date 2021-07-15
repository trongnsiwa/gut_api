package com.ecommerce.gut.entity;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Table(name = "images")
public class Image {
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "image_id")
  private Long id;

  @Column(name = "image_url", columnDefinition = "TEXT", nullable = false)
  private String imageUrl;

  @Column(name = "title", length = 255)
  private String title;

  @OneToMany(mappedBy = "image", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<ProductImage> productImages = new HashSet<>(); 

}
