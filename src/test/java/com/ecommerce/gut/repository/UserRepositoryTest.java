package com.ecommerce.gut.repository;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import com.ecommerce.gut.entity.ERole;
import com.ecommerce.gut.entity.Role;
import com.ecommerce.gut.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@SpringBootTest
public class UserRepositoryTest {

  @MockBean
  private UserRepository userRepository;

  @MockBean
  private RoleRepository roleRepository;

  @BeforeEach
  public void setUp() throws Exception {
    Role userRole = new Role(1L, ERole.ROLE_USER);
    Role adminRole = new Role(2L, ERole.ROLE_ADMIN);

    Set<Role> roles = new HashSet<>();
    roles.add(userRole);
    roles.add(adminRole);

    Set<Role> roles2 = new HashSet<>();
    roles2.add(userRole);

    Mockito.when(roleRepository.findByName(ERole.ROLE_USER))
        .thenReturn(Optional.of(userRole));
    Mockito.when(roleRepository.findByName(ERole.ROLE_ADMIN))
        .thenReturn(Optional.of(adminRole));

    User user = new User();
    user.setId(UUID.randomUUID());
    user.setLastName("abc");
    user.setEmail("test@gmail.com");
    user.setRoles(roles);

    User user2 = new User();
    user2.setId(UUID.randomUUID());
    user2.setLastName("test");
    user2.setEmail("abc@gmail.com");
    user2.setRoles(roles);
    
    User user3 = new User();
    user3.setId(UUID.fromString("628c6f73-459c-4c8d-afa5-6345b28f80ef"));
    user3.setLastName("test2");
    user3.setEmail("test2@gmail.com");
    user3.setRoles(roles2);

    when(userRepository.findByEmail(user.getEmail()))
        .thenReturn(Optional.of(user));
    when(userRepository.findByEmail(user2.getEmail()))
        .thenReturn(Optional.of(user2));
    
    when(userRepository.findById(UUID.fromString("628c6f73-459c-4c8d-afa5-6345b28f80ef")))
        .thenReturn(Optional.of(user3));
    
  }

  @Test
  public void testCreateUserSuccess() {
    Role userRole = roleRepository.findByName(ERole.ROLE_USER).get();
    Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN).get();
    Set<Role> roles = new HashSet<>();
    roles.add(userRole);
    roles.add(adminRole);

    User user = new User();
    user.setId(UUID.randomUUID());
    user.setEmail("test@gmail.com");
    user.setRoles(roles);

    User user2 = new User();
    user2.setId(UUID.randomUUID());
    user2.setEmail("test@gmail.com");
    user2.setRoles(roles);

    when(userRepository.save(any(User.class))).thenReturn(user);

    assertNotNull(userRepository.save(user));
  }

  @Test
  public void testGetUsersPerPageSortByNameAscSuccess() {
    User user = userRepository.findByEmail("test@gmail.com").get();
    User user2 = userRepository.findByEmail("abc@gmail.com").get();
    List<User> expected = new ArrayList<>();
    expected.add(user);
    expected.add(user2);

    int pageNum = 1;
    int pageSize = 2;

    Sort sort = Sort.by("lastName").ascending()
        .and(Sort.by("roles.name")).ascending()
        .and(Sort.by("modifiedDate").descending())
        .and(Sort.by("status").ascending());

    PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, sort);

    Page<User> expectedUser = new PageImpl<User>(expected, pageRequest, expected.size());

    when(userRepository.findAll(pageRequest)).thenReturn(expectedUser);

    List<User> actual = userRepository.findAll(pageRequest).getContent();

    assertNotNull(actual);
    assertEquals(2, actual.size());
    assertEquals("abc", actual.get(0).getLastName());
  }

  @Test
  public void testGetUsersPerPageSortByRoleAscSuccess() {
    User user = userRepository.findByEmail("test@gmail.com").get();
    User user2 = userRepository.findByEmail("abc@gmail.com").get();
    List<User> expected = new ArrayList<>();
    expected.add(user);
    expected.add(user2);

    int pageNum = 1;
    int pageSize = 2;

    Sort sort = Sort.by("roles.name").ascending()
    .and(Sort.by("modifiedDate").descending()
        .and(Sort.by("status").ascending()));

    PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, sort);

    Page<User> expectedUser = new PageImpl<User>(expected, pageRequest, expected.size());

    when(userRepository.findAll(pageRequest)).thenReturn(expectedUser);

    List<User> users = userRepository.findAll(pageRequest).getContent();

    ERole erole =
        users.get(0).getRoles().stream().filter((role) -> role.getName().equals(ERole.ROLE_ADMIN))
            .collect(Collectors.toList()).get(0).getName();

    assertNotNull(users);
    assertEquals(2, users.size());
    assertEquals(ERole.ROLE_ADMIN, erole);
  }

  @Test
  public void testEditUserProfileSuccess() {
    User user = userRepository.findById(UUID.fromString("628c6f73-459c-4c8d-afa5-6345b28f80ef")).get();
    user.setFirstName("Tran");
    user.setAddress("Address");
    user.setPhone("0977158941");

    when(userRepository.save(any(User.class))).thenReturn(user);

    assertEquals("Tran", userRepository.save(user).getFirstName());
  }

  @Test
  public void testDeleteUserSuccess() {
    User user = userRepository.findById(UUID.fromString("628c6f73-459c-4c8d-afa5-6345b28f80ef")).get();
    user.setDeleted(true);

    when(userRepository.save(any(User.class))).thenReturn(user);

    assertEquals(true, userRepository.save(user).isDeleted());
  }

  @Test
  public void testDeactiveUserSuccess() {
    User user = userRepository.findById(UUID.fromString("628c6f73-459c-4c8d-afa5-6345b28f80ef")).get();
    user.setStatus("INACTIVE");

    when(userRepository.save(any(User.class))).thenReturn(user);

    assertEquals("INACTIVE", userRepository.save(user).getStatus());
  }

  @Test
  public void testChangeUserRolesSuccess() {
    Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN).get();
    Role userRole = roleRepository.findByName(ERole.ROLE_USER).get();
    Set<Role> roles = new HashSet<>();
    roles.add(adminRole);
    roles.add(userRole);

    User user = userRepository.findById(UUID.fromString("628c6f73-459c-4c8d-afa5-6345b28f80ef")).get();
    user.setRoles(roles);

    when(userRepository.save(any(User.class))).thenReturn(user);

    assertEquals(roles, userRepository.save(user).getRoles());
  }

}
