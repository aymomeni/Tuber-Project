package cs4000.tuber;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class RegistrationActivity extends AppCompatActivity {
    private static final String TAG = "RegistrationActivity";

    @InjectView(R.id.input_first_name) EditText firstNameText;
    @InjectView(R.id.input_last_name) EditText lastNameText;
    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.btn_signup) Button _signupButton;
    @InjectView(R.id.link_login) TextView _loginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        ButterKnife.inject(this);


        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivityNew.class);
                startActivity(intent);
            }
        });

    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(RegistrationActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();


        String fName = firstNameText.getText().toString();
        String lName = lastNameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: For now it's a simple create_user
        // in the future this must validate
        // email address
        // bank account information
        JSONObject newUser = new JSONObject();
        try{
            newUser.put("userEmail", email);
            newUser.put("userPassword", password);
            newUser.put("userFirstName", fName);
            newUser.put("userLastName", lName);
            newUser.put("userBillingAddress", "219 Clayton Dr.");
            newUser.put("userBillingCity", "Yorktown");
            newUser.put("userBillingState", "VA");
            newUser.put("userBillingCCNumber" ,"1234567890112233");
            newUser.put("userBillingCCExpDate", "2018-01-19");
            newUser.put("userBillingCCV", "801");


        } catch(JSONException e){

            onSignupFailed();
            e.printStackTrace();

        }

        ConnectionTask cT = new ConnectionTask(newUser);
        cT.create_user(new ConnectionTask.CallBack() {
            @Override
            public void Done(JSONObject result) {
                if(result != null){
                    Log.i("CreateUser/Success", "User successfully added to the db");

                } else {

                    onSignupFailed();
                    Log.i("CreateUser/Error", "Issue connecting to the server");
                }
            }
        });

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    public void onSignupSuccess() {
        Toast.makeText(getBaseContext(), "Registration successful", Toast.LENGTH_LONG).show();
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        Intent intent = new Intent(getApplicationContext(), LoginActivityNew.class);
        startActivity(intent);

    }


    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Registration failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }


    public boolean validate() {
        boolean valid = true;

        String fname = firstNameText.getText().toString();
        String lname = lastNameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (fname.isEmpty() || fname.length() < 3) {
            firstNameText.setError("at least 3 characters");
            valid = false;
        } else {
            firstNameText.setError(null);
        }

        if (lname.isEmpty() || lname.length() < 3) {
            lastNameText.setError("at least 3 characters");
            valid = false;
        } else {
            lastNameText.setError(null);
        }

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
