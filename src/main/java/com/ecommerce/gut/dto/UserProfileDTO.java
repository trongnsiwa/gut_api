package com.ecommerce.gut.dto;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
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
public class UserProfileDTO {

  @NotNull(message = "{profile.id.notNull}")
  @Min(value = 1, message = "{profile.id.min}")
  private UUID id;

  @Pattern(
      regexp = "^[_A-Za-z0-9-+]+(.[_A-Za-z0-9-]+)*@"
          + "[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$",
      message = "{profile.email.invalid}"
  )
  private String email;

  @NotBlank(message = "{profile.firstName.notBlank}")
  @Size(
      min = 1,
      max = 50,
      message = "{profile.firstName.size}"
  )
  private String firstName;

  @NotBlank(message = "{profile.lastName.notBlank}")
  @Size(
      min = 1,
      max = 50,
      message = "{profile.lastName.size}"
  )
  private String lastName;

  @Pattern(
    regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b",
    message = "{profile.phone.invalid}"
  )
  private String phone;

  private String address;

  private String status;

  private boolean deleted;
  
  private Set<RoleDTO> roles = new HashSet<>();

  private UserAvatarDTO avatar;

}
