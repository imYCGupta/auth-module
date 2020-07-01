/**
 * 
 */
package com.geekwise.auth.module.dto;

/**
 * @author Yogesh
 * @since Oct 14, 2019
 * @version 0.1
 */
public class RegistrationDTO {

	private String email;
	private String username;
	private String password;
	private boolean verifyEmailAutomatically;
	
	
	public boolean isVerifyEmailAutomatically() {
		return verifyEmailAutomatically;
	}

	public void setVerifyEmailAutomatically(boolean verifyEmailAutomatically) {
		this.verifyEmailAutomatically = verifyEmailAutomatically;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
