package com.ecommerce.gut.controller;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import com.ecommerce.gut.converters.UserConverter;
import com.ecommerce.gut.dto.RoleSetDTO;
import com.ecommerce.gut.dto.UserDTO;
import com.ecommerce.gut.dto.UserProfileDTO;
import com.ecommerce.gut.entity.Role;
import com.ecommerce.gut.entity.User;
import com.ecommerce.gut.exception.DataNotFoundException;
import com.ecommerce.gut.exception.DeleteDataFailException;
import com.ecommerce.gut.exception.LoadDataFailException;
import com.ecommerce.gut.exception.UpdateDataFailException;
import com.ecommerce.gut.payload.response.ErrorCode;
import com.ecommerce.gut.payload.response.ResponseDTO;
import com.ecommerce.gut.payload.response.SuccessCode;
import com.ecommerce.gut.service.UserService;

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
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/user")
@Tag(name = "user")
@Validated
public class UserController {

  @Autowired
  private UserService userService;

  @Autowired
  private UserConverter converter;

  @Operation(summary = "Get users per page")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Found the page's users", content = @Content),
      @ApiResponse(responseCode = "400", description = "Invalid data", content = @Content),
  })
  @GetMapping("/page")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ResponseDTO> getUsersPerPage(@RequestParam("num") @Min(1) Integer pageNum,
      @RequestParam("size") @Min(1) Integer pageSize,
      @RequestParam("sort") @NotNull String sortBy) throws LoadDataFailException {
    ResponseDTO response = new ResponseDTO();
    try {
      List<UserDTO> users = userService.getUsersPerPage(pageNum, pageSize, sortBy).stream()
          .map(user -> converter.convertUserToDto(user))
          .collect(Collectors.toList());
      response.setSuccessCode(SuccessCode.USER_LOADED_SUCCESS);
      response.setData(users);
    } catch (Exception ex) {
      throw new LoadDataFailException(ErrorCode.ERR_LOAD_USERS_FAIL);
    }

    return ResponseEntity.ok().body(response);
  }

  @Operation(summary = "Get the profile of user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Found the profile of user", content = @Content),
      @ApiResponse(responseCode = "400", description = "Enter invalid data", content = @Content),
      @ApiResponse(responseCode = "404", description = "User Id is not found", content = @Content),
  })
  @GetMapping("/profile/{id}")
  @PreAuthorize("hasRole('ADMIN') || hasRole('USER')")
  public ResponseEntity<ResponseDTO> getUserProfileById(@PathVariable("id") @NotNull UUID id) {
    ResponseDTO response = new ResponseDTO();
    try {
      User foundUser = userService.getUserProfileById(id);
      UserProfileDTO responseUser = converter.convertUserProfileToDto(foundUser);
      response.setData(responseUser);
      response.setSuccessCode(SuccessCode.USER_LOADED_SUCCESS);
    } catch (Exception ex) {
      response.setErrorCode(ErrorCode.ERR_USER_PROFILE_LOADED_FAIL);
      throw new DataNotFoundException(ErrorCode.ERR_USER_PROFILE_LOADED_FAIL);
    }

    return ResponseEntity.ok().body(response);
  }

  @Operation(summary = "Edit the profile of user by Id",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "User profile object to updated"))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Update a user profile successful",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Enter invalid data", content = @Content),
      @ApiResponse(responseCode = "404", description = "User Id is not found", content = @Content),
  })
  @PutMapping("/profile")
  @PreAuthorize("hasRole('ADMIN') || hasRole('USER')")
  public ResponseEntity<ResponseDTO> editUserProfile(@Valid @RequestBody UserProfileDTO userDto) throws UpdateDataFailException {
    ResponseDTO response = new ResponseDTO();
    try {
      User user = converter.convertUserProfileToEntity(userDto);
      User updatedUser = userService.editUserProfile(user, userDto.getId());
      UserProfileDTO dto = converter.convertUserProfileToDto(updatedUser);
      response.setData(dto);
      response.setSuccessCode(SuccessCode.USER_PROFILE_EDITED_SUCCESS);
    } catch (Exception ex) {
      response.setErrorCode(ErrorCode.ERR_USER_PROFILE_EDITED_FAIL);
      throw new UpdateDataFailException(ErrorCode.ERR_USER_PROFILE_EDITED_FAIL);
    }

    return ResponseEntity.ok().body(response);
  }

  @Operation(summary = "Delete a user by Id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Delete a user successful",
          content = @Content),
      @ApiResponse(responseCode = "404", description = "User Id is not found", content = @Content),
  })
  @DeleteMapping("/delete/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ResponseDTO> deleteUser(@PathVariable("id") @NotNull UUID id)
      throws DeleteDataFailException {
    ResponseDTO response = new ResponseDTO();
    try {
      boolean deleted = userService.deleteUser(id);
      if (deleted) {
        response.setData(null);
        response.setSuccessCode(SuccessCode.USER_DELETED_SUCCESS);
      }
    } catch (Exception ex) {
      response.setErrorCode(ErrorCode.ERR_USER_DELETED_FAIL);
      throw new DeleteDataFailException(ErrorCode.ERR_USER_DELETED_FAIL);
    }

    return ResponseEntity.ok().body(response);
  }

  @Operation(summary = "Deactivate user by Id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Deactivate user successful",
          content = @Content),
      @ApiResponse(responseCode = "404", description = "User Id is not found", content = @Content),
  })
  @PatchMapping("/deactivate/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ResponseDTO> deactivateUser(@PathVariable("id") @NotNull UUID id)
      throws UpdateDataFailException {
    ResponseDTO response = new ResponseDTO();
    try {
      User deactivateUser = userService.deactivateUser(id);
      UserDTO responseUser = converter.convertUserToDto(deactivateUser);
      response.setData(responseUser);
      response.setSuccessCode(SuccessCode.USER_DEACTIVATED_SUCCESS);

    } catch (Exception ex) {
      response.setErrorCode(ErrorCode.ERR_USER_DEACTIVATED_FAIL);
      throw new UpdateDataFailException(ErrorCode.ERR_USER_DEACTIVATED_FAIL);
    }

    return ResponseEntity.ok().body(response);
  }

  @Operation(summary = "Activate user by Id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Activate user successful",
          content = @Content),
      @ApiResponse(responseCode = "404", description = "User Id is not found", content = @Content),
  })
  @PatchMapping("/activate/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> activateUser(@PathVariable("id") @NotNull UUID id)
      throws UpdateDataFailException {
    ResponseDTO response = new ResponseDTO();
    try {
      User activateUser = userService.activateUser(id);
      UserDTO responseUser = converter.convertUserToDto(activateUser);
      response.setData(responseUser);
      response.setSuccessCode(SuccessCode.USER_ACTIVATED_SUCCESS);

    } catch (Exception ex) {
      response.setErrorCode(ErrorCode.ERR_USER_ACTIVATED_FAIL);
      throw new UpdateDataFailException(ErrorCode.ERR_USER_ACTIVATED_FAIL);
    }

    return ResponseEntity.ok().body(response);
  }

  @Operation(summary = "Change user roles by Id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Change user roles successful",
          content = @Content),
      @ApiResponse(responseCode = "404", description = "User Id or roles are not found",
          content = @Content),
  })
  @PatchMapping("/roles")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ResponseDTO> changeUserRoles(@RequestBody RoleSetDTO roleDto) throws UpdateDataFailException {
    ResponseDTO response = new ResponseDTO();
    try {
      Set<Role> roles = roleDto.getRoles().stream()
          .map(role -> converter.convertRoleToEntity(role))
          .collect(Collectors.toSet());
      User updatedUser = userService.changeUserRoles(roleDto.getUserId(), roles);
      UserDTO responseUser = converter.convertUserToDto(updatedUser);
      response.setData(responseUser);
      response.setSuccessCode(SuccessCode.USER_ROLES_CHANGED_SUCCESS);

    } catch (Exception ex) {
      response.setErrorCode(ErrorCode.ERR_USER_ROLES_CHANGED_FAIL);
      throw new UpdateDataFailException(ErrorCode.ERR_USER_ROLES_CHANGED_FAIL);
    }

    return ResponseEntity.ok().body(response);
  }

}
