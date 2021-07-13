package com.ecommerce.gut.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import com.ecommerce.gut.dto.ErrorCode;
import com.ecommerce.gut.entity.ERole;
import com.ecommerce.gut.entity.Role;
import com.ecommerce.gut.entity.User;
import com.ecommerce.gut.exception.DataNotFoundException;
import com.ecommerce.gut.exception.DeleteDataFailException;
import com.ecommerce.gut.exception.UpdateDataFailException;
import com.ecommerce.gut.repository.RoleRepository;
import com.ecommerce.gut.repository.UserRepository;
import com.ecommerce.gut.security.service.UserDetailsImpl;
import com.ecommerce.gut.service.UserService;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserServiceImpl implements UserService {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

  private UserRepository userRepository;

  private RoleRepository roleRepository;

  public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
  }

  @Override
  public List<User> getUsersPerPage(Integer pageNum, Integer pageSize, String sortBy) {
    Sort sort = null;
    switch (sortBy) {
      case "EMAIL_A-Z":
        sort = Sort.by("email").ascending()
            .and(Sort.by("roles.name")).ascending()
            .and(Sort.by("modifiedDate").descending())
            .and(Sort.by("status").ascending());
        break;
      case "EMAIL_Z-A":
        sort = Sort.by("email").descending()
            .and(Sort.by("roles.name")).ascending()
            .and(Sort.by("modifiedDate").descending())
            .and(Sort.by("status").ascending());
        break;
      case "NAME_A-Z":
        sort = Sort.by("lastName").ascending()
            .and(Sort.by("roles.name")).ascending()
            .and(Sort.by("modifiedDate").descending())
            .and(Sort.by("status").ascending());
        break;
      case "NAME_Z-A":
        sort = Sort.by("lastName").descending()
            .and(Sort.by("roles.name")).ascending()
            .and(Sort.by("modifiedDate").descending())
            .and(Sort.by("status").ascending());
        break;
      default:
        sort = Sort.by("roles.name").ascending()
            .and(Sort.by("modifiedDate").descending()
                .and(Sort.by("status").ascending()));
        break;
    }

    PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, sort);
    return userRepository.findAll(pageRequest).getContent();
  }

  @Override
  public User getUserProfileById(UUID id) {
    return userRepository.findById(id)
        .orElseThrow(() -> {
          LOGGER.info("User %s is not found", id);
          return new DataNotFoundException(ErrorCode.ERR_USER_NOT_FOUND);
        });
  }

  @Override
  public User editUserProfile(User user, UUID id) throws UpdateDataFailException {
    try {
      User oldUser = userRepository.findById(id)
          .orElseThrow(() -> {
            LOGGER.info("User %s is not found", id);
            return new DataNotFoundException(ErrorCode.ERR_USER_NOT_FOUND);
          });

      oldUser.setFirstName(user.getFirstName());
      oldUser.setLastName(user.getLastName());
      oldUser.setAddress(user.getAddress());
      oldUser.setPhone(user.getPhone());

      return userRepository.save(oldUser);
    } catch (Exception e) {
      LOGGER.info("Fail to edit profile of user %s", id);
      throw new UpdateDataFailException(ErrorCode.ERR_USER_PROFILE_EDITED_FAIL);
    }
  }

  @Override
  public boolean deleteUser(UUID id) throws DeleteDataFailException {
    try {
      Optional<User> existedUser = userRepository.findById(id);
      if (!existedUser.isPresent()) {
        LOGGER.info("User %s is not found", id);
        throw new DataNotFoundException(ErrorCode.ERR_USER_NOT_FOUND);
      }

      User user = existedUser.get();
      user.setDeleted(true);
      userRepository.save(user);
    } catch (Exception e) {
      LOGGER.info("Fail to delete user %s", id);
      throw new DeleteDataFailException(ErrorCode.ERR_USER_DELETED_FAIL);
    }

    return true;
  }

  @Override
  public User editCurrentUserProfile(User user, UUID id) throws UpdateDataFailException {
    try {
      User oldUser = userRepository.findById(id)
          .orElseThrow(() -> {
            LOGGER.info("User %s is not found", id);
            return new DataNotFoundException(ErrorCode.ERR_USER_NOT_FOUND);
          });

      oldUser.setFirstName(user.getFirstName());
      oldUser.setLastName(user.getLastName());
      oldUser.setAddress(user.getAddress());
      oldUser.setPhone(user.getPhone());

      User updatedUser = userRepository.save(oldUser);

      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

      UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
      userDetails.setFirstName(user.getFirstName());
      userDetails.setLastName(user.getLastName());
      userDetails.setPhone(user.getPhone());
      userDetails.setAddress(user.getAddress());

      SecurityContextHolder.getContext().setAuthentication(authentication);

      return updatedUser;
    } catch (Exception e) {
      LOGGER.info("Fail to edit profile of current user %s", id);
      throw new UpdateDataFailException(ErrorCode.ERR_CURRENT_USER_PROFILE_EDITED_FAIL);
    }
  }

  @Override
  public User deactivateUser(UUID id) throws UpdateDataFailException {
    try {
      User oldUser = userRepository.findById(id)
          .orElseThrow(() -> {
            LOGGER.info("User %s is not found", id);
            return new DataNotFoundException(ErrorCode.ERR_USER_NOT_FOUND);
          });

      oldUser.setStatus("INACTIVE");
      return userRepository.save(oldUser);
    } catch (Exception e) {
      LOGGER.info("Fail to deactivate user %s", id);
      throw new UpdateDataFailException(ErrorCode.ERR_USER_DEACTIVATED_FAIL);
    }
  }

  @Override
  public User activateUser(UUID id) throws UpdateDataFailException {
    try {
      User oldUser = userRepository.findById(id)
          .orElseThrow(() -> {
            LOGGER.info("User %s is not found", id);
            return new DataNotFoundException(ErrorCode.ERR_USER_NOT_FOUND);
          });

      oldUser.setStatus("ACTIVE");
      return userRepository.save(oldUser);
    } catch (Exception e) {
      LOGGER.info("Fail to activate user %s", id);
      throw new UpdateDataFailException(ErrorCode.ERR_USER_ACTIVATED_FAIL);
    }
  }

  @Override
  public User changeUserRoles(UUID id, Set<Role> roles) throws UpdateDataFailException {
    try {
      User oldUser = userRepository.findById(id)
          .orElseThrow(() -> {
            LOGGER.info("User %s is not found", id);
            return new DataNotFoundException(ErrorCode.ERR_USER_NOT_FOUND);
          });

      roles.forEach(role -> {
        if ("admin".equals(role.getName().name())) {
          Optional<Role> adminRole = roleRepository.findByName(ERole.ROLE_ADMIN);
          if (!adminRole.isPresent()) {
            LOGGER.info("Role %s is not found", role.getName().name());
            throw new DataNotFoundException(ErrorCode.ERR_ROLE_NOT_FOUND);
          }
        } else {
          Optional<Role> userRole = roleRepository.findByName(ERole.ROLE_USER);
          if (!userRole.isPresent()) {
            LOGGER.info("Role %s is not found", role.getName().name());
            throw new DataNotFoundException(ErrorCode.ERR_ROLE_NOT_FOUND);
          }
        }
      });

      oldUser.setRoles(roles);
      return userRepository.save(oldUser);
    } catch (Exception e) {
      LOGGER.info("Fail to change roles of user %s", id);
      throw new UpdateDataFailException(ErrorCode.ERR_USER_ROLES_CHANGED_FAIL);
    }
  }

}
