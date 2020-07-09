/**
 * 
 */
package com.geekwise.auth.module.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Yogesh
 * @since Oct 14, 2019
 * @version 0.1
 */
public class LoginResponseDTO {
	
	private String title;
	private String firstName;
	private String lastName;
	private String email;
	private String token;
	private String tokenType;
	private Integer expiresInSec;
	private long currentTimeInMs;
	private String refreshToken;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getTokenType() {
		return tokenType;
	}
	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}
	public Integer getExpiresInSec() {
		return expiresInSec;
	}
	public void setExpiresInSec(Integer expiresInSec) {
		this.expiresInSec = expiresInSec;
	}
	public long getCurrentTimeInMs() {
		return currentTimeInMs;
	}
	public void setCurrentTimeInMs(long currentTimeInMs) {
		this.currentTimeInMs = currentTimeInMs;
	}
	public String getRefreshToken() {
		return refreshToken;
	}
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
}
