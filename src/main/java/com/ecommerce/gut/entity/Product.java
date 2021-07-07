package com.ecommerce.gut.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
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
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
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
  private double price;

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
  private Date createdDate;

  @UpdateTimestamp
  @Column(name = "updated_date")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date updatedDate;

  @Column(name = "brand_new", nullable = false)
  private boolean brandNew;

  @Column(name = "sale", nullable = false)
  private boolean sale;

  @Column(name = "price_sale")
  private Double priceSale;

  @Column(name = "sale_from_date")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date saleFromDate;

  @Column(name = "sale_to_date")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date saleToDate;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id")
  @JsonIgnore
  @Schema(hidden = true)
  private Category category;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "product", cascade = CascadeType.ALL,
      orphanRemoval = true)
  @JsonManagedReference
  @Schema(accessMode = AccessMode.READ_ONLY)
  private Collection<ProductImage> productImages = new ArrayList<>();

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<ProductColorSize> colorSizes = new HashSet<>();

  public Product(String name, double price, String shortDesc, String longDesc,
      String material, String handling, boolean brandNew, boolean sale, Double priceSale,
      Date saleFromDate, Date saleToDate, Category category,
      Collection<ProductImage> productImages) {
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
    this.productImages = productImages;
  }

  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public double getPrice() {
    return this.price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public String getShortDesc() {
    return this.shortDesc;
  }

  public void setShortDesc(String shortDesc) {
    this.shortDesc = shortDesc;
  }

  public String getLongDesc() {
    return this.longDesc;
  }

  public void setLongDesc(String longDesc) {
    this.longDesc = longDesc;
  }

  public String getMaterial() {
    return this.material;
  }

  public void setMaterial(String material) {
    this.material = material;
  }

  public String getHandling() {
    return this.handling;
  }

  public void setHandling(String handling) {
    this.handling = handling;
  }

  public Date getCreatedDate() {
    return this.createdDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  public Date getUpdatedDate() {
    return this.updatedDate;
  }

  public void setUpdatedDate(Date updatedDate) {
    this.updatedDate = updatedDate;
  }

  public boolean isBrandNew() {
    return this.brandNew;
  }

  public boolean getBrandNew() {
    return this.brandNew;
  }

  public void setBrandNew(boolean brandNew) {
    this.brandNew = brandNew;
  }

  public boolean isSale() {
    return this.sale;
  }

  public boolean getSale() {
    return this.sale;
  }

  public void setSale(boolean sale) {
    this.sale = sale;
  }

  public Double getPriceSale() {
    return this.priceSale;
  }

  public void setPriceSale(Double priceSale) {
    this.priceSale = priceSale;
  }

  public Date getSaleFromDate() {
    return this.saleFromDate;
  }

  public void setSaleFromDate(Date saleFromDate) {
    this.saleFromDate = saleFromDate;
  }

  public Date getSaleToDate() {
    return this.saleToDate;
  }

  public void setSaleToDate(Date saleToDate) {
    this.saleToDate = saleToDate;
  }

  public Category getCategory() {
    return this.category;
  }

  public void setCategory(Category category) {
    this.category = category;
  }

  public Collection<ProductImage> getProductImages() {
    return this.productImages;
  }

  public void setProductImages(Collection<ProductImage> productImages) {
    this.productImages = productImages;
  }

  public Set<ProductColorSize> getColorSizes() {
    return this.colorSizes;
  }

  public void setColorSizes(Set<ProductColorSize> colorSizes) {
    this.colorSizes = colorSizes;
  }

  public void addColorSize(Color color, PSize size, int quantity) {
    ProductColorSize colorSize = new ProductColorSize(this, color, size);
    colorSize.setQuantity(quantity);
    colorSizes.add(colorSize);
  }

  public static class Builder {
    private String name;
    private double price;
    private String shortDesc;
    private String longDesc;
    private String material;
    private String handling;
    private boolean brandNew;
    private boolean sale;
    private double priceSale;
    private Date saleFromDate;
    private Date saleToDate;
    private Category category;
    private Collection<ProductImage> images;

    public Builder(String name) {
      this.name = name;
    }

    public Builder withPrice(double price) {
      this.price = price;
      return this;
    }

    public Builder withDescription(String shortDesc, String longDesc, String material,
        String handling) {
      this.shortDesc = shortDesc;
      this.longDesc = longDesc;
      this.material = material;
      this.handling = handling;
      return this;
    }

    public Builder withNew(boolean brandNew) {
      this.brandNew = brandNew;
      return this;
    }

    public Builder withSale(boolean sale, double priceSale, Date saleFromDate, Date saleToDate) {
      this.sale = sale;
      this.priceSale = priceSale;
      this.saleFromDate = saleFromDate;
      this.saleToDate = saleToDate;
      return this;
    }

    public Builder withCategory(Category category) {
      this.category = category;
      return this;
    }

    public Builder withImages(Collection<ProductImage> images) {
      this.images = images;
      return this;
    }

    public Product build() {
      return new Product(this.name, this.price, this.shortDesc, this.longDesc, this.material,
          this.handling, this.brandNew, this.sale, this.priceSale, this.saleFromDate,
          this.saleToDate, this.category, this.images);
    }

  }

}
