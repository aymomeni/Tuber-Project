package cs4000.tuber;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;

/*
 * In between view where tutors can select options in the tutoring context
 */
public class OfferToTutorActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_offer_to_tutor);
  }

  public void tutor_services_immediate_service(View view) {
//	Log.i("Immediate Tutor Service", "clicked!");
	startActivity(new Intent(OfferToTutorActivity.this, ImmediateTutorServiceMapsActivity.class));
  }


    public void view_available_requests(View view) {
//        Log.i("OnListner", "clicked!");
        startActivity(new Intent(OfferToTutorActivity.this, TutoringRequestsTutor.class));
    }


}
