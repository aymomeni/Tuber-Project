package cs4000.tuber;

import android.app.Activity;
import android.os.Bundle;

import com.parse.ParseAnalytics;

public class LoginActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_login);



	ParseAnalytics.trackAppOpenedInBackground(getIntent());
  }
}
