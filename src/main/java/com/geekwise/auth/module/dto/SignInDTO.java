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
public class SignInDTO {
	
	private String title;
	private String firstName;
	private String lastName;
	private String email;
	private String token;
	private String tokenType;
	private Date tokenLastUpdate;	
	private Date tokenExpiryDate;
	
	private List<String> exceptionsList = new ArrayList<>();
	private List<String> challengesList = new ArrayList<>();
	private Integer expiresInSec;
	private long currentTimeInMs;
	private String refreshToken;
	
	private String password;
	private String newPassword;
	
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
	public Date getTokenLastUpdate() {
		return tokenLastUpdate;
	}
	public void setTokenLastUpdate(Date tokenLastUpdate) {
		this.tokenLastUpdate = tokenLastUpdate;
	}
	public Date getTokenExpiryDate() {
		return tokenExpiryDate;
	}
	public void setTokenExpiryDate(Date tokenExpiryDate) {
		this.tokenExpiryDate = tokenExpiryDate;
	}
	
	public List<String> getExceptionsList() {
		return exceptionsList;
	}
	public void setExceptionsList(List<String> exceptionsList) {
		this.exceptionsList = exceptionsList;
	}
	public List<String> getChallengesList() {
		return challengesList;
	}
	public void setChallengesList(List<String> challengesList) {
		this.challengesList = challengesList;
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
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
}
