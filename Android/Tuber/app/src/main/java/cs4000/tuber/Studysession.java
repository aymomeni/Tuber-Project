package cs4000.tuber;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class Studysession extends AppCompatActivity {

    Switch session_switch;
    Button submitRating_button;
    TextView ratingText;
    RatingBar rating_bar;
    ImageView status_light;
    TextView statusTextV;

    private SharedPreferences sharedPreferences;
    Intent intent;

    private boolean automated = false;
    private boolean exited = false;

    private String _userEmail;
    private String _userToken;

    private String course;
    private String session_id;
    private String studentEmail;
    private String cost = "-1";

    Handler handler = new Handler();

    @Override
    public void onBackPressed() {
        exited = true;
        finish();
    }

    private String from;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studysession);
        getSupportActionBar().hide();

        session_switch = (Switch) findViewById(R.id.session_switch);
        submitRating_button = (Button) findViewById(R.id.submit_rating_button);
        ratingText = (TextView) findViewById(R.id.rating_text);
        rating_bar = (RatingBar) findViewById(R.id.userRating);
        status_light = (ImageView) findViewById(R.id.statusImageV2);
        statusTextV = (TextView) findViewById(R.id.statusView);

        ratingText.setVisibility(View.INVISIBLE);
        rating_bar.setVisibility(View.INVISIBLE);
        submitRating_button.setVisibility(View.INVISIBLE);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        _userEmail = sharedPreferences.getString("userEmail", "");
        _userToken = sharedPreferences.getString("userToken", "");

        intent = getIntent();
        course = intent.getStringExtra("course");
        Log.i("@course_check",course);

        String state = intent.getStringExtra("status");
        from = intent.getStringExtra("from");

        if(state.equals("1")){ //TODO
            session_switch.setChecked(true);
            status_light.setImageResource(R.drawable.green_light);
        }

        if(state.equals("2")) {
            session_switch.setChecked(true);
            session_switch.setClickable(false);
            status_light.setImageResource(R.drawable.yellow_light);
            Check_sessionStart();
        }

        submitRating_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                JSONObject obj = new JSONObject();
                try {
                    obj.put("userEmail",_userEmail);
                    obj.put("userToken",_userToken);
                    obj.put("tutorSessionID", session_id);
                    obj.put("studentEmail",studentEmail);
                    obj.put("rating", String.valueOf(rating_bar.getRating()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ConnectionTask rate_student1 = new ConnectionTask(obj);
                rate_student1.rate_student(new ConnectionTask.CallBack() {
                    @Override
                    public void Done(JSONObject result) {
                        if(result != null) {
                            Toast.makeText(getBaseContext(),
                                    "Thank you for your feedback. Your rating has been submitted successfully",
                                    Toast.LENGTH_LONG).show();
                            submitRating_button.setClickable(false);
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

        session_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!automated){
                    if (isChecked) {
                        // The toggle is enabled
                        //do stuff when Switch is ON

                        JSONObject sesssion_info = new JSONObject();
                        try {
                            sesssion_info.put("userEmail", _userEmail);
                            sesssion_info.put("userToken", _userToken);
                            sesssion_info.put("course", course);


                            //intent.putExtra("from", "scheduling");

                            //String from = intent.getStringExtra("from");
                            //Log.i("@from",from);
                            if (from != null && from.equals("scheduling")) {
                                sesssion_info.put("dateTime", intent.getStringExtra("dateTime"));
                                ConnectionTask task = new ConnectionTask(sesssion_info);
                                task.start_scheduled_tutor_session_tutor(new ConnectionTask.CallBack() {
                                    @Override
                                    public void Done(JSONObject result) {
                                        if (result != null) {
//                                            Log.i("@start_scheduled_sessin", "session pending!");
                                            status_light.setImageResource(R.drawable.yellow_light);
                                            session_switch.setClickable(false);
                                            Check_sessionStart();

                                        } else {
                                            automated = true;
                                            session_switch.setChecked(false);
//                                            Log.i("@start_scheduled_sessin", "start session failed!");
                                        }
                                    }
                                });

                            } else {
                                ConnectionTask task = new ConnectionTask(sesssion_info);
                                task.start_tutoring_session_tutor(new ConnectionTask.CallBack() {
                                    @Override
                                    public void Done(JSONObject result) {
                                        if (result != null) {
//                                            Log.i("@start_tutor_session", "session started!");
                                            status_light.setImageResource(R.drawable.yellow_light);
                                            session_switch.setClickable(false);
                                            Check_sessionStart();
                                        } else {
                                            automated = true;
                                            session_switch.setChecked(false);
//                                            Log.i("@start_tutor_session", "start session failed!");
                                        }
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        // The toggle is disabled
                        JSONObject sesssion_info = new JSONObject();
                        try {
                            sesssion_info.put("userEmail", _userEmail);
                            sesssion_info.put("userToken", _userToken);
                            sesssion_info.put("course", course);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ConnectionTask task = new ConnectionTask(sesssion_info);
                        task.end_tutoring_session(new ConnectionTask.CallBack() {
                            @Override
                            public void Done(JSONObject result) {
                                if (result != null) {

                                    try {
                                        session_id = result.getString("tutorSessionID");
                                        studentEmail = result.getString("studentEmail");
                                        cost = result.getString("sessionCost");


                                        // move to rate the session
                                        session_switch.setClickable(false);

                                        statusTextV.setVisibility(View.INVISIBLE);
                                        status_light.setVisibility(View.INVISIBLE);
                                        ratingText.setVisibility(View.VISIBLE);
                                        rating_bar.setVisibility(View.VISIBLE);
                                        submitRating_button.setVisibility(View.VISIBLE);


                                        Toast.makeText(getBaseContext(),
                                                "The cost of the session is: $"+ cost,
                                                Toast.LENGTH_LONG).show();


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    if (from != null && from.equals("scheduling")) {
                                        AvailableAcceptedRequestPage.getInstance().finish();
                                        //TutoringRequestsPager.getInstance().finish();
                                    }
//                                    Log.i("@end_tutor_session", "session ended!");
                                } else {
                                    automated = true;
                                    session_switch.setChecked(true);
//                                    Log.i("@end_tutor_session", "end session failed!");
                                }
                            }
                        });

//                        // move to rate the session
//                        session_switch.setClickable(false);
//
//                        statusTextV.setVisibility(View.INVISIBLE);
//                        status_light.setVisibility(View.INVISIBLE);
//                        ratingText.setVisibility(View.VISIBLE);
//                        rating_bar.setVisibility(View.VISIBLE);
//                        submitRating_button.setVisibility(View.VISIBLE);
//
//
//                        //
////                        final AlertDialog dialog = new AlertDialog.Builder(Studysession.this)
////                                .setTitle("Session Ended")
////                                .setMessage("The cost of the session is: $"+ cost)
////                                .setCancelable(false)
////                                .setPositiveButton("Acknowledge", new DialogInterface.OnClickListener() {
////                                    @Override
////                                    public void onClick(DialogInterface dialog, int which) {
////                                        dialog.cancel();
////                                    }
////                                }).show();
//
//                        Toast.makeText(getBaseContext(),
//                                "The cost of the session is: $"+ cost,
//                                Toast.LENGTH_LONG).show();
//
//                        Log.i("@end_tutor_session2", cost);

                    }
                }
                automated = false;
            }
        });

    }

    public void Check_sessionStart(){


        JSONObject obj = new JSONObject();
        try {
            obj.put("userEmail", _userEmail);
            obj.put("userToken", _userToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ConnectionTask task = new ConnectionTask(obj);
        task.check_session_status_tutor(new ConnectionTask.CallBack() {
            @Override
            public void Done(JSONObject result) {
                if(result != null) {
                    try {
                        if(result.getString("session_status").equals("active")){

                            final AlertDialog dialog = new AlertDialog.Builder(Studysession.this)
                                    .setTitle("Session Active")
                                    .setMessage("Your session has been started")
                                    .setCancelable(false)
                                    .setPositiveButton("Acknowledge", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    }).show();
                            status_light.setImageResource(R.drawable.green_light);
                            session_switch.setClickable(true);
                        } else {
                            if(!exited && session_switch.isChecked()) {
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Check_sessionStart();
                                    }
                                }, 500);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else {

                }
            }
        });
    }

}
