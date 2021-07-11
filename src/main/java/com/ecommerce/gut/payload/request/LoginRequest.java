package com.ecommerce.gut.payload.request;

import javax.validation.constraints.NotBlank;
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
public class LoginRequest {
  
  @NotBlank(message = "{login.email.notBlank}")
  private String email;

  @NotBlank(message = "{login.password.notBlank}")
  private String password;

}
