package com.tejnal.stockexchange.security.jwt.advice;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.tejnal.stockexchange.model.response.ExecutionResponse;
import com.tejnal.stockexchange.security.exception.CustomInputException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ValidationExceptionHandler {

  private static final Pattern ENUM_MSG =
      Pattern.compile("values accepted for Enum class: [\\s\\S]*?(?=])");

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ExecutionResponse methodArgumentNotValidException(MethodArgumentNotValidException ex) {
    BindingResult result = ex.getBindingResult();
    List<org.springframework.validation.FieldError> fieldErrors = result.getFieldErrors();
    return processFieldErrors(fieldErrors);
  }

  @ExceptionHandler(CustomInputException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ExecutionResponse handleException(CustomInputException e) {
    return new ExecutionResponse(
        HttpStatus.BAD_REQUEST.value(),
        new Date(),
        "REQUEST PARAM VALIDATION FAILED!",
        e.getMessage());
  }

  private ExecutionResponse processFieldErrors(
      List<org.springframework.validation.FieldError> fieldErrors) {
    String errorMessage = "Field Error ";
    for (org.springframework.validation.FieldError fieldError : fieldErrors) {
      errorMessage =
          errorMessage + " | " + fieldError.getField() + " : " + fieldError.getDefaultMessage();
    }
    return new ExecutionResponse(
        HttpStatus.BAD_REQUEST.value(), new Date(), "REQUEST VALIDATION FAILED!", errorMessage);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ExecutionResponse handleJsonErrors(HttpMessageNotReadableException exception) {
    if (exception.getCause() != null && exception.getCause() instanceof InvalidFormatException) {
      Matcher match = ENUM_MSG.matcher(exception.getCause().getMessage());
      if (match.find()) {
        return new ExecutionResponse(
            HttpStatus.BAD_REQUEST.value(),
            new Date(),
            "REQUEST VALIDATION FAILED!",
            "Value should be: " + match.group(0) + "]");
      }
    }

    return new ExecutionResponse(
        HttpStatus.BAD_REQUEST.value(),
        new Date(),
        "REQUEST VALIDATION FAILED!",
        exception.getMessage());
  }
}
