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
import android.net.Uri;
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

    boolean exited = false;

    Button offerToTutorButton;
    Button log_out_botton;
    Boolean tutor_offered = false; // offered
    Boolean initial_pairing = true;
    TextView infoTextView;

    private SharedPreferences sharedPreferences;

    private String _userEmail;
    private String _userToken;

    private String studentLatitude;
    private String studentLongitude;
    private String tutorLatitude;
    private String tutorLongitude;

    private String course;

    Handler handler = new Handler(); // used for polling

    @Override
    public void onBackPressed() {
        exited = true;
        finish();
    }

    Intent intent;


    // UonlocationChanged: check the dist to start the tutoring session!

    public void checkForUpdate2() {


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
            public void Done(final JSONObject result) {
                try {
                    if(result != null && !result.get("studentEmail").equals(null)){
                        offerToTutorButton.setVisibility(View.INVISIBLE);

                        //if (initial_pairing == true) {
                        // pairing complete
                        final AlertDialog dialog = new AlertDialog.Builder(ImmediateTutorServiceMapsActivity.this)
                                .setTitle("Paired")
                                .setMessage("You paired successfully with a Student.")
                                .setCancelable(false)
                                .setNeutralButton("Directions", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent directionsIntent = new Intent(Intent.ACTION_VIEW,
                                                Uri.parse("http://maps.google.com/maps?saddr=" + intent.getDoubleExtra("tutorLatitude", 0) + "," + intent.getDoubleExtra("tutorLongitude", 0) + "&daddr=" + intent.getDoubleExtra("requestLatitude", 0) + "," + intent.getDoubleExtra("requestLongitude", 0)));
                                        startActivity(directionsIntent);
                                    }
                                })
                                .setPositiveButton("Acknowledged", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                }).show();

                        studentLatitude = result.getString("studentLatitude");
                        studentLongitude = result.getString("studentLongitude");

                        LatLng studentLocationLatLng = new LatLng(Double.parseDouble(studentLatitude), Double.parseDouble(studentLongitude));
                        LatLng requestLocationLatLng = new LatLng(Double.parseDouble(tutorLatitude), Double.parseDouble(tutorLongitude));

                        ArrayList<Marker> markers = new ArrayList<>();
                        mMap.clear(); // clears all existing markers
                        markers.add(mMap.addMarker(new MarkerOptions().position(requestLocationLatLng).title("Your Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))));
                        markers.add(mMap.addMarker(new MarkerOptions().position(studentLocationLatLng).title("Student Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))));

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
                                dialog.dismiss();

                                try {
                                    if(Double.valueOf(result.getString("distanceFromStudent")) < 0.5){


                                        Intent intent = new Intent(ImmediateTutorServiceMapsActivity.this, Studysession.class);
                                        intent.putExtra("status", "0");
                                        startActivity(intent);
                                        finish();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 4000);

                        //}
                        //initial_pairing = false;


                    } else { // no student accepted yet
//                        Log.i("@checkstatust2", "check status failed!");

                        if(tutor_offered && !exited){
                            handler.postDelayed(new Runnable() {

                                @Override

                                public void run() {
                                    checkForUpdate2();
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

//        Log.i("Info", "Logout Tutor Immediate Service");
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

//        Log.i("Info", "Tutor Requested");

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
//                            Log.i("@checkstatust3", "check status successful w/EmptyJson");


                            JSONObject obj2 = new JSONObject();
                            obj2.put("userEmail", _userEmail);
                            obj2.put("userToken", _userToken);

                            // deleting existing request
                            ConnectionTask deleteTutor = new ConnectionTask(obj2);
                            deleteTutor.delete_tutor_available(new ConnectionTask.CallBack() {
                                @Override
                                public void Done(JSONObject result) {
                                    if(result != null){
//                                        Log.i("@deleteTutor", "delete Tutor successful");
                                    } else {
//                                        Log.i("@deleteTutor", "delete Tutor failed!");
                                    }
                                }
                            });

                            tutor_offered = false;
                            offerToTutorButton.setText("Offer to Tutor");

                        } else {
//                            Log.i("@checkstatust3", "check status failed");
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
                        obj3.put("tutorCourse", course);
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
//                                Log.i("@OfferToTutor", "Offer to tutor successful");

                                offerToTutorButton.setText("Cancel Offer");
                                tutor_offered = true;

                                checkForUpdate2();

                            } else {
//                                Log.i("@OfferToTutor", "Offer to tutor failed!");
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

        intent = getIntent();

        course = intent.getStringExtra("course");


        JSONObject obj2 = new JSONObject();
        try{
            obj2.put("userEmail", _userEmail);
            obj2.put("userToken", _userToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ConnectionTask check_session_status = new ConnectionTask(obj2);
        check_session_status.check_session_status_tutor(new ConnectionTask.CallBack() {
            @Override
            public void Done(JSONObject result) {
                if(result != null){
//                    Log.i("@check_session_status", "check session completed");

                    try {

                        String status = result.getString("session_status");
                        if(status.equals("available")){ // only offered but looking to pair
                            tutor_offered = true;
                            offerToTutorButton.setText("Cancel Offer");

                            checkForUpdate2();
                        } else if(status.equals("paired")){ // paired
                            checkForUpdate2();
                        } else if(status.equals("pending")){
                            Intent intent = new Intent(ImmediateTutorServiceMapsActivity.this, Studysession.class);
                            intent.putExtra("status", "2");
                            startActivity(intent);
                            finish();
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
//                    Log.i("@check_session_status", "check session status failed!"); // has not offered yet
                }
            }
        });
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
    public void updateMap(final Location location) {
        tutorLatitude = String.valueOf(location.getLatitude());
        tutorLongitude = String.valueOf(location.getLongitude());

        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

        mMap.clear(); // clears all existing markers
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 14));
        mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));


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


                    double DistanceToStudent = 0;
                    try {
                        DistanceToStudent = distance(location.getLatitude(), location.getLongitude(), Double.valueOf(result.getString("studentLatitude")), Double.valueOf(result.getString("studentLongitude")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    if(DistanceToStudent < 0.5){


                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(ImmediateTutorServiceMapsActivity.this, Studysession.class);
                                intent.putExtra("status", "0");
                                startActivity(intent);
                                finish();
                            }
                        }, 4000);
                    }

//                    Log.i("@onLocationChanged","Location updated successfully");
                }
                else {
//                    Log.i("@onLocationChanged","Location update failed");
                }
            }
        });
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}