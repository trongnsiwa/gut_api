package com.ecommerce.gut.service;

import java.util.List;
import java.util.UUID;
import com.ecommerce.gut.entity.User;
import org.springframework.http.ResponseEntity;

public interface UserService {
  
  List<User> getCustomersPerPage(int pageNum, int pageSize, String sortBy);

  User getUserById(UUID id);

  ResponseEntity<?> updateUser(User user, UUID id);

  ResponseEntity<?> deleteUser(UUID id);

}
