package com.ecommerce.gut.entity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(
    name = "category_groups",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "category_groups_un",
            columnNames = "group_name"
        )
    })
public class CategoryGroup {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "group_id")
  private Long id;

  @NotBlank(message = "Name must not be blank.")
  @Column(name = "group_name", length = 50)
  private String name;

  @JsonIgnore
  @OneToMany
  @JoinColumn(name = "group_id")
  private Set<Category> categories = new HashSet<>();

  public CategoryGroup() {
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

  public Set<Category> getCategories() {
    return this.categories;
  }

  public void setCategories(Set<Category> categories) {
    this.categories = categories;
  }

  @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof CategoryGroup)) {
            return false;
        }
        CategoryGroup categoryGroup = (CategoryGroup) o;
        return Objects.equals(id, categoryGroup.id) && Objects.equals(name, categoryGroup.name) && Objects.equals(categories, categoryGroup.categories);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, categories);
  }

}

