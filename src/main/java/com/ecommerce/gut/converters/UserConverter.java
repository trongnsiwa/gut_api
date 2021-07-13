package com.ecommerce.gut.converters;

import java.util.Set;
import java.util.stream.Collectors;
import com.ecommerce.gut.dto.RoleDTO;
import com.ecommerce.gut.dto.UserDTO;
import com.ecommerce.gut.dto.UserProfileDTO;
import com.ecommerce.gut.entity.ERole;
import com.ecommerce.gut.entity.Role;
import com.ecommerce.gut.entity.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {

  @Autowired
  private ModelMapper modelMapper;
  
  public UserDTO convertUserToDto(User user) {
    UserDTO dto = modelMapper.map(user, UserDTO.class);
    Set<String> roles = user.getRoles().stream()
        .map(role -> role.getName().name())
        .collect(Collectors.toSet());

    dto.setRoles(roles);

    return dto;
  }

  public UserProfileDTO convertUserProfileToDto(User user) {
    UserProfileDTO dto = modelMapper.map(user, UserProfileDTO.class);
    Set<RoleDTO> roles = user.getRoles().stream()
        .map(role -> {
          if (role.getName().equals(ERole.ROLE_ADMIN)) {
            return new RoleDTO(role.getId(), "Admin");
          } else {
            return new RoleDTO(role.getId(), "User");
          }
        })
        .collect(Collectors.toSet());

    dto.setRoles(roles);

    return dto;
  }

  public User convertUserProfileToEntity(UserProfileDTO dto) {
    User user = modelMapper.map(dto, User.class);
  
    Set<Role> roles = dto.getRoles().stream()
        .map(this::convertRoleToEntity)
        .collect(Collectors.toSet());
    
    user.setRoles(roles);
    
    return user;
  }

  public Role convertRoleToEntity(RoleDTO dto) {
    Role role = modelMapper.map(dto, Role.class);

    if (dto.getName().equals("Admin")) {
      role.setName(ERole.ROLE_ADMIN);
    } else {
      role.setName(ERole.ROLE_USER);
    }

    return role;
  }

}
