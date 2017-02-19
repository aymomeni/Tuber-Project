package cs4000.tuber;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class ScheduleATutor extends AppCompatActivity implements OnCompleteListener {


    String _username;
    String _useToken;
    String course;
    String topic;
    String dateTime;
    String duration = "1";
    private SharedPreferences sharedPreferences;


    NumberPicker np;

    EditText dateView;
    EditText timeView;

    EditText topicTextBox;

    Button submitButton;

    Intent intent;


    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

//    TimePickerDialog.OnTimeSetListener test = new TimePickerDialog.OnTimeSetListener(){
//        @Override
//        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//            //time_finish.setText(String.valueOf(hourOfDay) + ":" + String.valueOf(minute));
//
//            Toast.makeText(ScheduleATutor.this, String.valueOf(hourOfDay) + ":" + String.valueOf(minute), Toast.LENGTH_LONG).show();
//        }
//    };

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_atutor);
        setTitle("Schedule A Tutor Request");

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        _username = sharedPreferences.getString("userEmail", "");
        _useToken = sharedPreferences.getString("userToken", "");

        intent = getIntent();

        course = intent.getStringExtra("course");

        topicTextBox = (EditText) findViewById(R.id.topiceditText2);
        submitButton = (Button) findViewById(R.id.submitButton);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dateTime = dateView.getText().toString() + " " + timeView.getText().toString();
                //Toast.makeText(ScheduleATutor.this, dateTime + " " + duration, Toast.LENGTH_LONG).show();

                topic = topicTextBox.getText().toString();
                JSONObject jO = new JSONObject();
                try{
                    jO.put("userEmail", _username);
                    jO.put("userToken", _useToken);
                    jO.put("course", course);
                    jO.put("topic", topic);
                    jO.put("dateTime", dateTime);
                    jO.put("duration", duration);

//                    Log.i("@userEmail",_username);
//                    Log.i("@userToken",_useToken);
//                    Log.i("@course",course);
//                    Log.i("@topic",topic);
//                    Log.i("@dateTime",dateTime);
//                    Log.i("@duration",duration);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ConnectionTask scheduleTutor = new ConnectionTask(jO);
                scheduleTutor.schedule_tutor(new ConnectionTask.CallBack() {
                    @Override
                    public void Done(JSONObject result) {
                        if(result != null) {
                            Toast.makeText(ScheduleATutor.this, "Your have submitted a scheduled tutor request", Toast.LENGTH_LONG).show();

                            //Intent intent = new Intent(ScheduleATutor.this, TutorServicesActivity.class);
                            //startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(ScheduleATutor.this, "Something went wrong! Try again", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });



        dateView = (EditText) findViewById(R.id.dateEditText2);
        final java.util.Calendar c = java.util.Calendar.getInstance();
        int year = c.get(java.util.Calendar.YEAR);
        int month = c.get(java.util.Calendar.MONTH);
        int day = c.get(java.util.Calendar.DAY_OF_MONTH);

        String str_mon = String.valueOf(month+1);
        String str_day = String.valueOf(day);

        if(month <= 9){str_mon = "0" + str_mon;}
        if(day <= 9){ str_day = "0" + str_day;}

        dateView.setText(year + "-" + str_mon + "-" + str_day);

        //dateView.setEnabled(false);


        timeView = (EditText) findViewById(R.id.timeEditText2);
        int hour = c.get(java.util.Calendar.HOUR_OF_DAY);
        int minute = c.get(java.util.Calendar.MINUTE);

        String str_hour = String.valueOf(hour);
        String str_min = String.valueOf(minute);

        if(str_hour.length() == 1){str_hour = "0" + str_hour;}
        if(str_min.length() == 1){ str_min = "0" + str_min;}

        timeView.setText(str_hour + ":" + str_min);


        np = (NumberPicker) findViewById(R.id.np);

        String[] nums = new String[12];
        for(int i=0; i<nums.length; i++)
            nums[i] = Integer.toString(i+1);

        //Set the minimum value of NumberPicker
        np.setMinValue(1);
        //Specify the maximum value/number of NumberPicker
        np.setMaxValue(12);
        np.setWrapSelectorWheel(true);
        np.setDisplayedValues(nums);
        np.setValue(1);

        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                //Display the newly selected number from picker
                duration = String.valueOf(newVal);
            }
        });
    }

    @Override
    public void onTimeComplete(int hour, int minute) {

        //Toast.makeText(ScheduleATutor.this, (hour%12) + ":" + minute + " " + str, Toast.LENGTH_LONG).show();

        String str_hour = String.valueOf(hour);
        String str_min = String.valueOf(minute);

        if(str_hour.length() == 1){str_hour = "0" + str_hour;}
        if(str_min.length() == 1){ str_min = "0" + str_min;}

        timeView.setText(str_hour + ":" + str_min);
    }

    @Override
    public void onDateComplete(int month, int day, int year) {
        //Toast.makeText(ScheduleATutor.this, month+1 + "/" + day + "/" + year , Toast.LENGTH_LONG).show();

        String str_mon = String.valueOf(month+1);
        String str_day = String.valueOf(day);

        if(str_mon.length() == 1){str_mon = "0" + str_mon;}
        if(str_day.length() == 1){ str_day = "0" + str_day;}


        dateView.setText(year + "-" + str_mon + "-" + str_day);
    }
}


