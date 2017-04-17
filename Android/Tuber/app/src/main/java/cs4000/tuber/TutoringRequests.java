package cs4000.tuber;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TutoringRequests extends AppCompatActivity {

    private SwipeRefreshLayout swipeContainer;

    RecyclerView Requests_rv;
    ArrayList<Request> requests = new ArrayList<Request>();
    RequestsAdapter adapter;


//    public static TutoringRequests getInstance(){
//        return activity;
//    }
//    static TutoringRequests activity;

    private SharedPreferences sharedPreferences;
    private String _userEmail;
    private String _userToken;

    private String course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        //activity = this;

        setTitle("My Scheduled Requests");

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        _userEmail = sharedPreferences.getString("userEmail", "");
        _userToken = sharedPreferences.getString("userToken", "");

        course = getIntent().getStringExtra("course");
        Log.i("@course_check",course);

        Requests_rv = (RecyclerView) findViewById(R.id.users_rv);
        Requests_rv.setHasFixedSize(true);
        Requests_rv.setItemAnimator(new SlideInUpAnimator());

        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        Requests_rv.addItemDecoration(itemDecoration);

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
                                intent.putExtra("tutorFirstName", requests.get(position).getTutorFirstName());
                                intent.putExtra("tutorLastName", requests.get(position).getTutorLastName());
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
    }

    @Override
    protected void onStart() {
        super.onStart();

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
                            intent.putExtra("course", course);
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
                    UpdateList();
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
                                String tutorFirstName = temp.getString("firstName");
                                String tutorLastName = temp.getString("lastName");
                                String topic = temp.getString("topic");

                                Request temp1 = new Request(course, topic);
                                temp1.setTutorEmail(tutorEmail);
                                temp1.setTutorFirstName(tutorFirstName);
                                temp1.setTutorLastName(tutorLastName);
                                temp1.setCourse(course);
                                temp1.setDateTime(datetime);
                                temp1.setDuration(duration);
                                temp1.setStatus(status);

                                adapter.add(temp1);

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
