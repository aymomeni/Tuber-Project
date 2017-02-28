package cs4000.tuber;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Ali on 2/17/2017.
 *
 * Adapter for the student/tutor course menu fragments
 */
public class CourseViewPageAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments;

    public CourseViewPageAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    public Fragment getItem(int i) {
        return fragments.get(i);
    }

    public int getCount() {
        return fragments.size();
    }

}
