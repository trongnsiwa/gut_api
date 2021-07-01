package com.ecommerce.gut.payload.response;

import java.util.List;
import java.util.UUID;

public class JwtResponse {
  private String token;
  private String type = "Bearer";
  private UUID id;
  private String email;
  private String firstName;
  private String lastName;
  private List<String> roles;

  public JwtResponse(String accessToken, UUID id, String email, String firstName, String lastName,
      List<String> roles) {
    this.token = accessToken;
    this.id = id;
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastName;
    this.roles = roles;
  }

  public String getAccessToken() {
    return token;
  }

  public void setAccessToken(String accessToken) {
    this.token = accessToken;
  }

  public String getTokenType() {
    return type;
  }

  public void setTokenType(String tokenType) {
    this.type = tokenType;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public List<String> getRoles() {
    return roles;
  }

}
