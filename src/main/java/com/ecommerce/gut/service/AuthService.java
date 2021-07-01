package com.ecommerce.gut.service;

import com.ecommerce.gut.payload.request.LoginRequest;
import com.ecommerce.gut.payload.request.SignUpRequest;
import org.springframework.http.ResponseEntity;

public interface AuthService {

  ResponseEntity<?> authenticateUser(LoginRequest loginRequest);

  ResponseEntity<?> registerUser(SignUpRequest signUpRequest);

}
