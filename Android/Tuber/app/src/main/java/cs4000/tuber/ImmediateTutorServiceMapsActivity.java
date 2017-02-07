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
    Boolean requestActive = false;
    Boolean studentActive = false;
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
            obj.put("latitude", "40.7674");
            obj.put("longitude", "-111.844");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        // cheching if already offered to tutor
        ConnectionTask checkstatust = new ConnectionTask(new ConnectionTask.CallBack() {
            @Override
            public boolean Done(JSONObject result) {
                try {
                    if(result != null && !result.get("studentEmail").equals(null)){
                        Log.i("@checkstatust2", "check status successful w/non-EmptyJson");

                        //session_status = result.getString("session_status");
                        //studentEmail = result.getString("studentEmail");
                        studentLatitude = result.getString("studentLatitude");
                        studentLongitude = result.getString("studentLongitude");
                        //tutorCourse = result.getString("tutorCourse");
                        tutorLatitude = result.getString("tutorLatitude");
                        tutorLongitude = result.getString("tutorLongitude");
                        //userEmail = result.getString("userEmail");
                        //userToken = result.getString("userToken");


                        if (studentActive == false) {

                            new AlertDialog.Builder(ImmediateTutorServiceMapsActivity.this)
                                    .setTitle("Paired")
                                    .setMessage("You paired successfully with a Tutor.")
                                    .setCancelable(false)
                                    .setPositiveButton("Acknowledged", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    }).show();
                        }

                        studentActive = true;

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
                                            infoTextView.setText("");
                                            offerToTutorButton.setVisibility(View.VISIBLE);
                                            offerToTutorButton.setText("Offer to Tutor");
                                            requestActive = false;
                                            studentActive = false;
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

                                    offerToTutorButton.setVisibility(View.INVISIBLE);

                                    handler.postDelayed(new Runnable() {

                                        @Override

                                        public void run() {

                                            checkForUpdate();

                                        }
                                    }, 2000);

                                }

                            }

                        }

                        return true;
                    } else {
                        Log.i("@checkstatust2", "check status failed!");

                        if(requestActive){
                            handler.postDelayed(new Runnable() {

                                @Override

                                public void run() {

                                    checkForUpdate();

                                }
                            }, 2000);
                        }
                        return false;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        });
        checkstatust.check_paired_status(obj);
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
        if(requestActive){

            JSONObject obj = new JSONObject();
            try{
                obj.put("userEmail", _userEmail);
                obj.put("userToken", _userToken);
                obj.put("latitude", "40.7674");
                obj.put("longitude", "-111.844");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // cheching if already offered to tutor
            ConnectionTask checkstatust = new ConnectionTask(new ConnectionTask.CallBack() {
                @Override
                public boolean Done(JSONObject result) {
                    try {
                        if(result != null && result.get("studentEmail").equals(null)){ // offered but hasn't been paired yet - delete from list
                            Log.i("@checkstatust3", "check status successful w/EmptyJson");


                            JSONObject obj2 = new JSONObject();

                            obj2.put("userEmail", _userEmail);
                            obj2.put("userToken", _userToken);


                            // deleting existing request
                            ConnectionTask deleteTutor = new ConnectionTask(new ConnectionTask.CallBack() {
                                @Override
                                public boolean Done(JSONObject result) {
                                    if(result != null){
                                        Log.i("@deleteTutor", "delete Tutor successful");
                                        return true;
                                    } else {
                                        Log.i("@deleteTutor", "delete Tutor failed!");
                                        return false;
                                    }
                                }
                            });
                            deleteTutor.delete_tutor_available(obj2);

                            requestActive = false;
                            offerToTutorButton.setText("Offer to Tutor");


                            return true;
                        } else {
                            Log.i("@checkstatust3", "check status failed");
                            return false;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            });
            checkstatust.check_paired_status(obj);



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
                        obj3.put("latitude", "40.7674");
                        obj3.put("longitude", "-111.844");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ConnectionTask offerToTutorTask = new ConnectionTask(new ConnectionTask.CallBack() {
                        @Override
                        public boolean Done(JSONObject result) {
                            if(result != null){
                                Log.i("@OfferToTutor", "Offer to tutor successful");
                                Log.i("userEmail", _userEmail);
                                Log.i("userToken", _userToken);
                                Log.i("tutorCourse", _userCourse);

                                offerToTutorButton.setText("Cancel Offer");
                                requestActive = true;
                                //Log.i("Info", "Tutor Service Cancelled");

                                checkForUpdate();

                                return true;
                            } else {
                                Log.i("@OfferToTutor", "Offer to tutor failed!");
                                Log.i("userEmail", _userEmail);
                                Log.i("userToken", _userToken);
                                Log.i("tutorCourse", _userCourse);
                                return false;
                            }
                        }
                    });
                    offerToTutorTask.make_tutor_available(obj3);


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

//        ParseUser.getCurrentUser().put("studentOrTutor", userType);
//        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                // TODO: empty for now
//                //redirectActivity?
//            }
//        });

        offerToTutorButton = (Button) findViewById(R.id.offerToTutorButton);
        infoTextView = (TextView) findViewById(R.id.infoTextView);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // grab user's email and password from shared preferences as well as the class in which the tutor
        // user course should come from a message delivered by the prior activity
        _userEmail = sharedPreferences.getString("userEmail", "");
        _userToken = sharedPreferences.getString("userToken", "");
        _userCourse = "CS 2420"; // TODO: must come from the message supplied by the intent driver


        JSONObject obj = new JSONObject();
        try{
            obj.put("userEmail", _userEmail);
            obj.put("userToken", _userToken);
            obj.put("latitude", "40.7674");
            obj.put("longitude", "-111.844");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // cheching if already offered to tutor
        ConnectionTask checkstatust = new ConnectionTask(new ConnectionTask.CallBack() {
            @Override
            public boolean Done(JSONObject result) {

                if(result != null){ // offered but hasn't been paired yet - delete from list
                    Log.i("@checkstatust1", "check status successful");
                    try {
                        if(result.get("studentEmail").equals(null)){
                            requestActive = true;
                            offerToTutorButton.setText("Cancel Offer");

                            checkForUpdate();
                        } else {
                            checkForUpdate();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return true;
                } else {
                    Log.i("@checkstatust1", "check status failed!");
                    return false;
                }
            }
        });
        checkstatust.check_paired_status(obj);

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

                ConnectionTask UpdateLocation = new ConnectionTask(new ConnectionTask.CallBack() {
                    @Override
                    public boolean Done(JSONObject result) {
                        if(result != null){
                            Log.i("@onLocationChanged","Location updated successfully");
                            return true;
                        }
                        else {
                            Log.i("@onLocationChanged","Location update failed");
                            return false;
                        }
                    }
                });
                UpdateLocation.update_tutor_location(jO);

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

        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

        mMap.clear(); // clears all existing markers
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 14));
        mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
    }
}