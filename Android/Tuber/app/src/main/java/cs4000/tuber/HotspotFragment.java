package cs4000.tuber;

/**
 * Created by Ali on 2/20/2017.
 */

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static cs4000.tuber.HotspotAdapter.getmDataSet;

public class HotspotFragment extends Fragment implements View.OnClickListener {

    private Toolbar toolbar;
    private int index;
    private Button mJoinButton;
    private ArrayAdapter<String> mListAdapter;
    private Adapter mStringAdapter;
    private ListView mHotspotInformationListView;
    private View mFragmentView;
    private ProgressDialog mProgressDialog;
    private SharedPreferences sharedPreferences;
    private ConnectionTask mConnectionTask;
    private String mUserEmail;
    private String mUserToken;
    private String mCourse;
    private Handler mHandler;
    private Boolean mHotspotOwner;
    private Boolean mOwnerExistsInDataSet;
    private ViewPager viewPager;
    private MapViewPager mvp;
    private ArrayList<HotspotObject> mDataSet;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private Location mLastKnownLocation;


    public HotspotFragment() { }

    public static HotspotFragment newInstance(int i) {
        HotspotFragment f = new HotspotFragment();
        Bundle args = new Bundle();
        args.putInt("INDEX", i);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mFragmentView = inflater.inflate(R.layout.hotspot_fragment, container, false);
        toolbar = (Toolbar) mFragmentView.findViewById(R.id.toolbar);
        mJoinButton = (Button) mFragmentView.findViewById(R.id.hotspot_pager_join_button);
        mJoinButton.setOnClickListener(this);
        return mFragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) index = args.getInt("INDEX", 0);

        ViewCompat.setElevation(getView(), 10f);
        ViewCompat.setElevation(toolbar, 4f);
        mDataSet = (ArrayList<HotspotObject>) getmDataSet();

        toolbar.setTitle(getmDataSet().get(index).getmTopic());
        toolbar.inflateMenu(R.menu.hotspot_pager_toolbar_options);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        mUserEmail = sharedPreferences.getString("userEmail", "");
        mUserToken = sharedPreferences.getString("userToken", "");

        ArrayList<String> listElements = new ArrayList<String>();

        // Below loop doesn't need to be done (can be pulled from HotspotEntryActivity)
        mOwnerExistsInDataSet = false;
        for(int i = 0; i < mDataSet.size(); i++){

            if(mUserEmail.equals(mDataSet.get(i).getmOwnerEmail())){
                mOwnerExistsInDataSet = true;
            }
        }

        mHotspotOwner = false;
        if(getmDataSet().size() > 0){
            mCourse = mDataSet.get(index).getmCourse();
            listElements.add("Class: " + mDataSet.get(index).getmCourse() + "\n" + "Email: " + mDataSet.get(index).getmOwnerEmail() + "\n" + "Location Description: " + mDataSet.get(index).getmLocationDiscription() + "\n" + "Distance: ~" + String.format("%.1f", mDataSet.get(index).getMdistanceToHotspot()) + " miles" + "\n" +"Student Count: " + mDataSet.get(index).getmStudentCount() + "\n");
            mListAdapter = new ArrayAdapter<String>(getContext(), R.layout.hotspot_fragment_text_elements, listElements);

            if(HotspotEntryMenuActivity.isMemberOfHotspot && mDataSet.get(index).getmHotspotID().equals(HotspotEntryMenuActivity.mMemberOfHotspotID)){
                mJoinButton.setText("Leave Hotspot");
            }

            if(mOwnerExistsInDataSet && !mHotspotOwner) {
                mJoinButton.setClickable(false);
                mJoinButton.setText("Join Hotspot Disabled");
                mJoinButton.setBackgroundColor(getResources().getColor(R.color.aluminum));
                mJoinButton.setAlpha((float)0.4);
            }
            if(mDataSet.get(index).getmOwnerEmail().equals(mUserEmail)){
                mHotspotOwner = true;
                mJoinButton.setClickable(false);
                mJoinButton.setText("Your Hotspot");
                mJoinButton.setAlpha((float).7);
                mJoinButton.setBackgroundColor(getResources().getColor(R.color.colorAccent_Green));
            }
            mHotspotInformationListView = (ListView) this.mFragmentView.findViewById(R.id.hotspot_fragment_text_list_view);

            mHotspotInformationListView.setAdapter(mListAdapter);
        } else {
            mCourse = ""; //TODO: VERY BAD WAY TO DO THINGS
        }

        // check shared preferences for joined boolean
        // read in the data from the server and create Hotspot Objects
        // grab user's email and password from shared preferences as well as the class in which the tutor
        // user course should come from a message delivered by the prior activity
        mHandler = new Handler();

        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
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

            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            } else {

                // else we have permission
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,  mLocationListener);
                // gets last knwon location else it'll update the location based on the current location
                mLastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (mLastKnownLocation != null) {
                    Log.i("HotspotFragment", "my Latitude: " + mLastKnownLocation.getLatitude()  + " Longitude: " + mLastKnownLocation.getLongitude());
                } else {
                    Log.e("HotspotFragment", "Error retrieving own location.");
                    // TODO: should not allow further access if no location permission
                }
            }
        }

        mLastKnownLocation = getMyLocation();

    }

    @Override
    public void onClick(View v) {
        //do what you want to do when button is clicked
        switch (v.getId()) {
            case R.id.hotspot_pager_join_button:

                ViewPager vp = (ViewPager) (v.getParent().getParent().getParent().getParent());
                if(mJoinButton.getText().equals("Leave Hotspot")) {
                    try {
                        leaveStudyHotspot();
                        return;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("HotspotFragOnClick", "Clicked " + vp.getCurrentItem());
                Log.i("HSDataElement", getmDataSet().get(vp.getCurrentItem()).getmOwnerEmail());
                try{
                    if(HotspotEntryMenuActivity.isMemberOfHotspot == false) {
                        joinStudyHostpot(getmDataSet().get(vp.getCurrentItem()).getmCourse(), getmDataSet().get(vp.getCurrentItem()).getmHotspotID());
                    } else {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Info")
                                .setMessage("You can't join two Hotspots at the same time. First leave the other one.")
                                .setPositiveButton("Acknowledged", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // continue with delete
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                } catch(JSONException e){
                    e.printStackTrace();
                }

                break;
        }
    }

    /**
     * Joins a hotspot
     */
    private void joinStudyHostpot(String course, final String hotspotID) throws JSONException{

        mProgressDialog = new ProgressDialog(getContext(), R.style.AppTheme_Dark_Dialog);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Joining Hotspot...");
        mProgressDialog.show();

        // filling JSON object
        JSONObject me = new JSONObject();
        me.put("userEmail", mUserEmail);
        me.put("userToken", mUserToken);
        me.put("course", course);
        me.put("hotspotID", hotspotID);

        mConnectionTask = new ConnectionTask(me);
        mConnectionTask.join_study_hotspots(new ConnectionTask.CallBack() {
            @Override
            public void Done(JSONObject result) {

                if(result != null) {
                    mJoinButton.setText("Leave Hotspot");
                    HotspotEntryMenuActivity.mMemberOfHotspotID = hotspotID;
                    HotspotEntryMenuActivity.isMemberOfHotspot = true;
                    // more needs to happen if join or leave
                    mProgressDialog.dismiss();
                } else {
                    Log.e("HotspotFragment", "Null response from server");
                    // TODO: Does null mean no Hotspots?
                }

            }
        });
    }


    /**
     * Disconnects a user from a hotspot
     */
    private void leaveStudyHotspot() throws JSONException {
        mProgressDialog = new ProgressDialog(getContext(), R.style.AppTheme_Dark_Dialog);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Leaving Hotspot...");
        mProgressDialog.show();

        // filling JSON object
        JSONObject me = new JSONObject();
        me.put("userEmail", mUserEmail);
        me.put("userToken", mUserToken);

        mConnectionTask = new ConnectionTask(me);
        mConnectionTask.leave_study_hotspots(new ConnectionTask.CallBack() {
            @Override
            public void Done(JSONObject result) {

                if(result != null) {
                    mJoinButton.setText("Join Hotspot");
                    HotspotEntryMenuActivity.mMemberOfHotspotID = "-1";
                    HotspotEntryMenuActivity.isMemberOfHotspot = false;
                    // more needs to happen if join or leave
                    mProgressDialog.dismiss();
                } else {
                    Log.e("HotspotFragment", "Null response from server");
                    // TODO: Does null mean no Hotspots?
                }

            }
        });
    }

    /**
     * Returns the location of the user in the context of this class
     * returns null if there is no access to current location (Android system Preferences)
     * @return
     */
    private Location getMyLocation() {

        // Checking for GPS permissions
        if (Build.VERSION.SDK_INT < 23 || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mLastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


        } else {

            if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

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