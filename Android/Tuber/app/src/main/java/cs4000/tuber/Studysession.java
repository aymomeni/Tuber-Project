package cs4000.tuber;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Switch;

import org.json.JSONObject;

public class Studysession extends Activity {

    Switch session_switch;


    public void pic_clicked(View view){

        if(session_switch.isActivated()){

            ConnectionTask task = new ConnectionTask(new JSONObject());
            task.start_tutor_session(new ConnectionTask.CallBack() {
                @Override
                public void Done(JSONObject result) {

                }
            });

        } else { // move to rate the session
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studysession);
        session_switch = (Switch) findViewById(R.id.session_switch);
    }

}
