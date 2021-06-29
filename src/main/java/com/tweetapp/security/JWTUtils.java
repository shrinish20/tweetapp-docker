package com.tweetapp.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;

@Component
public class JWTUtils {

	@Value("${jwt.secret}")
	private String secretKey;

	private long accessTokenExpiraton;

	private long refreshTokenExpiration;

	/**
	 * @param accessTokenExpiraton the accessTokenExpiraton to set
	 */
	@Value("${jwt.accessexpiration}")
	public void setAccessTokenExpiraton(long accessTokenExpiraton) {
		this.accessTokenExpiraton = accessTokenExpiraton;
	}

	/**
	 * @param refreshTokenExpiration the refreshTokenExpiration to set
	 */
	@Value("${jwt.refreshexpiration}")
	public void setRefreshTokenExpiration(long refreshTokenExpiration) {
		this.refreshTokenExpiration = refreshTokenExpiration;
	}

	/**
	 * Method to get username from Token
	 * 
	 * @param token
	 * @return
	 */
	public String getUsernameFromToken(String token, HttpServletRequest request) {
		return getClaimFromToken(token, Claims::getSubject, request);
	}

	/**
	 * Method to check the Token Expiration
	 * 
	 * @param token
	 * @return
	 */
	public Boolean isTokenExpired(String token, HttpServletRequest request) {
		try {
		final Date expiration = getExpirationDateFromToken(token, request);
		return expiration.before(new Date());
		}catch (ExpiredJwtException exp) {
			request.setAttribute("Expired", exp.getMessage());
			throw new ExpiredJwtException(null, null, exp.getMessage());
		}
	}

	/**
	 * Method to get ExpirationDate from Token
	 * 
	 * @param token
	 * @return
	 */
	public Date getExpirationDateFromToken(String token, HttpServletRequest request) {
		return getClaimFromToken(token, Claims::getExpiration, request);
	}

	/**
	 * Method to get claims from Token
	 * 
	 * @param <T>
	 * @param token
	 * @param claimsResolver
	 * @return
	 */
	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver, HttpServletRequest request) {
		final Claims claims = getAllClaimsFromToken(token, request);
		return claimsResolver.apply(claims);
	}

	/**
	 * Method to get all claims from Token
	 * 
	 * @param token
	 * @param request
	 * @return
	 */
	private Claims getAllClaimsFromToken(String token, HttpServletRequest request) {
		try {
			return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
		} catch (ExpiredJwtException exp) {
			request.setAttribute("Expired", exp.getMessage());
			throw new ExpiredJwtException(null, null, exp.getMessage());
		} catch (MalformedJwtException exp) {
			request.setAttribute("Malformed", exp.getMessage());
			throw new MalformedJwtException(exp.getMessage());
		} catch (SignatureException exp) {
			request.setAttribute("Signature", exp.getMessage());
			throw new SignatureException(exp.getMessage());
		}
	}

	/**
	 * Method to Generate Token for User
	 * 
	 * @param userDetails
	 * @param string
	 * @return
	 */
	public String generateToken(UserDetails userDetails, String token, HttpServletRequest request) {

		Map<String, Object> claims = new HashMap<>();
		if ("Access".equalsIgnoreCase(token)) {
			return Jwts.builder().setClaims(claims).setSubject(userDetails.getUsername())
					.setIssuedAt(new Date(System.currentTimeMillis()))
					.setExpiration(new Date(System.currentTimeMillis() + (accessTokenExpiraton*1000)))
					.signWith(SignatureAlgorithm.HS512, secretKey).compact();
		} else {
			return Jwts.builder().setClaims(claims).setSubject(userDetails.getUsername())
					.setIssuedAt(new Date(System.currentTimeMillis()))
					.setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration*1000))
					.signWith(SignatureAlgorithm.HS512, secretKey).compact();
		}
	}

	/**
	 * Method to Validate Token
	 * 
	 * @param token
	 * @param userDetails
	 * @return
	 */
	public Boolean validateToken(String token, UserDetails userDetails, HttpServletRequest request) {
		try {
		final String username = getUsernameFromToken(token, request);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token, request));
		} catch (ExpiredJwtException exp) {
			throw new ExpiredJwtException(null, null, exp.getMessage());
		}
	}
}
