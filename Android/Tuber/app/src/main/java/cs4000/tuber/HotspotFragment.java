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
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class HotspotFragment extends Fragment implements View.OnClickListener {

    private Toolbar toolbar;
    private int index;
    private Button mJoinButton;
    private ArrayAdapter<String> mListAdapter;
    private Adapter mStringAdapter;
    private ListView mHotspotInformationListView;
    private View mFragmentView;

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

        toolbar.setTitle(HotspotAdapter.getmDataSet().get(index).getmOwnerEmail());
        toolbar.inflateMenu(R.menu.hotspot_pager_toolbar_options);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });


        ArrayList<String> listElements = new ArrayList<String>();

        if(HotspotAdapter.getmDataSet().size() > 0){

            //listElements.add("User: " + HotspotAdapter.getmDataSet().get(index).getmOwnerEmail() + "\n" + "Student Count: " + HotspotAdapter.getmDataSet().get(index).getmStudentCount() + "\n");
            listElements.add("User: <Name> " + "\n" + "Distance: " + String.format("%.1f", HotspotAdapter.getmDataSet().get(index).getMdistanceToHotspot()) + " miles" + "\n" +"Student Count: " + HotspotAdapter.getmDataSet().get(index).getmStudentCount() + "\n");
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
                Log.i("HSDataElement", HotspotAdapter.getmDataSet().get(vp.getCurrentItem()).getmOwnerEmail());
                break;
//            case R.id.textView_settings:
//                switchFragment(SettingsFragment.TAG);
//                break;
        }
    }

}