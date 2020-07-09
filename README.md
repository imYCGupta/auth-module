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
</pre>

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

Once you have authService object, you can call function like below:

