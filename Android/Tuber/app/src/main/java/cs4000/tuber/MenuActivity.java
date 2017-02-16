package cs4000.tuber;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.parse.ParseAnalytics;

/*
 * Initiates the a view in which a user can view academic classes
 */
public class MenuActivity extends Activity {

    public void pic_clicked(View view){
//        Log.i("OnListner","clicked!");
        startActivity(new Intent(MenuActivity.this, ClassActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_menu);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String useremail = sharedPreferences.getString("useremail", "");
        String userpw = sharedPreferences.getString("userpassword", "");
        String usertoken = sharedPreferences.getString("usertoken", "");


        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(false);
    }


    protected void switchToMenu() {

        Intent intent = new Intent(this, ClassActivity.class);
        //EditText editText = (EditText) findViewById(R.id.edit_message);
        //String message = editText.getText().toString();
        intent.putExtra("", "");
        startActivity(intent);

    }
}
