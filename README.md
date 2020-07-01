This module gives you support for Authenticating a user in Token Based Authentication. As of now we do have support for Amazon Cognito Authentication. It lets you
<pre>
    <div class="container">
        <div class="block two first">
			<ul>
			  <li><b>Sign Up User</b></li>
			  <li><b>Sign In User</b></li>
			  <li><b>Validate User using access token</b></li>
			  <li><b>Refresh Access Token</b></li>
			  <li><b>Change Password</b></li>
			</ul>
		</div>
	</div>
</prep>

It can be used as dependency as well in your maven project as below:

<dependency>
	<groupId>com.geekwise</groupId>
	<artifactId>auth-module</artifactId>
	<version>0.1</version>
</dependency>

and define the repository as 
</repositories>
	<repository>
		<id>auth-module</id>
	   	<url>https://github.com/imYCGupta/auth-module/raw/mvn-repo/</url>
	</repository>
</repositories>
