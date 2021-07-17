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
import com.ecommerce.gut.exception.ConvertEntityDTOException;
import com.ecommerce.gut.payload.response.ErrorCode;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserConverter.class);

  @Autowired
  private ModelMapper modelMapper;

  public UserDTO convertUserToDto(User user) throws ConvertEntityDTOException {
    try {
      UserDTO dto = modelMapper.map(user, UserDTO.class);
      Set<RoleDTO> roles = user.getRoles().stream()
          .map(this::convertRoleToDTO)
          .collect(Collectors.toSet());

      dto.setRoles(roles);
      if (user.getImage() != null) {
        dto.setAvatar(convertImageToDTO(user.getImage()));
      }

      return dto;
    } catch (Exception ex) {
      LOGGER.info("Fail to convert User to UserDTO");
      throw new ConvertEntityDTOException(ErrorCode.ERR_DATA_CONVERT_FAIL);
    }

  }

  public UserProfileDTO convertUserProfileToDto(User user) throws ConvertEntityDTOException {
    try {
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
    } catch (Exception ex) {
      LOGGER.info("Fail to convert User to UserProfileDTO");
      throw new ConvertEntityDTOException(ErrorCode.ERR_DATA_CONVERT_FAIL);
    }
  }

  public User convertUserProfileToEntity(UserProfileDTO dto) throws ConvertEntityDTOException {
    try {
      User user = modelMapper.map(dto, User.class);

      Set<Role> roles = dto.getRoles().stream()
          .map(this::convertRoleToEntity)
          .collect(Collectors.toSet());

      user.setRoles(roles);
      if (dto.getAvatar() != null) {
        user.setImage(convertImageToEntity(dto.getAvatar()));
      }

      return user;
    } catch (Exception ex) {
      LOGGER.info("Fail to convert UserProfileDTO to User");
      throw new ConvertEntityDTOException(ErrorCode.ERR_DATA_CONVERT_FAIL);
    }
  }

  public Role convertRoleToEntity(RoleDTO dto) throws ConvertEntityDTOException {
    try {
      Role role = new Role();
      role.setId(dto.getId());

      if (dto.getName().equals("Admin")) {
        role.setName(ERole.ROLE_ADMIN);
      } else {
        role.setName(ERole.ROLE_USER);
      }

      return role;
    } catch (Exception ex) {
      LOGGER.info("Fail to convert RoleDTO to Role");
      throw new ConvertEntityDTOException(ErrorCode.ERR_DATA_CONVERT_FAIL);
    }
  }

  public RoleDTO convertRoleToDTO(Role role) throws ConvertEntityDTOException {
    try {
      RoleDTO dto = new RoleDTO();
      dto.setId(role.getId());
      if (role.getName().equals(ERole.ROLE_ADMIN)) {
        dto.setName("Admin");
      } else {
        dto.setName("User");
      }

      return dto;
    } catch (Exception ex) {
      LOGGER.info("Fail to convert Role to RoleDTO");
      throw new ConvertEntityDTOException(ErrorCode.ERR_DATA_CONVERT_FAIL);
    }
  }

  public Image convertImageToEntity(UserAvatarDTO avatar) throws ConvertEntityDTOException {
    try {
      return modelMapper.map(avatar, Image.class);
    } catch (Exception ex) {
      LOGGER.info("Fail to convert UserAvatarDTO to Image");
      throw new ConvertEntityDTOException(ErrorCode.ERR_DATA_CONVERT_FAIL);
    }
  }

  public UserAvatarDTO convertImageToDTO(Image image) throws ConvertEntityDTOException {
    try {
      return modelMapper.map(image, UserAvatarDTO.class);
    } catch (Exception ex) {
      LOGGER.info("Fail to convert Image to UserAvatarDTO");
      throw new ConvertEntityDTOException(ErrorCode.ERR_DATA_CONVERT_FAIL);
    }
  }

}
