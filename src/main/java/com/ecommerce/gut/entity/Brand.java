package com.ecommerce.gut.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

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
  name = "brands"
)
public class Brand {
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "brand_id")
  private Long id;

  @Column(name = "brand_name")
  private String name;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "brand")
  private List<Product> products = new ArrayList<>();

}
