package cs4000.tuber;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LoginActivityNew extends AppCompatActivity {
    private static final String TAG = "LoginActivityNew";
    private static final int REQUEST_SIGNUP = 0;
    private String _userEmail = "";
    private String _userPassword = "";
    private String _userToken = "";
    private String _userStudentCourses = "";
    private String _userTutorCourses = "";
    private Boolean _lastLoginSuccess = false;

    private SharedPreferences sharedPreferences;

    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.btn_login) Button _loginButton;
    @InjectView(R.id.link_signup) TextView _signupLink;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_new);
        ButterKnife.inject(this);

        // finding out if last login was successful and if it was we enter in email and password automatically
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        _lastLoginSuccess = sharedPreferences.getBoolean("lastLoginSuccess", false);
        getSupportActionBar().hide();

        if(_lastLoginSuccess){

            _userEmail = sharedPreferences.getString("userEmail", "");
            _userPassword = sharedPreferences.getString("userPassword", "");
            _emailText.setText(_userEmail);
            _passwordText.setText(_userPassword);

        } else {
            _emailText.setText("");
            _passwordText.setText("");
        }

        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        _emailText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                _emailText.setText("");
                _passwordText.setText("");
                return false;
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), RegistrationActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivityNew.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        _userEmail = _emailText.getText().toString();
        _userPassword = _passwordText.getText().toString();


        JSONObject userLoginJSON = new JSONObject();

        try {

            userLoginJSON.put("userEmail", _userEmail);
            userLoginJSON.put("userPassword", _userPassword);

        }catch(JSONException e){
            Log.i(TAG, "JSON Exception filling the object");
            e.printStackTrace();
        }

        ConnectionTask userVerification = new ConnectionTask(userLoginJSON);
        userVerification.verify_user(new ConnectionTask.CallBack() {
            @Override
            public void Done(JSONObject result) {
                if(result != null) {
                    try {
                        // for some reason the password is messed up when it comes from the server
//			_userEmail = result.getString("userToken").toString();
//			_userPassword = result.getString("userPassword").toString();
                        _userToken = result.getString("userToken");
                        _userStudentCourses =  result.getString("userStudentCourses");
                        _userTutorCourses = result.getString("userTutorCourses");
                        _lastLoginSuccess = true;

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    _lastLoginSuccess = false;
                    Log.i(TAG, "JSON result object null");

                }
            }
        });



        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess(_lastLoginSuccess);
                        //onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess(Boolean _lastLoginSuccess) {
        _loginButton.setEnabled(true);
        if(_lastLoginSuccess == true) {

            SharedPreferences.Editor sPref = sharedPreferences.edit();
            sPref.putString("userEmail", _userEmail);
            sPref.putString("userPassword", _userPassword);
            sPref.putString("userToken", _userToken);
            sPref.putString("userStudentCourses", _userStudentCourses);
            sPref.putString("userTutorCourses", _userTutorCourses);
            sPref.putBoolean("lastLoginSuccess", _lastLoginSuccess);
            sPref.commit();

            Log.i(TAG, "userEmail: " + _userEmail);
            Log.i(TAG, "userPassword: " + _userPassword);
            Log.i(TAG, "userToken: " + _userToken);
            Log.i(TAG, "userStudentCourses: " + _userStudentCourses);
            Log.i(TAG, "userTutorCourses: " + _userTutorCourses);
            Log.i(TAG, "lastLoginSuccess: " + _lastLoginSuccess);

            Toast.makeText(getBaseContext(), "login successful", Toast.LENGTH_LONG).show();
            //Intent intent = new Intent(getApplicationContext(), CourseViewActivity.class);
            Intent intent = new Intent(getApplicationContext(), PersonsActivity.class);
            startActivityForResult(intent, REQUEST_SIGNUP);
            //finish();

        } else {
            onLoginFailed();
        }
    }

    public void onLoginFailed() {
        //Toast.makeText(getBaseContext(), "Username or Password where incorrect", Toast.LENGTH_LONG).show();
        _emailText.setError("enter a valid email address");
        _passwordText.setError("enter a valid password");
        _loginButton.setEnabled(true);
    }


    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }


}