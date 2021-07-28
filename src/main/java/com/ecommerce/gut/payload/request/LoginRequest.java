package com.ecommerce.gut.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

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
  @Pattern(
      regexp = "^[_A-Za-z0-9-+]+(.[_A-Za-z0-9-]+)*@"
          + "[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$",
      message = "{signup.email.invalid}"
  )
  private String email;

  @NotBlank(message = "{login.password.notBlank}")
  private String password;

}
