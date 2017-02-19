package cs4000.tuber;

import android.*;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

public class TutoringRequests extends AppCompatActivity {

    //ListView requestsListView;
    //ArrayList<String> requests = new ArrayList<String>();
    //ArrayList<String> tutorEmails = new ArrayList<String>();
    //ArrayList<String> courses = new ArrayList<String>();
    //ArrayList<String> topics = new ArrayList<String>();
    //ArrayList<String> dateTimes = new ArrayList<String>();
    //ArrayList<String> durations = new ArrayList<String>();
    //ArrayList<Boolean> statuses = new ArrayList<Boolean>();

    //ArrayAdapter arrayAdapter;

    private SwipeRefreshLayout swipeContainer;

    RecyclerView Requests_rv;
    ArrayList<Request> requests = new ArrayList<Request>();
    RequestsAdapter adapter;


    public static TutoringRequests getInstance(){
        return activity;
    }
    static TutoringRequests activity;

    private SharedPreferences sharedPreferences;
    private String _userEmail;
    private String _userToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persons);

        activity = this;

        setTitle("My Scheduled Requests");

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        _userEmail = sharedPreferences.getString("userEmail", "");
        _userToken = sharedPreferences.getString("userToken", "");

        Requests_rv = (RecyclerView) findViewById(R.id.persons_rv);
        Requests_rv.setHasFixedSize(true);
        Requests_rv.setItemAnimator(new SlideInUpAnimator());

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                //updateListView(getLocation());
                UpdateList();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        ItemClickSupport.addTo(Requests_rv).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                        if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted, but the user clicked it before the UI removed it
                            if(requests.size() > position) {

                                Intent intent = new Intent(getApplicationContext(), TutoringRequestPage.class);

                                intent.putExtra("tutorEmail", requests.get(position).getTutorEmail());
                                intent.putExtra("topic", requests.get(position).getTopic());
                                intent.putExtra("dateTime", requests.get(position).getDateTime());
                                intent.putExtra("duration", requests.get(position).getDuration());
                                intent.putExtra("isPaired", requests.get(position).isStatus());
                                intent.putExtra("course", requests.get(position).getCourse());

                                startActivity(intent);
                            }                        }

                    }
                }
        );


        // Create adapter passing in the sample user data
        adapter = new RequestsAdapter(this, requests);
        // Set layout manager to position the items
        Requests_rv.setLayoutManager(new LinearLayoutManager(this));

        adapter.clear();
        // Attach the adapter to the recyclerview to populate items
        Requests_rv.setAdapter(adapter);

        //requestsListView = (ListView) findViewById(R.id.requestsListId);

        //requests.clear();
        //requests.add("Getting scheduled requests...");

        //arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, requests);

        //requestsListView.setAdapter(arrayAdapter);


//        requestsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//
//                if(requests.size() > i && tutorEmails.size() > i && courses.size() > i && topics.size() > i
//                        && dateTimes.size() > i && durations.size() > i && statuses.size() > i) {
//
//                    Intent intent = new Intent(getApplicationContext(), TutoringRequestPage.class);
//
//                    intent.putExtra("tutorEmail", tutorEmails.get(i));
//                    intent.putExtra("topic", topics.get(i));
//                    intent.putExtra("dateTime", dateTimes.get(i));
//                    intent.putExtra("duration", durations.get(i));
//                    intent.putExtra("isPaired", statuses.get(i));
//                    intent.putExtra("course", courses.get(i));
//
//                    startActivity(intent);
//                }
//            }
//        });


        JSONObject obj = new JSONObject();
        try{
            obj.put("userEmail", _userEmail);
            obj.put("userToken", _userToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ConnectionTask check_session_student = new ConnectionTask(obj);
        check_session_student.check_session_status_student(new ConnectionTask.CallBack() {
            @Override
            public void Done(JSONObject result) {
                if(result != null) {
                    try {
                        String status = result.getString("session_status");
                        if(status.equals("pending") || status.equals("active")) {
                            Intent intent = new Intent(TutoringRequests.this, StudentStudySession.class);
                            //intent.putExtra("status", "1");
                            startActivity(intent);
                            finish();
                        } else {
                            UpdateList();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {

                }
            }
        });
    }


    public void UpdateList() {

        JSONObject obj = new JSONObject();
        try {
            obj.put("userEmail", _userEmail);
            obj.put("userToken", _userToken);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        ConnectionTask task = new ConnectionTask(obj);
        task.check_scheduled_paired_status(new ConnectionTask.CallBack() {
            @Override
            public void Done(JSONObject result) {
                if(result != null) {

                    adapter.clear();
//                    tutorEmails.clear();
//                    courses.clear();
//                    topics.clear();
//                    dateTimes.clear();
//                    durations.clear();
//                    statuses.clear();

                    try {
                        JSONArray array = result.getJSONArray("requests");


                        if (array.length() > 0) {

                            for(int i = 0; i < array.length(); i++) {
                                JSONObject temp = array.getJSONObject(i);
                                String course = temp.getString("course");
                                String datetime = temp.getString("dateTime");
                                datetime = datetime.substring(0,16);

                                String duration = temp.getString("duration");
                                Boolean status = temp.getBoolean("isPaired");
                                String tutorEmail = temp.getString("tutorEmail");
                                String topic = temp.getString("topic");

                                Request temp1 = new Request(course, topic);
                                temp1.setTutorEmail(tutorEmail);
                                temp1.setCourse(course);
                                temp1.setDateTime(datetime);
                                temp1.setDuration(duration);
                                temp1.setStatus(status);

                                adapter.add(temp1);

//                                requests.add(course + ": " + topic);
//                                tutorEmails.add(tutorEmail);
//                                courses.add(course);
//                                topics.add(topic);
//                                dateTimes.add(datetime);
//                                durations.add(duration);
//                                statuses.add(status);
                            }

                        } else {
                            //requests.add("no scheduled requests found");
                            Toast.makeText(TutoringRequests.this, "No scheduled requests found", Toast.LENGTH_SHORT).show();
                        }

                        //arrayAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {

                    // error getting list from server

                }
                swipeContainer.setRefreshing(false);
            }
        });
    }
}
