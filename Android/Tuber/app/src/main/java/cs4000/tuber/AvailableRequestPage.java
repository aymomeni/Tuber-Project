package cs4000.tuber;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class AvailableRequestPage extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private String _userEmail;
    private String _userToken;

    private String course;
    private String studentEmail;
    private String topic;
    private String dateTime;
    private String duration;

    TextView studetEmailTextView;
    TextView courseTextView;
    TextView topicTextView;
    TextView dateTimeTextView;
    TextView durationTextView;
    Button sessionButton;


    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_request_page);

        intent = getIntent();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        _userEmail = sharedPreferences.getString("userEmail", "");
        _userToken = sharedPreferences.getString("userToken", "");

        course = intent.getStringExtra("course");
        studentEmail = intent.getStringExtra("studentEmail");
        topic = intent.getStringExtra("topic");
        dateTime = intent.getStringExtra("dateTime");
        duration = intent.getStringExtra("duration");

        studetEmailTextView = (TextView) findViewById(R.id.studentNameTextValue);
        courseTextView = (TextView) findViewById(R.id.courseTextValue2);
        topicTextView = (TextView) findViewById(R.id.topicTextValue2);
        dateTimeTextView = (TextView) findViewById(R.id.dateTimeTextValue2);
        durationTextView = (TextView) findViewById(R.id.durationTextvalue2);
        sessionButton = (Button) findViewById(R.id.sessionButton2);


        studetEmailTextView.setText(studentEmail);
        courseTextView.setText(course);
        topicTextView.setText(topic);
        dateTimeTextView.setText(dateTime);
        durationTextView.setText(duration);


        sessionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONObject obj = new JSONObject();
                try {
                    obj.put("userEmail", _userEmail);
                    obj.put("userToken", _userToken);
                    obj.put("studentEmail", studentEmail);
                    obj.put("course", course);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ConnectionTask task = new ConnectionTask(obj);
                task.accept_student_scheduled_request(new ConnectionTask.CallBack() {
                    @Override
                    public void Done(JSONObject result) {

                        if(result != null) {

                            Toast.makeText(AvailableRequestPage.this, "You have accepted the resquest Successfully. You can now view the session"
                                    , Toast.LENGTH_LONG).show();
                            sessionButton.setText("VIEW SESSION");
                        } else {
                            Toast.makeText(AvailableRequestPage.this, "Something went wrong! Try again"
                                    , Toast.LENGTH_LONG).show();
                        }

                    }
                });

            }
        });

    }
}
