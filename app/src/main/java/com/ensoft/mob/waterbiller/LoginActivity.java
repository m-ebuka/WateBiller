package com.ensoft.mob.waterbiller;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.bluetooth.BluetoothClass;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ensoft.mob.waterbiller.DB.DBUser;
import com.ensoft.mob.waterbiller.DB.DataDB;
import com.ensoft.mob.waterbiller.Devices.Devices;
import com.ensoft.mob.waterbiller.Locations.GPSTracker;
import com.ensoft.mob.waterbiller.Models.AuthorizationHttpResponse;
import com.ensoft.mob.waterbiller.Services.AuthorizationService;
import com.ensoft.mob.waterbiller.WebServices.HttpURLConnectionHelper;
import com.ensoft.mob.waterbiller.WebServices.JSONParser;
import com.ensoft.mob.waterbiller.WebServices.PushAPI;
import com.ensoft.mob.waterbiller.helpers.EncryptDecryptHelper;
import com.ensoft.mob.waterbiller.helpers.GenericHelper;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends ActionBarActivity implements LoaderCallbacks<Cursor> {
    //DBUser data = new DBUser();
    DataDB data = new DataDB();
    EncryptDecryptHelper encryptDecryptHelper = new EncryptDecryptHelper(LoginActivity.this);
    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "ebuka@me.com:1234567", "enem@do.cc:1234567"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    Button btnAuthorize;
    Button btnTestCamera;
    //Toolbar toolbar1;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this, AuthorizationService.class));
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#5D617A"));
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setTitle(Html.fromHtml("<font color='#ffffff'>Sign-in</font>"));

        /*String stringData = "Ebuka is here";
        String enc_Data = encryptDecryptHelper.encryptStringRequest(stringData);
        String decr_Data = encryptDecryptHelper.dencryptStringRequest(enc_Data);
        Log.e("Login","Normal Data;" + stringData);
        Log.e("Login","Encrypted Data;" + enc_Data);
        Log.e("Login", "Decrypted Data;" + decr_Data);*/

        //navigate to authorization page
        btnAuthorize = (Button) findViewById(R.id.authorization_button);
        //btnTestCamera = (Button) findViewById(R.id.testing_button2);
        btnAuthorize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent myintent = new Intent(getApplicationContext(),LoginActivity.class);
                //startActivity(myintent);

                startActivity(new Intent(getApplicationContext(), AuthorizationActivity.class));

            }
        });
        /*btnTestCamera.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
                {
                    startActivity(new Intent(getApplicationContext(), PrintMeActivity.class)); //TestPrinterActivity
                }
        });*/
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {

                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);


    }

    @Override
    public void onBackPressed(){
        // TODO: 21/09/2015 show alertbox to end the app
        final android.support.v7.app.AlertDialog.Builder adb = new android.support.v7.app.AlertDialog.Builder(
                LoginActivity.this);
        adb.setTitle("Leave Application!");
        adb.setIcon(R.drawable.logo);
        adb.setMessage("I guess you really want to quit the application");
        adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        adb.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        adb.show();

    }

    private void populateAutoComplete() {
        if (VERSION.SDK_INT >= 14) {
            // Use ContactsContract.Profile (API 14+)
            getLoaderManager().initLoader(0, null, this);
        } else if (VERSION.SDK_INT >= 8) {
            // Use AccountManager (API 8+)
            new SetupEmailAutoCompleteTask().execute(null, null);
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();



        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }
        else if(!isPasswordValid(password))
        {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
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
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 6;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Use an AsyncTask to fetch the user's email addresses on a background thread, and update
     * the email text field with results on the main UI thread.
     */
    class SetupEmailAutoCompleteTask extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... voids) {
            ArrayList<String> emailAddressCollection = new ArrayList<String>();

            // Get all emails from the user's contacts and copy them to a list.
            ContentResolver cr = getContentResolver();
            Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                    null, null, null);
            while (emailCur.moveToNext()) {
                String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract
                        .CommonDataKinds.Email.DATA));
                emailAddressCollection.add(email);
            }
            emailCur.close();

            return emailAddressCollection;
        }

        @Override
        protected void onPostExecute(List<String> emailAddressCollection) {
            addEmailsToAutoComplete(emailAddressCollection);
        }
    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }
            int myLoginCount = data.login(getApplication(), mEmail.trim(), mPassword.trim());
            Log.i("Login", Integer.toString(myLoginCount));
            if(myLoginCount == 1)
            {
                /*String loca=null;
                //lets call location provider
                GPSTracker gpsTracker = new GPSTracker(getApplicationContext());
                Log.i("LOCATION_HELPER", "Call Location Provider");
                if(gpsTracker.getIsGPSTrackingEnabled())
                {
                    Log.i("LOCATION_HELPER", "This device is GPS Enabled");
                    String stringLatitude = String.valueOf(gpsTracker.latitude);
                    Log.i("GPS Location", "My Latitude;" + stringLatitude);

                    String stringLongitude = String.valueOf(gpsTracker.longitude);
                    Log.i("GPS Location", "My Longitude;" + stringLongitude);

                    String country = gpsTracker.getCountryName(getApplicationContext());
                    Log.i("GPS Location", "My Country;" + country);

                    String city = gpsTracker.getLocality(getApplicationContext());
                    Log.i("GPS Location", "My City;" + city);

                    String postalCode = gpsTracker.getPostalCode(getApplicationContext());
                    Log.i("GPS Location", "My Postal Code;" + postalCode);

                    String addressLine = gpsTracker.getAddressLine(getApplicationContext());
                    Log.i("GPS Location", "AddressLine;" + addressLine);

                    loca = stringLatitude +":"+ stringLongitude +":" + country + ":" + city + ":" + postalCode + ":" + addressLine;
                }
                else{
                    Log.i("LOCATION_HELPER", "This device is not GPS Enabled");
                    loca = Devices.getDeviceLocation(getApplicationContext());
                    Log.i("LOCATION_HELPER", "Location:" + loca);
                }
                //lets send to webservice
                try {
                    Log.i("API_HELPER", "Call Webservice");
                    String urlParameters =
                            "username=" + URLEncoder.encode("ebukaman", "UTF-8") +
                                    "&service_id=" + URLEncoder.encode("serve123456", "UTF-8") +
                                    "&device_imei=" + URLEncoder.encode(Devices.getDeviceIMEI(getApplicationContext()),"UTF-8") +
                                    "&device_name=" + URLEncoder.encode(Devices.getDeviceName(),"UTF-8") +
                                    "&device_model=" + URLEncoder.encode(Devices.getDeviceModel(),"UTF-8") +
                                    "&device_location=" + URLEncoder.encode("","UTF-8") +
                                    "&device_location=" + URLEncoder.encode(loca,"UTF-8") +
                                    "&device_version=" + URLEncoder.encode(Devices.getDeviceVersion(),"UTF-8");
                    String url = "http://payment.titsallglobalschools.org/webapi/device";


                    Log.i("API_HELPER", "Parameters: " + urlParameters);
                    //check that device is connected to the internet
                    Boolean isConnected = GenericHelper.checkInternetConenction(getApplicationContext());
                    if(isConnected) {
                        //String resp = HttpURLConnectionHelper.executePost(url,urlParameters);
                        Log.i("API_HELPER", "Response:" + null);
                        //Toast.makeText(getApplicationContext(), "Response:" + resp, Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        //Toast.makeText(getApplicationContext(),"Device not connected to the internet", Toast.LENGTH_LONG).show();
                        Log.i("API_HELPER", "Else Response");
                    }

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }*/
                return true;
            }
            /*for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }*/

            // TODO: register the new account here.
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
                //Log.i("LoginLog", "Finished login");
                LoginActivity.this.finish();
                MyApp app;
                app = ((MyApp)getApplicationContext());
                if(Integer.parseInt(app.getUserGroup_id()) == 0)
                {
                    Log.i("Navigate user to Mobile Payment", app.getUserGroup_id());
                    startActivity(new Intent(getApplicationContext(), ChooseMobilePaymentActivity.class));
                }
                else {
                    Log.i("Navigate user to Main", app.getUserGroup_id());
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }

                //startActivity(new Intent(getApplicationContext(), StreetListActivity.class));
            } else {
                Log.i("LoginLog", "Failed login");
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
                Intent intent2 = new Intent(this, AboutActivity.class);
                this.startActivity(intent2);
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_bar) {//.action_settings

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}

