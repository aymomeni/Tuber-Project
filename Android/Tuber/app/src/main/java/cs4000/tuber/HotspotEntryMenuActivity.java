package cs4000.tuber;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Ali on 2/20/2017.
 */

public class HotspotEntryMenuActivity extends Activity {

    public static String mMemberOfHotspotID;
    public static boolean isMemberOfHotspot = false;
    private Switch mHotspotCreateSwitch;
    private Button mJoinHotspotButton;
    private String mUserEmail;
    private String mUserHotspotStatus = "null"; // can be "null", "Member", or "Owner"
    private String mUserToken;
    private String mCourse;
    private String mTopicCreateHotspot;
    private String mLocationDescriptionCreatedHotspot;
    private String mHotspotIDCreatedHotspot;
    private boolean mFirstCheck = true;
    private boolean mCreatedHotspotBool;
    private boolean mJoinedHotspotBool;
    private ProgressDialog mProgressDialog;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private Location mLastKnownLocation;
    private ConnectionTask mConnectionTask;
    private HotspotObject mHostpotObjectCheckStatus;
    private String mTAG = "HotEMenuAct";

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotspot_entry_menu);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUserEmail = sharedPreferences.getString("userEmail", "");
        mUserToken = sharedPreferences.getString("userToken", "");
        mCourse = getIntent().getStringExtra("course"); // Careful if this returns null (could)
        mMemberOfHotspotID = "-1";

        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // maybe deal with this somehow (if someone leaves a hotspot)
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

        mHotspotCreateSwitch = (Switch) findViewById(R.id.create_hotspot_switch);

        try {

            userHotspotStatus();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        mHotspotCreateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // create hotspot if not created before. else delete hotspot
                if(isChecked && !mFirstCheck){

                    if(isMemberOfHotspot == true) {
                        new AlertDialog.Builder(HotspotEntryMenuActivity.this)
                                .setTitle("Info")
                                .setMessage("You have already joined in a hotspot. First leave that hotspot before you create one.")
                                .setPositiveButton("Acknowledged", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // continue with delete
                                        mHotspotCreateSwitch.setChecked(false);
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                        return;
                    }


                    final AlertDialog.Builder builder = new AlertDialog.Builder(HotspotEntryMenuActivity.this, R.style.MyAlertDialogStyle);
                    builder.setTitle("Before creating your Study Hotspot...");
                    // I'm using fragment here so I'm using getView() to provide ViewGroup
                    // but you can provide here any other instance of ViewGroup from your Fragment / Activity
                    View viewInflated = LayoutInflater.from(HotspotEntryMenuActivity.this).inflate(R.layout.dialog_create_hotspot, null);

                    // Set up the input
                    final EditText input_topic = (EditText) viewInflated.findViewById(R.id.input_hotspot_topic);
                    final EditText input_location_description = (EditText) viewInflated.findViewById(R.id.input_hotspot_location_description);
                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    builder.setView(viewInflated);

                    // Set up the buttons
                    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            mTopicCreateHotspot = input_topic.getText().toString(); //TODO: do something useful with course name
                            mLocationDescriptionCreatedHotspot = input_location_description.getText().toString();

                            boolean temp = checkDialogueInputTopicLocationDesc();

                            if(temp == true && mUserHotspotStatus.equals("null")) {

                                try {

                                    createStudyHotspot();
                                    mUserHotspotStatus = "owner";
                                    Toast.makeText(getApplicationContext(), (String)"You successfully created a Study Hotspot.", Toast.LENGTH_SHORT).show();

                                } catch(JSONException e) {

                                    e.printStackTrace();

                                }
                            }
                        }
                    });
                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mHotspotCreateSwitch.setChecked(false);
                            new AlertDialog.Builder(HotspotEntryMenuActivity.this)
                                    .setTitle("Info")
                                    .setMessage("Study Hotspot was not created.")
                                    .setPositiveButton("Acknowledged", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // continue with delete
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                            dialog.dismiss();
                            dialog.cancel();
                        }
                    });
                    builder.show();

                } else if(isChecked == false && !mFirstCheck){

                    try {

                        if(mUserHotspotStatus.equals("owner")) {
                            deleteHotspot();

                        }
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        mJoinHotspotButton = (Button) findViewById(R.id.join_hotspot_button);
        mJoinHotspotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check the shared preference boolean first
                Log.i(mTAG, getIntent().getStringExtra("course"));
                Intent intent = new Intent(HotspotEntryMenuActivity.this, HotspotActivity.class);
                intent.putExtra("course", getIntent().getStringExtra("course"));

                startActivity(intent);
            }
        });


    }


    private Boolean checkDialogueInputTopicLocationDesc( ){

        if(mTopicCreateHotspot.isEmpty() || mLocationDescriptionCreatedHotspot.isEmpty()) {
            mHotspotCreateSwitch.setChecked(false);
            new AlertDialog.Builder(this)
                    .setTitle("Info")
                    .setMessage("Please satisfy the Study Hotspot creation conditions when creating a Study Hotspot.")
                    .setPositiveButton("Acknowledged", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return false;
        }

        if(mTopicCreateHotspot.length() > 30 || mLocationDescriptionCreatedHotspot.length() > 30) {
            mHotspotCreateSwitch.setChecked(false);
            new AlertDialog.Builder(this)
                    .setTitle("Info")
                    .setMessage("Please satisfy the Study Hotspot creation conditions when creating a Study Hotspot.")
                    .setPositiveButton("Acknowledged", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return false;
        }

        return true;
    }

    /**
     * Returns the location of the user in the context of this class
     * returns null if there is no access to current location (Android system Preferences)
     * @return
     */
    private Location getMyLocation() {

        // Checking for GPS permissions
        if (Build.VERSION.SDK_INT < 23 || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mLastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


        } else {

            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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



    /*createstudyhotspot -> POST
     * {
     *      "userEmail" : "anne@cox.net",
     *      "userToken" : "d2bf6c7e-49f3-4659-be16-d30e4ff4a8d2",
     *      "course" : "CS 4000",
     *      "topic" : "Midterm 2",
     *      "latitude" : "40.867701",
     *      "longitude" : "111.845200",
     *      "locationDescription" : "Room 205"
     * }
     * {"userEmail":"u0820304@utah.edu",
     *      "userToken":"06d32451-b70a-4a8a-93d1-73f298a3b39f",
     *       "course":"CS 4400",
     *       "latitude":"40.7677","longitude":"-111.8453"}
     *  {"userEmail":"u0820304@utah.edu","userToken":"06d32451-b70a-4a8a-93d1-73f298a3b39f","course":"CS 4400","latitude":"40.7677","longitude":"-111.8453"}
     */
    private void createStudyHotspot() throws JSONException {
        mProgressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Creating Hotspot...");
        mProgressDialog.show();

        // filling JSON object
        JSONObject me = new JSONObject();
        me.put("userEmail", mUserEmail);
        me.put("userToken", mUserToken);
        me.put("course", mCourse);
        me.put("topic", mTopicCreateHotspot);
        me.put("latitude", mLastKnownLocation.getLatitude());
        me.put("longitude", mLastKnownLocation.getLongitude());
        me.put("locationDescription", mLocationDescriptionCreatedHotspot);

        mConnectionTask = new ConnectionTask(me);
        mConnectionTask.create_study_hotspot(new ConnectionTask.CallBack() {
            @Override
            public void Done(JSONObject result) {

                if(result != null) {
                    //mJoinButton.setText("Join Hotspot");
                    // more needs to happen if join or leave
                    try {
                        mHotspotIDCreatedHotspot = result.getString("hotspotID");
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                    mProgressDialog.dismiss();
                } else {
                    Log.e("Create Hotspot", "Null response from server");
                    // TODO: Does null mean no Hotspots?
                }

            }
        });
    }

   /* userhotspotstatus
    * {
    *    "userEmail" : "brandontobin@cox.net",
    *    "userToken" : "1ca3ed40-9f2f-49ca-85c4-42324dd3fe55"
    * }
    *
    * Returns : 200 OK
    * {
    *    "hotspot": {
    *    "course": "CS 4000",
    *            "distanceToHotspot": 0,
    *            "hotspotID": "31",
    *            "latitude": 40.867701,
    *            "longitude": 111.8452,
    *            "ownerEmail": "brandontobin@cox.net",
    *            "student_count": "1",
    *            "topic": "Midterm 2",
    *            "locationDescription" : "Room 205"
    * },
    *    "hotspotStatus": "owner"
    * }
    */
    private void userHotspotStatus() throws JSONException {
        mProgressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Refreshing your Hotspot status...");
        mProgressDialog.show();

        // filling JSON object
        JSONObject me = new JSONObject();
        me.put("userEmail", mUserEmail);
        me.put("userToken", mUserToken);

        mConnectionTask = new ConnectionTask(me);
        mConnectionTask.user_hotspot_status(new ConnectionTask.CallBack() {
            @Override
            public void Done(JSONObject result) {

                if(result != null) {

                    processHotspotStatus(result);

                    Log.i(mTAG, mUserHotspotStatus);

                    if(mUserHotspotStatus.equals("null")) {

                        mHotspotCreateSwitch.setChecked(false);
                        mFirstCheck = false;
                        mProgressDialog.dismiss();

                    } else if(mUserHotspotStatus.equals("member")) {

                        JSONObject tempRes = null;
                        try {
                            tempRes = result.getJSONObject("hotspot");
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }

                        isMemberOfHotspot = true;
                        try {
                            mMemberOfHotspotID = tempRes.getString("hotspotID");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mFirstCheck = false;
                        mProgressDialog.dismiss();

                    } else if(mUserHotspotStatus.equals("owner")){

                        mHotspotCreateSwitch.setChecked(true);
                        mFirstCheck = false;
                        mProgressDialog.dismiss();

                    }

                    mProgressDialog.dismiss();
                } else {
                    Log.e("Hotspot Status", "Null response from server");
                    // TODO: Does null mean no Hotspots?
                }

            }
        });

        return;
    }

    /**
     * Checks the status of hotspot creation and join in this context
     */
    private void processHotspotStatus(JSONObject result){

        JSONObject jsonObject = null;
        ArrayList<HotspotObject> freshlyPulledDataset = new ArrayList<HotspotObject>();

        Log.i(mTAG, result.toString());
        try {

            mUserHotspotStatus = result.getString("hotspotStatus");

            if(mUserHotspotStatus.equals("null")){
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            jsonObject = result.getJSONObject("hotspot");

                try {

                    mHostpotObjectCheckStatus = new HotspotObject();

                    mHostpotObjectCheckStatus.setmCourse(jsonObject.getString("course"));
                    mHostpotObjectCheckStatus.setMdistanceToHotspot(jsonObject.getDouble("distanceToHotspot"));
                    mHostpotObjectCheckStatus.setmHotspotID(jsonObject.getString("hotspotID"));
                    mHotspotIDCreatedHotspot = jsonObject.getString("hotspotID");
                    mHostpotObjectCheckStatus.setmLatitude(jsonObject.getDouble("latitude"));
                    mHostpotObjectCheckStatus.setmLongitude(jsonObject.getDouble("longitude"));
                    mHostpotObjectCheckStatus.setmOwnerEmail(jsonObject.getString("ownerEmail"));
                    mHostpotObjectCheckStatus.setmStudentCount(jsonObject.getString("student_count"));
                    mHostpotObjectCheckStatus.setmTopic(jsonObject.getString("topic"));
                    mHostpotObjectCheckStatus.setmLocationDiscription(jsonObject.getString("locationDescription"));



                    Log.i("HS_JSON OBJECT RETURN: ", mHostpotObjectCheckStatus.getmCourse() + " " + mHostpotObjectCheckStatus.getmTopic() + " " + mHostpotObjectCheckStatus.getMdistanceToHotspot() + " " + mHostpotObjectCheckStatus.getmHotspotID() + " " + mHostpotObjectCheckStatus.getmLatitude() + " " +
                            mHostpotObjectCheckStatus.getmLongitude() + " " + mHostpotObjectCheckStatus.getmOwnerEmail() + " " + mHostpotObjectCheckStatus.getmStudentCount() + " " + mUserHotspotStatus);

                } catch (JSONException e) {
                    mProgressDialog.dismiss();
                    e.printStackTrace();
                    Log.e("HotspotFragment", "ERROR parsing returned hotspot JSON");
                }


        } catch(JSONException e) {
            e.printStackTrace();
        }

        return;
    }


    /*deletestudyhotspot
    * {
    *     "userEmail" : "anne@cox.net",
    *     "userToken" : "d2bf6c7e-49f3-4659-be16-d30e4ff4a8d2",
    *     "hotspotID" : "8"
    * }
    *  Returns : 200 OK
    *  Nothing
    */
    private void deleteHotspot() throws JSONException {
        mProgressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Removing your hotspot instance...");
        mProgressDialog.show();

        // filling JSON object
        JSONObject me = new JSONObject();
        me.put("userEmail", mUserEmail);
        me.put("userToken", mUserToken);
        me.put("hotspotID", mHotspotIDCreatedHotspot);

        Log.i(mTAG, me.toString());
        mConnectionTask = new ConnectionTask(me);
        mConnectionTask.delete_study_hotspots(new ConnectionTask.CallBack() {
            @Override
            public void Done(JSONObject result) {

                if(result != null) {
                    mUserHotspotStatus = "null";
                    mHotspotCreateSwitch.setChecked(false);
                    Toast.makeText(getApplicationContext(), (String)"Your Study Hotspot was successfully removed.", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                } else {
                    Log.e("Delete Hotspot", "Null response");
                }

            }
        });
    }


    @Override
    protected void onResume(  ) {
        super.onResume();


    }

}
