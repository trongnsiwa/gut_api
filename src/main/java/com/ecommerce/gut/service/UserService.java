package com.ecommerce.gut.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.ecommerce.gut.entity.Role;
import com.ecommerce.gut.entity.User;
import com.ecommerce.gut.exception.DataNotFoundException;
import com.ecommerce.gut.exception.DeleteDataFailException;
import com.ecommerce.gut.exception.LoadDataFailException;
import com.ecommerce.gut.exception.UpdateDataFailException;

public interface UserService {
  
  List<User> getUsersPerPage(Integer pageNum, Integer pageSize, String sortBy) throws LoadDataFailException;

  User getUserProfileById(UUID id);

  User editUserProfile(User user, UUID id) throws UpdateDataFailException, DataNotFoundException;

  boolean deleteUser(UUID id) throws DeleteDataFailException, DataNotFoundException;

  User deactivateUser(UUID id) throws UpdateDataFailException, DataNotFoundException;

  User activateUser(UUID id) throws UpdateDataFailException, DataNotFoundException;

  User changeUserRoles(UUID id, Set<Role> roles) throws UpdateDataFailException, DataNotFoundException;

}
