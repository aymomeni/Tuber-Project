package cs4000.tuber;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class TutoringRequestPage extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private String _userEmail;
    private String _userToken;

    private String course;
    private String tutorEmail;
    private String tutorFirstName;
    private String tutorLastName;
    private String topic;
    private String dateTime;
    private String duration;
    private boolean status;

    TextView tutorEmailTextView;
    TextView courseTextView;
    TextView topicTextView;
    TextView dateTimeTextView;
    TextView durationTextView;
    Button sessionButton;
    ImageView status_icon;


    Intent intent;

    public static TutoringRequestPage getInstance(){
        return activity;
    }
    static TutoringRequestPage activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutoring_request_page);
        setTitle("Open Request");

        intent = getIntent();

        activity = this;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        _userEmail = sharedPreferences.getString("userEmail", "");
        _userToken = sharedPreferences.getString("userToken", "");

        course = intent.getStringExtra("course");
        Log.i("@course_check",course);
        tutorEmail = intent.getStringExtra("tutorEmail");
        tutorFirstName = intent.getStringExtra("tutorFirstName");
        tutorLastName = intent.getStringExtra("tutorLastName");
        topic = intent.getStringExtra("topic");
        dateTime = intent.getStringExtra("dateTime");
        duration = intent.getStringExtra("duration");
        status = intent.getBooleanExtra("isPaired", false);

        tutorEmailTextView = (TextView) findViewById(R.id.tutorNameTextValue);
        courseTextView = (TextView) findViewById(R.id.courseTextValue);
        topicTextView = (TextView) findViewById(R.id.topicTextValue);
        dateTimeTextView = (TextView) findViewById(R.id.dateTimeTextValue);
        durationTextView = (TextView) findViewById(R.id.durationTextvalue);
        sessionButton = (Button) findViewById(R.id.sessionButton);
        status_icon = (ImageView) findViewById(R.id.statusImageView);

        if(status){
            String temp = tutorFirstName + " " + tutorLastName;
            tutorEmailTextView.setText(getPadding(17 - temp.length()) + temp);
            status_icon.setImageResource(R.drawable.green_light);
            sessionButton.setEnabled(true);
        }

        courseTextView.setText(course);
        topicTextView.setText(topic);
        dateTimeTextView.setText(dateTime);
        durationTextView.setText(duration);

        sessionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TutoringRequestPage.this, StudentStudySession.class);
                intent.putExtra("course", getIntent().getStringExtra("course"));
                startActivity(intent);
            }
        });
    }

    private String getPadding(int x){
        String res = "";
        for(int i = 0; i < x; i++){
            res += " ";
        }
        return res;
    }
}
