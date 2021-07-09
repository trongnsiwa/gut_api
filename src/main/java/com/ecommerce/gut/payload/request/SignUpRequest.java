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
@PasswordMatches
public class SignUpRequest {
  @Pattern(
      regexp = "^[_A-Za-z0-9-+]+(.[_A-Za-z0-9-]+)*@"
          + "[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$",
      message = "Invalid email"
  )
  private String email;

  @NotBlank(message = "Password is required.")
  @Size(
      min = 5,
      message = "Password must be at least 5 characters"
  )
  private String password;

  private String retypePassword;

  @NotBlank(message = "First name is required.")
  @Size(
      min = 1,
      max = 50,
      message = "First name accepts only upto 50 characters and minimum 1 character"
  )
  private String firstName;

  @NotBlank(message = "Last name is required.")
  @Size(
      min = 1,
      max = 50,
      message = "Last name accepts only upto 50 characters and minimum 1 character"
  )
  private String lastName;

  private Set<String> roles = new HashSet<>();

}
