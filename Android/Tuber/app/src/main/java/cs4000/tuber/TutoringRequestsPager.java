package cs4000.tuber;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Vector;

public class TutoringRequestsPager extends AppCompatActivity {


    private String _userEmail;
    private String _userToken;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutoring_requests_pager);

        setTitle("");

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        _userEmail = sharedPreferences.getString("userEmail", "");
        _userToken = sharedPreferences.getString("userToken", "");


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
                    try {

                        String status = result.getString("session_status");
                        Log.i("@status_tutor",status);
                        if(status.equals("pending")){
                            Intent intent = new Intent(TutoringRequestsPager.this, Studysession.class);
                            intent.putExtra("course", getIntent().getStringExtra("course"));
                            intent.putExtra("status", "2");
                            startActivity(intent);
                            finish();
                        } else if(status.equals("active")){ // in an active session
                            Intent intent = new Intent(TutoringRequestsPager.this, Studysession.class);
                            intent.putExtra("course", getIntent().getStringExtra("course"));
                            intent.putExtra("status", "1");
                            startActivity(intent);
                            finish();
                        } else {
                            preparePages();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    preparePages();
                }
            }
        });
    }

    public void preparePages() {

        List<Fragment> fragments = new Vector<>();
        fragments.add(Fragment.instantiate(getBaseContext(), AvailableRequestsFragment.class.getName()));
        fragments.add(Fragment.instantiate(getBaseContext(), AcceptedRequestsFragment.class.getName()));

        RequestsPagerAdapter adapter = new RequestsPagerAdapter(getSupportFragmentManager(), fragments);
        final ViewPager pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setAdapter(adapter);


        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

            }
        };

        actionBar.addTab(actionBar.newTab().setText("Available Requests").setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setText("Accepted Requests").setTabListener(tabListener));


        pager.addOnPageChangeListener( new ViewPager.SimpleOnPageChangeListener() {
            public void onPageSelected(int position) {

                actionBar.setSelectedNavigationItem(position);
            }
        });

    }
}
