/**
 * 
 */
package com.geekwise.auth.module.dto;

/**
 * @author Yogesh
 * @since Oct 14, 2019
 * @version 0.1
 */
public class ResetPasswordDTO {
	
	private String confirmationCode;
	
	private String username;
	
	private String password;
	
	private boolean resetSuccessful;
	
	private String cause;

	public boolean isResetSuccessful() {
		return resetSuccessful;
	}

	public void setResetSuccessful(boolean resetSuccessful) {
		this.resetSuccessful = resetSuccessful;
	}

	public String getCause() {
		return cause;
	}

	public void setCause(String cause) {
		this.cause = cause;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getConfirmationCode() {
		return confirmationCode;
	}

	public void setConfirmationCode(String confirmationCode) {
		this.confirmationCode = confirmationCode;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	
}
