package cs4000.tuber;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static cs4000.tuber.R.id.infoTextView;
import static cs4000.tuber.R.id.logoutButton;
import static cs4000.tuber.R.id.offerToTutorButton;

/*
 * Represents
 */
public class ImmediateTutorServiceMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;

    String userType = "tutor";

    Button offerToTutorButton;
    Button log_out_botton;
    Boolean tutor_offered = false; // offered
    Boolean initial_pairing = true;
    TextView infoTextView;

    private SharedPreferences sharedPreferences;

    private String _userEmail;
    private String _userToken;
    private String _userCourse;
    //private String _userLatitude;
    //private String _userLongitude;

    //private String session_status;
    //private String studentEmail;
    private String studentLatitude;
    private String studentLongitude;
    //private String tutorCourse;
    private String tutorLatitude;
    private String tutorLongitude;
    //private String userEmail;
    //private String userToken;


    Handler handler = new Handler(); // used for polling

    public void checkForUpdate() {


        JSONObject obj = new JSONObject();
        try{
            obj.put("userEmail", _userEmail);
            obj.put("userToken", _userToken);
            obj.put("latitude", tutorLatitude);
            obj.put("longitude", tutorLongitude);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        // checking if already offered to tutor
        ConnectionTask checkstatust = new ConnectionTask(obj);
        checkstatust.check_paired_status(new ConnectionTask.CallBack() {
            @Override
            public void Done(JSONObject result) {
                try {
                    if(result != null && !result.get("studentEmail").equals(null)){ // student has accepted (check session_status)
                        Log.i("@checkstatust2", "check status successful w/non-EmptyJson");

                        offerToTutorButton.setVisibility(View.INVISIBLE);

                        //session_status = result.getString("session_status");
                        //studentEmail = result.getString("studentEmail");
                        studentLatitude = result.getString("studentLatitude");
                        studentLongitude = result.getString("studentLongitude");
                        //tutorCourse = result.getString("tutorCourse");
                        tutorLatitude = result.getString("tutorLatitude");
                        tutorLongitude = result.getString("tutorLongitude");
                        //userEmail = result.getString("userEmail");
                        //userToken = result.getString("userToken");


                        if (initial_pairing == true) { // show this only first-time we enter this method

                            final AlertDialog dialog = new AlertDialog.Builder(ImmediateTutorServiceMapsActivity.this)
                                    .setTitle("Paired")
                                    .setMessage("You paired successfully with a Tutor.")
                                    .setCancelable(false)
                                    .setPositiveButton("Acknowledged", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    }).show();

                            handler.postDelayed(new Runnable() {

                                @Override

                                public void run() {
                                    dialog.dismiss();
                                }
                            }, 4000);
                        }
                        initial_pairing = false;

                        if (Build.VERSION.SDK_INT < 23 || ContextCompat.checkSelfPermission(ImmediateTutorServiceMapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                            // gets last knwon location else it'll update the location based on the current location
                            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                            if (lastKnownLocation != null) {

                                Double distanceInMiles = Double.parseDouble(result.getString("distanceFromStudent"));

                                if (distanceInMiles < 0.5) { // student has arrived!
                                    infoTextView.setTextColor(Color.GREEN);
                                    infoTextView.setText("Your student has arrived");


                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
//                                            infoTextView.setText("");
//                                            offerToTutorButton.setVisibility(View.VISIBLE);
//                                            offerToTutorButton.setText("Offer to Tutor");
//                                            tutor_offered = false;
//                                            initial_pairing = true;

                                            Intent intent = new Intent(ImmediateTutorServiceMapsActivity.this, Studysession.class);
                                            intent.putExtra("status", "0");
//
                                            startActivity(intent);
                                            finish();

                                        }
                                    }, 5000);
                                } else { // studet hasn't arrived yet


                                    Double distanceOneDP = (double) Math.round(distanceInMiles * 10) / 10;

                                    infoTextView.setText("Your student is " + distanceOneDP.toString() + " miles away!");


                                    LatLng studentLocationLatLng = new LatLng(Double.parseDouble(studentLatitude), Double.parseDouble(studentLongitude));
                                    LatLng requestLocationLatLng = new LatLng(Double.parseDouble(tutorLatitude), Double.parseDouble(tutorLongitude));

                                    ArrayList<Marker> markers = new ArrayList<>();

                                    mMap.clear(); // clears all existing markers
                                    markers.add(mMap.addMarker(new MarkerOptions().position(studentLocationLatLng).title("Student Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))));
                                    markers.add(mMap.addMarker(new MarkerOptions().position(requestLocationLatLng).title("Your Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))));

                                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                    for (Marker marker : markers) {
                                        builder.include(marker.getPosition());
                                    }
                                    LatLngBounds bounds = builder.build();

                                    int padding = 60;
                                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

                                    mMap.animateCamera(cu);

                                    handler.postDelayed(new Runnable() {

                                        @Override

                                        public void run() {

                                            checkForUpdate();

                                        }
                                    }, 2000);

                                }

                            }

                        }

                    } else { // no student accepted yet
                        Log.i("@checkstatust2", "check status failed!");

                        if(tutor_offered){
                            handler.postDelayed(new Runnable() {

                                @Override

                                public void run() {

                                    checkForUpdate();

                                }
                            }, 2000);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void logout(View view) {

        Log.i("Info", "Logout Tutor Immediate Service");
        //ParseUser.logOut();

        //Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
        //startActivity(intent);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    updateMap(lastKnownLocation);

                }
            }
        }
    }


    public void offerToTutor(View view) {

        Log.i("Info", "Tutor Requested");

        // checking if a request is active
        if(tutor_offered){

            JSONObject obj = new JSONObject();
            try{
                obj.put("userEmail", _userEmail);
                obj.put("userToken", _userToken);
                obj.put("latitude", tutorLatitude);
                obj.put("longitude", tutorLongitude);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // cheching if already offered to tutor
            ConnectionTask checkstatust = new ConnectionTask(obj);
            checkstatust.check_paired_status(new ConnectionTask.CallBack() {
                @Override
                public void Done(JSONObject result) {
                    try {
                        if(result != null && result.get("studentEmail").equals(null)){ // offered but hasn't been paired yet - delete from list
                            Log.i("@checkstatust3", "check status successful w/EmptyJson");


                            JSONObject obj2 = new JSONObject();
                            obj2.put("userEmail", _userEmail);
                            obj2.put("userToken", _userToken);

                            // deleting existing request
                            ConnectionTask deleteTutor = new ConnectionTask(obj2);
                            deleteTutor.delete_tutor_available(new ConnectionTask.CallBack() {
                                @Override
                                public void Done(JSONObject result) {
                                    if(result != null){
                                        Log.i("@deleteTutor", "delete Tutor successful");
                                    } else {
                                        Log.i("@deleteTutor", "delete Tutor failed!");
                                    }
                                }
                            });

                            tutor_offered = false;
                            offerToTutorButton.setText("Offer to Tutor");

                        } else {
                            Log.i("@checkstatust3", "check status failed");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (lastKnownLocation != null) { // make tutor avaliable


                    JSONObject obj3 = new JSONObject();
                    try{
                        obj3.put("userEmail", _userEmail);
                        obj3.put("userToken", _userToken);
                        obj3.put("tutorCourse", _userCourse);
                        obj3.put("latitude", String.valueOf(lastKnownLocation.getLatitude()));
                        obj3.put("longitude", String.valueOf(lastKnownLocation.getLongitude()));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    ConnectionTask offerToTutorTask = new ConnectionTask(obj3);
                    offerToTutorTask.make_tutor_available(new ConnectionTask.CallBack() {
                        @Override
                        public void Done(JSONObject result) {
                            if(result != null){
                                Log.i("@OfferToTutor", "Offer to tutor successful");
                                Log.i("userEmail", _userEmail);
                                Log.i("userToken", _userToken);
                                Log.i("tutorCourse", _userCourse);

                                offerToTutorButton.setText("Cancel Offer");
                                tutor_offered = true;
                                //Log.i("Info", "Tutor Service Cancelled");

                                checkForUpdate();

                            } else {
                                Log.i("@OfferToTutor", "Offer to tutor failed!");
                                Log.i("userEmail", _userEmail);
                                Log.i("userToken", _userToken);
                                Log.i("tutorCourse", _userCourse);
                            }
                        }
                    });

                } else {
                    Toast.makeText(this, "Could not find your location. Please try again later.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_immediate_tutor_service_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.activity_immediate_tutor_service_map);
        mapFragment.getMapAsync(this);

        log_out_botton = (Button) findViewById(R.id.logoutButton);
        log_out_botton.setVisibility(View.INVISIBLE);

        offerToTutorButton = (Button) findViewById(R.id.offerToTutorButton);
        infoTextView = (TextView) findViewById(R.id.infoTextView);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // grab user's email and password from shared preferences as well as the class in which the tutor
        // user course should come from a message delivered by the prior activity
        _userEmail = sharedPreferences.getString("userEmail", "");
        _userToken = sharedPreferences.getString("userToken", "");
        _userCourse = "CS 2420"; // TODO: must come from the message supplied by the intent driver


        JSONObject obj2 = new JSONObject();
        try{
            obj2.put("userEmail", _userEmail);
            obj2.put("userToken", _userToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ConnectionTask check_session_status = new ConnectionTask(obj2);
        check_session_status.check_session_status(new ConnectionTask.CallBack() {
            @Override
            public void Done(JSONObject result) {
                if(result != null){
                    Log.i("@check_session_status", "check session completed");

                    try {

                        String status = result.getString("session_status");
                        if(status.equals("available")){ // only offered but looking to pair
                            tutor_offered = true;
                            offerToTutorButton.setText("Cancel Offer");

                            checkForUpdate();
                        } else if(status.equals("paired")){ // paired
                            checkForUpdate();
                        } else if(status.equals("active")){ // in an active session
                            Intent intent = new Intent(ImmediateTutorServiceMapsActivity.this, Studysession.class);
                            intent.putExtra("status", "1");
                            startActivity(intent);
                            finish();
                        } else if(status.equals("completed")){ // session has ended
                            // do nothing
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    Log.i("@check_session_status", "check session status failed!"); // has not offered yet
                }
            }
        });

//        // cheching if already offered to tutor
//        ConnectionTask checkstatust = new ConnectionTask(obj);
//        checkstatust.check_paired_status(new ConnectionTask.CallBack() {
//            @Override
//            public void Done(JSONObject result) {
//                if(result != null){
//                    Log.i("@checkstatust1", "check status successful");
//                    try {
//                        if(result.get("studentEmail").equals(null)){ // offered but hasn't been paired yet
//                            tutor_offered = true;
//                            offerToTutorButton.setText("Cancel Offer");
//
//                            checkForUpdate();
//                        } else { // paired
//
//
//
//                            checkForUpdate();
//
////                            if(result.getString("session_status").equals("0")) {
////                                checkForUpdate();
////                            } else { // go to session page - session is active
////                                Intent intent = new Intent(ImmediateTutorServiceMapsActivity.this, Studysession.class);
//////
//////                                intent.putExtra("tutorLatitude", requestLatitudes.get(i));
//////                                intent.putExtra("tutorLongitude", requestLongitudes.get(i));
//////                                intent.putExtra("studentLatitude", lastKnownLocation.getLatitude());
//////                                intent.putExtra("studentLongitude", lastKnownLocation.getLongitude());
//////                                intent.putExtra("studentCourse", courses.get(i));
//////                                intent.putExtra("username", usernames.get(i));
//////
////                                startActivity(intent);
////                                finish();
////                                //finishAndRemoveTask();
////                                //return;
////                            }
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    Log.i("@checkstatust1", "check status failed!");
//                }
//            }
//        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                tutorLatitude = String.valueOf(location.getLatitude());
                tutorLongitude = String.valueOf(location.getLongitude());

                updateMap(location);

                JSONObject jO = new JSONObject();
                try{
                    jO.put("userEmail", _userEmail);
                    jO.put("userToken", _userToken);
                    jO.put("latitude", String.valueOf(location.getLatitude()));
                    jO.put("longitude", String.valueOf(location.getLongitude()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ConnectionTask UpdateLocation = new ConnectionTask(jO);
                UpdateLocation.update_tutor_location(new ConnectionTask.CallBack() {
                    @Override
                    public void Done(JSONObject result) {
                        if(result != null){
                            Log.i("@onLocationChanged","Location updated successfully");
                        }
                        else {
                            Log.i("@onLocationChanged","Location update failed");
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
                    updateMap(lastKnownLocation);
                }
            }
        }
    }

    // Updates the map when location of user changes
    public void updateMap(Location location) {
        tutorLatitude = String.valueOf(location.getLatitude());
        tutorLongitude = String.valueOf(location.getLongitude());

        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

        mMap.clear(); // clears all existing markers
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 14));
        mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
    }
}