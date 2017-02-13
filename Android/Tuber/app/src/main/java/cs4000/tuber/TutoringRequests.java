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

public class TutoringRequests extends Activity {

    ListView requestsListView;
    ArrayList<String> requests = new ArrayList<String>();
    ArrayAdapter arrayAdapter;

    private SharedPreferences sharedPreferences;
    private String _userEmail;
    private String _userToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutoring_requests);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        _userEmail = sharedPreferences.getString("userEmail", "");
        _userToken = sharedPreferences.getString("userToken", "");

        requestsListView = (ListView) findViewById(R.id.requestsListId);

        requests.clear();
        requests.add("Getting scheduled requests...");

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, requests);

        requestsListView.setAdapter(arrayAdapter);


        requestsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                if(requests.size() > i) {

                    //Intent intent = new Intent(getApplicationContext(), StudentMapActivity.class);

//                    intent.putExtra("tutorLatitude", requestLatitudes.get(i));
//                    intent.putExtra("tutorLongitude", requestLongitudes.get(i));
//                    intent.putExtra("studentLatitude", lastKnownLocation.getLatitude());
//                    intent.putExtra("studentLongitude", lastKnownLocation.getLongitude());
//                    intent.putExtra("studentCourse", courses.get(i));
//                    intent.putExtra("username", usernames.get(i));

                    //startActivity(intent);
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

        } catch (JSONException e) {
            e.printStackTrace();
        }
        ConnectionTask task = new ConnectionTask(obj);
        task.check_scheduled_paired_status(new ConnectionTask.CallBack() {
            @Override
            public void Done(JSONObject result) {
                if(result != null) {

                    requests.clear();

                    try {
                        JSONArray array = result.getJSONArray("requests");


                        if (array.length() > 0) {

                            for(int i = 0; i < array.length(); i++) {
                                JSONObject temp = array.getJSONObject(i);
                                String course = temp.getString("course");
                                String datetime = temp.getString("dateTime");
                                String duration = temp.getString("duration");
                                String studentEmail = temp.getString("studentEmail");
                                String topic = temp.getString("topic");

                                requests.add(course + ": " + topic);
                            }

                        } else {
                            requests.add("no scheduled requests found");
                            Toast.makeText(TutoringRequests.this, "no scheduled requests found", Toast.LENGTH_SHORT).show();
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
