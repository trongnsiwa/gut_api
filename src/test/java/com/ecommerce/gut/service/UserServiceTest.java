package com.ecommerce.gut.service;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import com.ecommerce.gut.entity.ERole;
import com.ecommerce.gut.entity.Role;
import com.ecommerce.gut.entity.User;
import com.ecommerce.gut.exception.DataNotFoundException;
import com.ecommerce.gut.exception.LoadDataFailException;
import com.ecommerce.gut.repository.RoleRepository;
import com.ecommerce.gut.repository.UserRepository;
import com.ecommerce.gut.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@SpringBootTest
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    public void setUp() {

        Role userRole = new Role(1L, ERole.ROLE_USER);
        Role adminRole = new Role(2L, ERole.ROLE_ADMIN);

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        roles.add(adminRole);

        Set<Role> roles2 = new HashSet<>();
        roles2.add(userRole);

        // Mockito.when(roleRepository.findByName(ERole.ROLE_USER))
        //         .thenReturn(Optional.of(userRole));
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

        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);
    }

    @Test
    public void testGetUsersPerPageSortByNameAscSuccess() throws LoadDataFailException {
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

        assertEquals(userService.getUsersPerPage(pageNum, pageSize, "NAME_A-Z"), expected);
    }

    @Test
    public void testGetUserProfileByIdSuccess() {
        assertNotNull(userService
                .getUserProfileById(UUID.fromString("628c6f73-459c-4c8d-afa5-6345b28f80ef")));
    }

    @Test
    public void testEditUserProfileSuccess() throws Exception {
        User user3 = new User();
        user3.setId(UUID.fromString("628c6f73-459c-4c8d-afa5-6345b28f80ef"));
        user3.setFirstName("Trong");

        User actualUser = userService.editUserProfile(user3,
                UUID.fromString("628c6f73-459c-4c8d-afa5-6345b28f80ef"));

        assertNotNull(actualUser);
        assertEquals(user3.getFirstName(), actualUser.getFirstName());
    }

    @Test
    public void testDeleteUserSuccess() throws Exception {
        assertEquals(true,
                userService.deleteUser(UUID.fromString("628c6f73-459c-4c8d-afa5-6345b28f80ef")));
    }

    @Test
    public void testDeactiveUserSuccess() throws Exception {
        assertEquals("INACTIVE",
                userService.deactivateUser(UUID.fromString("628c6f73-459c-4c8d-afa5-6345b28f80ef"))
                        .getStatus());
    }

    @Test
    public void testActiveUserSuccess() throws Exception {
        assertEquals("ACTIVE", userService
                .activateUser(UUID.fromString("628c6f73-459c-4c8d-afa5-6345b28f80ef")).getStatus());
    }

    @Test
    public void testChangeUserRolesSuccess() throws Exception {
        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN).get();
        Role userRole = roleRepository.findByName(ERole.ROLE_USER).get();
        Set<Role> roles = new HashSet<>();
        roles.add(adminRole);
        roles.add(userRole);

        assertEquals(2, userService
                .changeUserRoles(UUID.fromString("628c6f73-459c-4c8d-afa5-6345b28f80ef"), roles)
                .getRoles().size());
    }

    @Test
    public void testChangeUserRolesFail() throws Exception {
        Role role = new Role(1L, ERole.ROLE_USER);
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        assertThrows(DataNotFoundException.class, () -> userService
                .changeUserRoles(UUID.fromString("628c6f73-459c-4c8d-afa5-6345b2880ef"), roles));
    }

}
