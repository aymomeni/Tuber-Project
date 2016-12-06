package cs4000.tuber;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class ClassActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_class);
  }

  public void class_option_tutorServices_clicked(View view) {
	Log.i("OnListner", "clicked!");
	startActivity(new Intent(ClassActivity.this, TutorServicesActivity.class));
  }

  public void class_option_offerToTutor_clicked(View view) {
	Log.i("OnListner", "clicked!");
	startActivity(new Intent(ClassActivity.this, OfferToTutorActivity.class));
  }


}
