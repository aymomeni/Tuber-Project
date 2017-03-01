package cs4000.tuber;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.net.Uri;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import it.slyce.messaging.SlyceMessagingFragment;
import it.slyce.messaging.listeners.LoadMoreMessagesListener;
import it.slyce.messaging.listeners.OnOptionSelectedListener;
import it.slyce.messaging.listeners.UserSendsMessageListener;
import it.slyce.messaging.message.GeneralOptionsMessage;
import it.slyce.messaging.message.MediaMessage;
import it.slyce.messaging.message.Message;
import it.slyce.messaging.message.MessageSource;
import it.slyce.messaging.message.TextMessage;


public class MessagingActivity extends AppCompatActivity {

    private String msg_body;
    private BroadcastReceiver broadcastReceiver;

    SlyceMessagingFragment slyceMessagingFragment;
    private boolean hasLoadedMore;

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







        hasLoadedMore = false;

        slyceMessagingFragment = (SlyceMessagingFragment) getFragmentManager().findFragmentById(R.id.fragment_for_slyce_messaging);
        slyceMessagingFragment.setDefaultAvatarUrl("https://scontent-lga3-1.xx.fbcdn.net/v/t1.0-9/10989174_799389040149643_722795835011402620_n.jpg?oh=bff552835c414974cc446043ac3c70ca&oe=580717A5");
        slyceMessagingFragment.setDefaultDisplayName("Matthew Page");
        slyceMessagingFragment.setDefaultUserId("uhtnaeohnuoenhaeuonthhntouaetnheuontheuo");

        slyceMessagingFragment.setOnSendMessageListener(new UserSendsMessageListener() {
            @Override
            public void onUserSendsTextMessage(String text) {
                Log.d("inf", "******************************** " + text);
            }

            @Override
            public void onUserSendsMediaMessage(Uri imageUri) {
                Log.d("inf", "******************************** " + imageUri);
            }
        });

//        slyceMessagingFragment.setLoadMoreMessagesListener(new LoadMoreMessagesListener() {
//            @Override
//            public List<Message> loadMoreMessages() {
//                Log.d("info", "loadMoreMessages()");
//
//                if (!hasLoadedMore) {
//                    hasLoadedMore = true;
//                    ArrayList<Message> messages = new ArrayList<>();
//                    GeneralOptionsMessage generalTextMessage = new GeneralOptionsMessage();
//                    generalTextMessage.setTitle("Started group");
//                    generalTextMessage.setFinalText("Accepted");
//                    generalTextMessage.setOptions(new String[]{"Accept", "Reject"});
//                    generalTextMessage.setOnOptionSelectedListener(new OnOptionSelectedListener() {
//                        @Override
//                        public String onOptionSelected(int optionSelected) {
//                            if (optionSelected == 0) {
//                                return "Accepted";
//                            } else {
//                                return "Rejected";
//                            }
//                        }
//                    });
//                    messages.add(generalTextMessage);
//                    for (int i = 0; i < 50; i++)
//                        messages.add(getRandomMessage());
//                    messages.add(generalTextMessage);
//                    Log.d("info", "loadMoreMessages() returns");
//                    return messages;
//                } else {
//                    slyceMessagingFragment.setMoreMessagesExist(false);
//                    return new ArrayList<>();
//                }
//            }
//        });

        slyceMessagingFragment.setLoadMoreMessagesListener(new LoadMoreMessagesListener() {
            @Override
            public List<Message> loadMoreMessages() {
                ArrayList<Message> messages = new ArrayList<>();
//                for (int i = 0; i < 50; i++)
                //messages.add(getRandomMessage());
                return messages;
            }
        });

        slyceMessagingFragment.setMoreMessagesExist(false);




//        try {
//            Thread.sleep(1000 * 3);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(1);
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                TextMessage textMessage = new TextMessage();
                textMessage.setText("Another message...");
                textMessage.setAvatarUrl("https://lh3.googleusercontent.com/-Y86IN-vEObo/AAAAAAAAAAI/AAAAAAAKyAM/6bec6LqLXXA/s0-c-k-no-ns/photo.jpg");
                textMessage.setDisplayName("Gary Johnson");
                textMessage.setUserId("LP");
                textMessage.setDate(new Date().getTime());
                textMessage.setSource(MessageSource.EXTERNAL_USER);
                slyceMessagingFragment.addNewMessage(textMessage);
            }
        }, 3, 3, TimeUnit.SECONDS);
    }
}
