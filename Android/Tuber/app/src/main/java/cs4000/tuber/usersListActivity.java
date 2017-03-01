package cs4000.tuber;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static java.security.AccessController.getContext;


/*
 * Displays all students that created a immediate tutor request
 * In list form where each element can be clicked
 */
public class UsersListActivity extends AppCompatActivity {


    private String _userEmail;
    private String _userToken;


    private SharedPreferences sharedPreferences;

    private SwipeRefreshLayout swipeContainer;

    RecyclerView Users_rv;
    ArrayList<User> users = new ArrayList<User>();
    UserAdapter adapter;

    private Intent intent;

    Handler handler = new Handler(); // used for polling


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        _userEmail = sharedPreferences.getString("userEmail", "");
        _userToken = sharedPreferences.getString("userToken", "");

        intent = getIntent();


        // Lookup the recyclerview in activity layout
        Users_rv = (RecyclerView) findViewById(R.id.users_rv);
        Users_rv.setHasFixedSize(true);
        Users_rv.setItemAnimator(new SlideInUpAnimator());

        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        Users_rv.addItemDecoration(itemDecoration);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                updateListView();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        ItemClickSupport.addTo(Users_rv).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                        if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted, but the user clicked it before the UI removed it
                            if(users.size() > position) {

                                Intent intent = new Intent(getApplicationContext(), MessagingActivity.class);

                                intent.putExtra("recipientEmail", users.get(position).getUserEmail());
                                intent.putExtra("recipientFirstname", users.get(position).getFirstName());
                                intent.putExtra("recipientLastname", users.get(position).getLastName());

                                startActivity(intent);
                            }
                        }

                    }
                }
        );

        setTitle("Available Users");

        // Create adapter passing in the sample user data
        adapter = new UserAdapter(this, users);
        // Set layout manager to position the items
        Users_rv.setLayoutManager(new LinearLayoutManager(this));

        adapter.clear();
        // Attach the adapter to the recyclerview to populate items
        Users_rv.setAdapter(adapter);
    }

    // Updates the list view that the students can view
    public void updateListView() {

        JSONObject jsonParam3 = new JSONObject();
        try {
            jsonParam3.put("userEmail", _userEmail);
            jsonParam3.put("userToken", _userToken);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ConnectionTask task = new ConnectionTask(jsonParam3);
        task.get_users(new ConnectionTask.CallBack() {
            @Override
            public void Done(JSONObject result) {

                // Do Something after the task has finished
                if(result != null) {

                    adapter.clear();

                    try {


                        JSONArray array = result.getJSONArray("users");

                        if (array.length() > 0) {
                            for(int i = 0; i < array.length(); i++) {
                                JSONObject temp = array.getJSONObject(i);
                                String firstName = temp.getString("firstName");
                                String lastName = temp.getString("lastName");
                                String userEmail = temp.getString("email");

                                User temp1 = new User(userEmail, 0.0);
                                temp1.setFirstName(firstName);
                                temp1.setLastName(lastName);
                                temp1.setType("alt");
                                adapter.add(temp1);

                            }

                        } else {

                            Toast.makeText(getBaseContext(), "No users found", Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                swipeContainer.setRefreshing(false);
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();

        updateListView();
    }
}