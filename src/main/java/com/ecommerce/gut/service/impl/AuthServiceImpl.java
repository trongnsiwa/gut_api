package com.ecommerce.gut.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import com.ecommerce.gut.entity.ERole;
import com.ecommerce.gut.entity.Role;
import com.ecommerce.gut.entity.User;
import com.ecommerce.gut.payload.request.LoginRequest;
import com.ecommerce.gut.payload.request.SignUpRequest;
import com.ecommerce.gut.payload.response.JwtResponse;
import com.ecommerce.gut.payload.response.MessageResponse;
import com.ecommerce.gut.repository.RoleRepository;
import com.ecommerce.gut.repository.UserRepository;
import com.ecommerce.gut.security.jwt.JwtUtils;
import com.ecommerce.gut.security.service.UserDetailsImpl;
import com.ecommerce.gut.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

  private static final String ROLE_NOT_FOUND_MSG = "Error: Role is not found.";

  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  @Override
  public ResponseEntity<?> authenticateUser(LoginRequest loginRequest) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
            loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);

    String jwt = jwtUtils.generateJwtToken(authentication);

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    List<String> roles = userDetails.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toList());

    return ResponseEntity.ok().header(jwtUtils.getAuthorizationHeader(), jwtUtils.getTokenPrefix() + jwt).body(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(),
    userDetails.getFirstName(), userDetails.getLastName(), roles));
  }

  @Override
  public ResponseEntity<?> registerUser(SignUpRequest signUpRequest) {

    Map<String, String> messages = new HashMap<>();

    boolean existedEmail = userRepository.existsByEmail(signUpRequest.getEmail());
    if (existedEmail) {
      messages.put("error", "Email is already taken.");
      return ResponseEntity.badRequest()
          .body(new MessageResponse(messages));
    }

    User user = new User();
    user.setEmail(signUpRequest.getEmail());
    user.setPassword(encoder.encode(signUpRequest.getPassword()));
    user.setFirstName(signUpRequest.getFirstName());
    user.setLastName(signUpRequest.getLastName());
    user.setEnabled(true);

    Set<String> strRoles = signUpRequest.getRole();
    Set<Role> roles = new HashSet<>();

    if (strRoles == null) {
      Role userRole =
          roleRepository.findByName(ERole.ROLE_USER).orElseThrow(() -> new RuntimeException(
              ROLE_NOT_FOUND_MSG));
      roles.add(userRole);
    } else {
      strRoles.forEach(role -> {
        if ("admin".equals(role)) {
          Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
              .orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND_MSG));
          roles.add(adminRole);

        } else {
          Role userRole = roleRepository.findByName(ERole.ROLE_USER)
              .orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND_MSG));
          roles.add(userRole);
        }

      });
    }

    user.setRoles(roles);
    userRepository.save(user);

    messages.clear();
    messages.put("success", "User registered successfully!");
    return ResponseEntity.ok().body(new MessageResponse(messages));
  }
  
}
