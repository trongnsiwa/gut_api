package com.ecommerce.gut.service.impl;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.ecommerce.gut.entity.ERole;
import com.ecommerce.gut.entity.Role;
import com.ecommerce.gut.entity.User;
import com.ecommerce.gut.entity.VerificationToken;
import com.ecommerce.gut.exception.CustomNotFoundException;
import com.ecommerce.gut.payload.request.LoginRequest;
import com.ecommerce.gut.payload.request.SignUpRequest;
import com.ecommerce.gut.payload.response.JwtResponse;
import com.ecommerce.gut.repository.RoleRepository;
import com.ecommerce.gut.repository.UserRepository;
import com.ecommerce.gut.repository.VerificationTokenRepository;
import com.ecommerce.gut.security.jwt.JwtUtils;
import com.ecommerce.gut.security.service.UserDetailsImpl;
import com.ecommerce.gut.service.AuthService;
import com.ecommerce.gut.util.CustomResponseEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.WebRequest;

@Service
public class AuthServiceImpl implements AuthService {

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private VerificationTokenRepository verificationTokenRepository;

  @Autowired
  private PasswordEncoder encoder;

  @Autowired
  private CustomResponseEntity customResponseEntity;

  @Autowired
  private JwtUtils jwtUtils;

  @Autowired
  private MessageSource messages;

  @Autowired
  private JavaMailSender mailSender;

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

    return ResponseEntity.ok()
        .header(jwtUtils.getAuthorizationHeader(), jwtUtils.getTokenPrefix() + jwt)
        .body(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(),
            userDetails.getFirstName(), userDetails.getLastName(), roles));
  }

  @Override
  public ResponseEntity<?> registerUser(SignUpRequest signUpRequest, HttpServletRequest request) {

    Locale locale = request.getLocale();

    boolean existedEmail = userRepository.existsByEmail(signUpRequest.getEmail());
    if (existedEmail) {
      return customResponseEntity.generateMessageResponseEntity("Email is already taken.",
          HttpStatus.CONFLICT);
    }

    User user = new User();
    user.setEmail(signUpRequest.getEmail());
    user.setPassword(encoder.encode(signUpRequest.getPassword()));
    user.setFirstName(signUpRequest.getFirstName());
    user.setLastName(signUpRequest.getLastName());

    Set<String> strRoles = signUpRequest.getRoles();
    Set<Role> roles = new HashSet<>();

    if (strRoles == null) {
      Role userRole =
          roleRepository.findByName(ERole.ROLE_USER).orElseThrow(() -> new CustomNotFoundException(
            messages.getMessage("auth.message.roleNotFound", null, locale)
          ));
      roles.add(userRole);
    } else {
      strRoles.forEach(role -> {
        if ("admin".equals(role)) {
          Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
              .orElseThrow(() -> new CustomNotFoundException(
                messages.getMessage("auth.message.roleNotFound", null, locale)
              ));
          roles.add(adminRole);

        } else {
          Role userRole = roleRepository.findByName(ERole.ROLE_USER)
              .orElseThrow(() -> new CustomNotFoundException(
                messages.getMessage("auth.message.roleNotFound", null, locale)
              ));
          roles.add(userRole);
        }

      });
    }

    user.setRoles(roles);
    user.setEnabled(false);

    userRepository.save(user);

    String token = UUID.randomUUID().toString();
    VerificationToken verificationToken = new VerificationToken(token, user);
    verificationTokenRepository.save(verificationToken);

    String recipientAddress = user.getEmail();
    String subject = "Registration Confirmation";
    String message = messages.getMessage("auth.message.mailRegSucc", null, locale);

    SimpleMailMessage email = new SimpleMailMessage();
    email.setTo(recipientAddress);
    email.setSubject(subject);
    email.setText(message + "\r\n" + token);
    mailSender.send(email);

    String messageSucc = messages.getMessage("auth.message.regSucc", null, locale);
    return customResponseEntity.generateMessageResponseEntity(messageSucc, HttpStatus.CREATED);
  }

  @Override
  public ResponseEntity<?> confirmRegistration(WebRequest request, String token) {
    Locale locale = request.getLocale();

    VerificationToken verificationToken = verificationTokenRepository.findByToken(token).orElseThrow(() -> {
      return new CustomNotFoundException(
        messages.getMessage("auth.message.invalidToken", null, locale)
      );
    });

    User user = verificationToken.getUser();
    Calendar cal = Calendar.getInstance();
    if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
      return customResponseEntity.generateMessageResponseEntity(messages.getMessage("auth.message.expired", null, locale), HttpStatus.BAD_REQUEST);
    }

    user.setEnabled(true);
    userRepository.save(user);

    return customResponseEntity.generateMessageResponseEntity(
      messages.getMessage("auth.message.confirmSucc", null, locale), HttpStatus.OK);
  }

}
