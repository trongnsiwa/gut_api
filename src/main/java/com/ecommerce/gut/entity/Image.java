package com.ecommerce.gut.entity;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
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
  name = "images",
  indexes = {
    @Index(name = "mul_idx_url", columnList = "image_url")
  }
)
public class Image {
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "image_id")
  private Long id;

  @Column(name = "image_url", columnDefinition = "TEXT", nullable = false)
  private String imageUrl;

  @Column(name = "title", length = 255)
  private String title;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "image")
  private Set<ProductImage> productImages = new HashSet<>();

  public Image(String imageUrl, String title) {
    this.imageUrl = imageUrl;
    this.title = title;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((imageUrl == null) ? 0 : imageUrl.hashCode());
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
    Image other = (Image) obj;
    if (imageUrl == null) {
      if (other.imageUrl != null)
        return false;
    } else if (!imageUrl.equals(other.imageUrl))
      return false;
    return true;
  }
  
  

}
