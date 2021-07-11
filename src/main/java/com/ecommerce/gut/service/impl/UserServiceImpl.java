package com.ecommerce.gut.service.impl;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import com.ecommerce.gut.entity.ERole;
import com.ecommerce.gut.entity.Role;
import com.ecommerce.gut.entity.User;
import com.ecommerce.gut.exception.CustomNotFoundException;
import com.ecommerce.gut.repository.RoleRepository;
import com.ecommerce.gut.repository.UserRepository;
import com.ecommerce.gut.security.service.UserDetailsImpl;
import com.ecommerce.gut.service.UserService;
import com.ecommerce.gut.util.CustomResponseEntity;

import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

  private UserRepository userRepository;

  private RoleRepository roleRepository;

  private CustomResponseEntity customResponseEntity;

  private MessageSource messages;

  private HttpServletRequest request;

  public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
      CustomResponseEntity customResponseEntity, MessageSource messages, HttpServletRequest request) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.customResponseEntity = customResponseEntity;
    this.messages = messages;
    this.request = request;
  }

  @Override
  public List<User> getUsersPerPage(Integer pageNum, Integer pageSize, String sortBy) {
    Sort sort = null;
    switch (sortBy) {
      case "EMAIL_A-Z":
        sort = Sort.by("email").ascending()
            .and(Sort.by("roles.name")).ascending()
            .and(Sort.by("modifiedDate").descending())
            .and(Sort.by("enabled").ascending());
        break;
      case "EMAIL_Z-A":
        sort = Sort.by("email").descending()
            .and(Sort.by("roles.name")).ascending()
            .and(Sort.by("modifiedDate").descending())
            .and(Sort.by("enabled").ascending());
        break;
      case "NAME_A-Z":
        sort = Sort.by("lastName").ascending()
            .and(Sort.by("roles.name")).ascending()
            .and(Sort.by("modifiedDate").descending())
            .and(Sort.by("enabled").ascending());
        break;
      case "NAME_Z-A":
        sort = Sort.by("lastName").descending()
            .and(Sort.by("roles.name")).ascending()
            .and(Sort.by("modifiedDate").descending())
            .and(Sort.by("enabled").ascending());
        break;
      default:
        sort = Sort.by("roles.name").ascending()
            .and(Sort.by("modifiedDate").descending()
                .and(Sort.by("enabled").ascending()));
        break;
    }

    PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, sort);
    return userRepository.findAll(pageRequest).getContent();
  }

  @Override
  public User getUserProfileById(UUID id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new CustomNotFoundException(String.format(messages.getMessage("user.message.notFound", null, request.getLocale()), id)));
  }

  @Override
  public ResponseEntity<?> editUserProfile(User user, UUID id) {

    Locale locale = request.getLocale();

    User oldUser = userRepository.findById(id)
        .orElseThrow(() -> new CustomNotFoundException(String.format(messages.getMessage("user.message.notFound", null, locale), id)));

    oldUser.setFirstName(user.getFirstName());
    oldUser.setLastName(user.getLastName());
    oldUser.setAddress(user.getAddress());
    oldUser.setPhone(user.getPhone());

    userRepository.save(oldUser);

    return customResponseEntity.generateMessageResponseEntity(messages.getMessage("user.message.editProfileSucc", null, locale),
        HttpStatus.OK);
  }

  @Override
  public ResponseEntity<?> deleteUser(UUID id) {

    Locale locale = request.getLocale();

    boolean existed = userRepository.existsById(id);
    if (!existed) {
      throw new CustomNotFoundException(String.format(messages.getMessage("user.message.notFound", null, locale), id));
    }

    userRepository.deleteById(id);

    return customResponseEntity.generateMessageResponseEntity(messages.getMessage("user.message.delSucc", null, locale),
        HttpStatus.OK);
  }

  @Override
  public ResponseEntity<?> editCurrentUserProfile(User user, UUID id) {

    Locale locale = request.getLocale();

    User oldUser = userRepository.findById(id)
        .orElseThrow(() -> new CustomNotFoundException(String.format(messages.getMessage("user.message.notFound", null, locale), id)));

    oldUser.setFirstName(user.getFirstName());
    oldUser.setLastName(user.getLastName());
    oldUser.setAddress(user.getAddress());
    oldUser.setPhone(user.getPhone());

    userRepository.save(oldUser);

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    userDetails.setFirstName(user.getFirstName());
    userDetails.setLastName(user.getLastName());
    userDetails.setPhone(user.getPhone());
    userDetails.setAddress(user.getAddress());

    SecurityContextHolder.getContext().setAuthentication(authentication);

    return customResponseEntity.generateMessageResponseEntity(messages.getMessage("user.message.editProfileSucc", null, locale),
        HttpStatus.OK);
  }

  @Override
  public ResponseEntity<?> deactivateUser(UUID id) {

    Locale locale = request.getLocale();

    User oldUser = userRepository.findById(id)
        .orElseThrow(() -> new CustomNotFoundException(String.format(messages.getMessage("user.message.notFound", null, locale), id)));

    oldUser.setEnabled(false);
    userRepository.save(oldUser);

    return customResponseEntity.generateMessageResponseEntity(messages.getMessage("user.message.deactivateSucc", null, locale),
        HttpStatus.OK);
  }

  @Override
  public ResponseEntity<?> activateUser(UUID id) {

    Locale locale = request.getLocale();

    User oldUser = userRepository.findById(id)
        .orElseThrow(() -> new CustomNotFoundException(String.format(messages.getMessage("user.message.notFound", null, locale), id)));

    oldUser.setEnabled(true);
    userRepository.save(oldUser);

    return customResponseEntity.generateMessageResponseEntity(messages.getMessage("user.message.activateSucc", null, locale),
        HttpStatus.OK);
  }

  @Override
  public ResponseEntity<?> changeUserRoles(UUID id, Set<Role> roles) {

    Locale locale = request.getLocale();

    User oldUser = userRepository.findById(id)
        .orElseThrow(() -> new CustomNotFoundException(String.format(messages.getMessage("user.message.notFound", null, locale), id)));

    roles.forEach(role -> {
      if ("admin".equals(role.getName().name())) {
        Optional<Role> adminRole = roleRepository.findByName(ERole.ROLE_ADMIN);
        if (!adminRole.isPresent()) {
          throw new CustomNotFoundException(messages.getMessage("role.message.notFound", null, locale));
        }
      } else {
        Optional<Role> userRole = roleRepository.findByName(ERole.ROLE_USER);
        if (!userRole.isPresent()) {
          throw new CustomNotFoundException(messages.getMessage("role.message.notFound", null, locale));
        }
      }
    });

    oldUser.setRoles(roles);
    userRepository.save(oldUser);

    return customResponseEntity.generateMessageResponseEntity(messages.getMessage("user.message.changeRoles", null, locale),
        HttpStatus.OK);
  }

}
