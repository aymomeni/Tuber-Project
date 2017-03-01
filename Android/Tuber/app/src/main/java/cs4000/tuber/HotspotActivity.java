package cs4000.tuber;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.SupportMapFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


/**
 * Created by Ali on 2/20/2017.
 */

public class HotspotActivity extends AppCompatActivity implements MapViewPager.Callback {

    private ViewPager viewPager;
    private MapViewPager mvp;
    private String TAG = "HotspotActivity";
    private SharedPreferences sharedPreferences;
    private ConnectionTask mConnectionTask;
    private String mUserEmail;
    private String mUserToken;
    private String mCourse;

    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private Location mLastKnownLocation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotspot);

        // check shared preferences for joined boolean
        // read in the data from the server and create Hotspot Objects
        // grab user's email and password from shared preferences as well as the class in which the tutor
        // user course should come from a message delivered by the prior activity
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUserEmail = sharedPreferences.getString("userEmail", "");
        mUserToken = sharedPreferences.getString("userToken", "");
        mCourse = getIntent().getStringExtra("course");

        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // some kind of update of things?
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

            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);

        } else {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            } else {

                // else we have permission
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,  mLocationListener);

                // gets last knwon location else it'll update the location based on the current location
                mLastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (mLastKnownLocation != null) {
                    Log.i(TAG, "my Latitude: " + mLastKnownLocation.getLatitude()  + " Longitude: " + mLastKnownLocation.getLongitude());
                }
            }
        }

        mLastKnownLocation = getMyLocation();
        try {
            getHotspotObjects();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        SupportMapFragment map = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setPageMargin(HotspotUtils.dp(this, 18));
        HotspotUtils.setMargins(viewPager, 0, 0, 0, HotspotUtils.getNavigationBarHeight(this));

        mvp = new MapViewPager.Builder(this)
                .mapFragment(map)
                .viewPager(viewPager)
                .position(2)
                .adapter(new HotspotAdapter(getSupportFragmentManager()))
                .callback(this)
                .build();
    }


    /*
     * returns a list of currently available HotspotObjects (Students that created a hotspot)
     */
    private List<HotspotObjects> getHotspotObjects() throws JSONException {


        // filling JSON object
        JSONObject me = new JSONObject();
        me.put("userEmail", mUserEmail);
        me.put("userToken", mUserToken);
        me.put("course", mCourse);
        me.put("latitude", "" + mLastKnownLocation.getLatitude());
        me.put("longitude", "" + mLastKnownLocation.getLongitude());


        mConnectionTask = new ConnectionTask(me);
        mConnectionTask.find_study_hotspots(new ConnectionTask.CallBack() {
            @Override
            public void Done(JSONObject result) {

                if(result != null) {

                    try{
                        parseJSONFindHotspotsReturnList(result);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    // TODO: Does null mean no Hotspots?
                }

            }
        });

        // userEmail
        // userToken
        // course
        // latitude
        // longitude


        return null;
    }


    /**
     * Parses the result json array of find hotspots
     *
     * Returns : 200 OK
     * {
     * "studyHotspots": [
     * {
     * "course": "CS 4000",
     * "distanceToHotspot": 0.00005229515916537725,
     * "hotspotID": "11",
     * "latitude": 40.867701,
     * "longitude": 111.8452,
     * "ownerEmail": "brandontobin@cox.net",
     * "student_count": "1"
     * },
     * ],
     * }
     * @param result
     * @return
     */
    private List<HotspotObjects> parseJSONFindHotspotsReturnList(JSONObject result) throws JSONException {

        JSONArray jsonMainArr = null;
        try {

            jsonMainArr = result.getJSONArray("studyHotspots");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < jsonMainArr.length(); i++) {

            JSONObject childJSONObject = jsonMainArr.getJSONObject(i);
            String course = childJSONObject.getString("course");
            double distanceToHotspot = childJSONObject.getDouble("distanceToHotspot");
            String hotspotID = childJSONObject.getString("hotspotID");
            double latitude = childJSONObject.getDouble("latitude");
            double longitude = childJSONObject.getDouble("longitude");
            String ownerEmail = childJSONObject.getString("ownerEmail");
            String student_count = childJSONObject.getString("student_count");

            Log.i("HS_JSON OBJECT RETURN: ", course + " " + distanceToHotspot + " " + hotspotID + " " + latitude + " " +
            longitude + " " + ownerEmail + " " + student_count);

        }


        return null;
    }






    @Override
    public void onMapViewPagerReady() {
        mvp.getMap().setPadding(
                0,
                HotspotUtils.dp(this, 40),
                HotspotUtils.getNavigationBarWidth(this),
                viewPager.getHeight() + HotspotUtils.getNavigationBarHeight(this));
    }



    /**
     * Returns the location of the user in the context of this class
     * returns null if there is no access to current location (Android system Preferences)
     * @return
     */
    private Location getMyLocation() {

        // Checking for GPS permissions
        if (Build.VERSION.SDK_INT < 23 || ContextCompat.checkSelfPermission(HotspotActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mLastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


        } else {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            } else {

                // else we have permission
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) mLocationListener);

                // gets last knwon location else it'll update the location based on the current location
                mLastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        }

        return mLastKnownLocation;
    }


}