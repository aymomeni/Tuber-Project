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
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by Ali on 2/17/2017.
 */

public class StudentCourseFragment extends Fragment {

    // will contain the courses a user is enrolled in
    private ArrayList<RecyclerCourseObject> studentCourseDataSet = new ArrayList<RecyclerCourseObject>();
    RecyclerView recList;
    RecyclerAdapter mAdapter;
    private SharedPreferences sharedPreferences;
    private String _userEmail;
    private String _userToken;
    private String _studentClasses;
    private String TAG = "StudentCourseFragment";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
        View rootView = inflater.inflate(R.layout.fragment_student_courses, container, false);
        if(container == null){
            return null;
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        _userEmail = sharedPreferences.getString("userEmail", "");
        _userToken = sharedPreferences.getString("userToken", "");
        _studentClasses = sharedPreferences.getString("userStudentCourses", "");

        recList = (RecyclerView)rootView.findViewById(R.id.recyclerViewStudent);
        recList.setHasFixedSize(true);
        recList.setItemAnimator(new SlideInUpAnimator());

        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST);
        recList.addItemDecoration(itemDecoration);

//        LinearLayoutManager llm = new LinearLayoutManager(this.getContext());
//        llm.setOrientation(LinearLayoutManager.VERTICAL);
//
//        //GridLayoutManager gl= new GridLayoutmanager(context,6,GridLayoutManager.HORIZONTAL,reverseLayout);
//        //StaggeredGridLayoutManager sgl= new StaggeredGridLayoutManager(6, StaggeredGridLayoutManager.HORIZONTAL);
//        recList.setLayoutManager(llm);


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

                                } else if(v.getId() == R.id.ic_red_minus) {

                                    ArrayList<String> strarr = new ArrayList<String>();
                                    strarr.add(studentCourseDataSet.get(pos).getCourse());

                                    DeleteCourses(strarr, pos);
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
                        String courseName = input.getText().toString(); //TODO: do something useful with course name
                        dialog.dismiss();

                        ArrayList<String> strarr = new ArrayList<String>();
                        strarr.add(courseName);

                        AddCourses(strarr);
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

        try {
            JSONArray array = new JSONArray(_studentClasses);


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

        // Create adapter passing in the sample user data
        mAdapter = new RecyclerAdapter(studentCourseDataSet);
        // Set layout manager to position the items
        recList.setLayoutManager(new LinearLayoutManager(getContext()));

        //mAdapter.clear();
        // Attach the adapter to the recyclerview to populate items
        recList.setAdapter(mAdapter);


        return rootView;
    }

    public void AddCourses(final ArrayList<String> courses) {

        JSONArray courses_array = new JSONArray();
        for(int i = 0; i < courses.size(); i++){
            courses_array.put(courses.get(i));
        }
        Log.i("@courses_array",courses_array.toString());
        JSONObject obj = new JSONObject();
        try {
            obj.put("userEmail", _userEmail);
            obj.put("userToken", _userToken);
            obj.put("classesToBeAdded", courses_array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ConnectionTask task = new ConnectionTask(obj);
        task.add_student_classes(new ConnectionTask.CallBack() {
            @Override
            public void Done(JSONObject result) {
                if(result != null) {

                    for(int i = 0; i < courses.size(); i++){
                        RecyclerCourseObject newOffer = new RecyclerCourseObject();
                        newOffer.setCourse(courses.get(i));
                        newOffer.setSubTitle("Subtitle");
                        newOffer.setType("one");

                        mAdapter.add(newOffer);
                    }
                    if(mAdapter.getItemCount() >= 5) {
                        recList.scrollToPosition(mAdapter.getItemCount() - 1);
                    }
                } else {
                    Toast.makeText(getContext(), "adding courses failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void DeleteCourses(final ArrayList<String> courses, final int Pos) {

        JSONArray courses_array = new JSONArray();
        for(int i = 0; i < courses.size(); i++){
            courses_array.put(courses.get(i));
        }
        Log.i("@courses_array",courses_array.toString());
        JSONObject obj = new JSONObject();
        try {
            obj.put("userEmail", _userEmail);
            obj.put("userToken", _userToken);
            obj.put("classesToBeRemoved", courses_array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ConnectionTask task = new ConnectionTask(obj);
        task.remove_student_classes(new ConnectionTask.CallBack() {
            @Override
            public void Done(JSONObject result) {
                if(result != null) {

//                    for(int i = 0; i < courses.size(); i++){
//                        RecyclerCourseObject newOffer = new RecyclerCourseObject();
//                        newOffer.setCourse(courses.get(i));
//                        newOffer.setSubTitle("Subtitle");
//                        newOffer.setType("one");
//
//                        mAdapter.remove(newOffer);
//                    }
                    mAdapter.remove(Pos);


                } else {
                    Toast.makeText(getContext(), "removing courses failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public int getLastVisiblePos() {
        LinearLayoutManager layoutManager = ((LinearLayoutManager)recList.getLayoutManager());
        int findLastCompletelyVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();
        return findLastCompletelyVisibleItemPosition;
    }
}
