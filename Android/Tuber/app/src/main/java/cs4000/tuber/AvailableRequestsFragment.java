package cs4000.tuber;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by FahadTmem on 2/18/17.
 */

public class AvailableRequestsFragment extends Fragment {


    private SwipeRefreshLayout swipeContainer;

    RecyclerView Requests_rv;
    ArrayList<Request> requests = new ArrayList<Request>();
    RequestsAdapter adapter;

    private SharedPreferences sharedPreferences;
    private String _userEmail;
    private String _userToken;

    private String course;

    @Override
    public void onStart() {
        super.onStart();
        UpdateList();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View rootView = inflater.inflate(R.layout.available_requests_fragment_layout, container, false);
        if(container == null) {
            return null;
        }


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        _userEmail = sharedPreferences.getString("userEmail", "");
        _userToken = sharedPreferences.getString("userToken", "");

        course = getActivity().getIntent().getStringExtra("course");
        Log.i("@course_check",course);

        Requests_rv = (RecyclerView) rootView.findViewById(R.id.requests_rv);
        Requests_rv.setHasFixedSize(true);
        Requests_rv.setItemAnimator(new SlideInUpAnimator());

        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST);
        Requests_rv.addItemDecoration(itemDecoration);

        swipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeContainer);
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

                                Intent intent = new Intent(getContext(), AvailableAcceptedRequestPage.class);

                                intent.putExtra("studentEmail", requests.get(position).getStudentEmail());
                                intent.putExtra("studentFirstName", requests.get(position).getStudentFirstName());
                                intent.putExtra("studentLastName", requests.get(position).getStudentLastName());
                                intent.putExtra("topic", requests.get(position).getTopic());
                                intent.putExtra("dateTime", requests.get(position).getDateTime());
                                intent.putExtra("duration", requests.get(position).getDuration());
                                intent.putExtra("course", requests.get(position).getCourse());
                                intent.putExtra("type", "available");

                                startActivity(intent);
                            }
                        }
                    }
                }
        );

        // Create adapter passing in the sample user data
        adapter = new RequestsAdapter(getContext(), requests);
        // Set layout manager to position the items
        Requests_rv.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter.clear();
        // Attach the adapter to the recyclerview to populate items
        Requests_rv.setAdapter(adapter);

        return rootView;
    }

    public void UpdateList() {

        JSONObject obj = new JSONObject();
        try {
            obj.put("userEmail", _userEmail);
            obj.put("userToken", _userToken);
            obj.put("course", course);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ConnectionTask task = new ConnectionTask(obj);
        task.find_all_scheduled_tutor_requests(new ConnectionTask.CallBack() {
            @Override
            public void Done(JSONObject result) {
                if(result != null) {


                    adapter.clear();
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
                                String studentFirstName = temp.getString("firstName");
                                String studentLastName = temp.getString("lastName");
                                String topic = temp.getString("topic");


                                Request temp1 = new Request(course, topic);
                                temp1.setStudentEmail(studentEmail);
                                temp1.setStudentFirstName(studentFirstName);
                                temp1.setStudentLastName(studentLastName);
                                temp1.setCourse(course);
                                temp1.setDateTime(datetime);
                                temp1.setDuration(duration);

                                adapter.add(temp1);

                            }

                        } else {
                            Toast.makeText(getContext(), "No available requests found", Toast.LENGTH_SHORT).show();
                        }

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
