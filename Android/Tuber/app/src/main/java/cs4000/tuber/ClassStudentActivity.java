package cs4000.tuber;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
/*
 * After accessing an academic class, this view displays
 * Options that the user can select in the context of the
 * given academic class
 */
public class ClassStudentActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_student);
    }

    public void class_option_tutorServices_clicked(View view) {
	    Log.i("TutorService", getIntent().getStringExtra("course"));
        Intent intent = new Intent(ClassStudentActivity.this, TutorServicesActivity.class);
        intent.putExtra("course", getIntent().getStringExtra("course"));
        startActivity(intent);
    }

    public void class_option_study_hotspot_clicked(View view) {
        Intent intent = new Intent(ClassStudentActivity.this, HotspotEntryMenuActivity.class);
        intent.putExtra("course", getIntent().getStringExtra("course"));
        startActivity(intent);

    }

    public void class_option_messageing_clicked(View view) {
        Log.i("Messaging", "getting users list");
        Intent intent = new Intent(ClassStudentActivity.this, UsersListActivity.class);
        startActivity(intent);

    }
}
