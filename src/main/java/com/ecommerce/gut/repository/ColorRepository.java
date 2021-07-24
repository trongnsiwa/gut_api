package com.ecommerce.gut.repository;

import java.util.Optional;
import com.ecommerce.gut.entity.Color;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ColorRepository extends JpaRepository<Color, Long> {
  
  boolean existsByName(String name);

  boolean existsBySource(String source);

  Optional<Color> findByName(String name);

  Optional<Color> findBySource(String name);

  @Query(
    value = "SELECT c "
          + "FROM Color c "
          + "WHERE UPPER(c.name) LIKE CONCAT('%',UPPER(:name),'%')"
  )
  Page<Color> searchByName(@Param("name") String name, Pageable pageable);

  long countByName(String name);
}
