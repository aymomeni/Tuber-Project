package cs4000.tuber;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by Ali on 2/20/2017.
 */

/**
 * Entry activity for Study hotspot. Populates and retrieves hotspot data from server and
 * initiates HotspotActivity -> map view pager
 */
public class HotspotActivity extends AppCompatActivity implements MapViewPager.Callback {

    private ViewPager viewPager;
    private MapViewPager mvp;
    private String mTAG = "HotspotActivity";
    private SharedPreferences sharedPreferences;
    private ConnectionTask mConnectionTask;
    private String mUserEmail;
    private String mUserToken;
    private String mCourse;
    private List<HotspotObject> mDataSet;
    private ProgressDialog mProgressDialog;
    private Handler mHandler;
    private int mDelay = 1500; //milliseconds

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
        mHandler = new Handler();

        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // some kind of update of things?
                // ask if still part of hotspot? cancel hotspot?
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

        // Checking for GPS permissions to retrieve own location
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
                    Log.i(mTAG, "my Latitude: " + mLastKnownLocation.getLatitude()  + " Longitude: " + mLastKnownLocation.getLongitude());
                } else {
                    Log.e(mTAG, "Error retrieving own location.");
                    // TODO: should not allow further access if no location permission
                }
            }
        }

        mLastKnownLocation = getMyLocation();


        try {
            firstSetupOfHotspots();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    /*
     * returns a list of currently available HotspotObject (Students that created a hotspot)
     */
    private void Looper() throws JSONException {

        // filling JSON object
        JSONObject me = new JSONObject();
        me.put("userEmail", mUserEmail);
        me.put("userToken", mUserToken);
        me.put("course", mCourse);
        try {
            me.put("latitude", "" + mLastKnownLocation.getLatitude());
            me.put("longitude", "" + mLastKnownLocation.getLongitude());
        } catch (NullPointerException e) {
            e.printStackTrace();
            me.put("latitude", "" + 0.0);
            me.put("longitude", "" + 0.0);
        }
        mConnectionTask = new ConnectionTask(me);
        mConnectionTask.find_study_hotspots(new ConnectionTask.CallBack() {
            @Override
            public void Done(JSONObject result) {

                if(result != null) {
                    LooperHelper(result);
                } else {
                    Log.e(mTAG, "Null response from server");
                    // TODO: Does null mean no Hotspots?
                }

            }
        });

        return;
    }

    /**
     * Parses the result json array of find_hotspots
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
    private void LooperHelper(JSONObject result) {

        JSONArray jsonMainArr = null;
        ArrayList<HotspotObject> freshlyPulledDataset = new ArrayList<HotspotObject>();
        try {
            jsonMainArr = result.getJSONArray("studyHotspots");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(jsonMainArr.length() == 0) {
            // no hotspots
            //TODO: what to do when there is no hotspots?
            // create study hotspot
            return;
        }
        mDataSet = new ArrayList<HotspotObject>();
        Log.i("HS_JSON OBJECT L: ", ""+jsonMainArr.toString());
        for (int i = 0; i < jsonMainArr.length(); i++) {
            try {

                HotspotObject tempStudyHotspotObject = new HotspotObject();

                JSONObject childJSONObject = jsonMainArr.getJSONObject(i);
                tempStudyHotspotObject.setmCourse(childJSONObject.getString("course"));

                tempStudyHotspotObject.setMdistanceToHotspot(childJSONObject.getDouble("distanceToHotspot"));
                tempStudyHotspotObject.setmHotspotID(childJSONObject.getString("hotspotID"));
                tempStudyHotspotObject.setmTopic(childJSONObject.getString("topic"));
                tempStudyHotspotObject.setmLocationDiscription(childJSONObject.getString("locationDescription"));
                tempStudyHotspotObject.setmLatitude(childJSONObject.getDouble("latitude"));
                tempStudyHotspotObject.setmLongitude(childJSONObject.getDouble("longitude"));
                tempStudyHotspotObject.setmOwnerEmail(childJSONObject.getString("ownerEmail"));
                tempStudyHotspotObject.setmStudentCount(childJSONObject.getString("student_count"));

                freshlyPulledDataset.add(tempStudyHotspotObject);

                Log.i("HS_JSON OBJECT RETURN: ", tempStudyHotspotObject.getmCourse() + " " +  tempStudyHotspotObject.getmTopic() + " " + tempStudyHotspotObject.getMdistanceToHotspot() + " " + tempStudyHotspotObject.getmHotspotID() + " " + tempStudyHotspotObject.getmLatitude() + " " +
                        tempStudyHotspotObject.getmLongitude() + " " + tempStudyHotspotObject.getmOwnerEmail() + " " + tempStudyHotspotObject.getmStudentCount());

            } catch(JSONException e){
                e.printStackTrace();
                Log.e(mTAG, "ERROR parsing returned hotspot JSON");
            }

        }

        if(Arrays.deepEquals(freshlyPulledDataset.toArray(),mDataSet.toArray()) == true){
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }

        return;
    }


    /**
     * Unused but written
     * @param hotspotID
     * @throws JSONException
     */
    private void getStudyHotspotMembers(int hotspotID) throws JSONException {

        // filling JSON object
        JSONObject me = new JSONObject();
        me.put("userEmail", mUserEmail);
        me.put("userToken", mUserToken);
        me.put("studyHotspotID", hotspotID);

        mConnectionTask = new ConnectionTask(me);
        mConnectionTask.get_study_hotspot_members(new ConnectionTask.CallBack() {
            @Override
            public void Done(JSONObject result) {

                if(result != null) {
                    // some method
                    mProgressDialog.dismiss();
                } else {
                    Log.e(mTAG, "Null response from server");
                    // TODO: Does null mean no Hotspots?
                }

            }
        });
    }


    /*
     * returns a list of currently available HotspotObject (Students that created a hotspot)
     */
    private void firstSetupOfHotspots() throws JSONException {

        mProgressDialog = new ProgressDialog(HotspotActivity.this,
                R.style.AppTheme_Dark_Dialog);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Looking for hotspots...");
        mProgressDialog.show();

        // filling JSON object
        JSONObject me = new JSONObject();
        me.put("userEmail", mUserEmail);
        me.put("userToken", mUserToken);
        me.put("course", mCourse);
        try {
            me.put("latitude", "" + mLastKnownLocation.getLatitude());
            me.put("longitude", "" + mLastKnownLocation.getLongitude());
        } catch (NullPointerException e) {
            e.printStackTrace();
            me.put("latitude", "" + 0.0);
            me.put("longitude", "" + 0.0);
        }
        mConnectionTask = new ConnectionTask(me);
        mConnectionTask.find_study_hotspots(new ConnectionTask.CallBack() {
            @Override
            public void Done(JSONObject result) {

                if(result != null) {
                    parseJSONFindHotspotsReturnList(result);
                    mProgressDialog.dismiss();
                } else {
                    Log.e(mTAG, "Null response from server");
                    // TODO: Does null mean no Hotspots?
                }

            }
        });

        return;
    }




    /**
     * Parses the result json array of find_hotspots
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
    private void parseJSONFindHotspotsReturnList(JSONObject result) {

        JSONArray jsonMainArr = null;
        try {

            jsonMainArr = result.getJSONArray("studyHotspots");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(jsonMainArr.length() == 0) {
            // no hotspots
            //TODO: what to do when there is no hotspots?
            // create study hotspot
            return;
        }
        mDataSet = new ArrayList<HotspotObject>();
        Log.i("HS_JSON OBJECT L: ", ""+jsonMainArr.toString());
        for (int i = 0; i < jsonMainArr.length(); i++) {
            try {

                HotspotObject tempStudyHotspotObject = new HotspotObject();

                JSONObject childJSONObject = jsonMainArr.getJSONObject(i);
                tempStudyHotspotObject.setmCourse(childJSONObject.getString("course"));

                tempStudyHotspotObject.setMdistanceToHotspot(childJSONObject.getDouble("distanceToHotspot"));
                tempStudyHotspotObject.setmHotspotID(childJSONObject.getString("hotspotID"));
                tempStudyHotspotObject.setmTopic(childJSONObject.getString("topic"));
                tempStudyHotspotObject.setmLocationDiscription(childJSONObject.getString("locationDescription"));
                tempStudyHotspotObject.setmLatitude(childJSONObject.getDouble("latitude"));
                tempStudyHotspotObject.setmLongitude(childJSONObject.getDouble("longitude"));
                tempStudyHotspotObject.setmOwnerEmail(childJSONObject.getString("ownerEmail"));
                tempStudyHotspotObject.setmStudentCount(childJSONObject.getString("student_count"));

                mDataSet.add(tempStudyHotspotObject);

                Log.i("HS_JSON OBJECT RETURN: ", tempStudyHotspotObject.getmCourse() + " " +  tempStudyHotspotObject.getmTopic() + " " + tempStudyHotspotObject.getMdistanceToHotspot() + " " + tempStudyHotspotObject.getmHotspotID() + " " + tempStudyHotspotObject.getmLatitude() + " " +
                        tempStudyHotspotObject.getmLongitude() + " " + tempStudyHotspotObject.getmOwnerEmail() + " " + tempStudyHotspotObject.getmStudentCount());

            } catch(JSONException e){
                e.printStackTrace();
                Log.e(mTAG, "ERROR parsing returned hotspot JSON");
            }

        }


        SupportMapFragment map = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setPageMargin(HotspotUtils.dp(this, 18));
        HotspotUtils.setMargins(viewPager, 0, 0, 0, HotspotUtils.getNavigationBarHeight(this));

        mvp = new MapViewPager.Builder(this)
                .mapFragment(map)
                .viewPager(viewPager)
                .position(2)
                .adapter(new HotspotAdapter(getSupportFragmentManager(), mDataSet))
                .callback(this)
                .build();


        return;
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