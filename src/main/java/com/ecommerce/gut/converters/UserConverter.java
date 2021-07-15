package com.ecommerce.gut.converters;

import java.util.Set;
import java.util.stream.Collectors;
import com.ecommerce.gut.dto.RoleDTO;
import com.ecommerce.gut.dto.UserAvatarDTO;
import com.ecommerce.gut.dto.UserDTO;
import com.ecommerce.gut.dto.UserProfileDTO;
import com.ecommerce.gut.entity.ERole;
import com.ecommerce.gut.entity.Image;
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
    Set<RoleDTO> roles = user.getRoles().stream()
        .map(this::convertRoleToDTO)
        .collect(Collectors.toSet());

    dto.setRoles(roles);
    if (user.getImage() != null) {
      dto.setAvatar(convertImageToDTO(user.getImage()));
    }

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
    if (user.getImage() != null) {
      dto.setAvatar(convertImageToDTO(user.getImage()));
    }

    return dto;
  }

  public User convertUserProfileToEntity(UserProfileDTO dto) {
    User user = modelMapper.map(dto, User.class);
  
    Set<Role> roles = dto.getRoles().stream()
        .map(this::convertRoleToEntity)
        .collect(Collectors.toSet());
    
    user.setRoles(roles);
    if (dto.getAvatar() != null) {
      user.setImage(convertImageToEntity(dto.getAvatar()));
    }
    
    return user;
  }

  public Role convertRoleToEntity(RoleDTO dto) {
    Role role = new Role();
    role.setId(dto.getId());

    if (dto.getName().equals("Admin")) {
      role.setName(ERole.ROLE_ADMIN);
    } else {
      role.setName(ERole.ROLE_USER);
    }

    return role;
  }

  public RoleDTO convertRoleToDTO(Role role) {
    RoleDTO dto = new RoleDTO();
    dto.setId(role.getId());
    if (role.getName().equals(ERole.ROLE_ADMIN)) {
      dto.setName("Admin");
    } else {
      dto.setName("User");
    }

    return dto;
  }

  public Image convertImageToEntity(UserAvatarDTO avatar) {
    return modelMapper.map(avatar, Image.class);
  }

  public UserAvatarDTO convertImageToDTO(Image image) {
    return modelMapper.map(image, UserAvatarDTO.class);
  }

}
