package com.ecommerce.gut.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "size_features")
public class SizeFeature {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "feature_id")
  private Long id;

  @Size(max = 10, message = "Feature name must be lower than 10.")
  @Column(name = "feature_name", length = 50)
  private String name;
  
}
