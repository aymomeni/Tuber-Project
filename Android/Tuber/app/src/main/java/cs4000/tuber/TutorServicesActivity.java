package cs4000.tuber;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;

public class TutorServicesActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_tutor_services);
  }

  public void tutor_services_immediate_service(View view) {
	Log.i("OnListner", "clicked!");
	startActivity(new Intent(TutorServicesActivity.this, ClassActivity.class));
  }

}
