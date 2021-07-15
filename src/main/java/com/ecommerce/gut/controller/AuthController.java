package com.ecommerce.gut.controller;

import javax.validation.Valid;
import com.ecommerce.gut.exception.CreateDataFailException;
import com.ecommerce.gut.payload.request.LoginRequest;
import com.ecommerce.gut.payload.request.SignUpRequest;
import com.ecommerce.gut.payload.response.ErrorCode;
import com.ecommerce.gut.payload.response.JwtResponse;
import com.ecommerce.gut.payload.response.ResponseDTO;
import com.ecommerce.gut.payload.response.SuccessCode;
import com.ecommerce.gut.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
@Tag(name = "auth")
public class AuthController {

  @Autowired
  private AuthService authService;

  @Operation(summary = "Login and authenticate the username and password",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Login information of user to authenticate"))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Login successful", content = @Content),
      @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
      @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
  })
  @PostMapping("/login")
  public ResponseEntity<ResponseDTO> authenticateUser(
      @Valid @RequestBody LoginRequest loginRequest) {
    ResponseDTO response = new ResponseDTO();
    JwtResponse jwtResponse = authService.authenticateUser(loginRequest);
    response.setSuccessCode(SuccessCode.USER_LOGIN_SUCCESS);
    response.setData(jwtResponse);

    return ResponseEntity.ok()
        .header("AUTHORIZATION", jwtResponse.getType() + " " + jwtResponse.getToken())
        .body(response);
  }

  @Operation(summary = "Create new user",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Sign up information of user to be create new"))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Signup successful", content = @Content),
      @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
      @ApiResponse(responseCode = "404", description = "Role is not found", content = @Content),
      @ApiResponse(responseCode = "409", description = "Email is already taken",
          content = @Content),
  })
  @PostMapping("/signup")
  public ResponseEntity<ResponseDTO> registerUser(@Valid @RequestBody SignUpRequest signUpRequest)
      throws CreateDataFailException {
    ResponseDTO response = new ResponseDTO();
    try {
      boolean registered = authService.registerUser(signUpRequest);
      if (registered) {
        response.setData(null);
        response.setSuccessCode(SuccessCode.USER_SIGNUP_SUCCESS);
      }
    } catch (Exception ex) {
      response.setErrorCode(ErrorCode.ERR_USER_CREATED_FAIL);
      throw new CreateDataFailException(ErrorCode.ERR_USER_CREATED_FAIL);
    }
    return ResponseEntity.ok().body(response);
  }

}
