This module gives you support for Authenticating a user in Token Based Authentication. As of now we do have support for Amazon Cognito Authentication. It lets you

<ul>
  <li><b>Sign Up User</b></li>
  <li><b>Sign In User</b></li>
  <li><b>Validate User using access token</b></li>
  <li><b>Refresh Access Token</b></li>
  <li><b>Change Password</b></li>
</ul>
It can be used as dependency as well in your maven project as below:

<pre>
 &lt;dependency&gt;
	 &lt;groupId&gt;com.geekwise&lt;/groupId&gt;
	 &lt;artifactId&gt;auth-module&lt;/artifactId&gt;
	 &lt;version&gt;0.1&lt;/version&gt;
 &lt;/dependency&gt;

and define the repository as 
&lt;/repositories&gt;
	&lt;repository&gt;
		&lt;id&gt;geekwise-auth-module&lt;/id&gt;
	   	&lt;url&gt;https://github.com/imYCGupta/auth-module/raw/mvn-repo/&lt;/url&gt;
	&lt;/repository&gt;
&lt;/repositories&gt;
</pre>

After adding dependency, you can use following syntax to do 

AuthService authService = new AmazonCognitoAuthServiceImpl(&lt;Cognito-User-Pool-Id&gt;, &lt;Cognito-Client-Id&gt;, &lt;Cognito-Region&gt;, &lt;Credential-File-Path&gt;);  

Once you have authService object, you can call functions like below:  
SignIn - signin(String email, String password) returns LoginResponseDTO which has AccessToken, Access Token Issued At, Access Token Expires At, Refresh Token  \

Validate Access Token - isValidToken(String accessToken) returns boolean (true - if valid / false - if invalid)    \

Get User Name Using Token - getUserNameByToken(String accessToken) returns String  \

Descode Access Token - decodeToken(String accessToken) returns JSONObject  \

Sign Up -  signup(RegistrationDTO signupRequestDTO) returns UserType (userCretion details)  

List Of User Group - getListOfUserGroups() returns List&lt;String&gt;  

Create New Group - createNewUserGroup(String groupName, String groupDesc) returns boolean (true - if success / false - if fails)  

Add User To Existing Group - addUserToGroup(String userName, String groupName) returns boolean (true - if success / false - if fails)  

Remove User from Group - removeUserFromGroup(String userName, String groupName) returns boolean (true - if success / false - if fails)  

Get User Groups For User - getUserGroupsForUser(String userName) returns List&lt;String&gt; 

Receive OTP on Email - forgotPassword(String userName) returns boolean (true - if email sent / false - if fails)  

Reset Password with OTP - resetPassword(ResetPasswordDTO newPasswordDTO) returns boolean (true - if success / false - if fails)  

Refresh Access Token with Refresh Token - refreshAccessToken(String refreshToken) returns LoginResponseDTO which has new AccessToken, Access Token Issued At, Access Token 
Expires At, Refresh Token  

Mark Email Verified - verifyEmail(String email) returns boolean (true - if success / false - if fails)  

Change Password - changePassword(String username, String oldPassword, String newPassword) returns boolean (true - if success / false - if fails)  

Update Temp Password - updateTempPassword(String username, String oldPassword, String newPassword) returns LoginResponseDTO which has AccessToken, Access Token Issued At, Access Token Expires At, Refresh Token  
