package com.ecommerce.gut.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
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
import org.hibernate.annotations.UpdateTimestamp;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "users_email_key",
                        columnNames = "email")
        })
public class User {

    @Id
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "UUID")
    @Column(
            name = "user_id",
            unique = true,
            nullable = false)
    private UUID id;

    @Column(
            name = "email",
            nullable = false,
            columnDefinition = "TEXT")
    private String email;

    @Column(
            name = "password",
            nullable = false,
            columnDefinition = "TEXT")
    private String password;

    @Column(
            name = "first_name",
            nullable = false,
            length = 50)
    private String firstName;

    @Column(
            name = "last_name",
            nullable = false,
            length = 50)
    private String lastName;

    @Column(
            name = "phone",
            length = 20)
    private String phone;

    @Column(
            name = "address",
            columnDefinition = "TEXT")
    private String address;

    @CreationTimestamp
    @Column(name = "registration_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime registrationDate;

    @UpdateTimestamp
    @Column(name = "modified_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDate;

    @Column(name = "is_deleted")
    private boolean deleted;

    @Column(name = "status", length = 20)
    private String status;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(
                    name = "user_id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public User() {
      this.deleted = false;
    }

}
