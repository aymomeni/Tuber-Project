package cs4000.tuber;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class Studysession extends Activity {

    Switch session_switch;
    Button submitRating_button;
    TextView ratingText;
    RatingBar rating_bar;

    private SharedPreferences sharedPreferences;
    Intent intent;


    private String _userEmail;
    private String _userToken;
    private String _course;
    private String session_id;
    private String studentEmail;

//    public void onBackPressed()
//    {
//        super.onBackPressed();
//        startActivity(new Intent(Studysession.this, TutorServicesActivity.class));
//        finish();
//    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studysession);

        session_switch = (Switch) findViewById(R.id.session_switch);
        submitRating_button = (Button) findViewById(R.id.submit_rating_button);
        ratingText = (TextView) findViewById(R.id.rating_text);
        rating_bar = (RatingBar) findViewById(R.id.ratingBar);

        ratingText.setVisibility(View.INVISIBLE);
        rating_bar.setVisibility(View.INVISIBLE);
        submitRating_button.setVisibility(View.INVISIBLE);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        _userEmail = sharedPreferences.getString("userEmail", "");
        _userToken = sharedPreferences.getString("userToken", "");

        intent = getIntent();
        _course = intent.getStringExtra("course");

        String state = intent.getStringExtra("status");
        String fromwhere = intent.getStringExtra("from");

        if(state.equals("1")){
            session_switch.setChecked(true);
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
                ConnectionTask rate_student = new ConnectionTask(obj);
                rate_student.rate_student(new ConnectionTask.CallBack() {
                    @Override
                    public void Done(JSONObject result) {
                        if(result != null) {
                            Toast.makeText(getBaseContext(),
                                    "Thank you for your feedback. Your rating has been submitted successfully",
                                    Toast.LENGTH_LONG).show();
                            submitRating_button.setClickable(false);
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
                if (isChecked) {
                    // The toggle is enabled
                    //do stuff when Switch is ON

                    JSONObject sesssion_info = new JSONObject();
                    try {
                        sesssion_info.put("userEmail", _userEmail);
                        sesssion_info.put("userToken", _userToken);
                        sesssion_info.put("course", "CS 2420");


                        //intent.putExtra("from", "scheduling");

                        String from = intent.getStringExtra("from");
                        Log.i("@from",from);
                        if(from != null && from.equals("scheduling")) {
                            sesssion_info.put("dateTime", intent.getStringExtra("dateTime"));
                            ConnectionTask task = new ConnectionTask(sesssion_info);
                            task.start_scheduled_tutor_session(new ConnectionTask.CallBack() {
                                @Override
                                public void Done(JSONObject result) {
                                    if (result != null) {
                                        Log.i("@start_scheduled_sessin", "session started!");
                                    } else {
                                        Log.i("@start_scheduled_sessin", "start session failed!");
                                    }
                                }
                            });

                        } else {
                            ConnectionTask task = new ConnectionTask(sesssion_info);
                            task.start_tutor_session(new ConnectionTask.CallBack() {
                                @Override
                                public void Done(JSONObject result) {
                                    if (result != null) {
                                        Log.i("@start_tutor_session", "session started!");
                                    } else {
                                        Log.i("@start_tutor_session", "start session failed!");
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
                        sesssion_info.put("course", "CS 2420");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ConnectionTask task = new ConnectionTask(sesssion_info);
                    task.end_tutor_session(new ConnectionTask.CallBack() {
                        @Override
                        public void Done(JSONObject result) {
                            if(result != null){

                                try {
                                    session_id = result.getString("tutorSessionID");
                                    studentEmail = result.getString("studentEmail");

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

//                        "course": "CS 3500",
//                        "sessionCost": 16.11,
//                        "sessionEndTime": "01,31,2017 12:49:46 PM",
//                        "sessionStartTime": "01,31,2017 11:45:19 AM",
//                        "studentEmail": "brandontobin2@cox.net",
//                        "userEmail": "brandontobin@cox.net"

                                Log.i("@end_tutor_session","session ended!");
                            } else {
                                Log.i("@end_tutor_session","end session failed!");
                            }
                        }
                    });

                    // move to rate the session
                    session_switch.setClickable(false);

                    ratingText.setVisibility(View.VISIBLE);
                    rating_bar.setVisibility(View.VISIBLE);
                    submitRating_button.setVisibility(View.VISIBLE);
                }
            }
        });

    }

}
