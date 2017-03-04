package cs4000.tuber;

/**
 * Created by Ali on 2/20/2017.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class HotspotFragment extends Fragment implements View.OnClickListener {

    private Toolbar toolbar;
    private int index;
    private Button mJoinButton;

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
        View view = inflater.inflate(R.layout.hotspot_fragment, container, false);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mJoinButton = (Button) view.findViewById(R.id.hotspot_pager_join_button);
        mJoinButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) index = args.getInt("INDEX", 0);

        ViewCompat.setElevation(getView(), 10f);
        ViewCompat.setElevation(toolbar, 4f);

        toolbar.setTitle(HotspotAdapter.getmDataSet().get(index).getmOwnerEmail());
        toolbar.inflateMenu(R.menu.hotspot_pager_toolbar_options);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    @Override
    public void onClick(View v) {
        //do what you want to do when button is clicked
        switch (v.getId()) {
            case R.id.hotspot_pager_join_button:

                ViewPager vp = (ViewPager) (v.getParent().getParent().getParent().getParent());

                Log.i("HotspotFragOnClick", "Clicked " + vp.getCurrentItem());
                break;
//            case R.id.textView_settings:
//                switchFragment(SettingsFragment.TAG);
//                break;
        }
    }

}