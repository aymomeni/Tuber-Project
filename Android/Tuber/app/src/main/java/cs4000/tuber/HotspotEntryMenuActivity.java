package cs4000.tuber;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

/**
 * Created by Ali on 2/20/2017.
 */

public class HotspotEntryMenuActivity extends AppCompatActivity {

    private Switch mHotspotCreateSwitch;
    private Button mJoinHotspotButton;
    private View mViewGroup;

    private SharedPreferences sharedPreferences;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotspot_entry_menu);

        // check shared preferences for previously activated hotspots and the time for them
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //mViewGroup = (View) findViewById(android.R.id.content);;

        mJoinHotspotButton = (Button) findViewById(R.id.join_hotspot_button);
        mJoinHotspotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check the shared preference boolean first
                Log.i("StudyHotspot", getIntent().getStringExtra("course"));
                Intent intent = new Intent(HotspotEntryMenuActivity.this, HotspotActivity.class);
                intent.putExtra("course", getIntent().getStringExtra("course"));

                startActivity(intent);
            }
        });

        mHotspotCreateSwitch = (Switch) findViewById(R.id.create_hotspot_switch);
        mHotspotCreateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // create hotspot if not created before. else delete hotspot
                if(isChecked){
                    Toast.makeText(getApplicationContext()
                            , (String)"Hotspot Created",
                            Toast.LENGTH_SHORT).show();
//                    final AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext(), R.style.MyAlertDialogStyle);
//                    builder.setTitle("Hotspot Info");
//                    // I'm using fragment here so I'm using getView() to provide ViewGroup
//                    // but you can provide here any other instance of ViewGroup from your Fragment / Activity
//
//                    View viewInflated = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_create_hotspot, (ViewGroup) mViewGroup, false);
//                    // Set up the input
//                    final EditText input_topic = (EditText) viewInflated.findViewById(R.id.input_hotspot_topic);
//                    final EditText input_location_description = (EditText) viewInflated.findViewById(R.id.input_hotspot_location_description);
//                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
//                    builder.setView(viewInflated);
//
//                    // Set up the buttons
//                    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            String topic = input_topic.getText().toString(); //TODO: do something useful with course name
//                            String location_description = input_location_description.getText().toString();
//                            dialog.dismiss();
//                        }
//                    });
//                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.cancel();
//                        }
//                    });
//                    builder.show();
                } else {
                    Toast.makeText(getApplicationContext()
                            , (String)"Hotspot Deleted",
                            Toast.LENGTH_SHORT).show();
                }
                Log.i("HotspotEntryActivity", "Switch Create hotspot");
            }
        });


        //mHotspotCreateSwitch.setChecked(true);
        // check server?
        // set the buttons for Create PersonalHotspot
    }




}
