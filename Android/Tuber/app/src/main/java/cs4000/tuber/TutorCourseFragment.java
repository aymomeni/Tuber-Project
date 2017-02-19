package cs4000.tuber;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ali on 2/17/2017.
 */

public class TutorCourseFragment extends Fragment {

    // will contain the courses a user is enrolled in
    private ArrayList<RecyclerCourseObject> tutorCourseDataSet;
    private SharedPreferences sharedPreferences;
    private String TAG = "TutorCourseFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
        View rootView = inflater.inflate(R.layout.fragment_tutor_courses, container, false);
        if(container == null) {
            return null;
        }

        RecyclerView recList = (RecyclerView)rootView.findViewById(R.id.recyclerViewTutor);

        LinearLayoutManager llm = new LinearLayoutManager(this.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        //GridLayoutManager gl= new GridLayoutmanager(context,6,GridLayoutManager.HORIZONTAL,reverseLayout);
        //StaggeredGridLayoutManager sgl= new StaggeredGridLayoutManager(6, StaggeredGridLayoutManager.HORIZONTAL);
        recList.setLayoutManager(llm);

        ItemClickSupport.addTo(recList).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        position--;

                        if(tutorCourseDataSet.size() > position) {


                            Intent intent = new Intent(getActivity(), ClassTutorActivity.class);
                            intent.putExtra("course", tutorCourseDataSet.get(position).getCourse());
                            startActivity(intent);

                        }


                    }
                }
        );


        tutorCourseDataSet = new ArrayList<RecyclerCourseObject>();


        // read course information from shared preferences, parse it and add it to an array.
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        String tutorClasses = sharedPreferences.getString("userTutorCourses", "");

        Pattern p = Pattern.compile("\"(\\w+\\s*\\w+)\"");

        Matcher m2 = p.matcher(tutorClasses);
        if (!m2.matches()) {
            Log.i(TAG, "No existing courses");
        }

        while (m2.find()) {
            RecyclerCourseObject newOffer = new RecyclerCourseObject();
            newOffer.setCourse(m2.group(1));
            newOffer.setSubTitle("Subtitle");
            newOffer.setType("one");

            tutorCourseDataSet.add(newOffer);
        }


        RecyclerAdapter mAdapter = new RecyclerAdapter(tutorCourseDataSet);
        recList.setAdapter(mAdapter);

        return rootView;
    }
}
