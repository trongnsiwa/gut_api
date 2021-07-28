package com.ecommerce.gut.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
@Entity
@Table(
  name = "products",
  indexes = {
    @Index(name = "mul_idx_name_price", columnList = "product_name, price"),
    @Index(name = "idx_new", columnList = "brandnew"),
    @Index(name = "idx_sale", columnList = "sale"),
    @Index(name = "idx_p_category_id", columnList = "category_id")
  }
)
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "product_id")
  private Long id;

  @Column(name = "product_name", length = 100, nullable = false)
  private String name;

  @Column(name = "price", nullable = false)
  private Double price;

  @Column(name = "short_desc", length = 1000)
  private String shortDesc;

  @Column(name = "long_desc", columnDefinition = "TEXT")
  private String longDesc;

  @Column(name = "material", length = 255)
  private String material;

  @Column(name = "handling", length = 255)
  private String handling;

  @CreationTimestamp
  @Column(name = "created_date", updatable = false)
  private LocalDateTime createdDate;

  @UpdateTimestamp
  @Column(name = "updated_date")
  private LocalDateTime updatedDate;

  @Column(name = "brandnew")
  private boolean brandNew;

  @Column(name = "sale")
  private boolean sale;

  @Column(name = "price_sale")
  private Double priceSale;

  @Column(name = "sale_from_date")
  private LocalDateTime saleFromDate;

  @Column(name = "sale_to_date")
  private LocalDateTime saleToDate;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id")
  private Category category;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "brand_id")
  private Brand brand;

  @Builder.Default
  @OneToMany(fetch = FetchType.EAGER, mappedBy = "product", cascade = CascadeType.ALL,
      orphanRemoval = true)
  private Set<ProductImage> productImages = new HashSet<>();

  @Builder.Default
  @OneToMany(fetch = FetchType.EAGER, mappedBy = "product", cascade = CascadeType.ALL,
      orphanRemoval = true)
  private Set<ColorSize> colorSizes = new HashSet<>();

  @Column(name = "is_deleted")
  private boolean deleted;

  @Column(name = "in_stock")
  private boolean inStock;

  public Product() {
    this.brandNew = true;
    this.sale = false;
    this.deleted = false;
    this.inStock = true;
  }

  public Product(Long id, String name, Double price, String shortDesc) {
    this.id = id;
    this.name = name;
    this.price = price;
    this.shortDesc = shortDesc;
  }

  public Product(String name, Double price, String shortDesc, String longDesc, String material,
      String handling, boolean sale, Double priceSale, LocalDateTime saleFromDate, LocalDateTime saleToDate) {
    this.name = name;
    this.price = price;
    this.shortDesc = shortDesc;
    this.longDesc = longDesc;
    this.material = material;
    this.handling = handling;
    this.sale = sale;
    this.priceSale = priceSale;
    this.saleFromDate = saleFromDate;
    this.saleToDate = saleToDate;
  }

  public Product(Long id, String name, Double price, String shortDesc, Double priceSale,
      LocalDateTime saleFromDate, LocalDateTime saleToDate) {
    this.id = id;
    this.name = name;
    this.price = price;
    this.shortDesc = shortDesc;
    this.priceSale = priceSale;
    this.saleFromDate = saleFromDate;
    this.saleToDate = saleToDate;
  }

  public Product(Long id, String name, Double price, LocalDateTime updatedDate, boolean brandNew,
      boolean sale, Category category) {
    this.id = id;
    this.name = name;
    this.price = price;
    this.updatedDate = updatedDate;
    this.brandNew = brandNew;
    this.sale = sale;
    this.category = category;
  }

  public Product(Long id, String name, Double price, LocalDateTime updatedDate, boolean brandNew,
      boolean sale, Double priceSale, LocalDateTime saleFromDate, LocalDateTime saleToDate,
      Category category) {
    this.id = id;
    this.name = name;
    this.price = price;
    this.updatedDate = updatedDate;
    this.brandNew = brandNew;
    this.sale = sale;
    this.priceSale = priceSale;
    this.saleFromDate = saleFromDate;
    this.saleToDate = saleToDate;
    this.category = category;
  }

  public void addColorSize(Color color, PSize size, int quantity) {
    ColorSize colorSize = new ColorSize(this, color, size);
    colorSize.setQuantity(quantity);
    colorSizes.add(colorSize);
  }

  public void addImage(Image image, Long colorCode) {
    ProductImage productImage = new ProductImage(this, image);
    productImage.setColorCode(colorCode);
    productImages.add(productImage);
  }

}
