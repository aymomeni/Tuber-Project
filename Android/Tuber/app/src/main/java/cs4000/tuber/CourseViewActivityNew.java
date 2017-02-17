package cs4000.tuber;

import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class CourseViewActivityNew extends AppCompatActivity implements ActionBar.TabListener{

    // will contain the courses a user is enrolled in
    private ArrayList<RecyclerCourseObject> studentCourseDataSet;
    private ArrayList<RecyclerCourseObject> tutorCourseDataSet;
    private SharedPreferences sharedPreferences;
    private String TAG = "CourseViewActivityNew";
    ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_view_new);

        List<Fragment> fragments = new Vector<>();
        fragments.add(Fragment.instantiate(this, StudentCourseFragment.class.getName()));
        fragments.add(Fragment.instantiate(this, TutorCourseFragment.class.getName()));

        CourseViewPageAdapter studentTutorPagerAdapter = new CourseViewPageAdapter(getSupportFragmentManager(), fragments);
        pager = (ViewPager)findViewById(R.id.viewpager);
        pager.setAdapter(studentTutorPagerAdapter);


        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){

            public void onPageSelected(int position) {
                getSupportActionBar().setSelectedNavigationItem(position);
            }


        });


//        RecyclerView recList = (RecyclerView) findViewById(R.id.r_view);
//
//        LinearLayoutManager llm = new LinearLayoutManager(this);
//        llm.setOrientation(LinearLayoutManager.VERTICAL);
//
//        //GridLayoutManager gl= new GridLayoutmanager(context,6,GridLayoutManager.HORIZONTAL,reverseLayout);
//        //StaggeredGridLayoutManager sgl= new StaggeredGridLayoutManager(6, StaggeredGridLayoutManager.HORIZONTAL);
//        recList.setLayoutManager(llm);
//
//
//        studentCourseDataSet = new ArrayList<RecyclerCourseObject>();
//        tutorCourseDataSet = new ArrayList<RecyclerCourseObject>();
//
////        for (int i = 0; i < 6; i++) {
////            RecyclerCourseObject newoffer = new RecyclerCourseObject();
////            switch (i%2) {
////                case 0:
////
////                    newoffer.course = "CS 4400";
////                    newoffer.subTitle = "Computer Systems";
////                    newoffer.type = "one";
////                    break;
////                case 1:
////                    newoffer = new RecyclerCourseObject();
////                    newoffer.course = "CS 3500";
////                    newoffer.subTitle = "Software Practice 1";
////                    newoffer.type = "two"; // two
////
////                    break;
////            }
////
////
////            studentCourseDataSet.add(newoffer);
////        }
//
//        // read course information from shared preferences, parse it and add it to an array.
//        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        String studentClasses = sharedPreferences.getString("userStudentCourses", "");
//        String tutorClasses = sharedPreferences.getString("userTutorCourses", "");
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
//
//        Matcher m2 = p.matcher(tutorClasses);
//        if (!m2.matches()) {
//            Log.i(TAG, "No existing courses");
//        }
//
//        while (m2.find()) {
//            RecyclerCourseObject newOffer = new RecyclerCourseObject();
//            newOffer.setCourse(m2.group(1));
//            newOffer.setSubTitle("Subtitle");
//            newOffer.setType("one");
//
//            tutorCourseDataSet.add(newOffer);
//        }
//
//
//
//
//
////        for (int i = 0; i < 2; i++) {
////            RecyclerCourseObject newoffer = new RecyclerCourseObject();
////            switch (i%2) {
////                case 0:
////
////                    newoffer.course = "CS 4400";
////                    newoffer.subTitle = "Computer Systems";
////                    newoffer.type = "one";
////                    break;
////                case 1:
////                    newoffer = new RecyclerCourseObject();
////                    newoffer.course = "CS 3500";
////                    newoffer.subTitle = "Software Practice 1";
////                    newoffer.type = "one"; // two
////
////                    break;
////            }
////
////
////            studentCourseDataSet.add(newoffer);
////        }
//
//
//        RecyclerAdapter mAdapter = new RecyclerAdapter(tutorCourseDataSet);
//        recList.setAdapter(mAdapter);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayUseLogoEnabled(true);

        // hiding default app icon
        View mActionBarView = getLayoutInflater().inflate(R.layout.actionbar_logo, null);
        actionBar.setCustomView(mActionBarView);
        actionBar.setDisplayOptions(getSupportActionBar().DISPLAY_SHOW_CUSTOM);

        // actionbar tabs
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        getSupportActionBar().addTab(actionBar.newTab().setText("Student Classes").setTabListener(this));
        getSupportActionBar().addTab(actionBar.newTab().setText("Tutor Classes").setTabListener(this));

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primary_darker)));

        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            public void onPageSelected(int position){
                actionBar.setSelectedNavigationItem(position);
            }


        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
//        Log.i("CourseViewActivity", "inflating the menu");
        getMenuInflater().inflate(R.menu.activity_main_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {

        //Called when a tab is selected
        int nTabSelected = tab.getPosition();
        pager.setCurrentItem(tab.getPosition());
//        switch (nTabSelected) {
//            case 0:
//                setContentView(R.layout.actionbar_tab_1);
//                break;
//            case 1:
//                setContentView(R.layout.actionbar_tab_2);
//                break;
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }
}