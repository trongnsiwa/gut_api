package com.ecommerce.gut.repository;

import com.ecommerce.gut.entity.PSize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PSizeRepository extends JpaRepository<PSize, Long> {

}
