package com.ecommerce.gut.dto;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleSetDTO {
  
  @NotNull(message = "{profile.id.notNull}")
  @Min(value = 1, message = "{profile.id.min}")
  private UUID userId;

  private Set<RoleDTO> roles = new HashSet<>();

}
