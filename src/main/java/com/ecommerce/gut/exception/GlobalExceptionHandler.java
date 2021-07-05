package com.ecommerce.gut.exception;

import java.util.HashMap;
import java.util.Map;
import com.ecommerce.gut.payload.response.MessageResponse;
import com.ecommerce.gut.payload.response.MessagesResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<MessagesResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex,
      WebRequest request) {
    LOGGER.error("Handling " + ex.getClass().getSimpleName() + " due to " + ex.getMessage());

    HttpHeaders headers = new HttpHeaders();

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach(error -> {
      if (error instanceof FieldError) {
        String fieldName = ((FieldError) error).getField();
        String errorMessage = error.getDefaultMessage();
        errors.put(fieldName, errorMessage);
      } else {
        String objectName = error.getObjectName();
        String errorMessage = error.getDefaultMessage();
        if ("signUpRequest".equalsIgnoreCase(objectName)) {
          errors.put("retypePassword", errorMessage);
        }
      }
    });

    return new ResponseEntity<>(new MessagesResponse(errors), headers, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(CustomNotFoundException.class)
  public ResponseEntity<MessageResponse> handleCustomItemNotFoundException(
      CustomNotFoundException ex) {

    HttpHeaders headers = new HttpHeaders();

    return new ResponseEntity<>(new MessageResponse(ex.getMessage()), headers,
        HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(ConversionFailedException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<String> handleConnversion(RuntimeException ex) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

}
