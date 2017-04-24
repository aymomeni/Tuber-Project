package cs4000.tuber;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
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

    private String session_id;
    private String sessionStartTime;
    private String cost;

    private String tutorEmail;
    private String course;

    RatingBar rating_bar_student;

    Intent intent;

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

        intent = getIntent();

        course = intent.getStringExtra("course");
        Log.i("@course_check",course);

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
                            finish();
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


        JSONObject obj2 = new JSONObject();
        try {
            obj2.put("userEmail", _userEmail);
            obj2.put("userToken", _userToken);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        ConnectionTask task = new ConnectionTask(obj2);
        task.check_session_status_student(new ConnectionTask.CallBack() {
            @Override
            public void Done(JSONObject result) {
                if(result != null){
//                    Log.i("@SSS","Start session student good");

                    try {
                        if(result.getString("session_status").equals("pending")){

                            final AlertDialog dialog = new AlertDialog.Builder(StudentStudySession.this)
                                    .setTitle("Pending")
                                    .setMessage("Please confirm the start of the session.")
                                    .setCancelable(false)
                                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();

                                            JSONObject obj3 = new JSONObject();
                                            try{
                                                obj3.put("userEmail", _userEmail);
                                                obj3.put("userToken", _userToken);
                                                obj3.put("course", course);
//											obj2.put("longitude", _studentLong);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            ConnectionTask task2 = new ConnectionTask(obj3);
                                            task2.start_scheduled_tutor_session_student(new ConnectionTask.CallBack() {
                                                @Override
                                                public void Done(JSONObject result) {
                                                    if(result != null) {
//													Log.i("@STST", "start sessionStudent completed");
                                                        check_for_sessionEnd();
                                                    } else {
//													Log.i("@STST", "start sessionStudent FAILED");
                                                    }
                                                }
                                            });
                                        }
                                    }).show();

                        } else if(result.getString("session_status").equals("active")) {
                            check_for_sessionEnd();
                        } else {
                            if(!exited) {
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        check_for_sessionStart();
                                    }
                                }, 500);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else{
//                    Log.i("@SSS","Start session student bad");
                }
            }
        });


    }

    public void check_for_sessionEnd(){

        JSONObject obj = new JSONObject();
        try {
            obj.put("userEmail", _userEmail);
            obj.put("userToken", _userToken);

            obj.put("course", course);

            if(sessionStartTime != null) {
                obj.put("tutorEmail", tutorEmail);
                obj.put("sessionStartTime", sessionStartTime);
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

                        cost = result.getString("sessionCost");

                        rate_tutor.setEnabled(true);
//                        Toast.makeText(getBaseContext(),
//                                "Tutor ended the session, you can now rate him",
//                                Toast.LENGTH_LONG).show();

                        final AlertDialog dialog = new AlertDialog.Builder(StudentStudySession.this)
                                .setTitle("Session Ended")
                                .setMessage("Tutor ended the session, please rate your tutor. Session Cost is: $"+ cost)
                                .setCancelable(false)
                                .setPositiveButton("Acknowledge", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                }).show();

                        session_id = result.getString("tutorSessionID");

                        if(TutoringRequestPage.getInstance() != null) {TutoringRequestPage.getInstance().finish();}
                        //if(TutoringRequests.getInstance() != null) {TutoringRequests.getInstance().finish();}

                    } else if(result != null) {

                        sessionStartTime = result.getString("sessionStartTime");
                        tutorEmail = result.getString("tutorEmail");


                        if(!exited) {
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    check_for_sessionEnd();
                                }
                            }, 5000);
                        }
                    } else {

                        if(!exited) {
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    check_for_sessionEnd();
                                }
                            }, 5000);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
