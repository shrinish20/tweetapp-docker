package com.tweetapp.webservices;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tweetapp.domain.CustomResponse;
import com.tweetapp.domain.User;
import com.tweetapp.exception.EntityNotFoundException;
import com.tweetapp.exception.JwtTokenException;
import com.tweetapp.exception.NoResultsFoundException;
import com.tweetapp.security.JWTResponse;
import com.tweetapp.security.JWTUtils;
import com.tweetapp.service.UserService;
import com.tweetapp.util.TweetConstant;

import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Class UserController
 *
 */
@Api(tags = "UserController")
@RestController
@EnableAutoConfiguration
@RequestMapping(value = "/api/v1.0/tweets")
public class UserController {

	private UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JWTUtils jwtUtil;

	@Autowired
	private PasswordEncoder passEncoder;

	private static final Logger LOGGER = Logger.getLogger(UserController.class);

	@RequestMapping(value = "/login", method = RequestMethod.POST, consumes = TweetConstant.APPLICATION_JSON)
	public ResponseEntity<JWTResponse> authenticateUser(@RequestBody User user, HttpServletRequest request)
			throws AuthenticationCredentialsNotFoundException {
		LOGGER.info("Entering authenticateUser() API :::: {}");
		try {
			authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(user.getLoginId(), user.getUserPassword()));
		} catch (Exception e) {
			throw new BadCredentialsException("Invalid Credentials");
		}
		final UserDetails userDetails = userService.loadUserByUsername(user.getLoginId());
		final String accessToken = jwtUtil.generateToken(userDetails, "Access", request);
		final String refreshToken = jwtUtil.generateToken(userDetails, "Refresh", request);
		JWTResponse response = new JWTResponse("Login Successful", accessToken, refreshToken);
		LOGGER.info("Exiting authenticateUser() API :::: {}");
		return new ResponseEntity<JWTResponse>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/refreshToken", method = RequestMethod.POST, consumes = TweetConstant.APPLICATION_JSON)
	public ResponseEntity<?> refreshToken(@RequestBody JWTResponse jwtResponse, HttpServletRequest request)
			throws AuthenticationCredentialsNotFoundException {
		LOGGER.info("Entering refreshToken() API :::: {}");
		JWTResponse response = null;
		String token = "";
		try {
			final String userName = jwtUtil.getUsernameFromToken(jwtResponse.getRefreshToken(), request);
			final UserDetails userDetails = userService.loadUserByUsername(userName);
			String isRefreshToken = request.getHeader("isRefreshToken");
			String requestURL = request.getRequestURL().toString();
			if (isRefreshToken != null && isRefreshToken.equals("true") && requestURL.contains("refreshToken")) {
				token = jwtUtil.generateToken(userDetails, "Access", request);
			}
			final String accessToken = token;
			response = new JWTResponse("Access Token Re-generated", accessToken, jwtResponse.getRefreshToken());
		} catch (ExpiredJwtException ex) {
			throw new JwtTokenException("Refresh Token has Expired");
		}
		LOGGER.info("Exiting refreshToken() API :::: {}");
		return new ResponseEntity<JWTResponse>(response, HttpStatus.OK);
	}

	@ApiOperation(value = "Retrieves list of all Users")
	@RequestMapping(value = "/users/all", method = RequestMethod.GET, produces = TweetConstant.APPLICATION_JSON)
	public ResponseEntity<CustomResponse> getUserDetails() {
		LOGGER.info("Entering getUserDetails() API :::: {}");
		CustomResponse response;
		List<User> userList = userService.getUserDetails();
		if (userList.isEmpty()) {
			LOGGER.error("No Results Found");
			throw new NoResultsFoundException("No Results Found");
		} else {
			response = new CustomResponse(HttpStatus.OK, TweetConstant.SUCCESS_RETRIEVED, LocalDateTime.now(),
					userList);
		}
		LOGGER.info("Exiting getUserDetails() API :::: {}");
		return new ResponseEntity<CustomResponse>(response, response.getStatus());
	}

	@ApiOperation(value = "To Register as a User")
	@RequestMapping(value = "/register", method = RequestMethod.POST, consumes = TweetConstant.APPLICATION_JSON)
	public ResponseEntity<CustomResponse> registerUser(@RequestBody User user) {
		LOGGER.info("Entering registerUser() API :::: {}");
		CustomResponse response;
		String password = user.getUserPassword();
		user.setUserPassword(passEncoder.encode(password));
		String result = userService.registerUser(user);
		if ("Success".equalsIgnoreCase(result)) {
			response = new CustomResponse(HttpStatus.CREATED, TweetConstant.REGISTRATION, LocalDateTime.now());
		} else {
			response = new CustomResponse(HttpStatus.UNPROCESSABLE_ENTITY, result, LocalDateTime.now());
		}
		LOGGER.info("Exiting registerUser() API :::: {}");
		return new ResponseEntity<CustomResponse>(response, response.getStatus());
	}

	@ApiOperation(value = "Retrieves User based on Username")
	@RequestMapping(value = "/user/search/{loginId}", method = RequestMethod.GET, produces = TweetConstant.APPLICATION_JSON)
	public ResponseEntity<CustomResponse> searchByUsername(
			@ApiParam(value = "loginId", required = true) @PathVariable("loginId") String loginId) {
		LOGGER.info("Entering searchByUsername() API :::: {}");
		CustomResponse response;
		User userObj = userService.searchByUsername(loginId);

		List<User> userList = (userObj != null) ? Arrays.asList(userObj) : null;
		if (userList == null) {
			LOGGER.error("User Not Found");
			throw new EntityNotFoundException("User Not Found");
		} else {
			response = new CustomResponse(HttpStatus.OK, TweetConstant.SUCCESS_RETRIEVED, LocalDateTime.now(),
					userList);
		}
		LOGGER.info("Exiting searchByUsername() API :::: {}");
		return new ResponseEntity<CustomResponse>(response, response.getStatus());
	}

	@ApiOperation(value = "To Reset Password")
	@RequestMapping(value = "/resetPassword", method = RequestMethod.POST, consumes = TweetConstant.APPLICATION_JSON)
	public ResponseEntity<CustomResponse> resetPassword(@RequestBody User user) {
		LOGGER.info("Entering resetPassword() API :::: {}");
		CustomResponse response;
		String encodePassword = passEncoder.encode(user.getUserPassword());
		user.setUserPassword(encodePassword);
		String result = userService.resetPassword(user);
		if ("Success".equalsIgnoreCase(result)) {
			response = new CustomResponse(HttpStatus.OK, TweetConstant.RESET_SUCCESSFUL, LocalDateTime.now());
		} else {
			response = new CustomResponse(HttpStatus.UNPROCESSABLE_ENTITY, result, LocalDateTime.now());
		}
		LOGGER.info("Exiting resetPassword() API :::: {}");
		return new ResponseEntity<CustomResponse>(response, response.getStatus());
	}

}
