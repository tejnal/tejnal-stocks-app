package com.tejnal.stockexchange.security.jwt.advice;

import com.tejnal.stockexchange.model.response.ExecutionResponse;
import com.tejnal.stockexchange.security.exception.TokenRefreshException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@RestControllerAdvice
public class TokenControllerAdvice {
	@ExceptionHandler(value = TokenRefreshException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ExecutionResponse handleTokenRefreshException(TokenRefreshException ex, WebRequest request) {
		return new ExecutionResponse(HttpStatus.FORBIDDEN.value(), new Date(), ex.getMessage(),
				request.getDescription(false));
	}
}
