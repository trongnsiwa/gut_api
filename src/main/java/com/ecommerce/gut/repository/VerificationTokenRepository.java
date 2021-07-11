package com.ecommerce.gut.repository;

import java.util.Optional;
import com.ecommerce.gut.entity.User;
import com.ecommerce.gut.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
  
  Optional<VerificationToken> findByToken(String token);

  Optional<VerificationToken> findByUser(User user);

}
