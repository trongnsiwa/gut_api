package com.ecommerce.gut.payload.request;

import java.util.Objects;
import java.util.Set;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

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

  private Set<String> role;

  public SignUpRequest() {
  }

  public SignUpRequest(String email, String password, String retypePassword, String firstName, String lastName, Set<String> role) {
    this.email = email;
    this.password = password;
    this.retypePassword = retypePassword;
    this.firstName = firstName;
    this.lastName = lastName;
    this.role = role;
  }

  public String getEmail() {
    return this.email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return this.password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getRetypePassword() {
    return this.retypePassword;
  }

  public void setRetypePassword(String retypePassword) {
    this.retypePassword = retypePassword;
  }

  public String getFirstName() {
    return this.firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return this.lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public Set<String> getRole() {
    return this.role;
  }

  public void setRole(Set<String> role) {
    this.role = role;
  }

  @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof SignUpRequest)) {
            return false;
        }
        SignUpRequest signUpRequest = (SignUpRequest) o;
        return Objects.equals(email, signUpRequest.email) && Objects.equals(password, signUpRequest.password) && Objects.equals(retypePassword, signUpRequest.retypePassword) && Objects.equals(firstName, signUpRequest.firstName) && Objects.equals(lastName, signUpRequest.lastName) && Objects.equals(role, signUpRequest.role);
  }

  @Override
  public int hashCode() {
    return Objects.hash(email, password, retypePassword, firstName, lastName, role);
  }

}
