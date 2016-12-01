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
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class LoginActivity extends Activity {

  Button login_button, register_button;
  EditText username, password;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_login);
	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

	login_button = (Button)findViewById(R.id.login_button);
	register_button = (Button) findViewById(R.id.register_button);
	username = (EditText) findViewById(R.id.login_username);
	password = (EditText) findViewById(R.id.login_username);

	login_button.setOnClickListener(new View.OnClickListener() {
	  @Override
	  public void onClick(View v) {
		Log.i("Login Activity: ", "Login Clicked");
		// check if the database contains thaye name and the correct password

		Toast.makeText(getApplicationContext(),
				"Redirecting...", Toast.LENGTH_SHORT).show();

		ParseQuery<ParseUser> query = ParseQuery.getUserQuery(); //getQuery("User");

		query.getInBackground("2uEYmgq7QP", new GetCallback<ParseUser>() {

		  @Override
		  public void done(ParseUser user, ParseException e) {
			Log.i("Login Activity: ", "Login Clicked2");

			if (e == null && user != null) {
			  Log.i("Login Activity: ", "Login Clicked3");
			  String dbEmail = user.getString("username");
			  String dbPW = user.getString("email");

			  Log.i("ObjectUserName",dbEmail);
			  Log.i("ObjectPW", dbPW);
			}
			Log.i("Login Activity: ", "Login Clicked4");
		  }
		});

		switchToMenu();

		 //else if email doesn't exist report it

		 //else if email exists and password doesn't report incorrect password
	  }


	});


	ParseAnalytics.trackAppOpenedInBackground(getIntent());
  }

  protected void switchToMenu() {

	Intent intent = new Intent(this, MenuActivity.class);
	//EditText editText = (EditText) findViewById(R.id.edit_message);
	//String message = editText.getText().toString();
	intent.putExtra("", "");
	startActivity(intent);

  }
}
