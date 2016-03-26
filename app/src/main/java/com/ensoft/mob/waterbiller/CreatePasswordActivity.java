package com.ensoft.mob.waterbiller;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ensoft.mob.waterbiller.DB.AuthorizeUser;
import com.ensoft.mob.waterbiller.DB.DataDB;
import com.ensoft.mob.waterbiller.Devices.Devices;
import com.ensoft.mob.waterbiller.Devices.LocationMavnager;
import com.ensoft.mob.waterbiller.Models.AuthorizationHttpResponse;
import com.ensoft.mob.waterbiller.Models.CoordinateModel;
import com.ensoft.mob.waterbiller.WebServices.HttpURLConnectionHelper;
import com.ensoft.mob.waterbiller.helpers.GenericHelper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class CreatePasswordActivity extends ActionBarActivity {

    private CreatePasswordTask mAuthTask = null;
    // UI references.

    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mConfirmPasswordView;
    private View mProgressView;
    private View mAuthFormView;
    private TextView mAuthStatus;
    DataDB dataDB = new DataDB();
    private static final String TAG = "CreatePasswordActivity";

    String successMssg;
    String errorMssg;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createpassword);

        //actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setHomeAsUpIndicator(R.drawable.ic_action_back);
        actionBar.setTitle("Aqua Biller");
        actionBar.setLogo(R.drawable.logo);
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#5D617A"));
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setTitle(Html.fromHtml("<font color='#ffffff'>AquaBiller:Create/Change Password</font>"));


        // Set up the form.

        mEmailView = (EditText) findViewById(R.id.txt_email);
        mPasswordView = (EditText) findViewById(R.id.txt_password);
        mConfirmPasswordView = (EditText) findViewById(R.id.txt_confirmpassword);
        //populateAutoComplete();
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptAuth();
                    return true;
                }
                return false;
            }
        });

        Button createButton = (Button) findViewById(R.id.createPasswordButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptAuth();
            }
        });
        mAuthFormView = findViewById(R.id.authorize_form);
        mProgressView = findViewById(R.id.authorization_progress);


        /*authButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {

            }
        });*/


    }

    @Override
    public void onBackPressed()
    {
        return;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_authorization, menu); //menu_authorization
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(item.getItemId()){
            case R.id.action_about:
                //Toast.makeText(this,"The Item Clicked is: "+ id,Toast.LENGTH_SHORT).show();
                Intent in = new Intent(this, AboutActivity.class);
                this.startActivity(in);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptAuth() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mConfirmPasswordView.setError(null);

        // Store values at the time of the form submission attempt.
        String email = mEmailView.getText().toString().trim();
        String passw = mPasswordView.getText().toString().trim().toLowerCase();
        String confpassw = mConfirmPasswordView.getText().toString().trim().toLowerCase();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(passw)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }
        else if(!isConfirmPasswordMatchWithPassword(passw,confpassw))
        {
            mPasswordView.setError("Password field is required");
            focusView = mPasswordView;
            cancel = true;
        }

        // Does confirmpassword equals password.
        if (TextUtils.isEmpty(confpassw)) {
            mConfirmPasswordView.setError(getString(R.string.error_field_required));
            focusView = mConfirmPasswordView;
            cancel = true;
        } else if (!isConfirmPasswordMatchWithPassword(passw,confpassw)) {
            mConfirmPasswordView.setError("This does not match with the password");
            focusView = mConfirmPasswordView;
            cancel = true;
        }
        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new CreatePasswordTask(email,passw, confpassw);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isConfirmPasswordMatchWithPassword(String password, String confirmpassw) {
        //TODO: Replace this with your own logic
        boolean ohYeah = false;
        if(confirmpassw.equals(password))
        {
            ohYeah = true;
        }
        else{
            ohYeah = false;
        }
        return ohYeah;
    }

    private boolean isPasswordValid(String passw) {
        //TODO: Replace this with your own logic
        return passw.length() >= 7;
    }
    /**
     * Shows the progress UI and hides the authorization form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mAuthFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mAuthFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mAuthFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mAuthFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    /**
     * Represents an asynchronous task used to authorize users
     * the user.
     */
    public class CreatePasswordTask extends AsyncTask<Void, Void, Boolean> {
        private final String memail;
        private final String mpassw;
        private final String mconfpass;

        CreatePasswordTask(String email, String passw, String confpass) {
            memail = email;
            mconfpass = confpass;
            mpassw = passw;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.e("doInBackground","Trying do in Background");

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Log.e("doInBackground",e.toString() +". Trying to create user password in Background");
                return false;
            }
            mAuthStatus = (TextView)findViewById(R.id.auth_status);
            // TODO: attempt creating password for user
            ContentValues contentValue = new ContentValues();
            contentValue.put("pwd", mpassw);

            long updatedId = dataDB.myConnection(getApplicationContext()).onUpdateOrIgnore(contentValue,"users","email",memail.toString());
            if(updatedId>0){return true;}
            return false;

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                //finish();
                // TODO: 21/09/2015 Create password went successful. Show success dialog
                AlertDialog.Builder adb = new AlertDialog.Builder(
                        CreatePasswordActivity.this);
                adb.setTitle("Info!");
                adb.setIcon(R.drawable.logo);
                adb.setMessage("Good..Your password has been created successfully. Please do logon to continue using AquaBiller");
                adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    }
                });
                adb.show();
            } else {
                Log.i("CreatePasswordLog", "Failed Creating Password");

                mPasswordView.setError(errorMssg);
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }

    }

}
