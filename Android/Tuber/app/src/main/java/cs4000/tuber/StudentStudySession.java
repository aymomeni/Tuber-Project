package cs4000.tuber;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class StudentStudySession extends Activity {

    Button rate_tutor;

    private SharedPreferences sharedPreferences;
    private String _userEmail;
    private String _userToken;

    String session_id;
    String tutorEmail;

    RatingBar rating_bar_student;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_study_session);

        rating_bar_student = (RatingBar) findViewById(R.id.rating_tutor_bar);
        rate_tutor = (Button) findViewById(R.id.rate_tutor_button);


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        _userEmail = sharedPreferences.getString("userEmail", "");
        _userToken = sharedPreferences.getString("userToken", "");

        intent = getIntent();
        session_id = intent.getStringExtra("tutorSessionID");
        tutorEmail = intent.getStringExtra("tutorEmail");

        rate_tutor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                JSONObject obj = new JSONObject();
                try {
                    obj.put("userEmail",_userEmail);
                    obj.put("userToken",_userToken);
                    obj.put("tutorSessionID", session_id);
                    obj.put("studentEmail",tutorEmail);
                    obj.put("rating", String.valueOf(rating_bar_student.getRating()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ConnectionTask rate_student = new ConnectionTask(obj);
                rate_student.rate_student(new ConnectionTask.CallBack() {
                    @Override
                    public void Done(JSONObject result) {
                        if(result != null) {
                            Toast.makeText(getBaseContext(),
                                    "Thank you for your feedback. Your rating has been submitted successfully",
                                    Toast.LENGTH_LONG).show();
                            rate_tutor.setClickable(false);
                        } else {
                            Toast.makeText(getBaseContext(),
                                    "Something went wrong! Please try again in a moment",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });
    }
}
