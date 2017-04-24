package cs4000.tuber;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ali on 2/20/2017.
 */

public class HotspotAdapter extends MapViewPager.MultiAdapter {


    private static List<HotspotObject> mDataSet;
    private static List<CameraPosition> mCameraPostions;


    public static List<HotspotObject> getmDataSet() {
        return mDataSet;
    }

    public static void setmDataSet(List<HotspotObject> dataSet) {
        mDataSet = dataSet;
    }

    public HotspotAdapter(FragmentManager fm, List<HotspotObject> dataSet) {
        super(fm);

        this.mDataSet = dataSet;
        mCameraPostions = new ArrayList<CameraPosition>();

        for(int i = 0; i < mDataSet.size(); i++){
            CameraPosition temp = CameraPosition.fromLatLngZoom(new LatLng(mDataSet.get(i).getmLatitude(), mDataSet.get(i).getmLongitude()), 15f);
            mCameraPostions.add(temp);
        }
        // camera positions
    }

    @Override
    public int getCount() {
        return mDataSet.size();
    }

    @Override
    public Fragment getItem(int position) {
        return HotspotFragment.newInstance(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Log.i("HOTSPOTADAPTER", mDataSet.get(position).getmTopic());
        return mDataSet.get(position).getmTopic();
    }

    @Override
    public String getMarkerTitle(int page, int position) {
        return mDataSet.get(page).getmOwnerEmail();
    }

    @Override
    public List<CameraPosition> getCameraPositions(int page) {
        ArrayList<CameraPosition> cL = new ArrayList<CameraPosition>();
        cL.add(mCameraPostions.get(page));
        return cL;
    }

}