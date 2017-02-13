package cs4000.tuber;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TutoringRequestsTutor extends Activity {

    ListView requestsListView;
    //ListView acceptedRequestsListView;
    ArrayList<String> requests = new ArrayList<String>();
    ArrayList<String> studentEmails = new ArrayList<String>();
    ArrayList<String> courses = new ArrayList<String>();
    ArrayList<String> topics = new ArrayList<String>();
    ArrayList<String> dateTimes = new ArrayList<String>();
    ArrayList<String> durations = new ArrayList<String>();

    ArrayAdapter arrayAdapter;

    private SharedPreferences sharedPreferences;
    private String _userEmail;
    private String _userToken;
    private String _course = "CS 2420";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutoring_requests_tutor);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        _userEmail = sharedPreferences.getString("userEmail", "");
        _userToken = sharedPreferences.getString("userToken", "");

        requestsListView = (ListView) findViewById(R.id.t_requestsListId);
        //acceptedRequestsListView = (ListView) findViewById(R.id.t_requestsListId2);

        requests.clear();
        requests.add("Getting available requests...");

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, requests);

        requestsListView.setAdapter(arrayAdapter);
        //acceptedRequestsListView.setAdapter(arrayAdapter);


        requestsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                if(requests.size() > i && studentEmails.size() > i && courses.size() > i && topics.size() > i
                        && dateTimes.size() > i && durations.size() > i) {

                    Intent intent = new Intent(getApplicationContext(), AvailableRequestPage.class);

                    intent.putExtra("studentEmail", studentEmails.get(i));
                    intent.putExtra("topic", topics.get(i));
                    intent.putExtra("dateTime", dateTimes.get(i));
                    intent.putExtra("duration", durations.get(i));
                    intent.putExtra("course", courses.get(i));

                    startActivity(intent);
                }
            }
        });

        UpdateList();
    }

    public void UpdateList() {

        JSONObject obj = new JSONObject();
        try {
            obj.put("userEmail", _userEmail);
            obj.put("userToken", _userToken);
            obj.put("course", _course);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ConnectionTask task = new ConnectionTask(obj);
        task.find_all_scheduled_tutor_requests(new ConnectionTask.CallBack() {
            @Override
            public void Done(JSONObject result) {
                if(result != null) {

                    requests.clear();
                    studentEmails.clear();
                    courses.clear();
                    topics.clear();
                    dateTimes.clear();
                    durations.clear();

                    try {
                        JSONArray array = result.getJSONArray("tutorRequestItems");


                        if (array.length() > 0) {

                            for(int i = 0; i < array.length(); i++) {
                                JSONObject temp = array.getJSONObject(i);
                                String course = temp.getString("course");
                                String datetime = temp.getString("dateTime");
                                datetime = datetime.substring(0,16);
                                String duration = temp.getString("duration");
                                String studentEmail = temp.getString("studentEmail");
                                String topic = temp.getString("topic");

                                requests.add(course + ": " + topic);
                                studentEmails.add(studentEmail);
                                courses.add(course);
                                topics.add(topic);
                                dateTimes.add(datetime);
                                durations.add(duration);
                            }

                        } else {
                            requests.add("no available requests found");
                            Toast.makeText(TutoringRequestsTutor.this, "no available requests found", Toast.LENGTH_SHORT).show();
                        }

                        arrayAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {

                    // error getting list from server

                }
            }
        });

    }
}
