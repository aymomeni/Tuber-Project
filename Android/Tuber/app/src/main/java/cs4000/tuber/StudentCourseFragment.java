package cs4000.tuber;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;


/**
 * Created by Ali on 2/17/2017.
 */

public class StudentCourseFragment extends Fragment {

    // will contain the courses a user is enrolled in
    private ArrayList<RecyclerCourseObject> studentCourseDataSet;
    private SharedPreferences sharedPreferences;
    private String TAG = "StudentCourseFragment";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){

        if(container == null){
            return null;
        }
        View rootView = inflater.inflate(R.layout.fragment_student_courses, container, false);

        RecyclerView recList = (RecyclerView)rootView.findViewById(R.id.recyclerViewStudent);

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

                        if(studentCourseDataSet.size() > position) {

                            Intent intent = new Intent(getActivity(), ClassStudentActivity.class);
                            intent.putExtra("course", studentCourseDataSet.get(position).getCourse());
                            startActivity(intent);

                        }


                    }
                }
        );


        studentCourseDataSet = new ArrayList<RecyclerCourseObject>();

//        for (int i = 0; i < 6; i++) {
//            RecyclerCourseObject newoffer = new RecyclerCourseObject();
//            switch (i%2) {
//                case 0:
//
//                    newoffer.course = "CS 4400";
//                    newoffer.subTitle = "Computer Systems";
//                    newoffer.type = "one";
//                    break;
//                case 1:
//                    newoffer = new RecyclerCourseObject();
//                    newoffer.course = "CS 3500";
//                    newoffer.subTitle = "Software Practice 1";
//                    newoffer.type = "two"; // two
//
//                    break;
//            }
//
//
//            studentCourseDataSet.add(newoffer);
//        }

        // read course information from shared preferences, parse it and add it to an array.
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());

        try {
            JSONArray array = new JSONArray(sharedPreferences.getString("userStudentCourses", ""));


            for(int i = 0; i < array.length(); i++) {

                String str = array.getString(i);

                RecyclerCourseObject newOffer = new RecyclerCourseObject();
                newOffer.setCourse(str);
                newOffer.setSubTitle("Subtitle");
                newOffer.setType("one");

                studentCourseDataSet.add(newOffer);


            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


//        String studentClasses = sharedPreferences.getString("userStudentCourses", "");
//
//        Pattern p = Pattern.compile("\"(\\w+\\s*\\w+)\"");
//        Matcher m = p.matcher(studentClasses);
//        if (!m.matches()) {
//            Log.i(TAG, "No existing courses.");
//        }
//
//        while (m.find()) {
//            RecyclerCourseObject newOffer = new RecyclerCourseObject();
//            newOffer.setCourse(m.group(1));
//            newOffer.setSubTitle("Subtitle");
//            newOffer.setType("one");
//
//            studentCourseDataSet.add(newOffer);
//        }


//        for (int i = 0; i < 2; i++) {
//            RecyclerCourseObject newoffer = new RecyclerCourseObject();
//            switch (i%2) {
//                case 0:
//
//                    newoffer.course = "CS 4400";
//                    newoffer.subTitle = "Computer Systems";
//                    newoffer.type = "one";
//                    break;
//                case 1:
//                    newoffer = new RecyclerCourseObject();
//                    newoffer.course = "CS 3500";
//                    newoffer.subTitle = "Software Practice 1";
//                    newoffer.type = "one"; // two
//
//                    break;
//            }
//
//
//            studentCourseDataSet.add(newoffer);
//        }


        RecyclerAdapter mAdapter = new RecyclerAdapter(studentCourseDataSet);
        recList.setAdapter(mAdapter);

        return rootView;
    }

}
