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
@Table(name = "sizes")
public class PSize {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "size_id")
  private Integer id;

  @Size(max = 10, message = "Size name must be lower than 10.")
  @Column(name = "size_name", length = 10)
  private String name;

}
