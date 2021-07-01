package com.ecommerce.gut.exception;

import java.util.HashMap;
import java.util.Map;

import com.ecommerce.gut.payload.response.MessageResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.util.WebUtils;

@ControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @Nullable
  public final ResponseEntity<MessageResponse> handleException(Exception ex, WebRequest request) {

    HttpHeaders headers = new HttpHeaders();

    LOGGER.error("Handling " + ex.getClass().getSimpleName() + "  due to " + ex.getMessage());

    if (ex instanceof MethodArgumentNotValidException) {

      HttpStatus status = HttpStatus.BAD_REQUEST;

      MethodArgumentNotValidException manve = (MethodArgumentNotValidException) ex;

      return handleMethodArgumentNotValidException(manve, headers, status, request);
    }

    if (LOGGER.isWarnEnabled()) {
      LOGGER.warn("Unknown exception type: " + ex.getClass().getName());
    }

    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    return handleExceptionInternal(ex, null, headers, status, request);
  }

  protected ResponseEntity<MessageResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status,
      WebRequest request) {

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

    return handleExceptionInternal(ex, new MessageResponse(errors), headers, status, request);
  }

  protected ResponseEntity<MessageResponse> handleExceptionInternal(Exception ex,
  MessageResponse body, HttpHeaders headers, HttpStatus status, WebRequest request) {

    if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
      request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, RequestAttributes.SCOPE_REQUEST);
    }

    return new ResponseEntity<>(body, headers, status);
  }

}
