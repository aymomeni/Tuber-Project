package cs4000.tuber;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import com.google.android.gms.maps.SupportMapFragment;


/**
 * Created by Ali on 2/20/2017.
 */
public class HotspotActivity extends AppCompatActivity implements MapViewPager.Callback {

    private ViewPager viewPager;
    private MapViewPager mvp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotspot);

        // check shared preferences for joined boolean
        // read in the data from the server and create Hotspot Objects



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

    @Override
    public void onMapViewPagerReady() {
        mvp.getMap().setPadding(
                0,
                HotspotUtils.dp(this, 40),
                HotspotUtils.getNavigationBarWidth(this),
                viewPager.getHeight() + HotspotUtils.getNavigationBarHeight(this));
    }







}