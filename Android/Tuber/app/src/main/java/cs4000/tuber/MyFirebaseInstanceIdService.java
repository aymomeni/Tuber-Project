package cs4000.tuber;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by FahadTmem on 2/22/17.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {


    //public static final String TOKEN_BROADCAST = "myfirebasetokenBC";
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("myFirebaseiD", "Refreshed token: " + refreshedToken);

        // TODO: Implement this method to send any registration to your app's servers.

        getApplication().sendBroadcast(new Intent("BROADCAST_ID"));
        storeToken(refreshedToken);
    }

    private void storeToken(String token){
        SharedPrefManager.getInstance(getApplicationContext()).storeFCMToken(token);
    }
}
