/**
 * 
 */
package com.geekwise.auth.module.dto;

/**
 * @author Yogesh
 * @since Oct 14, 2019
 * @version 0.1
 */
public class SignupRequestDTO {

	private String title;
	private String firstName;
	private String lastName;
	private String username;
	private String password;

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
