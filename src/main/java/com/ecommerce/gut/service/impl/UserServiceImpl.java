package com.ecommerce.gut.service.impl;

import java.util.List;
import java.util.UUID;
import com.ecommerce.gut.entity.User;
import com.ecommerce.gut.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

  @Override
  public List<User> getCustomersPerPage(int pageNum, int pageSize, String sortBy) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public User getUserById(UUID id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ResponseEntity<?> updateUser(User user, UUID id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ResponseEntity<?> deleteUser(UUID id) {
    // TODO Auto-generated method stub
    return null;
  }
  
}
