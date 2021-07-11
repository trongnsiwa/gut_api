package com.ecommerce.gut.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import com.ecommerce.gut.entity.Role;
import com.ecommerce.gut.entity.User;
import org.springframework.http.ResponseEntity;

public interface UserService {
  
  List<User> getUsersPerPage(Integer pageNum, Integer pageSize, String sortBy);

  User getUserProfileById(UUID id);

  ResponseEntity<?> editUserProfile(User user, UUID id);

  ResponseEntity<?> editCurrentUserProfile(User user, UUID id);

  ResponseEntity<?> deleteUser(UUID id);

  ResponseEntity<?> deactivateUser(UUID id);

  ResponseEntity<?> activateUser(UUID id);

  ResponseEntity<?> changeUserRoles(UUID id, Set<Role> roles);

}
