package com.ecommerce.gut.service;

import com.ecommerce.gut.exception.CreateDataFailException;
import com.ecommerce.gut.exception.DataNotFoundException;
import com.ecommerce.gut.exception.DuplicateDataException;
import com.ecommerce.gut.payload.request.LoginRequest;
import com.ecommerce.gut.payload.request.SignUpRequest;
import com.ecommerce.gut.payload.response.JwtResponse;

public interface AuthService {

  JwtResponse authenticateUser(LoginRequest loginRequest);

  boolean registerUser(SignUpRequest signUpRequest) throws CreateDataFailException, DuplicateDataException, DataNotFoundException;

}
