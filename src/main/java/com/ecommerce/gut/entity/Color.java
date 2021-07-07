package com.ecommerce.gut.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
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
@Table(name = "colors",
    uniqueConstraints = @UniqueConstraint(
        name = "colors_un",
        columnNames = "color_name"))
public class Color {
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "color_id")
  private Integer id;

  @NotBlank(message = "Color name must not be blank.")
  @Size(max = 50, message = "Color name must be lower than 50 characters.")
  @Column(name = "color_name", length = 50, nullable = false)
  private String name;

  @NotBlank(message = "Source must not be blank.")
  @Column(name = "source", columnDefinition = "TEXT", nullable = false)
  private String source;

}
