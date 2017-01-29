package cs4000.tuber;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivityNew extends AppCompatActivity {


  Button login_button, register_button;
  EditText username, password;
  String usernameStr, passwordStr, userObjectID;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_login_new);
  }


}
