package com.geekwise.auth.module.service;

import java.util.List;

import org.json.JSONObject;

import com.amazonaws.services.cognitoidp.model.UserType;
import com.geekwise.auth.module.dto.ResetPasswordDTO;
import com.geekwise.auth.module.dto.RegistrationDTO;
import com.geekwise.auth.module.dto.SignInDTO;

public interface AuthService {

	public SignInDTO signin(SignInDTO userDTO) throws Exception;
	
	public boolean isValidToken(String accessToken) throws Exception;

	public String getUserNameByToken(String accessToken) throws Exception;

	public UserType signup(RegistrationDTO signupRequestDTO) throws Exception;

	public List<String> getUserGroupList() throws Exception;

	public boolean createNewUserGroup(String groupName, String groupDesc) throws Exception;

	public boolean addUserToGroup(String userName, String groupName) throws Exception;

	public boolean removeUserFromGroup(String userName, String groupName) throws Exception;

	public List<String> getUserGroupsForUser(String userName) throws Exception;

	public boolean forgotPassword(String userName) throws Exception;

	public boolean resetPassword(ResetPasswordDTO newPasswordDTO) throws Exception;

	public JSONObject decodeToken(String authToken) throws Exception;

	public SignInDTO refreshAccessToken(String refreshToken) throws Exception;

	public boolean verifyEmail(String username) throws Exception;
	
	public boolean updatePassword(String username, String oldPassword, String newPassword) throws Exception;

}
