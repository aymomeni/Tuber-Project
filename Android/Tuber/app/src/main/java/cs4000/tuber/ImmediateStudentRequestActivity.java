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
//import com.parse.FindCallback;
//import com.parse.ParseException;
//import com.parse.ParseGeoPoint;
//import com.parse.ParseObject;
//import com.parse.ParseQuery;
//import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

//import jp.wasabeef.recyclerview.animators.*;
//import jp.wasabeef.recyclerview.animators.FadeInAnimator;
//import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;


/*
 * Displays all students that created a immediate tutor request
 * In list form where each element can be clicked
 */
public class ImmediateStudentRequestActivity extends AppCompatActivity {


    private String _userEmail;
    private String _userToken;
    private String _userLatitude;
    private String _userLongitude;

    private String course;

    private Location temp;


    private ConnectionTask connectionTask;
    private SharedPreferences sharedPreferences;


    private SwipeRefreshLayout swipeContainer;

    RecyclerView Users_rv;
    ArrayList<User> users = new ArrayList<User>();
    UserAdapter adapter;

    LocationManager locationManager;
    LocationListener locationListener;

    private Intent intent;

    Handler handler = new Handler(); // used for polling
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


//    public static ImmediateStudentRequestActivity getInstance(){
//        return activity;
//    }
//    static ImmediateStudentRequestActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        //activity = this;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        _userEmail = sharedPreferences.getString("userEmail", "");
        _userToken = sharedPreferences.getString("userToken", "");

        intent = getIntent();

        course = intent.getStringExtra("course");
        Log.i("@course_check",course);


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
                updateListView(getLocation());
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
                            if(Build.VERSION.SDK_INT < 23 || ContextCompat.checkSelfPermission(ImmediateStudentRequestActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                                if(users.size() > position && lastKnownLocation != null) {

                                    Log.i("Inhere","123");
                                    Intent intent = new Intent(getApplicationContext(), StudentMapActivity.class);

                                    intent.putExtra("tutorLatitude", users.get(position).getLatitudes());
                                    intent.putExtra("tutorLongitude", users.get(position).getLongitude());
                                    intent.putExtra("studentLatitude", lastKnownLocation.getLatitude());
                                    intent.putExtra("studentLongitude", lastKnownLocation.getLongitude());
                                    intent.putExtra("course", course);
                                    intent.putExtra("username", users.get(position).getUserEmail());

                                    startActivity(intent);
                                }
                            }
                        }

                    }
                }
        );

        setTitle("Nearby Tutors");

        // Create adapter passing in the sample user data
        adapter = new UserAdapter(this, users);
        // Set layout manager to position the items
        Users_rv.setLayoutManager(new LinearLayoutManager(this));

        adapter.clear();
        // Attach the adapter to the recyclerview to populate items
        Users_rv.setAdapter(adapter);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                Log.i("@UpdatingList","Three");
                updateListView(location);

                _userLatitude = Double.toString(location.getLatitude());
                _userLongitude = Double.toString(location.getLongitude());

                JSONObject jO = new JSONObject();
                try{
                    jO.put("userEmail", _userEmail);
                    jO.put("userToken", _userToken);
                    jO.put("latitude", _userLatitude);
                    jO.put("longitude", _userLongitude);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ConnectionTask UpdateLocation = new ConnectionTask(jO);
                UpdateLocation.update_student_location(new ConnectionTask.CallBack() {
                    @Override
                    public void Done(JSONObject result) {
                        if(result != null){
//                            Log.i("@onLocationChanged","Location updated successfully");
                        }
                        else {
//                            Log.i("@onLocationChanged","Location update failed");
                        }
                    }
                });
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    // Updates the list view that the students can view
    public void updateListView(final Location location) {

        if (location != null) {


            JSONObject jsonParam3 = new JSONObject();
            try {
                jsonParam3.put("userEmail", _userEmail);
                jsonParam3.put("userToken", _userToken);
                jsonParam3.put("tutorCourse", course);
                jsonParam3.put("latitude", String.valueOf(location.getLatitude()));
                jsonParam3.put("longitude", String.valueOf(location.getLongitude()));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            ConnectionTask task = new ConnectionTask(jsonParam3);
            task.find_available_tutors(new ConnectionTask.CallBack() {
                @Override
                public void Done(JSONObject result) {

                    // Do Something after the task has finished
                    if(result != null) {

                        adapter.clear();

                        try {


                            JSONArray array = result.getJSONArray("availableTutors");

                            if (array.length() > 0) {
                                for(int i = 0; i < array.length(); i++) {
                                    JSONObject temp = array.getJSONObject(i);
                                    String distanceFromStudent = temp.getString("distanceFromStudent");
                                    String latitude = temp.getString("latitude");
                                    String longitude = temp.getString("longitude");
                                    String tutorCourse = temp.getString("tutorCourse");
                                    String userEmail = temp.getString("userEmail");
                                    String firstName = temp.getString("firstName");
                                    String lastName = temp.getString("lastName");
                                    String rating = temp.getString("averageRating");
                                    String numRating = temp.getString("ratingCount");

                                    Double distanceOneDP = (double) Math.round(Double.parseDouble(distanceFromStudent) * 10) / 10;

                                    User temp1 = new User(userEmail, distanceOneDP);
                                    temp1.setFirstName(firstName);
                                    temp1.setLastName(lastName);
                                    temp1.setLatitudes(Double.parseDouble(latitude));
                                    temp1.setLongitude(Double.parseDouble(longitude));
                                    temp1.setUserRating(Float.valueOf(rating));
                                    temp1.setNumOfRatings(Integer.valueOf(numRating));
                                    adapter.add(temp1);

                                    Log.i("@UpdatingList", temp.toString());

                                }

                            } else {
//                                Log.i("Second Thread", "No tutors");

                                Toast.makeText(getBaseContext(), "No active tutors nearby", Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    swipeContainer.setRefreshing(false);
                }
            });
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    Log.i("@UpdatingList","ONE");
                    updateListView(lastKnownLocation);

                }
            }
        }
    }

    private Location getLocation(){
        Location temp = new Location(LocationManager.GPS_PROVIDER);
        temp.setLatitude(0);
        temp.setLongitude(0);

        // Checking for GPS permissions
        if (Build.VERSION.SDK_INT < 23) {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        } else {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            } else {

                // else we have permission
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                // gets last knwon location else it'll update the location based on the current location
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (lastKnownLocation != null) {

                    return lastKnownLocation;
                }
            }
        }
        return temp;
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("ImmediateStudentRequest Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        temp = getLocation();
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

                        if(status.equals("paired")) {
                            Log.i("Inhere","321");


                            JSONObject obj2 = new JSONObject();
                            try{
                                obj2.put("userEmail", _userEmail);
                                obj2.put("userToken", _userToken);
                                obj2.put("latitude", String.valueOf(temp.getLatitude()));
                                obj2.put("longitude", String.valueOf(temp.getLongitude()));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ConnectionTask task = new ConnectionTask(obj2);
                            task.update_student_location(new ConnectionTask.CallBack() {
                                @Override
                                public void Done(JSONObject result) {
                                    if(result != null) {
                                        Intent intent = new Intent(getApplicationContext(), StudentMapActivity.class);
                                        try {
                                            intent.putExtra("tutorLatitude", Double.valueOf(result.getString("tutorLatitude")));
                                            intent.putExtra("tutorLongitude", Double.valueOf(result.getString("tutorLongitude")));
                                            intent.putExtra("studentLatitude", temp.getLatitude());
                                            intent.putExtra("studentLongitude", temp.getLongitude());
                                            intent.putExtra("course", course);
                                            intent.putExtra("username", result.getString("tutorEmail"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        startActivity(intent);
                                        finish();
                                    } else {

                                    }
                                }
                            });
                        } else if(status.equals("pending") || status.equals("active") ) {
                            Intent intent = new Intent(ImmediateStudentRequestActivity.this, StudentStudySession.class);
                            intent.putExtra("course", course);
                            //intent.putExtra("status", "1");
                            startActivity(intent);
                            finish();
                        }  else {
                            Log.i("@UpdatingList","TWO");
                            updateListView(getLocation());
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    updateListView(getLocation());
                }
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }


}
