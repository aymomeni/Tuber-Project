package cs4000.tuber;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;

public class OfferToTutorActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_offer_to_tutor);
  }

  public void tutor_services_immediate_service(View view) {
	Log.i("Immediate Tutor Service", "clicked!");
	startActivity(new Intent(OfferToTutorActivity.this, ImmediateTutorServiceActivity.class));
  }


}
