package com.tweetapp.domain;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomResponse {

	private HttpStatus status;

	private String statusMessage;

	private LocalDateTime timestamp;

	private List<?> message;

	/**
	 * 
	 */
	public CustomResponse() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Constructor
	 * 
	 * @param status
	 * @param statusMessage
	 * @param timestamp
	 */
	public CustomResponse(HttpStatus status, String statusMessage, LocalDateTime timestamp) {
		super();
		this.status = status;
		this.statusMessage = statusMessage;
		this.timestamp = timestamp;
	}

	/**
	 * @param status
	 * @param statusMessage
	 * @param timestamp
	 * @param message
	 */
	public CustomResponse(HttpStatus status, String statusMessage, LocalDateTime timestamp, List<?> message) {
		super();
		this.status = status;
		this.statusMessage = statusMessage;
		this.timestamp = timestamp;
		this.message = message;
	}

	/**
	 * @return the status
	 */
	public HttpStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	/**
	 * @return the statusMessage
	 */
	public String getStatusMessage() {
		return statusMessage;
	}

	/**
	 * @param statusMessage the statusMessage to set
	 */
	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	/**
	 * @return the timestamp
	 */
	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return the message
	 */
	public List<?> getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(List<?> message) {
		this.message = message;
	}

}
