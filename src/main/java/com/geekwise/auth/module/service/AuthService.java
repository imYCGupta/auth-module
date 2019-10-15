package com.geekwise.auth.module.service;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.amazonaws.services.cognitoidp.model.UserType;
import com.geekwise.auth.module.dto.NewPasswordDTO;
import com.geekwise.auth.module.dto.RoleDTO;
import com.geekwise.auth.module.dto.SignupRequestDTO;
import com.geekwise.auth.module.dto.UserDTO;
import com.geekwise.auth.module.enums.ExceptionsEnum;

public interface AuthService {

	public UserDTO signin(UserDTO userDTO);

	public Map<String, Object> validateToken(String authToken);

	public Map<String, Object> getUserDetailByToken(String authToken);

	public UserType signup(SignupRequestDTO signupRequestDTO);

	public List<String> getRoleList();

	public boolean createNewRole(RoleDTO roleDTO);

	public boolean addUserToGroup(String userName, String groupName);

	public boolean removeUserFromGroup(String userName, String groupName);

	public List<String> getUserGroupsForUser(String userName);

	public ExceptionsEnum forgotPassword(String userName);

	public ExceptionsEnum setNewPassword(NewPasswordDTO newPasswordDTO);

	public JSONObject decodeToken(String authToken);

	public UserDTO refreshAccessToken(String refreshToken);

}
