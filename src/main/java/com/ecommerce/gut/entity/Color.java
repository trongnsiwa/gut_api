package com.ecommerce.gut.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

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
  name = "colors",
  uniqueConstraints = {
      @UniqueConstraint(
        name = "colors_un",
        columnNames = "color_name"),
      @UniqueConstraint(
        name = "colors_source_un",
        columnNames = "source"),
      }
)
public class Color {
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "color_id")
  private Long id;

  @Column(name = "color_name", length = 50, nullable = false)
  private String name;

  @Column(name = "source", columnDefinition = "TEXT", nullable = false)
  private String source;

}
