package com.ecommerce.gut.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.ecommerce.gut.payload.request.LoginRequest;
import com.ecommerce.gut.payload.request.SignUpRequest;
import com.ecommerce.gut.service.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
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
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    return authService.authenticateUser(loginRequest);
  }

  @Operation(summary = "Create new user", 
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Sign up information of user to be create new"))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Signup successful", content = @Content),
      @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
      @ApiResponse(responseCode = "404", description = "Role is not found", content = @Content),
      @ApiResponse(responseCode = "409", description = "Email is already taken", content = @Content),
  })
  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest, HttpServletRequest request) {
    return authService.registerUser(signUpRequest, request);
  }

  @Operation(summary = "Confirm verified token after register")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Confirm successful", content = @Content),
      @ApiResponse(responseCode = "400", description = "Token is expired", content = @Content),
      @ApiResponse(responseCode = "404", description = "Token is invalid", content = @Content),
  })
  @GetMapping("/confirm")
  public ResponseEntity<?> confirmRegistration(WebRequest request, @RequestParam("token") String token) {
    return authService.confirmRegistration(request, token);
  }

}
