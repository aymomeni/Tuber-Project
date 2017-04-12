package cs4000.tuber;

/**
 * Created by Ali on 2/20/2017.
 */

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
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

        toolbar.setTitle(getmDataSet().get(index).getmOwnerEmail());
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

        if(getmDataSet().size() > 0){

            listElements.add("User: <Name> " + "\n" + "Location Description: <Location Description>" + "\n" + "Study Topic: <Study Topic>" + "\n" + "Distance: ~" + String.format("%.1f", getmDataSet().get(index).getMdistanceToHotspot()) + " miles" + "\n" +"Student Count: " + getmDataSet().get(index).getmStudentCount() + "\n");
            mListAdapter = new ArrayAdapter<String>(getContext(), R.layout.hotspot_fragment_text_elements, listElements);

            mHotspotInformationListView = (ListView) this.mFragmentView.findViewById(R.id.hotspot_fragment_text_list_view);

            mHotspotInformationListView.setAdapter(mListAdapter);
        }

    }

    @Override
    public void onClick(View v) {
        //do what you want to do when button is clicked
        switch (v.getId()) {
            case R.id.hotspot_pager_join_button:

                ViewPager vp = (ViewPager) (v.getParent().getParent().getParent().getParent());

                Log.i("HotspotFragOnClick", "Clicked " + vp.getCurrentItem());
                Log.i("HSDataElement", getmDataSet().get(vp.getCurrentItem()).getmOwnerEmail());
                try{
                    joinStudyHostpot(getmDataSet().get(vp.getCurrentItem()).getmCourse(), getmDataSet().get(vp.getCurrentItem()).getmHotspotID());
                } catch(JSONException e){
                    e.printStackTrace();
                }

                break;
//            case R.id.textView_settings:
//                switchFragment(SettingsFragment.TAG);
//                break;
        }
    }

    /**
     * Joins a hotspot
     */
    private void joinStudyHostpot(String course, String hotspotID) throws JSONException{

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
                    mJoinButton.setText("Leave Hotspot");
                    // more needs to happen if join or leave
                    mProgressDialog.dismiss();
                } else {
                    Log.e("HotspotFragment", "Null response from server");
                    // TODO: Does null mean no Hotspots?
                }

            }
        });
    }





}