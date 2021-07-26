package com.ecommerce.gut.repository;

import com.ecommerce.gut.entity.Color;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ColorRepository extends JpaRepository<Color, Long>, JpaSpecificationExecutor<Color> {
  
  boolean existsByName(String name);

  boolean existsBySource(String source);

}
