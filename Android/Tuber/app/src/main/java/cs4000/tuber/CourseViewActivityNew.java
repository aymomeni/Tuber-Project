package cs4000.tuber;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import java.util.ArrayList;

public class CourseViewActivityNew extends AppCompatActivity {


    ArrayList<RecyclerCourseObject> myDataset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_view_new);

        RecyclerView recList = (RecyclerView) findViewById(R.id.r_view);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        //GridLayoutManager gl= new GridLayoutmanager(context,6,GridLayoutManager.HORIZONTAL,reverseLayout);


        //StaggeredGridLayoutManager sgl= new StaggeredGridLayoutManager(6, StaggeredGridLayoutManager.HORIZONTAL);

        recList.setLayoutManager(llm);
        recList.setOverScrollMode(View.OVER_SCROLL_ALWAYS);


        myDataset = new ArrayList<RecyclerCourseObject>();

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
//            myDataset.add(newoffer);
//        }

        for (int i = 0; i < 2; i++) {
            RecyclerCourseObject newoffer = new RecyclerCourseObject();
            switch (i%2) {
                case 0:

                    newoffer.course = "CS 4400";
                    newoffer.subTitle = "Computer Systems";
                    newoffer.type = "one";
                    break;
                case 1:
                    newoffer = new RecyclerCourseObject();
                    newoffer.course = "CS 3500";
                    newoffer.subTitle = "Software Practice 1";
                    newoffer.type = "one"; // two

                    break;
            }


            myDataset.add(newoffer);
        }


        RecyclerAdapter mAdapter = new RecyclerAdapter(myDataset);
        recList.setAdapter(mAdapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        //hiding default app icon
        View mActionBarView = getLayoutInflater().inflate(R.layout.actionbar_logo, null);
        getSupportActionBar().setCustomView(mActionBarView);
        getSupportActionBar().setDisplayOptions(getSupportActionBar().DISPLAY_SHOW_CUSTOM);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primary_darker)));



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        Log.i("CourseViewActivity", "inflating the menu");
        getMenuInflater().inflate(R.menu.activity_main_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }
}