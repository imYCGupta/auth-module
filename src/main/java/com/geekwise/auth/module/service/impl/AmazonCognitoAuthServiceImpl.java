package com.geekwise.auth.module.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.AdminCreateUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminCreateUserResult;
import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.AdminUpdateUserAttributesRequest;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.AuthFlowType;
import com.amazonaws.services.cognitoidp.model.ChallengeNameType;
import com.amazonaws.services.cognitoidp.model.GetUserRequest;
import com.amazonaws.services.cognitoidp.model.GetUserResult;
import com.amazonaws.services.cognitoidp.model.NotAuthorizedException;
import com.amazonaws.services.cognitoidp.model.TooManyRequestsException;
import com.amazonaws.services.cognitoidp.model.UserNotFoundException;
import com.amazonaws.services.cognitoidp.model.UserType;
import com.geekwise.auth.module.dto.NewPasswordDTO;
import com.geekwise.auth.module.dto.RoleDTO;
import com.geekwise.auth.module.dto.SignupRequestDTO;
import com.geekwise.auth.module.dto.UserDTO;
import com.geekwise.auth.module.enums.ExceptionsEnum;
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
	 * password in input and return {@link UserDTO} in return. <br/>
	 * If signin is success then it return {@link UserDTO} with JWT Access Token,
	 * Token Expiry time, JWT Refresh Token, Token Type with it.
	 * 
	 * @param username - Username to login
	 * @param password - password to login
	 */
	@Override
	public UserDTO signin(String username, String password) {
		UserDTO userDTO = new UserDTO();
		if (logger.isInfoEnabled())
			logger.info("Validating Username {} :: for Login with Password {}", username, password);
		try {
			Map<String, String> authParams = new HashMap<String, String>();
			authParams.put("USERNAME", username);
			authParams.put("PASSWORD", password);

			/*
			 * Build the AdminInitiateAuthRequest Object with AuthFlow as ADMIN_NO_SRP_AUTH
			 * as we are signing user using username and password
			 */
			AdminInitiateAuthRequest authRequest = new AdminInitiateAuthRequest()
					.withAuthFlow(AuthFlowType.ADMIN_NO_SRP_AUTH).withAuthParameters(authParams).withClientId(clientId)
					.withUserPoolId(userPoolId);

			/* Receive the response from Cognito */
			AdminInitiateAuthResult authResponse = cognitoClient.adminInitiateAuth(authRequest);

			userDTO = handleSigninResponse(userDTO, authResponse);
			return userDTO;
		} catch (UserNotFoundException ex) {
			logger.error("User not found: {} :: Exception {}", username, ex.getMessage());
		} catch (NotAuthorizedException ex) {
			logger.error("invalid credentials: {} :: Exception {}", username, ex.getMessage());
		} catch (TooManyRequestsException ex) {
			logger.warn("caught TooManyRequestsException, delaying then retrying for {} :: Exception {} ", username,
					ex.getMessage());
		} catch (RuntimeException e) {
			logger.error("Unexpected Challenge on signin", e.getMessage());
		} catch (Exception e) {
			logger.error("Something Bad happened while trying login for user {} :: Exception {}", username,
					e.getMessage());
		}
		return userDTO;
	}

	/**
	 * It will handle signin response to see if user is signed in properly or we got
	 * some challenge while doing same. If there is a challenge then that can be
	 * found in Challenge List of {@link UserDTO}
	 * 
	 * @param userDTO
	 * @param authResponse
	 * @return
	 */
	private UserDTO handleSigninResponse(UserDTO userDTO, AdminInitiateAuthResult authResponse) {
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
			logger.info("{} attempted to sign in with temporary password :: Force Passowrd Change", userDTO.getEmail());
			userDTO.setForcePasswordChange(true);
			userDTO.getChallengesList().add(ChallengeNameType.NEW_PASSWORD_REQUIRED.name());
		} else {
			userDTO.getChallengesList().add(authResponse.getChallengeName());
			throw new RuntimeException("unexpected challenge on signin: " + authResponse.getChallengeName());
		}
		return userDTO;
	}

	/**
	 * This function will fetch user name using access token from Cognito. So
	 * basically it is validating token and fetching user details if token is found.
	 * 
	 * @param accessToken - Access Token to get user info
	 */
	@Override
	public String getUserNameByToken(String accessToken) {
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
	public UserType signup(SignupRequestDTO signupRequestDTO) {
		logger.info("Sign Up Request Received");
		AdminCreateUserRequest createUserRequest = new AdminCreateUserRequest();
		createUserRequest.setUsername(signupRequestDTO.getUsername());
		createUserRequest.setTemporaryPassword(signupRequestDTO.getPassword());
		createUserRequest.setUserPoolId(userPoolId);
		
		List<String> desirableDeliveryMedium = new ArrayList<String>();
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
		if(createUserResponse!=null && createUserResponse.getUser()!= null) {
			UserType createdUser = createUserResponse.getUser();
			if(signupRequestDTO.isVerifyEmailAutomatically()) {
				this.verifyEmail(createdUser.getUsername());
			}
		}
		return null;
	}
	
	@Override
	public boolean verifyEmail(String username) {
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
		}catch (Exception e) {
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

	/**
	 * This function will validate an access token.
	 * <code>getUserNameByToken(accessToken)</code> function can also be used for
	 * same functionality if we want user name also out of token.
	 */
	@Override
	public boolean isValidToken(String accessToken) {
		GetUserResult response = getUserResponse(accessToken);
		if (response != null && !StringUtils.isEmpty(response.getUsername())) {
			return true;
		}
		return false;
	}

	private GetUserResult getUserResponse(String accessToken) {
		GetUserRequest request = new GetUserRequest();
		request.setAccessToken(accessToken);

		/* Get response using cognito client */
		GetUserResult response = cognitoClient.getUser(request);
		return response;
	}

}
