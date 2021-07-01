package com.ecommerce.gut.entity;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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

  @Transient
  @NotNull(message = "Category must be belong to one group.")
  private Long groupId;

  @ManyToOne
  @JoinColumn(name = "group_id", insertable = false, updatable = false)
  private CategoryGroup categoryGroup;

  public Category() {
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

  public Long getGroupId() {
    return this.groupId;
  }

  public void setGroupId(Long groupId) {
    this.groupId = groupId;
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
        return Objects.equals(id, category.id) && Objects.equals(name, category.name) && Objects.equals(groupId, category.groupId) && Objects.equals(categoryGroup, category.categoryGroup);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, groupId, categoryGroup);
  }


}
