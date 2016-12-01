package cs4000.tuber;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.parse.ParseAnalytics;

public class LoginActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_login);


	ParseAnalytics.trackAppOpenedInBackground(getIntent());
  }
}
