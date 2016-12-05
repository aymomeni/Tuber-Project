package cs4000.tuber;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ViewPropertyAnimatorCompatSet;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;

import com.parse.ParseAnalytics;

public class MenuActivity2 extends Activity {

    public void pic_clicked(View view){
        Log.i("OnListner","clicked!");
        startActivity(new Intent(MenuActivity2.this, ClassActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_menu2);


        //Intent intent = getIntent();

        //switchToMenu();

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }


    protected void switchToMenu() {

        Intent intent = new Intent(this, ClassActivity.class);
        //EditText editText = (EditText) findViewById(R.id.edit_message);
        //String message = editText.getText().toString();
        intent.putExtra("", "");
        startActivity(intent);

    }
}
