package cs4000.tuber;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class CourseViewActivity extends AppCompatActivity {

    private ListView coursesListViewStudent;
    private ArrayList<String> _myCoursesArrListStudent = new ArrayList<String>();
    private ArrayAdapter arrayAdapterStudent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_view);

        coursesListViewStudent = (ListView) findViewById(R.id.coursesListViewStudent);
        arrayAdapterStudent = new ArrayAdapter(this, android.R.layout.simple_list_item_1, _myCoursesArrListStudent);
        coursesListViewStudent.setAdapter(arrayAdapterStudent);

        setListView();

        coursesListViewStudent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {

                Intent intent = new Intent(getApplicationContext(), ClassStudentActivity.class);
                intent.putExtra("course", _myCoursesArrListStudent.get(i));
                startActivity(intent);

            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        //hiding default app icon
        View mActionBarView = getLayoutInflater().inflate(R.layout.actionbar_logo, null);
        getSupportActionBar().setCustomView(mActionBarView);
        getSupportActionBar().setDisplayOptions(getSupportActionBar().DISPLAY_SHOW_CUSTOM);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primary_darker)));
    }



    private void setListView() {
        _myCoursesArrListStudent.add("CS 4400");
        _myCoursesArrListStudent.add("CS 3500");
        arrayAdapterStudent.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.activity_main_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
