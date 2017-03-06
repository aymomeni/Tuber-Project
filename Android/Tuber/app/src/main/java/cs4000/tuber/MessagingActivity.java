package cs4000.tuber;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.net.Uri;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
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

    private String _userEmail;
    private String _userToken;
    private String _firstName;
    private String _lastName;


    private static String recipientEmail;
    private String recipientFirstName;
    private String recipientLastName;

    private SharedPreferences sharedPreferences;

    private String msg_body;
    private BroadcastReceiver broadcastReceiver;

    ArrayList<Message> messages = new ArrayList<>();

    SlyceMessagingFragment slyceMessagingFragment;
    private boolean hasLoadedMore;

    Intent intent;
    static boolean active = false;

    public static String getRecipientEmail(){
        return recipientEmail;
    }

//    private static Message getRandomMessage() {
//        Message message;
//        if (Math.random() < 1.1) {
//            TextMessage textMessage = new TextMessage();
//            textMessage.setText(n + ""); // +  ": " + latin[(int) (Math.random() * 10)]);
//            message = textMessage;
//        } else {
//            MediaMessage mediaMessage = new MediaMessage();
//            mediaMessage.setUrl(urls[(int)(Math.random() * 5)]);
//            message = mediaMessage;
//        }
//        message.setDate(new Date().getTime());
//        if (Math.random() > 0.5) {
//            message.setAvatarUrl("https://lh3.googleusercontent.com/-Y86IN-vEObo/AAAAAAAAAAI/AAAAAAAKyAM/6bec6LqLXXA/s0-c-k-no-ns/photo.jpg");
//            message.setUserId("LP");
//            message.setSource(MessageSource.EXTERNAL_USER);
//        } else {
//            message.setAvatarUrl("https://scontent-lga3-1.xx.fbcdn.net/v/t1.0-9/10989174_799389040149643_722795835011402620_n.jpg?oh=bff552835c414974cc446043ac3c70ca&oe=580717A5");
//            message.setUserId("MP");
//            message.setSource(MessageSource.LOCAL_USER);
//        }
//        return message;
//    }

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
        active = false;
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

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        _userEmail = sharedPreferences.getString("userEmail", null);
        _userToken = sharedPreferences.getString("userToken", null);
        _firstName = sharedPreferences.getString("userFirstName", null);;
        _lastName = sharedPreferences.getString("userLastName", null);

        if(_userEmail == null){
            Intent intent2 = new Intent(getApplicationContext(), LoginActivityNew.class);
            startActivity(intent2);
            finish();
        }


        intent = getIntent();
        recipientEmail = intent.getStringExtra("recipientEmail");
        recipientFirstName = intent.getStringExtra("recipientFirstname");
        recipientLastName = intent.getStringExtra("recipientLastname");
        Log.i("@MessageActivity",recipientEmail);

        //msg_body = intent.getStringExtra("MessageBody");
        //Log.i("@MessageActivity",msg_body);


        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle extras = intent.getExtras();
                if(recipientEmail.equals(extras.getString("recipientEmail"))){
                    Log.i("@Broadcaster Here", extras.getString("Message"));

                    TextMessage textMessage = new TextMessage();
                    textMessage.setText(extras.getString("Message"));
                    textMessage.setAvatarUrl("http://betterpropertiesauburn.com/wp-content/uploads/2015/11/ad516503a11cd5ca435acc9bb6523536-1.png");
                    textMessage.setDisplayName(recipientFirstName + " " + recipientLastName);
                    textMessage.setUserId("LP");
                    textMessage.setDate(new Date().getTime());
                    textMessage.setSource(MessageSource.EXTERNAL_USER);
                    slyceMessagingFragment.addNewMessage(textMessage);
                }
            }
        };
        if(SharedPrefManager.getInstance((this)).getFCMToken() != null) {
//            textView.setText(SharedPrefManager.getInstance(MainActivity.this).getToken());
//            Log.i("@MyFirebaseID",SharedPrefManager.getInstance(MainActivity.this).getToken());
            registerReceiver(broadcastReceiver, new IntentFilter("BROADCAST_ID"));
        }


        hasLoadedMore = false;

        slyceMessagingFragment = (SlyceMessagingFragment) getFragmentManager().findFragmentById(R.id.fragment_for_slyce_messaging);
        slyceMessagingFragment.setDefaultAvatarUrl("http://betterpropertiesauburn.com/wp-content/uploads/2015/11/ad516503a11cd5ca435acc9bb6523536-1.png");
        slyceMessagingFragment.setDefaultDisplayName(_firstName + " " + _lastName);
        slyceMessagingFragment.setDefaultUserId("uhtnaeohnuoenhaeuonthhntouaetnheuontheuo");



        slyceMessagingFragment.setOnSendMessageListener(new UserSendsMessageListener() {
            @Override
            public void onUserSendsTextMessage(String text) {
                Log.d("inf", "******************************** " + text);

                JSONObject obj = new JSONObject();
                try {
                    obj.put("userEmail", _userEmail);
                    obj.put("userToken", _userToken);
                    obj.put("recipientEmail", recipientEmail);
                    obj.put("message", text);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ConnectionTask task = new ConnectionTask(obj);
                task.send_message(new ConnectionTask.CallBack() {
                    @Override
                    public void Done(JSONObject result) {
                        if(result != null){
                            Log.i("@Message", "message sent");
                        } else {
                            Log.i("@Message", "message sent failed");
                        }
                    }
                });
            }

            @Override
            public void onUserSendsMediaMessage(Uri imageUri) {
                Log.d("inf", "******************************** " + imageUri);
            }
        });

        set_con_megs();
//        slyceMessagingFragment.setLoadMoreMessagesListener(new LoadMoreMessagesListener() {
//            @Override
//            public List<Message> loadMoreMessages() {
//                Log.d("info", "loadMoreMessages()");
//                if (!hasLoadedMore) {
//                    hasLoadedMore = true;
//                    Log.i("@Message Count", String.valueOf(messages.size()));
//                    return messages;
//                } else {
//                    slyceMessagingFragment.setMoreMessagesExist(false);
//                    return new ArrayList<>();
//                }
//            }
//        });
        //slyceMessagingFragment.setMoreMessagesExist(false);




//        try {
//            Thread.sleep(1000 * 3);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(1);
//        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
//            @Override
//            public void run() {
//                TextMessage textMessage = new TextMessage();
//                textMessage.setText("Another message...");
//                textMessage.setAvatarUrl("https://lh3.googleusercontent.com/-Y86IN-vEObo/AAAAAAAAAAI/AAAAAAAKyAM/6bec6LqLXXA/s0-c-k-no-ns/photo.jpg");
//                textMessage.setDisplayName(recipientFirstName + " " + recipientLastName);
//                textMessage.setUserId("LP");
//                textMessage.setDate(new Date().getTime());
//                textMessage.setSource(MessageSource.EXTERNAL_USER);
//                slyceMessagingFragment.addNewMessage(textMessage);
//            }
//        }, 3, 3, TimeUnit.SECONDS);
    }

    public void set_con_megs(){
        JSONObject obj = new JSONObject();
        try {
            obj.put("userEmail", _userEmail);
            obj.put("userToken", _userToken);
            obj.put("recipientEmail", recipientEmail);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ConnectionTask task = new ConnectionTask(obj);
        task.get_conversation(new ConnectionTask.CallBack() {
            @Override
            public void Done(JSONObject result) {
                if(result != null){
                    try {
                        JSONArray array = result.getJSONArray("messages");
                        Log.i("@Message array", array.toString());

                        if (array.length() > 0) {
                            for(int i = 0; i < array.length(); i++) {
                                JSONObject temp = array.getJSONObject(i);
                                String fromEmail = temp.getString("fromEmail");
                                String message = temp.getString("message");
                                String toEmail = temp.getString("toEmail");
                                String time = temp.getString("time");

                                String[] temp2 = time.split("\\/");
                                String[] temp3 = temp2[2].split(" ");
                                String[] temp4 = temp3[1].split(":");
                                int year = Integer.valueOf(temp3[0]);
                                int month = Integer.valueOf(temp2[0]);
                                int day = Integer.valueOf(temp2[1]);
                                int hrs = Integer.valueOf(temp4[0]);
                                if(temp3[2].equals("PM")){
                                    hrs += 12;
                                }
                                int min = Integer.valueOf(temp4[1]);
                                int sec = Integer.valueOf(temp4[2]);
                                Date date = new Date(year, month, day, hrs, min, sec);

                                if(fromEmail.equals(_userEmail)){

                                    TextMessage textMessage = new TextMessage();
                                    textMessage.setText(message); // +  ": " + latin[(int) (Math.random() * 10)]);
                                    textMessage.setAvatarUrl("http://betterpropertiesauburn.com/wp-content/uploads/2015/11/ad516503a11cd5ca435acc9bb6523536-1.png");
                                    textMessage.setDisplayName(_firstName + " " + _lastName);
                                    textMessage.setUserId("MP");
                                    textMessage.setDate(date.getTime());
                                    textMessage.setSource(MessageSource.LOCAL_USER);
                                    slyceMessagingFragment.addNewMessage(textMessage);

                                } else {

                                    TextMessage textMessage = new TextMessage();
                                    textMessage.setText(message);
                                    textMessage.setAvatarUrl("http://betterpropertiesauburn.com/wp-content/uploads/2015/11/ad516503a11cd5ca435acc9bb6523536-1.png");
                                    textMessage.setDisplayName(recipientFirstName + " " + recipientLastName);
                                    textMessage.setUserId("LP");
                                    textMessage.setDate(date.getTime());
                                    textMessage.setSource(MessageSource.EXTERNAL_USER);
                                    slyceMessagingFragment.addNewMessage(textMessage);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else {
                }
            }
        });
    }
}
