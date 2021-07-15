package com.ecommerce.gut.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
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
        @Type(type = "org.hibernate.type.PostgresUUIDType")
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
        private LocalDateTime registrationDate;

        @UpdateTimestamp
        @Column(name = "modified_date")
        private LocalDateTime modifiedDate;

        @Column(name = "is_deleted")
        private boolean deleted;

        @Column(name = "status", length = 20)
        private String status;

        @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
        @JoinColumn(name = "image_id")
        private Image image;

        @ManyToMany(fetch = FetchType.EAGER)
        @JoinTable(
                name = "user_roles",
                joinColumns = @JoinColumn(
                        name = "user_id"),
                inverseJoinColumns = @JoinColumn(
                        name = "role_id"))
        private Set<Role> roles = new HashSet<>();

        @OneToOne(mappedBy = "user")
        private Cart cart;

        public User() {
                this.deleted = false;
        }

}
