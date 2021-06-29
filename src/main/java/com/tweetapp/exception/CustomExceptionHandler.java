package com.tweetapp.exception;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.springframework.data.mapping.MappingException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.tweetapp.domain.CustomResponse;

@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler({ NoResultsFoundException.class, EntityNotFoundException.class })
	public ResponseEntity<Object> handleNoResultsException(Exception exception) {
		List<?> defaultList = Collections.EMPTY_LIST;
		CustomResponse response = new CustomResponse(HttpStatus.NOT_FOUND, exception.getMessage(), LocalDateTime.now(),
				defaultList);
		return ResponseEntity.status(response.getStatus()).body(response);
	}
	
	@ExceptionHandler(MappingException.class)
	public ResponseEntity<Object> mappingException(MappingException exception) {
		List<?> defaultList = Collections.EMPTY_LIST;
		CustomResponse response = new CustomResponse(HttpStatus.NOT_FOUND, exception.getMessage(), LocalDateTime.now(),
				defaultList);
		return ResponseEntity.status(response.getStatus()).body(response);
	}	
	
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<Object> credentialException(BadCredentialsException exception) {
		List<?> defaultList = Collections.EMPTY_LIST;
		CustomResponse response = new CustomResponse(HttpStatus.UNAUTHORIZED, exception.getMessage(), LocalDateTime.now(),
				defaultList);
		return ResponseEntity.status(response.getStatus()).body(response);
	}	

	@Override
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		List<?> defaultList = Collections.EMPTY_LIST;
		CustomResponse response = new CustomResponse(status, ex.getMessage(), LocalDateTime.now(),
				defaultList);
		return ResponseEntity.status(response.getStatus()).body(response);
	}

	@Override
	protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		List<?> defaultList = Collections.EMPTY_LIST;
		CustomResponse response = new CustomResponse(status, ex.getMessage(), LocalDateTime.now(),
				defaultList);
		return ResponseEntity.status(response.getStatus()).body(response);
	}

	@Override
	protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		List<?> defaultList = Collections.EMPTY_LIST;
		CustomResponse response = new CustomResponse(status, ex.getMessage(), LocalDateTime.now(),
				defaultList);
		return ResponseEntity.status(response.getStatus()).body(response);
	}	
	
	@ExceptionHandler(JwtTokenException.class)
	public ResponseEntity<Object> tokenExpiredException(JwtTokenException exception) {
		List<?> defaultList = Collections.EMPTY_LIST;
		CustomResponse response = new CustomResponse(HttpStatus.UNAUTHORIZED, "Refresh Token has Expired", LocalDateTime.now(),
				defaultList);
		return ResponseEntity.status(response.getStatus()).body(response);
	}

}
