package cs4000.tuber;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.List;
import java.util.Vector;

public class TutoringRequestsPager extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutoring_requests_pager);

        List<Fragment> fragments = new Vector<>();
        fragments.add(Fragment.instantiate(this, AvailableRequestsFragment.class.getName()));
        fragments.add(Fragment.instantiate(this, AcceptedRequestsFragment.class.getName()));

        setTitle("");

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
