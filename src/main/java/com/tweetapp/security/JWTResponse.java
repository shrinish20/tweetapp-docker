package com.tweetapp.security;

public class JWTResponse {
	
	public String loginStatus;
	
	private String accessToken;
	
	private String refreshToken;
	
	private String Type = "Bearer";

	/**
	 * @param loginStatus
	 * @param accessToken
	 */
	public JWTResponse(String loginStatus, String accessToken, String refreshToken) {
		
		this.loginStatus = loginStatus;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}

	/**
	 * @param accessToken
	 */
	public JWTResponse(String accessToken) {
		this.accessToken = accessToken;
	}
	
	/**
	 * @return the loginStatus
	 */
	public String getLoginStatus() {
		return loginStatus;
	}

	/**
	 * @param loginStatus the loginStatus to set
	 */
	public void setLoginStatus(String loginStatus) {
		this.loginStatus = loginStatus;
	}

	/**
	 * @return the accessToken
	 */
	public String getAccessToken() {
		return accessToken;
	}

	/**
	 * @param accessToken the accessToken to set
	 */
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	/**
	 * @return the refreshToken
	 */
	public String getRefreshToken() {
		return refreshToken;
	}

	/**
	 * @param refreshToken the refreshToken to set
	 */
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return Type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		Type = type;
	}
	
}
