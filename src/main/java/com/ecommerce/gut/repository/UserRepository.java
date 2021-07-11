package com.ecommerce.gut.repository;

import java.util.Optional;
import java.util.UUID;
import com.ecommerce.gut.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

  Optional<User> findByEmail(String email);

  Boolean existsByEmail(String email);

}
