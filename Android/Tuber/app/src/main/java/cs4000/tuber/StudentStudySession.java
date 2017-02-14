package cs4000.tuber;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
    String sessionStartTime;

    String tutorEmail;

    RatingBar rating_bar_student;

    private Intent intent;

    private boolean exited = false;


    Handler handler = new Handler(); // used for polling


    @Override
    public void onBackPressed() {
        exited = true;
        finish();
    }


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
        //session_id = intent.getStringExtra("tutorSessionID");
        //tutorEmail = intent.getStringExtra("tutorEmail");


        rate_tutor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                JSONObject obj = new JSONObject();
                try {
                    obj.put("userEmail",_userEmail);
                    obj.put("userToken",_userToken);
                    obj.put("tutorSessionID", session_id);
                    obj.put("tutorEmail",tutorEmail);
                    obj.put("rating", String.valueOf(rating_bar_student.getRating()));

                    Log.i("userEmail",_userEmail);
                    Log.i("userToken",_userToken);
                    Log.i("tutorSessionID",session_id);
                    Log.i("tutorEmail",tutorEmail);
                    Log.i("rating",String.valueOf(rating_bar_student.getRating()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ConnectionTask rate_tutor_f = new ConnectionTask(obj);
                rate_tutor_f.rate_tutor(new ConnectionTask.CallBack() {
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


        check_for_sessionStart();
    }


    public void check_for_sessionStart(){

        JSONObject obj = new JSONObject();
        try {
            obj.put("userEmail", _userEmail);
            obj.put("userToken", _userToken);

            obj.put("course", "CS 2420");

            Log.i("userEmail",_userEmail);
            Log.i("userToken",_userToken);
            Log.i("course","CS 2420");
            if(sessionStartTime != null) {
                obj.put("tutorEmail", tutorEmail);
                obj.put("sessionStartTime", sessionStartTime);
                Log.i("tutorEmail",tutorEmail);
                Log.i("sessionStartTime",sessionStartTime);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ConnectionTask task = new ConnectionTask(obj);
        task.check_session_activeStatusStudent(new ConnectionTask.CallBack() {
            @Override
            public void Done(JSONObject result) {
                try {
                    if(result != null && !result.get("tutorSessionID").equals(null)) {

                        rate_tutor.setEnabled(true);
                        Toast.makeText(getBaseContext(),
                                "Tutor ended the session, you can now rate him",
                                Toast.LENGTH_LONG).show();

                        session_id = result.getString("tutorSessionID");

                    } else if(result != null) {

                        sessionStartTime = result.getString("sessionStartTime");
                        tutorEmail = result.getString("tutorEmail");


                        if(!exited) {
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    check_for_sessionStart();
                                }
                            }, 3000);
                        }
                    } else {

                        if(!exited) {
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    check_for_sessionStart();
                                }
                            }, 3000);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
