package com.ecommerce.gut.repository;

import java.util.Optional;
import com.ecommerce.gut.entity.Color;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ColorRepository extends JpaRepository<Color, Long> {
  
  boolean existsByName(String name);

  boolean existsBySource(String source);

  Optional<Color> findByName(String name);

  Optional<Color> findBySource(String name);

}
