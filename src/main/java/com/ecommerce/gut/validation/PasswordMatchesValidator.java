package com.ecommerce.gut.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import com.ecommerce.gut.payload.request.SignUpRequest;

public class PasswordMatchesValidator
    implements ConstraintValidator<PasswordMatches, SignUpRequest> {

  @Override
  public void initialize(PasswordMatches pm) {}

  @Override
  public boolean isValid(SignUpRequest user, ConstraintValidatorContext context) {
    return user.getPassword().equals(user.getRetypePassword());
  }

}
