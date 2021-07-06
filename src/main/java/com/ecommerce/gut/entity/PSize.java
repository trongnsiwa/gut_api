package com.ecommerce.gut.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;
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
@NaturalIdCache
@Cache(
    usage = CacheConcurrencyStrategy.READ_WRITE)
public class PSize {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "size_id")
  private Integer id;

  @Size(max = 10, message = "Size name must be lower than 10.")
  @Column(name = "size_name", length = 10)
  @NaturalId
  private String name;

  @OneToMany(
      mappedBy = "size",
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private List<ProductColorSize> colorSizes = new ArrayList<>();

}
