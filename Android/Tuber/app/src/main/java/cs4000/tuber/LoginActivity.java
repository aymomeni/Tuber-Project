package cs4000.tuber;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends Activity {

  Button login_button, register_button;
  EditText username, password;
  String usernameStr, passwordStr, userObjectID;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_login);
	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

	login_button = (Button)findViewById(R.id.login_button);
	register_button = (Button) findViewById(R.id.register_button);
	username = (EditText) findViewById(R.id.login_username);
	password = (EditText) findViewById(R.id.login_password);



	  register_button.setOnClickListener(new View.OnClickListener() {
		  @Override
		  public void onClick(View view) {
			  usernameStr = username.getText().toString();
			  passwordStr = password.getText().toString();

			  if(usernameStr.matches("") || passwordStr.matches("")){
				  Toast.makeText(LoginActivity.this, "A username and password are required", Toast.LENGTH_SHORT).show();
			  }else {
				  ParseUser user = new ParseUser();

				  user.setUsername(usernameStr);
				  user.setPassword(passwordStr);
				  userObjectID = user.getObjectId();

				  user.signUpInBackground(new SignUpCallback() {
					  @Override
					  public void done(ParseException e) {
						  if (e == null){
							  Log.i("SignUp", "Successful");
							  Toast.makeText(getApplicationContext(),
									  "Redirecting...", Toast.LENGTH_SHORT).show();
							  switchToMenu();
						  } else {
							  Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
						  }
					  }
				  });
			  }

		  }
	  });

	  login_button.setOnClickListener(new View.OnClickListener() {
		  @Override
		  public void onClick(View view) {
			  usernameStr = username.getText().toString();
			  passwordStr = password.getText().toString();

			  if(usernameStr.matches("") || passwordStr.matches("")) {
				  Toast.makeText(LoginActivity.this, "A username and password are required", Toast.LENGTH_SHORT).show();
			  }else{

				  ParseUser.logInInBackground(usernameStr, passwordStr, new LogInCallback() {
					  @Override
					  public void done(ParseUser user, ParseException e) {
						  if (user != null) {
							  usernameStr = user.getUsername();
							  userObjectID = user.getObjectId();

							  Log.i("LogIn", "Successful");
							  Log.i("LogIn", "username: " + user.getUsername());
							  Log.i("LogIn", "password: " + user.get("password"));
							  Log.i("LogIn", "UID: " + user.getObjectId());

							// TODO: What if the password is incorrect?
							// TODO: How to store username and maybe UserID Application Wide?

							  Toast.makeText(getApplicationContext(),
									  "Redirecting...", Toast.LENGTH_SHORT).show();
							  switchToMenu();
						  } else {
							  Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
						  }
					  }
				  });
			  }
		  }
	  });

	ParseAnalytics.trackAppOpenedInBackground(getIntent());
  }

  protected void switchToMenu() {

	Intent intent = new Intent(this, MenuActivity2.class);
	//EditText editText = (EditText) findViewById(R.id.edit_message);
	//String message = editText.getText().toString();
	intent.putExtra("UserID: ", userObjectID);
	startActivity(intent);

  }
}