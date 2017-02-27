package cs4000.tuber;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MessagingActivity extends AppCompatActivity {

    private String msg_body;
    private BroadcastReceiver broadcastReceiver;

    Intent intent;

    @Override
    protected void onStop()
    {
        unregisterReceiver(broadcastReceiver);
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter("BROADCAST_ID"));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        intent = getIntent();
        msg_body = intent.getStringExtra("MessageBody");
        Log.i("@MessageActivity",msg_body);


        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle extras = intent.getExtras();
            }
        };
        if(SharedPrefManager.getInstance((this)).getFCMToken() != null) {
//            textView.setText(SharedPrefManager.getInstance(MainActivity.this).getToken());
//            Log.i("@MyFirebaseID",SharedPrefManager.getInstance(MainActivity.this).getToken());
            registerReceiver(broadcastReceiver, new IntentFilter("BROADCAST_ID"));
        }
    }
}
