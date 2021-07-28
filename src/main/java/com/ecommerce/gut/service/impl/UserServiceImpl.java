package com.ecommerce.gut.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.ecommerce.gut.entity.ERole;
import com.ecommerce.gut.entity.Image;
import com.ecommerce.gut.entity.Role;
import com.ecommerce.gut.entity.User;
import com.ecommerce.gut.exception.DataNotFoundException;
import com.ecommerce.gut.exception.DeleteDataFailException;
import com.ecommerce.gut.exception.LoadDataFailException;
import com.ecommerce.gut.exception.UpdateDataFailException;
import com.ecommerce.gut.payload.response.ErrorCode;
import com.ecommerce.gut.repository.ImageRepository;
import com.ecommerce.gut.repository.RoleRepository;
import com.ecommerce.gut.repository.UserRepository;
import com.ecommerce.gut.service.UserService;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserServiceImpl implements UserService {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  ImageRepository imageRepository;

  @Override
  public List<User> getUsersPerPage(Integer pageNum, Integer pageSize, String sortBy) throws LoadDataFailException {
    try {
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
    } catch (Exception ex) {
      LOGGER.info("Fail to load users");
      throw new LoadDataFailException(ErrorCode.ERR_LOAD_USERS_FAIL); 
    }
  }

  @Override
  public User getUserProfileById(UUID id) {
    return userRepository.findById(id)
        .orElseThrow(() -> {
          LOGGER.info("User {} is not found", id);
          return new DataNotFoundException(ErrorCode.ERR_USER_NOT_FOUND);
        });
  }

  @Override
  public User editUserProfile(User user, UUID id) throws UpdateDataFailException, DataNotFoundException {
    try {
      User oldUser = userRepository.findById(id)
          .orElseThrow(() -> {
            LOGGER.info("User {} is not found", id);
            return new DataNotFoundException(ErrorCode.ERR_USER_NOT_FOUND);
          });

      oldUser.setFirstName(user.getFirstName());
      oldUser.setLastName(user.getLastName());
      oldUser.setAddress(user.getAddress());
      oldUser.setPhone(user.getPhone());

      Image image = user.getImage();

      if (image != null && oldUser.getImage() == null) {
        oldUser.setImage(image);
      } else if (image == null && oldUser.getImage() != null) {
        imageRepository.deleteById(oldUser.getImage().getId());
        oldUser.setImage(null);
      } else if (image != null && oldUser.getImage() != null && !image.getImageUrl().equals(oldUser.getImage().getImageUrl())) {
        imageRepository.deleteById(oldUser.getImage().getId());
        oldUser.setImage(image);
      }

      return userRepository.save(oldUser);
    } catch (DataNotFoundException ex) {
      throw new DataNotFoundException(ErrorCode.ERR_USER_NOT_FOUND);
    } catch (Exception e) {
      LOGGER.info("Fail to edit profile of user {}", id);
      throw new UpdateDataFailException(ErrorCode.ERR_USER_PROFILE_EDITED_FAIL);
    }
  }

  @Override
  public boolean deleteUser(UUID id) throws DeleteDataFailException, DataNotFoundException {
    try {
      Optional<User> existedUser = userRepository.findById(id);

      if (!existedUser.isPresent()) {
        LOGGER.info("User {} is not found", id);
        throw new DataNotFoundException(ErrorCode.ERR_USER_NOT_FOUND);
      }

      User user = existedUser.get();

      user.setDeleted(true);

      userRepository.save(user);
    } catch (DataNotFoundException e) {
      throw new DataNotFoundException(ErrorCode.ERR_USER_NOT_FOUND);
    } catch (Exception e) {
      LOGGER.info("Fail to delete user {}", id);
      throw new DeleteDataFailException(ErrorCode.ERR_USER_DELETED_FAIL);
    }

    return true;
  }

  @Override
  public User deactivateUser(UUID id) throws UpdateDataFailException, DataNotFoundException {
    try {
      User oldUser = userRepository.findById(id)
          .orElseThrow(() -> {
            LOGGER.info("User {} is not found", id);
            return new DataNotFoundException(ErrorCode.ERR_USER_NOT_FOUND);
          });

      oldUser.setStatus("INACTIVE");

      return userRepository.save(oldUser);
    } catch (DataNotFoundException e) {
      throw new DataNotFoundException(ErrorCode.ERR_USER_NOT_FOUND);
    } catch (Exception e) {
      LOGGER.info("Fail to deactivate user {}", id);
      throw new UpdateDataFailException(ErrorCode.ERR_USER_DEACTIVATED_FAIL);
    }
  }

  @Override
  public User activateUser(UUID id) throws UpdateDataFailException, DataNotFoundException {
    try {
      User oldUser = userRepository.findById(id)
          .orElseThrow(() -> {
            LOGGER.info("User {} is not found", id);
            return new DataNotFoundException(ErrorCode.ERR_USER_NOT_FOUND);
          });

      oldUser.setStatus("ACTIVE");

      return userRepository.save(oldUser);
    } catch (DataNotFoundException e) {
      throw new DataNotFoundException(ErrorCode.ERR_USER_NOT_FOUND);
    } catch (Exception e) {
      LOGGER.info("Fail to activate user {}", id);
      throw new UpdateDataFailException(ErrorCode.ERR_USER_ACTIVATED_FAIL);
    }
  }

  @Override
  public User changeUserRoles(UUID id, Set<Role> roles) throws UpdateDataFailException, DataNotFoundException {
    try {
      User oldUser = userRepository.findById(id)
          .orElseThrow(() -> {
            LOGGER.info("User {} is not found", id);
            return new DataNotFoundException(ErrorCode.ERR_USER_NOT_FOUND);
          });

      roles.forEach(role -> {
        if (role.getName().equals(ERole.ROLE_ADMIN)) {
          Optional<Role> adminRole = roleRepository.findByName(ERole.ROLE_ADMIN);
          if (!adminRole.isPresent()) {
            LOGGER.info("Role {} is not found", role.getName().name());
            throw new DataNotFoundException(ErrorCode.ERR_ROLE_NOT_FOUND);
          }
        } else {
          Optional<Role> userRole = roleRepository.findByName(ERole.ROLE_USER);
          if (!userRole.isPresent()) {
            LOGGER.info("Role {} is not found", role.getName().name());
            throw new DataNotFoundException(ErrorCode.ERR_ROLE_NOT_FOUND);
          }
        }
      });

      oldUser.setRoles(roles);

      return userRepository.save(oldUser);
    } catch (DataNotFoundException e) {

      if (e.getMessage().equals(ErrorCode.ERR_ROLE_NOT_FOUND)) {
        throw new DataNotFoundException(ErrorCode.ERR_ROLE_NOT_FOUND);
      } else {
        throw new DataNotFoundException(ErrorCode.ERR_USER_NOT_FOUND);
      }
      
    } catch (Exception e) {
      LOGGER.info("Fail to change roles of user {}", id);
      throw new UpdateDataFailException(ErrorCode.ERR_USER_ROLES_CHANGED_FAIL);
    }
  }

}
