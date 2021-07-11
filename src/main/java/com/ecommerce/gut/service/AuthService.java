package com.ecommerce.gut.service;

import javax.servlet.http.HttpServletRequest;
import com.ecommerce.gut.payload.request.LoginRequest;
import com.ecommerce.gut.payload.request.SignUpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

public interface AuthService {

  ResponseEntity<?> authenticateUser(LoginRequest loginRequest);

  ResponseEntity<?> registerUser(SignUpRequest signUpRequest, HttpServletRequest request);

  ResponseEntity<?> confirmRegistration(WebRequest request, String token);

}
