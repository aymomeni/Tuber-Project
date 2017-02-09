package cs4000.tuber;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class Studysession extends Activity {

    Switch session_switch;

    private SharedPreferences sharedPreferences;
    Intent intent;


    private String _userEmail;
    private String _userToken;
    private String _course;

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


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        _userEmail = sharedPreferences.getString("userEmail", "");
        _userToken = sharedPreferences.getString("userToken", "");

        intent = getIntent();
        _course = intent.getStringExtra("course");

        String state = intent.getStringExtra("status");

        if(state.equals("1")){
            session_switch.setChecked(true);
        }

//        JSONObject obj2 = new JSONObject();
//        try{
//            obj2.put("userEmail", _userEmail);
//            obj2.put("userToken", _userToken);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        ConnectionTask check_session_status = new ConnectionTask(obj2);
//        check_session_status.check_session_status(new ConnectionTask.CallBack() {
//            @Override
//            public void Done(JSONObject result) {
//                if(result != null){
//                    Log.i("@check_session_status", "check session completed");
//
//                    try {
//                        String status = result.getString("session_status");
//                        if(status.equals("available")){ // only offered but looking to pair
//                            // do nothing
//                        } else if(status.equals("paired")){ // paired
//                            // do nothing
//                        } else if(status.equals("active")){ // in an active session
//                            session_switch.setChecked(true);
//                        } else if(status.equals("completed")){ // session has ended
//                            // do nothing
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                } else {
//                    Log.i("@check_session_status", "check session status failed!"); // has not offered yet
//                }
//            }
//        });

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
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ConnectionTask task = new ConnectionTask(sesssion_info);
                    task.start_tutor_session(new ConnectionTask.CallBack() {
                        @Override
                        public void Done(JSONObject result) {
                            if(result != null){
                                Log.i("@start_tutor_session","session started!");
                            } else {
                                Log.i("@start_tutor_session","start session failed!");
                            }
                        }
                    });
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
//            Intent intent = new Intent(getApplicationContext(), StudentMapActivity.class);
//
////            intent.putExtra("tutorLatitude", requestLatitudes.get(i));
////            intent.putExtra("tutorLongitude", requestLongitudes.get(i));
////            intent.putExtra("studentLatitude", lastKnownLocation.getLatitude());
////            intent.putExtra("studentLongitude", lastKnownLocation.getLongitude());
////            intent.putExtra("studentCourse", courses.get(i));
////            intent.putExtra("username", usernames.get(i));
//
//            startActivity(intent);
                }
            }
        });

    }

}
