package com.ecommerce.gut.controller;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.ecommerce.gut.dto.RoleDTO;
import com.ecommerce.gut.dto.UserDTO;
import com.ecommerce.gut.dto.UserProfileDTO;
import com.ecommerce.gut.entity.ERole;
import com.ecommerce.gut.entity.Role;
import com.ecommerce.gut.entity.User;
import com.ecommerce.gut.service.UserService;

import org.modelmapper.ModelMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user")
@Tag(name = "user")
@Validated
public class UserController {
  
  @Autowired
  private UserService userService;

  @Autowired
  private ModelMapper modelMapper;

  @Operation(summary = "Get users per page")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Found the page's users",
          content = @Content(
              array = @ArraySchema(schema = @Schema(implementation = UserDTO.class)),
              mediaType = "application/json")),
      @ApiResponse(responseCode = "400", description = "Invalid data", content = @Content),
  })
  @GetMapping("/page")
  public List<UserDTO> getUsersPerPage(@RequestParam("num") @Min(1) Integer pageNum, @RequestParam("size") @Min(1) Integer pageSize, @RequestParam("sort") @NotNull @NotBlank String sortBy) {
    return userService.getUsersPerPage(pageNum, pageSize, sortBy).stream()
        .map(this::convertUserToDto)
        .collect(Collectors.toList());
  }

  @Operation(summary = "Get the profile of user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Found the profile of user",
          content = @Content(
              schema = @Schema(implementation = UserProfileDTO.class),
              mediaType = "application/json")),
              @ApiResponse(responseCode = "400", description = "Enter invalid data", content = @Content),
      @ApiResponse(responseCode = "404", description = "User Id is not found", content = @Content),
  })
  @GetMapping("/profile/{id}")
  public UserProfileDTO getUserProfileById(@PathVariable("id") @NotNull UUID id) {
    return convertUserProfileToDto(userService.getUserProfileById(id));
  }

  @Operation(summary = "Edit the profile of user by Id",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "User profile object to updated"))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Update a user profile successful", content = @Content),
      @ApiResponse(responseCode = "400", description = "Enter invalid data", content = @Content),
      @ApiResponse(responseCode = "404", description = "User Id is not found", content = @Content),
  })
  @PutMapping("/edit/profile/{id}")
  public ResponseEntity<?> editUserProfile(@Valid @RequestBody UserProfileDTO userDto, @PathVariable("id") @NotNull UUID id) {
    User user = convertUserProfileToEntity(userDto);
    return userService.editUserProfile(user, id);
  }

  @Operation(summary = "Edit the profile of current user by Id",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "User profile object to updated"))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Update a user profile successful", content = @Content),
      @ApiResponse(responseCode = "400", description = "Enter invalid data", content = @Content),
      @ApiResponse(responseCode = "404", description = "User Id is not found", content = @Content),
  })
  @PutMapping("/edit/current/profile/{id}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<?> editCurrentUserProfile(@Valid @RequestBody UserProfileDTO userDto, @PathVariable("id") @NotNull UUID id) {
    User user = convertUserProfileToEntity(userDto);
    return userService.editCurrentUserProfile(user, id);
  }

  @Operation(summary = "Delete a user by Id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Delete a user successful", content = @Content),
      @ApiResponse(responseCode = "404", description = "User Id is not found", content = @Content),
  })
  @DeleteMapping("/delete/{id}")
  public ResponseEntity<?> deleteUser(@PathVariable("id") @NotNull UUID id) {
    return userService.deleteUser(id);
  }

  @Operation(summary = "Deactivate user by Id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Deactivate user successful", content = @Content),
      @ApiResponse(responseCode = "404", description = "User Id is not found", content = @Content),
  })
  @PatchMapping("/deactivate/{id}")
  public ResponseEntity<?> deactivateUser(@PathVariable("id") @NotNull UUID id) {
    return userService.deactivateUser(id);
  }

  @Operation(summary = "Activate user by Id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Activate user successful", content = @Content),
      @ApiResponse(responseCode = "404", description = "User Id is not found", content = @Content),
  })
  @PatchMapping("/activate/{id}")
  public ResponseEntity<?> activateUser(@PathVariable("id") @NotNull UUID id) {
    return userService.activateUser(id);
  }

  @Operation(summary = "Change user roles by Id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Change user roles successful", content = @Content),
      @ApiResponse(responseCode = "404", description = "User Id or roles are not found", content = @Content),
  })
  @PatchMapping("/roles/change/{id}")
  public ResponseEntity<?> changeUserRoles(@PathVariable("id") @NotNull UUID id, @Valid @RequestBody Set<RoleDTO> roleDtos) {
    Set<Role> roles = roleDtos.stream()
        .map(this::convertRoleToEntity)
        .collect(Collectors.toSet());
    return userService.changeUserRoles(id, roles);
  }

  private UserDTO convertUserToDto(User user) {
    UserDTO dto = modelMapper.map(user, UserDTO.class);
    Set<String> roles = user.getRoles().stream()
        .map(role -> role.getName().name())
        .collect(Collectors.toSet());

    dto.setRoles(roles);

    return dto;
  }

  private UserProfileDTO convertUserProfileToDto(User user) {
    UserProfileDTO dto = modelMapper.map(user, UserProfileDTO.class);
    Set<RoleDTO> roles = user.getRoles().stream()
        .map(role -> {
          if (role.getName().equals(ERole.ROLE_ADMIN)) {
            return new RoleDTO(role.getId(), "Admin");
          } else {
            return new RoleDTO(role.getId(), "User");
          }
        })
        .collect(Collectors.toSet());

    dto.setRoles(roles);

    return dto;
  }

  private User convertUserProfileToEntity(UserProfileDTO dto) {
    User user = modelMapper.map(dto, User.class);
  
    Set<Role> roles = dto.getRoles().stream()
        .map(this::convertRoleToEntity)
        .collect(Collectors.toSet());
    
    user.setRoles(roles);
    
    return user;
  }

  private Role convertRoleToEntity(RoleDTO dto) {
    Role role = modelMapper.map(dto, Role.class);

    if (dto.getName().equals("Admin")) {
      role.setName(ERole.ROLE_ADMIN);
    } else {
      role.setName(ERole.ROLE_USER);
    }

    return role;
  }

}
