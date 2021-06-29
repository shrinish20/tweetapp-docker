package com.tweetapp.security;

import java.io.IOException;
import java.time.LocalDateTime;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tweetapp.domain.CustomResponse;

@Component
public class JWTAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private static final Logger LOGGER = LoggerFactory.getLogger(JWTAuthenticationEntryPoint.class);

	@Autowired
	ObjectMapper mapper;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		LOGGER.debug("Unauthorized Error{} :::: {}", authException.getMessage());
		final String expired = (String) request.getAttribute("Expired");
		final String malformed = (String) request.getAttribute("Malformed");
		final String signature = (String) request.getAttribute("Signature");
		String status = "";
		if (expired != null) {
			status = "Token Has Expired";
		} else if (malformed != null) {
			status = "Invalid JWT Token";
		} else if (signature != null) {
			status = "JWT Signature is not Valid";
		}
		ServletServerHttpResponse resp = new ServletServerHttpResponse(response);
		resp.setStatusCode(HttpStatus.UNAUTHORIZED);
		resp.getServletResponse().setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		resp.getBody()
				.write(mapper
						.writeValueAsString(new CustomResponse(HttpStatus.UNAUTHORIZED, status, LocalDateTime.now()))
						.getBytes());
		resp.close();
	}
}
