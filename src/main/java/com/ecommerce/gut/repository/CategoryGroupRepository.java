package com.ecommerce.gut.repository;

import com.ecommerce.gut.entity.CategoryGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryGroupRepository extends JpaRepository<CategoryGroup, Long> {

  boolean existsByName(String name);

}
