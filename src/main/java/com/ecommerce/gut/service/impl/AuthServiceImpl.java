package com.ecommerce.gut.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.ecommerce.gut.entity.ERole;
import com.ecommerce.gut.entity.Role;
import com.ecommerce.gut.entity.User;
import com.ecommerce.gut.exception.CreateDataFailException;
import com.ecommerce.gut.exception.DataNotFoundException;
import com.ecommerce.gut.exception.DuplicateDataException;
import com.ecommerce.gut.payload.request.LoginRequest;
import com.ecommerce.gut.payload.request.SignUpRequest;
import com.ecommerce.gut.payload.response.ErrorCode;
import com.ecommerce.gut.payload.response.JwtResponse;
import com.ecommerce.gut.repository.RoleRepository;
import com.ecommerce.gut.repository.UserRepository;
import com.ecommerce.gut.security.jwt.JwtUtils;
import com.ecommerce.gut.security.service.UserDetailsImpl;
import com.ecommerce.gut.service.AuthService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AuthServiceImpl.class);

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
  public JwtResponse authenticateUser(LoginRequest loginRequest) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
            loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);

    String jwt = jwtUtils.generateJwtToken(authentication);

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    List<String> roles = userDetails.getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toList());

    return new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getFullName(), userDetails.getAvatar(),  userDetails.getAddress(), userDetails.getPhone(), roles);
  }

  @Override
  public boolean registerUser(SignUpRequest signUpRequest) throws CreateDataFailException, DuplicateDataException {
    try {
      boolean existedEmail = userRepository.existsByEmail(signUpRequest.getEmail());

      if (existedEmail) {
        LOGGER.info("Email {} is already taken", signUpRequest.getEmail());
        throw new DuplicateDataException(ErrorCode.ERR_EMAIL_ALREADY_TAKEN);
      }

      User user = new User();
      user.setEmail(signUpRequest.getEmail());
      user.setPassword(encoder.encode(signUpRequest.getPassword()));
      user.setFirstName(signUpRequest.getFirstName());
      user.setLastName(signUpRequest.getLastName());
      user.setStatus("ACTIVE");

      Set<Role> roles = new HashSet<>();

      Role userRole =
          roleRepository.findByName(ERole.ROLE_USER)
              .orElseThrow(() -> {
                LOGGER.info("Role {} is not found", ERole.ROLE_USER.name());
                return new DataNotFoundException(ErrorCode.ERR_ROLE_NOT_FOUND);
              });

      roles.add(userRole);

      user.setRoles(roles);

      userRepository.save(user);
      
      return true;
    } catch (DuplicateDataException ex) {
      throw new DuplicateDataException(ErrorCode.ERR_EMAIL_ALREADY_TAKEN);
    } catch (Exception ex) {
      LOGGER.info("Fail to create new user {}", signUpRequest.getEmail());
      throw new CreateDataFailException(ErrorCode.ERR_USER_CREATED_FAIL);
    }
  }

}
