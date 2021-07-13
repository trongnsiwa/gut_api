package com.ecommerce.gut.security.service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import com.ecommerce.gut.entity.Role;
import com.ecommerce.gut.entity.User;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserDetailsImpl implements UserDetails {
  private static final long serialVersionUID = 1L;

  private UUID id;

  private String email;

  @JsonIgnore
  private String password;

  private String firstName;

  private String lastName;

  private String phone;
  
  private String address;

  private boolean enabled;

  private Collection<? extends GrantedAuthority> authorities;

  public UserDetailsImpl(UUID id, String email, String password, String firstName, String lastName, String phone, String address,
      String status,
      Collection<? extends GrantedAuthority> authorities) {
    this.id = id;
    this.email = email;
    this.password = password;
    this.firstName = firstName;
    this.lastName = lastName;
    this.phone = phone;
    this.address = address;
    this.authorities = authorities;
    if ("ACTIVE".equalsIgnoreCase(status)) {
      this.enabled = true;
    } else {
      this.enabled = false;
    }
  }

  public static UserDetailsImpl build(User user) {
    List<GrantedAuthority> authorities =
        user.getRoles()
            .stream()
            .map(role -> new SimpleGrantedAuthority(role.getName().name()))
            .collect(Collectors.toList());

    return new UserDetailsImpl(
        user.getId(), 
        user.getEmail(), 
        user.getPassword(),
        user.getFirstName(), 
        user.getLastName(), 
        user.getPhone(),
        user.getAddress(),
        user.getStatus(), 
        authorities
    );
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  public UUID getId() {
    return id;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getFullName() {
    return this.firstName + " " + this.lastName;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public void setAuthorities(Set<Role> roles) {
    this.authorities = roles.stream()
        .map(role -> new SimpleGrantedAuthority(role.getName().name()))
        .collect(Collectors.toList());
  }
  

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    UserDetailsImpl user = (UserDetailsImpl) o;
    return Objects.equals(id, user.id);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

}
