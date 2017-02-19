package cs4000.tuber;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TutoringRequestsTutor extends AppCompatActivity {


    public static TutoringRequestsTutor getInstance(){
        return activity;
    }
    static TutoringRequestsTutor activity;


    //ListView requestsListView;
    //ListView acceptedRequestsListView;
    //ArrayList<String> requests = new ArrayList<String>();
    //ArrayList<String> studentEmails = new ArrayList<String>();
    //ArrayList<String> courses = new ArrayList<String>();
    //ArrayList<String> topics = new ArrayList<String>();
    //ArrayList<String> dateTimes = new ArrayList<String>();
    //ArrayList<String> durations = new ArrayList<String>();

    //ArrayAdapter arrayAdapter;

    private SwipeRefreshLayout swipeContainer;

    RecyclerView Requests_rv;
    ArrayList<Request> requests = new ArrayList<Request>();
    RequestsAdapter adapter;

    private SharedPreferences sharedPreferences;
    private String _userEmail;
    private String _userToken;
    private String _course = "CS 2420";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persons);
        setTitle("Available Scheduled Requests");

        activity = this;

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

                                Intent intent = new Intent(getApplicationContext(), AvailableAcceptedRequestPage.class);

                                intent.putExtra("studentEmail", requests.get(position).getStudentEmail());
                                intent.putExtra("topic", requests.get(position).getTopic());
                                intent.putExtra("dateTime", requests.get(position).getDateTime());
                                intent.putExtra("duration", requests.get(position).getDuration());
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

        //requestsListView = (ListView) findViewById(R.id.t_requestsListId);
        //acceptedRequestsListView = (ListView) findViewById(R.id.t_requestsListId2);

//        requests.clear();
//        requests.add("Getting available requests...");
//
//        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, requests);
//
//        requestsListView.setAdapter(arrayAdapter);
        //acceptedRequestsListView.setAdapter(arrayAdapter);


//        requestsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//
//                if(requests.size() > i && studentEmails.size() > i && courses.size() > i && topics.size() > i
//                        && dateTimes.size() > i && durations.size() > i) {
//
//                    Intent intent = new Intent(getApplicationContext(), AvailableAcceptedRequestPage.class);
//
//                    intent.putExtra("studentEmail", studentEmails.get(i));
//                    intent.putExtra("topic", topics.get(i));
//                    intent.putExtra("dateTime", dateTimes.get(i));
//                    intent.putExtra("duration", durations.get(i));
//                    intent.putExtra("course", courses.get(i));
//
//                    startActivity(intent);
//                }
//            }
//        });

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


                    adapter.clear();
//                    requests.clear();
//                    studentEmails.clear();
//                    courses.clear();
//                    topics.clear();
//                    dateTimes.clear();
//                    durations.clear();

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


                                Request temp1 = new Request(course, topic);
                                temp1.setStudentEmail(studentEmail);
                                temp1.setCourse(course);
                                temp1.setDateTime(datetime);
                                temp1.setDuration(duration);

                                adapter.add(temp1);

//                                requests.add(course + ": " + topic);
//                                studentEmails.add(studentEmail);
//                                courses.add(course);
//                                topics.add(topic);
//                                dateTimes.add(datetime);
//                                durations.add(duration);
                            }

                        } else {
                            //requests.add("no available requests found");
                            Toast.makeText(TutoringRequestsTutor.this, "No available requests found", Toast.LENGTH_SHORT).show();
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
