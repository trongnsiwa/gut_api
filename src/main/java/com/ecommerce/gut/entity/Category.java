package com.ecommerce.gut.entity;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonBackReference;

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

  @NotBlank(message = "Name must not be blank.")
  @Column(name = "category_name", length = 50)
  private String name;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "group_id")
  @JsonBackReference
  private CategoryGroup categoryGroup;

  public Category() {
  }

  public Category(Long id, String name, CategoryGroup categoryGroup) {
    this.id = id;
    this.name = name;
    this.categoryGroup = categoryGroup;
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

  public CategoryGroup getCategoryGroup() {
    return this.categoryGroup;
  }

  public void setCategoryGroup(CategoryGroup categoryGroup) {
    this.categoryGroup = categoryGroup;
  }

  @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Category)) {
            return false;
        }
        Category category = (Category) o;
        return Objects.equals(id, category.id) && Objects.equals(name, category.name) && Objects.equals(categoryGroup, category.categoryGroup);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, categoryGroup);
  }

}
