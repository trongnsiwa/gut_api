package com.ecommerce.gut.entity;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;

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

  @Column(name = "group_name", length = 50)
  private String name;

  @OneToMany(fetch = FetchType.EAGER, mappedBy = "categoryGroup")
  @JsonManagedReference
  @Schema(accessMode = AccessMode.READ_ONLY)
  private Collection<Category> categories = new ArrayList<>();

}

