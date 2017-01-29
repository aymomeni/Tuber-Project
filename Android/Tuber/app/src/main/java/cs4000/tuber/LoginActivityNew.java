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

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivityNew extends Activity {

  // Member variables
  Button login_button, register_button;
  EditText username, password;
  String userEmailAdressStr, userPasswordStr, userObjectID;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_login_new);
	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

	login_button = (Button)findViewById(R.id.login_button);
	register_button = (Button)findViewById(R.id.register_button);
	username = (EditText) findViewById(R.id.login_username);
	password = (EditText) findViewById(R.id.login_password);


	register_button.setOnClickListener(new View.OnClickListener() {
	  @Override
	  public void onClick(View v) {
		// start intent for registration
	  }
	});


	login_button.setOnClickListener(new View.OnClickListener() {
	  @Override
	  public void onClick(View v) {

		userEmailAdressStr = username.getText().toString();
		userPasswordStr = password.getText().toString();


		if(userEmailAdressStr.isEmpty() || userPasswordStr.isEmpty()){

		  Toast.makeText(LoginActivityNew.this, "Please enter a valid username and password", Toast.LENGTH_SHORT).show();
		  // clear out the boxes?


		} else {

		  JSONObject userLoginJSON = new JSONObject();

		  try {

			userLoginJSON.put("userEmail", userEmailAdressStr);
		  	userLoginJSON.put("userPassword", userPasswordStr);

		  } catch(JSONException e){
			Log.i("LogIn", "JSON Exception filling the object");
			e.printStackTrace();

		  }

		  ConnectionTask userVerification = new ConnectionTask(new ConnectionTask.CallBack() {


			@Override
			public void Done(JSONObject result) {

			  if(result != null) {

				// TODO: once done save into preferences and call intent
				Log.i("Login Successful", result.toString());
				switchToMenu();

			  } else {

				// some error occured
				Log.i("Login", "JSON result object error");
				Toast.makeText(LoginActivityNew.this, "Username or Password where incorrect", Toast.LENGTH_SHORT).show();

			  }
			}
		  });

		  userVerification.verify_user(userLoginJSON);

		}
	  }
	});
  }

  /**
   * Method to run main menu activity through an intent
   */
  private void switchToMenu(){

	Intent intent = new Intent(this, MenuActivity.class);
	startActivity(intent);

  }

  /**
   * Method to run the registration activity through an intent
   */
  private void switchToRegistration(){

  }


}
