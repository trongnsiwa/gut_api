package com.ecommerce.gut.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonFormat;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(
    name = "users",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "users_email_key", 
            columnNames = "email"
        )
    })
public class User {

  @Id
  @GenericGenerator(
      name = "UUID", 
      strategy = "org.hibernate.id.UUIDGenerator"
  )
  @GeneratedValue(generator = "UUID")
  @Column(
      name = "user_id", 
      unique = true, 
      nullable = false
  )
  private UUID id;

  @Column(
      name = "email",
      nullable = false, 
      columnDefinition = "TEXT"
  )
  private String email;

  @Column(
      name = "password",
      nullable = false, 
      columnDefinition = "TEXT"
  )
  private String password;

  @Column(
      name = "first_name",
      nullable = false,
      length = 50
  )
  private String firstName;

  @Column(
      name = "last_name",
      nullable = false,
      length = 50
  )
  private String lastName;

  @Column(
      name = "phone",
      length = 20
  )
  private String phone;

  @Column(
      name = "address",
      columnDefinition = "TEXT"
  )
  private String address;

  @CreationTimestamp
  @Column(name = "registration_date")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date registrationDate;

  private boolean enabled;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "user_roles",
      joinColumns = @JoinColumn(
          name = "user_id"),
      inverseJoinColumns = @JoinColumn(
          name = "role_id"))
  private Set<Role> roles = new HashSet<>();


  public User() {
  }

  public UUID getId() {
    return this.id;
  }

  public void setId(UUID id) {
    this.id = id;
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

  public String getPhone() {
    return this.phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getAddress() {
    return this.address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public Date getRegistrationDate() {
    return this.registrationDate;
  }

  public void setRegistrationDate(Date registrationDate) {
    this.registrationDate = registrationDate;
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public boolean getEnabled() {
    return this.enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public Set<Role> getRoles() {
    return this.roles;
  }

  public void setRoles(Set<Role> roles) {
    this.roles = roles;
  }


  @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof User)) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(email, user.email) && Objects.equals(password, user.password) && Objects.equals(firstName, user.firstName) && Objects.equals(lastName, user.lastName) && Objects.equals(phone, user.phone) && Objects.equals(address, user.address) && Objects.equals(registrationDate, user.registrationDate) && enabled == user.enabled && Objects.equals(roles, user.roles);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, email, password, firstName, lastName, phone, address, registrationDate, enabled, roles);
  }

}
