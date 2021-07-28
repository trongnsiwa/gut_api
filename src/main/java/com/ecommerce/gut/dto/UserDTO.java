package com.ecommerce.gut.dto;

import java.time.LocalDateTime;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserDTO {

  private UUID id;
  private String email;
  private String fullName;
  private UserAvatarDTO avatar;
  private String status;
  private boolean deleted;
  private Set<RoleDTO> roles = new HashSet<>();
  private LocalDateTime registrationDate;
  private LocalDateTime modifiedDate;

}
