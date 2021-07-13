package com.ecommerce.gut.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
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
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createdDate;

  @UpdateTimestamp
  @Column(name = "updated_date")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime updatedDate;

  @Column(name = "brandnew")
  private boolean brandNew;

  @Column(name = "sale")
  private boolean sale;

  @Column(name = "price_sale")
  private Double priceSale;

  @Column(name = "sale_from_date")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime saleFromDate;

  @Column(name = "sale_to_date")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime saleToDate;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id")
  @JsonIgnore
  @Schema(hidden = true)
  private Category category;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "product", cascade = CascadeType.ALL,
      orphanRemoval = true)
  @JsonManagedReference
  @Schema(accessMode = AccessMode.READ_ONLY)
  private List<ProductImage> productImages = new ArrayList<>();

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<ProductColorSize> colorSizes = new HashSet<>();

  @Transient
  @JsonIgnore
  private Set<Color> colors = new HashSet<>();
  
  @Column(name = "is_deleted")
  private boolean deleted;

  @Column(name = "in_stock")
  private boolean inStock;

  public Product(String name, Double price, String shortDesc, String longDesc, String material,
      String handling, boolean brandNew, boolean sale, Double priceSale, LocalDateTime saleFromDate,
      LocalDateTime saleToDate, Category category) {
        this.name = name;
        this.price = price;
        this.shortDesc = shortDesc;
        this.longDesc = longDesc;
        this.material = material;
        this.handling = handling;
        this.brandNew = brandNew;
        this.sale = sale;
        this.priceSale = priceSale;
        this.saleFromDate = saleFromDate;
        this.saleToDate = saleToDate;
        this.category = category;
        this.deleted = false;
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

  public Product(Long id, String name, Double price, LocalDateTime updatedDate, boolean brandNew, boolean sale, Category category) {
    this.id = id;
    this.name = name;
    this.price = price;
    this.updatedDate = updatedDate;
    this.brandNew = brandNew;
    this.sale = sale;
    this.category = category;
  }

  public Product(Long id, String name, Double price, LocalDateTime updatedDate, boolean brandNew,
      boolean sale, Double priceSale, LocalDateTime saleFromDate, LocalDateTime saleToDate, Category category) {
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
    ProductColorSize colorSize = new ProductColorSize(this, color, size);
    colorSize.setQuantity(quantity);
    colorSizes.add(colorSize);
  }

}
