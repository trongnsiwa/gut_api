package com.ecommerce.gut.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import com.ecommerce.gut.entity.ERole;
import com.ecommerce.gut.entity.Role;
import com.ecommerce.gut.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Test
  public void testGetUsersPerPageSortByNameAscSuccess() {
    int pageNum = 1;
    int pageSize = 2;

    Sort sort = Sort.by("lastName").ascending()
        .and(Sort.by("roles.name")).ascending()
        .and(Sort.by("modifiedDate").descending())
        .and(Sort.by("status").ascending());

    PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, sort);

    List<User> users = userRepository.findAll(pageRequest).getContent();

    assertNotNull(users);
    assertEquals(2, users.size());
    assertEquals("Anh", users.get(0).getLastName());
  }

  @Test
  public void testGetUsersPerPageSortByRoleAscSuccess() {
    int pageNum = 1;
    int pageSize = 2;

    Sort sort = Sort.by("roles.name").ascending()
    .and(Sort.by("modifiedDate").descending()
        .and(Sort.by("status").ascending()));

    PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, sort);

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

    assertNotNull(userRepository.save(user));
  }

  @Test
  public void testDeleteUserSuccess() {
    User user = userRepository.findById(UUID.fromString("628c6f73-459c-4c8d-afa5-6345b28f80ef")).get();
    user.setDeleted(true);

    assertNotNull(userRepository.save(user));
  }

  @Test
  public void testDeactiveUserSuccess() {
    User user = userRepository.findById(UUID.fromString("628c6f73-459c-4c8d-afa5-6345b28f80ef")).get();
    user.setStatus("INACTIVE");

    assertNotNull(userRepository.save(user));
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

    assertNotNull(userRepository.save(user));
  }

}
