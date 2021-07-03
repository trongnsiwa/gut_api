package com.ecommerce.gut.entity;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "products")
public class Product {
  
  @Id
  @SequenceGenerator(
    name = "product_id_generator",
    sequenceName = "products_product_id_seq",
    allocationSize = 1
  )
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_id_generator")
  @Column(name = "product_id")
  private Long id;

  @Column(name = "product_name", length = 100, nullable = false)
  private String name;

  @Column(name = "price", nullable = false)
  private float price;
  
  @Column(name = "quantity", nullable = false)
  private int quantity;

  @Column(name = "cart_desc", length = 250)
  private String cartDesc;

  @Column(name = "short_desc", length = 1000)
  private String shortDesc;

  @Column(name = "long_desc", columnDefinition = "TEXT")
  private String longDesc;

  @Column(name = "category_id", nullable = false)
  private Long categoryId;

  @Column(name = "material", length = 255)
  private String material;

  @Column(name = "handling", length = 255)
  private String handling;

  @CreationTimestamp
  @Column(name = "created_date")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date createdDate;
  
  @UpdateTimestamp
  @Column(name = "updated_date")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date updatedDate;

  private boolean limited;

  @Column(name = "brand_new")
  private boolean brandNew;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "category_id", insertable = false, updatable = false)
  @JsonBackReference
  private Category category;

  @OneToMany(fetch = FetchType.EAGER)
  @JoinColumn(name = "product_id")
  @JsonManagedReference
  private Collection<ProductImage> productImages;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "product_colors",
      joinColumns = @JoinColumn(
          name = "product_id"),
      inverseJoinColumns = @JoinColumn(
          name = "color_id"))
  private Set<Color> colors = new HashSet<>();

  public Product(String name, float price, int quantity, String cartDesc, String shortDesc, String longDesc, String material, String handling) {
    this.name = name;
    this.price = price;
    this.quantity = quantity;
    this.cartDesc = cartDesc;
    this.shortDesc = shortDesc;
    this.longDesc = longDesc;
    this.material = material;
    this.handling = handling;
  }

  public static class ProductBuilder {
    private String name;
    private float price;
    private int quantity;
    private String cartDesc;
    private String shortDesc;
    private String longDesc;
    private String material;
    private String handling;

    public ProductBuilder withName(String name) {
      this.name = name;
      return this;
    }

    public ProductBuilder withPrice(float price) {
      this.price = price;
      return this;
    }

    public ProductBuilder withQuantity(int quantity) {
      this.quantity = quantity;
      return this;
    }

    public ProductBuilder withCartDesc(String cartDesc) {
      this.cartDesc = cartDesc;
      return this;
    }

    public ProductBuilder withShortDesc(String shortDesc) {
      this.shortDesc = shortDesc;
      return this;
    }

    public ProductBuilder withLongDesc(String longDesc) {
      this.longDesc = longDesc;
      return this;
    }

    public ProductBuilder withMaterial(String material) {
      this.material = material;
      return this;
    }

    public ProductBuilder withHandling(String handling) {
      this.handling = handling;
      return this;
    }

    public Product build() {
      return new Product(
        this.name, this.price, this.quantity,
        this.cartDesc, this.shortDesc, this.longDesc,
        this.material, this.handling
      );
    }
  }

}
