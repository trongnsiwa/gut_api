package com.ecommerce.gut.payload.response;

import java.util.List;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class JwtResponse {
  private String token;
  private String type = "Bearer";
  private UUID id;
  private String email;
  private String fullname;
  private String avatar;
  private String address;
  private String phone;
  private List<String> roles;

  public JwtResponse(String accessToken, UUID id, String email, String fullname, String avatar, String address, String phone,
      List<String> roles) {
    this.token = accessToken;
    this.id = id;
    this.email = email;
    this.fullname = fullname;
    this.avatar = avatar;
    this.address = address;
    this.phone = phone;
    this.roles = roles;
  }

}
