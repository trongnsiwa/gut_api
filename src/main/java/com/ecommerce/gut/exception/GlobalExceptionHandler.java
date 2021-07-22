package com.ecommerce.gut.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex,
      WebRequest request) {
    LOGGER.error("Handling " + ex.getClass().getSimpleName() + " due to " + ex.getMessage());

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

    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(AuthException.class)
  public ResponseEntity<?> handleAuthException(
    AuthException ex, WebRequest request) {
    ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
    return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(DataNotFoundException.class)
  public ResponseEntity<?> handleDataNotFoundExceptionException(
      DataNotFoundException ex, WebRequest request) {
    ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
    return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(CreateDataFailException.class)
  public ResponseEntity<?> handleCreateDataFailException(
    CreateDataFailException ex, WebRequest request) {
    ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
    return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(UpdateDataFailException.class)
  public ResponseEntity<?> handleUpdateDataFailException(
    UpdateDataFailException ex, WebRequest request) {
    ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
    return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(DeleteDataFailException.class)
  public ResponseEntity<?> handleDeleteDataFailException(
    DeleteDataFailException ex, WebRequest request) {
    ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
    return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(DuplicateDataException.class)
  public ResponseEntity<?> handleDuplicateDataException(
    DuplicateDataException ex, WebRequest request) {
    ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
    return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(UsernameNotFoundException.class)
  public ResponseEntity<?> handleUsernameNotFoundException(
    UsernameNotFoundException ex, WebRequest request) {
    ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
    return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(RestrictDataException.class)
  public ResponseEntity<?> handleRestrictDataException(
    RestrictDataException ex, WebRequest request) {
    ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
    return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
  }
  
  @ExceptionHandler(LoadDataFailException.class)
  public ResponseEntity<?> handleRestrictDataException(
    LoadDataFailException ex, WebRequest request) {
    ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
    return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
  }
  
  @ExceptionHandler(ConvertEntityDTOException.class)
  public ResponseEntity<?> handleRestrictDataException(
    ConvertEntityDTOException ex, WebRequest request) {
    ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
    return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<?> handleHttpMessageNotReadableException(
      HttpMessageNotReadableException e, WebRequest request)  {

    Map<String, String> errorResponse = new HashMap<>();
    errorResponse.put("message", e.getLocalizedMessage());
    errorResponse.put("status", HttpStatus.BAD_REQUEST.toString());

    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> handleGlobalException(
    Exception ex, WebRequest request) {
    ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
    return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
  }

}
