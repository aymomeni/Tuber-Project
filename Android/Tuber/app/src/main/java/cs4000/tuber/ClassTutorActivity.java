package cs4000.tuber;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class ClassTutorActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_tutor);
    }


    public void class_option_offerToTutor_clicked(View view) {
        Log.i("@OfferToTutor", getIntent().getStringExtra("course"));
        Intent intent = new Intent(ClassTutorActivity.this, OfferToTutorActivity.class);
        intent.putExtra("course", getIntent().getStringExtra("course"));
        startActivity(intent);
    }

    public void class_option_messageing__clicked(View view) {
        Log.i("Messaging", "getting users list");
        Intent intent = new Intent(ClassTutorActivity.this, UsersListActivity.class);
        startActivity(intent);

    }
}
