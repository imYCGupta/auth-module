package com.geekwise.auth.module.service.impl;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.amazonaws.services.cognitoidp.model.UserType;
import com.geekwise.auth.module.dto.NewPasswordDTO;
import com.geekwise.auth.module.dto.RoleDTO;
import com.geekwise.auth.module.dto.SignupRequestDTO;
import com.geekwise.auth.module.dto.UserDTO;
import com.geekwise.auth.module.enums.ExceptionsEnum;
import com.geekwise.auth.module.service.AuthService;

@Service("cognitoAuthService")
public class AmazonCognitoAuthServiceImpl implements AuthService{

	@Override
	public UserDTO signin(UserDTO userDTO) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> validateToken(String authToken) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getUserDetailByToken(String authToken) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserType signup(SignupRequestDTO signupRequestDTO) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getRoleList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean createNewRole(RoleDTO roleDTO) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addUserToGroup(String userName, String groupName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeUserFromGroup(String userName, String groupName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<String> getUserGroupsForUser(String userName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExceptionsEnum forgotPassword(String userName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExceptionsEnum setNewPassword(NewPasswordDTO newPasswordDTO) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject decodeToken(String authToken) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserDTO refreshAccessToken(String refreshToken) {
		// TODO Auto-generated method stub
		return null;
	}

}
