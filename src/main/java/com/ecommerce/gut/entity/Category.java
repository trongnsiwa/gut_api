package com.ecommerce.gut.entity;

import java.util.ArrayList;
import java.util.List;

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
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
@Table(
    name = "product_categories",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "product_categories_un",
            columnNames = "category_name")
    })
public class Category {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "category_id")
  private Long id;

  @Column(name = "category_name", length = 50)
  private String name;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "group_id")
  @JsonBackReference
  @Schema(hidden = true)
  private CategoryGroup categoryGroup;

  @OneToMany(fetch = FetchType.LAZY,mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore
  @Schema(hidden = true)
  private List<Product> products = new ArrayList<>();

  public Category(Long id, String name) {
    this.id = id;
    this.name = name;
  }

}
