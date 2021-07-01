package com.ecommerce.gut.repository;

import java.util.Optional;

import com.ecommerce.gut.entity.ERole;
import com.ecommerce.gut.entity.Role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

  Optional<Role> findByName(ERole name);

}
