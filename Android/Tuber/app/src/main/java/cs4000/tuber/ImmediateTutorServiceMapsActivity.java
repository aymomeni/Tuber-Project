package cs4000.tuber;

import android.content.Context;
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
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static cs4000.tuber.R.id.infoTextView;
import static cs4000.tuber.R.id.logoutButton;
import static cs4000.tuber.R.id.offerToTutorButton;


/**
 * Class that depicts the tutor and the student on google maps
 * <p>
 * There is a couple of checks that one should be aware of in onCreate:
 * <p>
 * - is tutor already offering to tutor?
 * - is tutor already paired?
 */
public class ImmediateTutorServiceMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "ImmTutorServMapsAct";
    private GoogleMap mMap;
    private LocationManager _locationManager;
    private LocationListener _locationListener;
    private Location _myLastKnownLocation;

    private String _userEmail;
    private String _userToken;
    private String _userCourse;
    private String _userLatitude;
    private String _userLongitude;
    private boolean _userIsOfferingToTutor;

    private boolean _pairingSessionActive;
    private String _studentEmail;
    private String _studentLatitude;
    private String _studentLongitude;

    private Button _offerToTutorButton;
    private Button _logoutButton;

    private TextView _infoTextView;
    private Handler handler;

    private ConnectionTask connectionTask;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_immediate_tutor_service_maps);

        Log.i(TAG, "here");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.activity_immediate_tutor_service_map);
        mapFragment.getMapAsync(this);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        handler = new Handler();

        // grab user's email and password from shared preferences as well as the class in which the tutor
        // user course should come from a message delivered by the prior activity
        _userEmail = sharedPreferences.getString("userEmail", "");
        _userToken = sharedPreferences.getString("userToken", "");
        _userCourse = "CS 4400"; // TODO: must come from the message supplied by the intent driver

        // initializing buttons and text view
        _offerToTutorButton = (Button) findViewById(offerToTutorButton);
        _logoutButton = (Button) findViewById(logoutButton);
        _infoTextView = (TextView) findViewById(infoTextView);

        // checking if there is already a tutor service offer existing
        checkStatus();

    }


    /*
     * run whenenver offer to tutor button is clicked
     */
    public void onOfferToTutorOrCancelToTutorClicked(View view) {

        // is there currently a offer to
        if(_userIsOfferingToTutor && !_pairingSessionActive) {

            if (cancelTutorServiceRequest()) {
                _userIsOfferingToTutor = false;
                Log.d(TAG, "Deleted tutor service request from Server");
                _offerToTutorButton.setText("Offer to Tutor");
            } else {
                Log.e(TAG, "Error in cancelling the tutor service request");
            }

        } else if(!_userIsOfferingToTutor && !_pairingSessionActive) {
            offerToTutor();
        } else if(_userIsOfferingToTutor && _pairingSessionActive) {
            Log.e(TAG, "Error offering to tutor and active pairing");
        }
    }

    /**
     * Cancels a request made prior by the tutor
     */
    private boolean cancelTutorServiceRequest() {

        JSONObject jObject = null;
        // if no active pairing is going on then cancel request

        if (_userIsOfferingToTutor && !_pairingSessionActive) { //TODO: FIX this

            // checking if the tutor is already actively offering to tutor
            try {
                jObject = getJsonForDeleteTutorAvailable();
            } catch (JSONException e) {
                Log.i(TAG, "Issue parsing the JSON object");
                e.printStackTrace();
            }

            connectionTask = new ConnectionTask(new ConnectionTask.CallBack() {

                @Override
                public boolean Done(JSONObject result) {
                    if (result != null) {
                        return true;
                    } else {
                        return false;
                    }
                }
            });

            connectionTask.delete_tutor_available(jObject);
        }
        return false;
    }


    /**
     * if the tutor pushes the offer to tutor button:
     * first check if there is already a active service
     * else notify server that tutor is ready to tutor
     */
    private boolean offerToTutor() {

        Log.i("OfferToTutor", "Tutor service request processing (offer to tutor method)");

        if (!_userIsOfferingToTutor && !_pairingSessionActive) {

            // run offerToTutor Server request and check for correct response
            // Change buttons accordingly
            // 1 Button should allow to cancel offer to tutor
            JSONObject jObject = null;
            // checking if the tutor is already actively offering to tutor
            try {
                jObject = getThisTutorJSONObjectForOfferToTutor();
            } catch (JSONException e) {
                Log.i(TAG, "Issue creating Tutor JSON object");
                e.printStackTrace();
            }

            connectionTask = new ConnectionTask(new ConnectionTask.CallBack() {
                @Override
                public boolean Done(JSONObject result) {
                    if (result != null) {
                        _userIsOfferingToTutor = true;
                        _offerToTutorButton.setText("Cancel");
                        Log.i(TAG, "Tutor service request active");
                        checkForUpdate();
                        return true;
                    } else {
                        Log.e(TAG, "Error offering to tutor");
                        return false;
                    }
                }
            });
            connectionTask.make_tutor_available(jObject);

        }

        return false;
    }


    /**
     * returns true if tutor is paired or is currently offering to tutor
     * false if none of them
     *
     * @return
     */
    private boolean checkStatus() {

        JSONObject jObject = null;
        // checking if the tutor is already actively offering to tutor
        try {
            jObject = getJsonForCheckPairedStatusTutorRequest();
        } catch (JSONException e) {
            Log.i("Tutor Service:", "Issue parsing the JSON object");
            e.printStackTrace();
        }

        Log.d(TAG, "Checking status");
        // if empty json - > tutor has been offering
        // if non-empty json -> tutor is being paired
        connectionTask = new ConnectionTask(new ConnectionTask.CallBack() {
            @Override
            public boolean Done(JSONObject result) {
                if (result != null) {

                    // checking if there is an active pairing
                    try{
                        if(!result.getString("studentEmail").equals("null")) {
                            Log.d(TAG, "Got to here 42");
                            _studentEmail = result.getString("studentEmail");
                            _studentLatitude = result.getString("studentLatitude");
                            _studentLongitude = result.getString("studentLongitude");
                            _pairingSessionActive = true;
                            _userIsOfferingToTutor = false;
                            _offerToTutorButton.setVisibility(View.INVISIBLE);
                            checkForUpdate();
                            return true;
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Checking JSON for student error");
                        e.printStackTrace();
                    }

                    Log.d(TAG, "Currently offering to tutor");
                    _offerToTutorButton.setText("Cancel");
                    _pairingSessionActive = false;
                    _userIsOfferingToTutor = true;
                    _offerToTutorButton.setVisibility(View.VISIBLE);
                    checkForUpdate();
                    return true;

                } else {

                    // if the return is a null object we know that there is no tutor currently
                    // offer to tutor, and no pairing
                    Log.d(TAG, "No tutor service in DB");
                    _offerToTutorButton.setText("Offer to tutor");
                    _pairingSessionActive = false;
                    _userIsOfferingToTutor = false;
                    _offerToTutorButton.setVisibility(View.VISIBLE);
                    return false;
                }
            }
        });
        connectionTask.check_paired_status(jObject);

        return false;
    }


    /**
     * Checks for pairing and for location changes once pairing happened
     */
    private void checkForUpdate() {

        // in a loop should check if paired yet
        // if yes should check if locations have changed or are close to each other
        // if changed redraw the information distance
        // if yes redraw locations

        if (!_userIsOfferingToTutor && _pairingSessionActive) {

            // grab my location (tutor)
            _myLastKnownLocation = getMyLocation();

            // grabbing student location\
            Double studentLat = 0.0;
            Double studentLong = 0.0;
            try {
                studentLat = Double.parseDouble(_studentLatitude);
                studentLong = Double.parseDouble(_studentLongitude);
            } catch (Exception e) {
                Log.d(TAG, "Error parsing Student Lat/Long");
            }

            Location studentLocation = new Location("");
            studentLocation.setLatitude(studentLat);
            studentLocation.setLongitude(studentLong);

            // calculate distance to student if close do something
            if(_myLastKnownLocation.distanceTo(studentLocation) < 0.2) {
                // student arrived
                _infoTextView.setTextColor(Color.GREEN);
                _infoTextView.setText("Your student has arrived");
                //TODO: delete the pairing
                return;
            }

            //Double distanceInMiles = .distanceInMilesTo(userLocation);
            Double distanceInMiles = 0.0;

            Double distanceOneDP = (double) Math.round(distanceInMiles * 10) / 10;

            _infoTextView.setText("Your student is " + distanceOneDP.toString() + " miles away!");

            LatLng studentLocationLatLng = new LatLng(studentLocation.getLatitude(), studentLocation.getLongitude());
            LatLng tutorLocationLatLng = new LatLng(_myLastKnownLocation.getLatitude(), _myLastKnownLocation.getLongitude());

            ArrayList<Marker> markers = new ArrayList<>();

            mMap.clear(); // clears all existing markers
            markers.add(mMap.addMarker(new MarkerOptions().position(tutorLocationLatLng).title("Student Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))));
            markers.add(mMap.addMarker(new MarkerOptions().position(tutorLocationLatLng).title("Your Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))));

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Marker marker : markers) {
                builder.include(marker.getPosition());
            }
            LatLngBounds bounds = builder.build();

            int padding = 60;
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

            mMap.animateCamera(cu);

            _offerToTutorButton.setVisibility(View.INVISIBLE);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkForUpdate();

                }
            }, 5000);


        } if(_userIsOfferingToTutor && !_pairingSessionActive) {

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkStatus();

                }
            }, 4000);
        }

        return;

    }

    public void logout(View view) {

        Log.i("Info", "Logout Tutor Immediate Service");
        ParseUser.logOut();

        Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
        startActivity(intent);

    }

    /**
     * Returns a JSON Object based on the make_tutor_available request
     *
     * @return
     */
    private JSONObject getThisTutorJSONObjectForOfferToTutor() throws JSONException {

        JSONObject jO = new JSONObject();
        jO.put("userEmail", _userEmail);
        jO.put("userToken", _userToken);
        jO.put("tutorCourse", _userCourse);

        try {
            Location myLocation = getMyLocation();
            _userLatitude = Double.toString(myLocation.getLatitude());
            _userLongitude = Double.toString(myLocation.getLongitude());
        } catch (NullPointerException e) {
            Log.i(TAG, "Error in getting tutor location");
            e.printStackTrace();
        }

        jO.put("latitude", _userLatitude);
        jO.put("longitude", _userLongitude);

        return jO;
    }


    private JSONObject getJsonForCheckPairedStatusTutorRequest() throws JSONException {

        JSONObject jO = new JSONObject();
        jO.put("userEmail", _userEmail);
        jO.put("userToken", _userToken);

        try {
            _userLatitude = Double.toString(getMyLocation().getLatitude());
            _userLongitude = Double.toString(getMyLocation().getLongitude());
        } catch (Exception e) {
            _userLatitude = "0.0";
            _userLongitude = "0.0";
            Log.i("Tutor Service Request:", "Error in loading tutor location");
            e.printStackTrace();
        }

        jO.put("latitude", _userLatitude);
        jO.put("longitude", _userLongitude);


        return jO;
    }

    private JSONObject getJsonForDeleteTutorAvailable() throws JSONException {

        JSONObject jO = new JSONObject();
        jO.put("userEmail", _userEmail);
        jO.put("userToken", _userToken);

        return jO;
    }




    /**
     * Returns the location of the user in the context of this class
     * returns null if there is no access to current location (Android system Preferences)
     * @return
     */
    private Location getMyLocation() {

        // Checking for GPS permissions
        if (Build.VERSION.SDK_INT < 23 || ContextCompat.checkSelfPermission(ImmediateTutorServiceMapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            _myLastKnownLocation = _locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


        } else {

            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            } else {

                // else we have permission
                _locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (android.location.LocationListener) _locationListener);

                // gets last knwon location else it'll update the location based on the current location
                _myLastKnownLocation = _locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        }

        return _myLastKnownLocation;
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

        _locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        _locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
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

            _locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, _locationListener);

        } else {

            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            } else {

                // else we have permission
                _locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,  _locationListener);

                // gets last knwon location else it'll update the location based on the current location
                Location lastKnownLocation = _locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (lastKnownLocation != null) {
                    updateMap(lastKnownLocation);
                }
            }
        }
    }

    /**
     * TODO: Might not be needed but leaving it here just in case
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    _locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, _locationListener);

                    Location lastKnownLocation = _locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    updateMap(lastKnownLocation);

                }
            }
        }
    }


    /**
     * Updated the map by clearing everything and adding location markers of students and tutors
     *
     * @param tutorLocation
     */
    private void updateMap(Location tutorLocation) {
        LatLng tutorLatLng = new LatLng(tutorLocation.getLatitude(), tutorLocation.getLongitude());

        mMap.clear(); // clears all existing markers
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tutorLatLng, 14));
        mMap.addMarker(new MarkerOptions().position(tutorLatLng).title("Your Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

    }

    /**
     * Updated the map by clearing everything and adding location markers of students and tutors
     *
     * @param studentLocation
     * @param tutorLocation
     */
    private void updateMap(Location studentLocation, Location tutorLocation) {
        LatLng studentLatLng = new LatLng(studentLocation.getLatitude(), studentLocation.getLongitude());
        LatLng tutorLatLng = new LatLng(studentLocation.getLatitude(), studentLocation.getLongitude());

        mMap.clear(); // clears all existing markers

        // adding student location
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(studentLatLng, 14));
        mMap.addMarker(new MarkerOptions().position(studentLatLng).title("Student's Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        // adding tutor location
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tutorLatLng, 14));
        mMap.addMarker(new MarkerOptions().position(tutorLatLng).title("Your Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

    }


}
