package com.ecommerce.gut.payload.request;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import com.ecommerce.gut.validation.PasswordMatches;
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
@PasswordMatches(message = "{passwordMatches.message}")
public class SignUpRequest {
  @Pattern(
      regexp = "^[_A-Za-z0-9-+]+(.[_A-Za-z0-9-]+)*@"
          + "[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$",
      message = "{signup.email.invalid}"
  )
  private String email;

  @NotBlank(message = "{signup.password.notBlank}")
  @Size(
      min = 5,
      message = "{signup.password.size}"
  )
  private String password;

  private String retypePassword;

  @NotBlank(message = "{signup.firstName.notBlank}")
  @Size(
      min = 1,
      max = 50,
      message = "{signup.firstName.size}"
  )
  private String firstName;

  @NotBlank(message = "{signup.lastName.notBlank}")
  @Size(
      min = 1,
      max = 50,
      message = "{signup.lastName.size}"
  )
  private String lastName;

  private Set<String> roles = new HashSet<>();

}
