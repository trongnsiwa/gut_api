package com.ecommerce.gut.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.ecommerce.gut.entity.ERole;
import com.ecommerce.gut.entity.Role;
import com.ecommerce.gut.entity.User;
import com.ecommerce.gut.exception.CustomNotFoundException;
import com.ecommerce.gut.payload.request.LoginRequest;
import com.ecommerce.gut.payload.request.SignUpRequest;
import com.ecommerce.gut.payload.response.JwtResponse;
import com.ecommerce.gut.repository.RoleRepository;
import com.ecommerce.gut.repository.UserRepository;
import com.ecommerce.gut.security.jwt.JwtUtils;
import com.ecommerce.gut.security.service.UserDetailsImpl;
import com.ecommerce.gut.service.AuthService;
import com.ecommerce.gut.util.CustomResponseEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

  private static final String ROLE_NOT_FOUND_MSG = "Role is not found.";

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private PasswordEncoder encoder;

  @Autowired
  private CustomResponseEntity customResponseEntity;

  @Autowired
  private JwtUtils jwtUtils;

  @Override
  public ResponseEntity<?> authenticateUser(LoginRequest loginRequest) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);

    String jwt = jwtUtils.generateJwtToken(authentication);

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    List<String> roles = userDetails.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toList());

    return ResponseEntity.ok()
        .header(jwtUtils.getAuthorizationHeader(), jwtUtils.getTokenPrefix() + jwt)
        .body(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getFirstName(), userDetails.getLastName(), roles));
  }

  @Override
  public ResponseEntity<?> registerUser(SignUpRequest signUpRequest) {

    boolean existedEmail = userRepository.existsByEmail(signUpRequest.getEmail());
    if (existedEmail) {
      return customResponseEntity.generateMessageResponseEntity("Email is already taken.", HttpStatus.CONFLICT);
    }

    User user = new User();
    user.setEmail(signUpRequest.getEmail());
    user.setPassword(encoder.encode(signUpRequest.getPassword()));
    user.setFirstName(signUpRequest.getFirstName());
    user.setLastName(signUpRequest.getLastName());
    user.setEnabled(true);

    Set<String> strRoles = signUpRequest.getRoles();
    Set<Role> roles = new HashSet<>();

    if (strRoles == null) {
      Role userRole =
          roleRepository.findByName(ERole.ROLE_USER).orElseThrow(() -> new CustomNotFoundException(
              ROLE_NOT_FOUND_MSG));
      roles.add(userRole);
    } else {
      strRoles.forEach(role -> {
        if ("admin".equals(role)) {
          Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
              .orElseThrow(() -> new CustomNotFoundException(ROLE_NOT_FOUND_MSG));
          roles.add(adminRole);

        } else {
          Role userRole = roleRepository.findByName(ERole.ROLE_USER)
              .orElseThrow(() -> new CustomNotFoundException(ROLE_NOT_FOUND_MSG));
          roles.add(userRole);
        }

      });
    }

    user.setRoles(roles);
    userRepository.save(user);

    return customResponseEntity.generateMessageResponseEntity("User registered successfully!", HttpStatus.CREATED);
  }
  
}
