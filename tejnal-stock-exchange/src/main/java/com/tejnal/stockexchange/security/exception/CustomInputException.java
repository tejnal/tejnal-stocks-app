package com.tejnal.stockexchange.security.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CustomInputException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public CustomInputException(String message, String description) {
    super(String.format("Failed for [%s]: %s", message, description));
  }
}
