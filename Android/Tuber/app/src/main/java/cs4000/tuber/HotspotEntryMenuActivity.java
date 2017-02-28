package cs4000.tuber;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Ali on 2/20/2017.
 */

public class HotspotEntryMenuActivity extends AppCompatActivity {




    private SharedPreferences sharedPreferences;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotspot_entry_menu);

        // check shared preferences for previously activated hotspots and the time for them
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // check server?
        // set the buttons for Create PersonalHotspot



    }


    // when join hotspot is clicked
    void join_hot_spot(View v){

        // Check the shared preference boolean first
        Log.i("StudyHotspot", getIntent().getStringExtra("course"));
        Intent intent = new Intent(HotspotEntryMenuActivity.this, HotspotActivity.class);
        intent.putExtra("course", getIntent().getStringExtra("course"));
        Toast.makeText(this, "looking for hotpsots...", Toast.LENGTH_LONG).show();
        startActivity(intent);

    }

}
