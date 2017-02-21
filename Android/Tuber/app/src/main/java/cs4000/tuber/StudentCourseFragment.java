package cs4000.tuber;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
        final View rootView = inflater.inflate(R.layout.fragment_student_courses, container, false);

        RecyclerView recList = (RecyclerView)rootView.findViewById(R.id.recyclerViewStudent);

        LinearLayoutManager llm = new LinearLayoutManager(this.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        //GridLayoutManager gl= new GridLayoutmanager(context,6,GridLayoutManager.HORIZONTAL,reverseLayout);
        //StaggeredGridLayoutManager sgl= new StaggeredGridLayoutManager(6, StaggeredGridLayoutManager.HORIZONTAL);
        recList.setLayoutManager(llm);

        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST);
        recList.addItemDecoration(itemDecoration);


        ItemClickSupport.addTo(recList).setOnItemClickListenerStudent(
                new ItemClickSupport.OnItemClickListenerStudent() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        final int pos = position - 1;

                        if(studentCourseDataSet.size() > pos) {

                            if(v instanceof ImageView) {

                                if(v.getId() == R.id.ic_study_hotspot_tower){

                                    Log.i("StudyHotspot", studentCourseDataSet.get(pos).getCourse());
                                    Intent intent = new Intent(getContext(), HotspotActivity.class);
                                    intent.putExtra("course", studentCourseDataSet.get(pos).getCourse());
                                    startActivity(intent);

                                } else if(v.getId() == R.id.ic_tutor_glasses) {

                                    Log.i("TutorService", studentCourseDataSet.get(pos).getCourse());
                                    Intent intent = new Intent(getContext(), TutorServicesActivity.class);
                                    intent.putExtra("course", studentCourseDataSet.get(pos).getCourse());
                                    startActivity(intent);


                                } else if(v.getId() == R.id.ic_discussion) {

                                    //do something...
                                    Toast.makeText(getContext(), "listener Definition missing", Toast.LENGTH_SHORT).show();

                                } else if(v.getId() == R.id.ic_message_closed) {

                                    //do something...
                                    Toast.makeText(getContext(), "listener Definition missing", Toast.LENGTH_SHORT).show();

                                }


                            } else {
                                Intent intent = new Intent(getActivity(), ClassStudentActivity.class);
                                intent.putExtra("course", studentCourseDataSet.get(pos).getCourse());
                                startActivity(intent);
                            }

                        }
                    }
                }
        );

//        ItemClickSupport.addTo(recList).setOnItemClickListenerStudent(
//                new ItemClickSupport.OnItemClickListenerStudent() {
//                    @Override
//                    public void onItemClicked(View v) {
//                        Log.i("@TESTING","CLICKED2");
//                        Toast.makeText(getContext(), "clicked!", Toast.LENGTH_SHORT).show();
//                    }
//                }
//        );


        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.studentCourseAddFloatingButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent in = new Intent(getActivity(), InsertActivity.class);
//                startActivity(in);
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MyAlertDialogStyle);
                builder.setTitle("Add a Course");
                // I'm using fragment here so I'm using getView() to provide ViewGroup
                // but you can provide here any other instance of ViewGroup from your Fragment / Activity
                View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_course, (ViewGroup) getView(), false);
                // Set up the input
                final EditText input = (EditText) viewInflated.findViewById(R.id.inputCourseDialog);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                builder.setView(viewInflated);

                // Set up the buttons
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String courseText = input.getText().toString(); //TODO: do something useful with course name
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();

            }
        });


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
