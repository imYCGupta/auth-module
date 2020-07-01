package com.geekwise.auth.module.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.AdminAddUserToGroupRequest;
import com.amazonaws.services.cognitoidp.model.AdminCreateUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminCreateUserResult;
import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.AdminListGroupsForUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminListGroupsForUserResult;
import com.amazonaws.services.cognitoidp.model.AdminRemoveUserFromGroupRequest;
import com.amazonaws.services.cognitoidp.model.AdminRespondToAuthChallengeRequest;
import com.amazonaws.services.cognitoidp.model.AdminRespondToAuthChallengeResult;
import com.amazonaws.services.cognitoidp.model.AdminUpdateUserAttributesRequest;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.AuthFlowType;
import com.amazonaws.services.cognitoidp.model.ChallengeNameType;
import com.amazonaws.services.cognitoidp.model.ChangePasswordRequest;
import com.amazonaws.services.cognitoidp.model.ConfirmForgotPasswordRequest;
import com.amazonaws.services.cognitoidp.model.CreateGroupRequest;
import com.amazonaws.services.cognitoidp.model.CreateGroupResult;
import com.amazonaws.services.cognitoidp.model.ForgotPasswordRequest;
import com.amazonaws.services.cognitoidp.model.ForgotPasswordResult;
import com.amazonaws.services.cognitoidp.model.GetUserRequest;
import com.amazonaws.services.cognitoidp.model.GetUserResult;
import com.amazonaws.services.cognitoidp.model.GroupType;
import com.amazonaws.services.cognitoidp.model.ListGroupsRequest;
import com.amazonaws.services.cognitoidp.model.ListGroupsResult;
import com.amazonaws.services.cognitoidp.model.UserType;
import com.geekwise.auth.module.dto.ResetPasswordDTO;
import com.geekwise.auth.module.dto.RegistrationDTO;
import com.geekwise.auth.module.dto.SignInDTO;
import com.geekwise.auth.module.service.AuthService;

/**
 * This class implements {@link AuthService} to provide basic auth
 * implementation of Amazon Cognito.
 * 
 * @author Yogesh
 * @since Oct 14, 2019
 * @version 0.1
 * 
 */
public class AmazonCognitoAuthServiceImpl implements AuthService {

	private static final String ROLES = "ROLES";

	private static final String USERNAME = "USERNAME";

	private static final Logger logger = LoggerFactory.getLogger(AmazonCognitoAuthServiceImpl.class);

	private String region;

	private String clientId;

	private String userPoolId;

	ClasspathPropertiesFileCredentialsProvider propertiesFileCredentialsProvider = new ClasspathPropertiesFileCredentialsProvider();

	protected AWSCognitoIdentityProvider cognitoClient = null;

	/**
	 * This is how we can create object of {@link AmazonCognitoAuthServiceImpl}. It
	 * needs 4 params to create object those are defined below:
	 * 
	 * @param userPoolId - User Pool Id of Cognito User Pool
	 * @param clientId   - Client Id of Cognito User Pool
	 * @param region     - Region of AWS account
	 * @param filePath   - AWS Credentials Property file path. If it is not passed,
	 *                   then it will be picking from Default Path defined in
	 *                   Cognito Standard ClassPath.
	 */
	public AmazonCognitoAuthServiceImpl(String userPoolId, String clientId, String region, String filePath) {
		this.userPoolId = userPoolId;
		this.clientId = clientId;
		this.region = region;
		if (StringUtils.isEmpty(filePath)) {
			cognitoClient = AWSCognitoIdentityProviderClientBuilder.standard()
					.withCredentials(propertiesFileCredentialsProvider).withRegion(this.region).build();
		} else {
			propertiesFileCredentialsProvider = new ClasspathPropertiesFileCredentialsProvider(filePath);
			cognitoClient = AWSCognitoIdentityProviderClientBuilder.standard()
					.withCredentials(propertiesFileCredentialsProvider).withRegion(this.region).build();
		}
	}

	/**
	 * This function is used to signin a user on Cognito. It takes username and
	 * password in input and return {@link SignInDTO} in return. <br/>
	 * If signin is success then it return {@link SignInDTO} with JWT Access Token,
	 * Token Expiry time, JWT Refresh Token, Token Type with it. This function is
	 * used even when a NewPasswordRequiredChallenge is raised. In this case,
	 * newPassword is also set in the userDTO and the function is invoked
	 * 
	 * @param username    - Username to login
	 * @param password    - password to login
	 * @param newPassword - newPassword to replace the temp password - This param is
	 *                    sent when a new password required challenge occurs
	 * 
	 */
	@Override
	public SignInDTO signin(SignInDTO userDTO) throws Exception {

		if (logger.isInfoEnabled())
			logger.info("Validating Username {} :: for Login with Password {}", userDTO.getEmail(),
					userDTO.getPassword());
		Map<String, String> authParams = new HashMap<>();
		authParams.put(USERNAME, userDTO.getEmail());
		authParams.put("PASSWORD", userDTO.getPassword());

		/*
		 * Build the AdminInitiateAuthRequest Object with AuthFlow as ADMIN_NO_SRP_AUTH
		 * as we are signing user using username and password
		 */
		AdminInitiateAuthRequest authRequest = new AdminInitiateAuthRequest()
				.withAuthFlow(AuthFlowType.ADMIN_NO_SRP_AUTH).withAuthParameters(authParams).withClientId(clientId)
				.withUserPoolId(userPoolId);

		/* Receive the response from Cognito */
		AdminInitiateAuthResult authResponse = cognitoClient.adminInitiateAuth(authRequest);

		handleSigninResponse(userDTO, authResponse);
		userDTO.setPassword("");
		userDTO.setNewPassword("");
		return userDTO;
	}

	/**
	 * It will handle signin response to see if user is signed in properly or we got
	 * some challenge while doing same. If there is a challenge then that can be
	 * found in Challenge List of {@link SignInDTO}
	 * 
	 * @param userDTO
	 * @param authResponse
	 * @return
	 * @throws Exception
	 */
	private void handleSigninResponse(SignInDTO userDTO, AdminInitiateAuthResult authResponse) throws Exception {
		if (StringUtils.isEmpty(authResponse.getChallengeName())) {
			logger.info("Login Success from Amazon Cognito");
			/*
			 * If challenge name is null then User is authenticated Based on the response
			 * returned Load the userDTO and send it back
			 */
			userDTO.setToken(authResponse.getAuthenticationResult().getAccessToken());
			userDTO.setTokenType(authResponse.getAuthenticationResult().getTokenType());
			userDTO.setExpiresInSec(authResponse.getAuthenticationResult().getExpiresIn());
			userDTO.setRefreshToken(authResponse.getAuthenticationResult().getRefreshToken());
			userDTO.setCurrentTimeInMs(System.currentTimeMillis());
		} else if (ChallengeNameType.NEW_PASSWORD_REQUIRED.name().equals(authResponse.getChallengeName())) {
			if (userDTO.getNewPassword() != null && !userDTO.getNewPassword().equals("")) {
				handleNewPasswordRequiredChallenge(userDTO, authResponse.getSession());
			}
			logger.info("{} attempted to sign in with temporary password :: Force Passowrd Change", userDTO.getEmail());
			userDTO.getChallengesList().add(ChallengeNameType.NEW_PASSWORD_REQUIRED.name());
		} else {
			userDTO.getChallengesList().add(authResponse.getChallengeName());
			throw new RuntimeException("unexpected challenge on signin: " + authResponse.getChallengeName());
		}
	}

	/**
	 * This function will fetch user name using access token from Cognito. So
	 * basically it is validating token and fetching user details if token is found.
	 * 
	 * @param accessToken - Access Token to get user info
	 */
	@Override
	public String getUserNameByToken(String accessToken) throws Exception {
		GetUserResult response = getUserResponse(accessToken);
		if (response != null && !StringUtils.isEmpty(response.getUsername())) {
			logger.info("Access Token is Validated.");
			return response.getUsername();
		} else {
			logger.warn("Token is not validated");
		}
		return null;
	}

	@Override
	public UserType signup(RegistrationDTO signupRequestDTO) throws Exception {
		logger.info("Sign Up Request Received");
		AdminCreateUserRequest createUserRequest = new AdminCreateUserRequest();
		createUserRequest.setUsername(signupRequestDTO.getUsername());
		createUserRequest.setTemporaryPassword(signupRequestDTO.getPassword());
		createUserRequest.setUserPoolId(userPoolId);

		List<String> desirableDeliveryMedium = new ArrayList<>();
		desirableDeliveryMedium.add("EMAIL");
		createUserRequest.setDesiredDeliveryMediums(desirableDeliveryMedium);

		List<AttributeType> attributeTypes = new ArrayList<>();
		if (!StringUtils.isEmpty(signupRequestDTO.getUsername())) {
			AttributeType emailAttribute = createAttributeType("email", signupRequestDTO.getEmail());
			attributeTypes.add(emailAttribute);
		}
		createUserRequest.setUserAttributes(attributeTypes);
		logger.info("Creation User Build done. Ready to create user");
		AdminCreateUserResult createUserResponse = cognitoClient.adminCreateUser(createUserRequest);
		if (createUserResponse != null && createUserResponse.getUser() != null) {
			UserType createdUser = createUserResponse.getUser();
			if (signupRequestDTO.isVerifyEmailAutomatically()) {
				this.verifyEmail(createdUser.getUsername());
			}
			return createUserResponse.getUser();
		}
		return null;
	}

	@Override
	public boolean verifyEmail(String username) throws Exception {
		AdminUpdateUserAttributesRequest attributeUpdateRequest = new AdminUpdateUserAttributesRequest();
		attributeUpdateRequest.setUsername(username);
		attributeUpdateRequest.setUserPoolId(userPoolId);
		AttributeType attributeType = createAttributeType("email_verified", "true");
		List<AttributeType> userAttributes = new ArrayList<>();
		userAttributes.add(attributeType);
		attributeUpdateRequest.setUserAttributes(userAttributes);

		try {
			cognitoClient.adminUpdateUserAttributes(attributeUpdateRequest);
			logger.info("Email Verified Successfully");
			return true;
		} catch (Exception e) {
			logger.error("Email Verification Failed.");
			throw e;
		}
	}

	private AttributeType createAttributeType(String name, String value) {
		AttributeType attributeType = new AttributeType();
		attributeType.setName(name);
		attributeType.setValue(value);

		return attributeType;
	}

	@Override
	public List<String> getUserGroupList() throws Exception {
		ListGroupsRequest listGroupsRequest = new ListGroupsRequest();
		listGroupsRequest.setUserPoolId(userPoolId);
		ListGroupsResult listGroupsResult = cognitoClient.listGroups(listGroupsRequest);
		List<GroupType> groups = listGroupsResult.getGroups();
		List<String> groupList = new ArrayList<>();
		for (GroupType group : groups) {
			groupList.add(group.getGroupName());
		}

		return groupList;
	}

	@Override
	public boolean createNewUserGroup(String groupName, String groupDesc) throws Exception {
		CreateGroupRequest createGroupRequest = new CreateGroupRequest();
		createGroupRequest.setGroupName(groupName);
		createGroupRequest.setDescription(groupDesc);
		createGroupRequest.setUserPoolId(userPoolId);

		CreateGroupResult createGroupResult = cognitoClient.createGroup(createGroupRequest);

		return createGroupResult.getGroup() != null;
	}

	@Override
	public boolean addUserToGroup(String userName, String groupName) throws Exception {
		AdminAddUserToGroupRequest adminAddUserToGroupRequest = new AdminAddUserToGroupRequest();
		adminAddUserToGroupRequest.setUsername(userName);
		adminAddUserToGroupRequest.setGroupName(groupName);
		adminAddUserToGroupRequest.setUserPoolId(userPoolId);

		cognitoClient.adminAddUserToGroup(adminAddUserToGroupRequest);

		return true;
	}

	@Override
	public boolean removeUserFromGroup(String userName, String groupName) throws Exception {
		AdminRemoveUserFromGroupRequest adminRemoveUserFromGroupRequest = new AdminRemoveUserFromGroupRequest();
		adminRemoveUserFromGroupRequest.setUsername(userName);
		adminRemoveUserFromGroupRequest.setUserPoolId(userPoolId);
		adminRemoveUserFromGroupRequest.setGroupName(groupName);

		cognitoClient.adminRemoveUserFromGroup(adminRemoveUserFromGroupRequest);

		return true;
	}

	@Override
	public List<String> getUserGroupsForUser(String userName) throws Exception {
		List<String> userGroups = new ArrayList<>();

		AdminListGroupsForUserRequest adminListGroupsForUserRequest = new AdminListGroupsForUserRequest();
		adminListGroupsForUserRequest.setUsername(userName);
		adminListGroupsForUserRequest.setUserPoolId(userPoolId);

		AdminListGroupsForUserResult adminListGroupsForUserResult = cognitoClient
				.adminListGroupsForUser(adminListGroupsForUserRequest);
		if (!CollectionUtils.isEmpty(adminListGroupsForUserResult.getGroups())) {
			List<GroupType> groupTypes = adminListGroupsForUserResult.getGroups();
			for (GroupType groupType : groupTypes) {
				userGroups.add(groupType.getGroupName());
			}
		}
		return userGroups;
	}

	/**
	 * Method to avail forgot password service Triggers a email to the user with an
	 * OTP which the user has to enter subsequently to reset the password
	 * 
	 * @param userName
	 * @return
	 */
	@Override
	public boolean forgotPassword(String userName) throws Exception {
		ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest();
		forgotPasswordRequest.setUsername(userName);
		forgotPasswordRequest.setClientId(clientId);

		ForgotPasswordResult forgotPasswordResult = cognitoClient.forgotPassword(forgotPasswordRequest);
		return forgotPasswordResult.getCodeDeliveryDetails() != null;
	}

	/**
	 * This method is used to reset the password (Forgot Password Service) The user
	 * needs to enter an OTP to set his new password
	 * 
	 * @param newPasswordDTO
	 */
	@Override
	public boolean resetPassword(ResetPasswordDTO newPasswordDTO) throws Exception {
		ConfirmForgotPasswordRequest confirmForgotPasswordRequest = new ConfirmForgotPasswordRequest();
		confirmForgotPasswordRequest.setUsername(newPasswordDTO.getUsername());
		confirmForgotPasswordRequest.setPassword(newPasswordDTO.getPassword());
		confirmForgotPasswordRequest.setConfirmationCode(newPasswordDTO.getConfirmationCode());
		confirmForgotPasswordRequest.setClientId(clientId);

		cognitoClient.confirmForgotPassword(confirmForgotPasswordRequest);
		return true;
	}

	@Override
	public JSONObject decodeToken(String authToken) throws Exception {
		Map<String, Object> map = new HashMap<>();
		if (authToken.toUpperCase().contains("BEARER")) {
			GetUserRequest getUserRequest = new GetUserRequest();
			String[] authTokenSplit = authToken.split(" ");
			getUserRequest.setAccessToken(authTokenSplit[1]);

			GetUserResult getUserResult = cognitoClient.getUser(getUserRequest);
			if (!getUserResult.getUsername().isEmpty()) {
				map.put(USERNAME, getUserResult.getUsername());

				String[] splitString = authTokenSplit[1].split("\\.");
				String base64EncodedBody = splitString[1];

				Base64 base64Url = new Base64(true);
				String body = new String(base64Url.decode(base64EncodedBody));

				JSONObject json = new JSONObject(body);
				JSONArray jsonArray = json.getJSONArray("cognito:groups");
				List<String> userGroups = new ArrayList<>();
				for (int i = 0; i < jsonArray.length(); i++) {
					userGroups.add(jsonArray.getString(i));
				}
				map.put(ROLES, userGroups);

			}
		} else if (authToken.toUpperCase().contains("BASIC")) {
			String[] authTokenSplit = authToken.split(" ");

			Base64 base64Credentials = new Base64(true);
			String credentials = new String(base64Credentials.decode(authTokenSplit[1]));
			String[] credentialSplit = credentials.split(":");
			SignInDTO userDTO = new SignInDTO();
			userDTO.setEmail(credentialSplit[0]);
			userDTO.setPassword(credentialSplit[1]);

			map.put(USERNAME, userDTO.getEmail());

			SignInDTO loggedInUser = this.signin(userDTO);
			if (loggedInUser.getToken() != null) {
				// User is authenticated from Amazon Cognito
				List<String> userGroups = this.getUserGroupsForUser(userDTO.getEmail());
				if (!CollectionUtils.isEmpty(userGroups)) {
					map.put(ROLES, userGroups);
				} else {
					map.put(ROLES, new ArrayList<>());
				}
			}
		}
		return new JSONObject(map);
	}

	@Override
	public SignInDTO refreshAccessToken(String refreshToken) throws Exception {
		SignInDTO userDTO = new SignInDTO();
		Map<String, String> authParams = new HashMap<>();
		authParams.put("REFRESH_TOKEN", refreshToken);
		AdminInitiateAuthRequest authRequest = new AdminInitiateAuthRequest()
				.withAuthFlow(AuthFlowType.REFRESH_TOKEN_AUTH).withAuthParameters(authParams).withClientId(clientId)
				.withUserPoolId(userPoolId);
		// Receive the response from Cognito
		AdminInitiateAuthResult authResponse = cognitoClient.adminInitiateAuth(authRequest);
		handleSigninResponse(userDTO, authResponse);
		return userDTO;
	}

	/**
	 * This function will validate an access token.
	 * <code>getUserNameByToken(accessToken)</code> function can also be used for
	 * same functionality if we want user name also out of token.
	 */
	@Override
	public boolean isValidToken(String accessToken) throws Exception {
		GetUserResult response = getUserResponse(accessToken);
		return (response != null && !StringUtils.isEmpty(response.getUsername()));
	}

	private GetUserResult getUserResponse(String accessToken) {
		GetUserRequest request = new GetUserRequest();
		request.setAccessToken(accessToken);

		/* Get response using cognito client */
		return cognitoClient.getUser(request);
	}

	/**
	 * This function is used when the user wants to change his/her password.
	 * 
	 * @param accessToken
	 * @param oldPassword
	 * @param newPassword
	 * @return
	 */
	@Override
	public boolean updatePassword(String accessToken, String oldPassword, String newPassword) throws Exception {

		ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
		changePasswordRequest.setAccessToken(accessToken);
		changePasswordRequest.setPreviousPassword(oldPassword);
		changePasswordRequest.setProposedPassword(newPassword);

		cognitoClient.changePassword(changePasswordRequest);

		return true;
	}

	// This function is also used to change the password on first login with
	// temporary password(when a new password required challenge is thrown)
	private SignInDTO handleNewPasswordRequiredChallenge(SignInDTO userDTO, String session) throws Exception {
		Map<String, String> challengeResponses = new HashMap<>();

		challengeResponses.put("NEW_PASSWORD", userDTO.getNewPassword());
		challengeResponses.put("PASSWORD", userDTO.getPassword());
		challengeResponses.put(USERNAME, userDTO.getEmail());
		challengeResponses.put("userAttributes.email", userDTO.getEmail());

		AdminRespondToAuthChallengeRequest adminRespondToAuthChallengeRequest = new AdminRespondToAuthChallengeRequest();
		adminRespondToAuthChallengeRequest.setSession(session);
		adminRespondToAuthChallengeRequest.setClientId(clientId);
		adminRespondToAuthChallengeRequest.setUserPoolId(userPoolId);
		adminRespondToAuthChallengeRequest.setChallengeName(ChallengeNameType.NEW_PASSWORD_REQUIRED.name());
		adminRespondToAuthChallengeRequest.setChallengeResponses(challengeResponses);

		AdminRespondToAuthChallengeResult adminRespondToAuthChallengeResult = cognitoClient
				.adminRespondToAuthChallenge(adminRespondToAuthChallengeRequest);
		if (StringUtils.isEmpty(adminRespondToAuthChallengeResult.getChallengeName())) {
			userDTO.setPassword(userDTO.getNewPassword());
			return this.signin(userDTO);
		}
		return null;

	}
}
