package com.ensoft.mob.waterbiller;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ensoft.mob.waterbiller.DB.DBCheckAuthorization;
import com.ensoft.mob.waterbiller.Devices.Devices;
import com.ensoft.mob.waterbiller.Devices.LocationMavnager;
import com.ensoft.mob.waterbiller.Locations.GPSTracker;
import com.ensoft.mob.waterbiller.Models.AuthorizationHttpResponse;
import com.ensoft.mob.waterbiller.Models.CoordinateModel;
import com.ensoft.mob.waterbiller.Models.VolleyHttpResponse;
import com.ensoft.mob.waterbiller.DB.AuthorizeUser;
import com.ensoft.mob.waterbiller.WebServices.ApplicationController;
import com.ensoft.mob.waterbiller.WebServices.HttpURLConnectionHelper;
import com.ensoft.mob.waterbiller.WebServices.JSONParser;
import com.ensoft.mob.waterbiller.WebServices.VolleyHelper;
import com.ensoft.mob.waterbiller.helpers.GenericHelper;
import com.ensoft.mob.waterbiller.helpers.rResult;
import com.goebl.david.Webb;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthorizationActivity extends ActionBarActivity {

    private AuthorizeTask mAuthTask = null;
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mServiceIDView;
    private View mProgressView;
    private View mAuthFormView;
    private TextView mAuthStatus;
    Button moveBtn;
    private static final String TAG = "AuthorizationActivity";
    public static String url= null; //"http://aquabiller.taxo-igr.com/webS/request.php/users/request";
    AuthorizeUser AUTH_USER = new AuthorizeUser();
    String successMssg;
    String errorMssg;

    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    // if GPS is enabled
    boolean isGPSEnabled = false;
    // if Network is enabled
    boolean isNetworkEnabled = false;
    String email, service_code;
    String latitude = "unknown";
    String longitude = "unknown";
    CoordinateModel coordinateModel = new CoordinateModel();

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);

            Log.e("Latitude: ", String.valueOf(location.getLatitude()));
            Log.e("Longitude: ", String.valueOf(location.getLongitude()));
            coordinateModel.setLatitude(String.valueOf(location.getLatitude()));
            coordinateModel.setLongitude(String.valueOf(location.getLongitude()));
            mLastLocation.set(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.NETWORK_PROVIDER),
            new LocationListener(LocationManager.GPS_PROVIDER)
    };

    //Button authButton;
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);
        url = getString(R.string.authorization_endpoint);

        //actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_action_back);
        actionBar.setTitle("Aqua Biller");
        actionBar.setLogo(R.drawable.logo);
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#5D617A"));
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setTitle(Html.fromHtml("<font color='#ffffff'>AquaBiller:Authorization</font>"));

        initializeLocationManager();
        try {
            // Getting GPS status
            isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (isNetworkEnabled) {
                mLocationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                        mLocationListeners[1]);
            }


        } catch (SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (isGPSEnabled) {
                mLocationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                        mLocationListeners[0]);
            }
            if (!isGPSEnabled) {
                // so asking user to open GPS
                //GenericHelper.askUserToOpenGPS(getApplicationContext());
            }

        } catch (SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }

        /*toolbar1 = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar1);
        toolbar1.setLogo(R.drawable.logo);
        toolbar1.setTitle("WaterBiller");
        //toolbar1.setSubtitle("Authorization");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar1.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onBackPressed();
                NavUtils.navigateUpFromSameTask(AuthorizationActivity.this);
            }
        });*/
        //toolbar1.setNavigationIcon(R.drawable.ic_action_back);
        //toolbar1.setNavigationContentDescription("Home");

        //toolbar1.setLogoDescription("Techvibes");

        /*Toolbar actionToolBar = (Toolbar) findViewById(R.id.my_action_bar_toolbar);
        setSupportActionBar(actionToolBar);
        actionToolBar.setNavigationIcon(R.drawable.ic_action);
        actionToolBar.setNavigationContentDescription("Home");
        actionToolBar.setLogo(R.drawable.logo);
        actionToolBar.setLogoDescription("Techvibes");*/


        /*moveBtn = (Button) findViewById(R.id.moveButton);
        moveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Devices dev = new Devices();
                Log.i("DEVICEINFO","DEVICE IMEI;" + Devices.getDeviceIMEI(getApplicationContext()));
                Log.i("DEVICEINFO","DEVICE NAME;" + Devices.getDeviceName());
                Log.i("DEVICEINFO","DEVICE MODEL;" + Devices.getDeviceModel());
                Log.i("DEVICEINFO","DEVICE VERSION;" + Devices.getDeviceVersion());
                Log.i("DEVICEINFO","DEVICE LOCATION;" + Devices.getDeviceLocation(getApplicationContext()));
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });*/
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.auth_email);
        mServiceIDView = (EditText) findViewById(R.id.txt_serviceid);
        //populateAutoComplete();
        mServiceIDView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptAuth();
                    return true;
                }
                return false;
            }
        });

        Button authButton = (Button) findViewById(R.id.authorizeButton);
        authButton.setOnClickListener(new View.OnClickListener() {
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
        switch (item.getItemId()) {
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

    /*private void populateAutoComplete() {
        if (Build.VERSION.SDK_INT >= 14) {
            // Use ContactsContract.Profile (API 14+)
            getLoaderManager().initLoader(0, null, this);
        } else if (Build.VERSION.SDK_INT >= 8) {
            // Use AccountManager (API 8+)
            new SetupEmailAutoCompleteTask().execute(null, null);
        }
    }*/

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
        mServiceIDView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String serviceid = mServiceIDView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(serviceid)) {
            mServiceIDView.setError(getString(R.string.error_field_required));
            focusView = mServiceIDView;
            cancel = true;
        } else if (!isServiceIDValid(serviceid)) {
            mServiceIDView.setError("Service Code is too short");
            focusView = mServiceIDView;
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
            // perform the user authorization attempt.
            // TODO: 22/09/2015 check if gps is on
            // Check if GPS is enabled
            String provider = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            if(!provider.equals(""))
            {
                //GPS is enabled
                Toast.makeText(getApplicationContext(), "GPS is enabled!", Toast.LENGTH_SHORT).show();
                showProgress(true);
                mAuthTask = new AuthorizeTask(email, serviceid);
                mAuthTask.execute((Void) null);
            }
            else{
                GenericHelper.askUserToOpenGPS(AuthorizationActivity.this);
            }

        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isServiceIDValid(String serviceid) {
        //TODO: Replace this with your own logic
        return serviceid.length() >= 4;
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

    /*@Override
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
    }*/

    /**
     * Use an AsyncTask to fetch the user's email addresses on a background thread, and update
     * the email text field with results on the main UI thread.
     */
    /*
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
                new ArrayAdapter<String>(AuthorizationActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }
*/

    /**
     * Represents an asynchronous task used to authorize users
     * the user.
     */
    public class AuthorizeTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mServiceID;

        AuthorizeTask(String email, String serviceid) {
            mEmail = email;
            mServiceID = serviceid;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.e("doInBackground", "Trying do in Background");
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Log.e("doInBackground", e.toString() + ". Trying do in Background");
                return false;
            }
            mAuthStatus = (TextView) findViewById(R.id.auth_status);
            Log.e("GenericHelper", "Try to check internet availability");
            //Log.e("Checking Internet Service",String.valueOf(GenericHelper.isOnline(getApplicationContext())));
            if (GenericHelper.isOnline(getApplicationContext())) {
                Log.e("GenericHelper", "Checking internet availability");
                String urlParameters;

                try {
                    //JSONParser jsonParser = new JSONParser();
                    //jsonParser.testPostForSync2();
                    Log.i("API_HELPER", "Call Webservice");
                    //GPSTracker gpsTracker = new GPSTracker(getApplicationContext());
                    //String stringLatitude = String.valueOf(gpsTracker.latitude);
                    //String stringLongitude = String.valueOf(gpsTracker.longitude);

                    Log.e("locationResult", "on top of locationMavnager");
                    String stringLatitude = null;
                    String stringLongitude = null;
                    if (coordinateModel.getLatitude() == null || coordinateModel.getLongitude() == null) {
                        LocationMavnager man = new LocationMavnager();
                        String locMan = man.getMyLoc(getApplicationContext());
                        if (!locMan.equalsIgnoreCase("err")) {
                            String[] splitedCoordinate = GenericHelper.splitString(locMan, ":");
                            Log.e("My LcMan", locMan);
                            Log.e("My LocMann", " latitude:" + splitedCoordinate[0] + " longitude:" + splitedCoordinate[1]);
                            latitude = splitedCoordinate[0];
                            longitude = splitedCoordinate[1];
                        }
                    } else {
                        latitude = coordinateModel.getLatitude();
                        longitude = coordinateModel.getLongitude();
                    }
                    /*LocationMavnager locationMavnager = new LocationMavnager();
                    Log.e("locationResult","under locationMavnager");
                    String locationResult = locationMavnager.getMyLoc(getApplicationContext());
                    Log.e("locationResult","under locationMavnager.getMyLoc");
                    if(locationResult != null) {
                        Log.e("locationResult",locationResult.toString());
                        String[] splitedCoordinate = GenericHelper.splitString(locationResult, ":");
                        stringLatitude = splitedCoordinate[0] != null ? splitedCoordinate[0] : "unknown";
                        stringLongitude = splitedCoordinate[1] != null ? splitedCoordinate[1] : "unknown";

                        Log.e("STRINGLOCATION", "Latitude:" + splitedCoordinate[0] + " Longitude: " + splitedCoordinate[1]);
                    }*/


                    urlParameters = "email=" + URLEncoder.encode(mEmail.trim(), "UTF-8") +
                            "&service_code=" + URLEncoder.encode(mServiceID.toUpperCase().trim(), "UTF-8") +
                            "&device_uuid=" + URLEncoder.encode(Devices.getDeviceUUID(getApplicationContext()), "UTF-8") +
                            "&device_imei=" + URLEncoder.encode(Devices.getDeviceIMEI(getApplicationContext()), "UTF-8") +
                            "&device_name=" + URLEncoder.encode(Devices.getDeviceName(), "UTF-8") +
                            "&device_model=" + URLEncoder.encode(Devices.getDeviceModel(), "UTF-8") +
                            "&device_os=" + URLEncoder.encode(Devices.getDeviceAndroidID(getApplicationContext()), "UTF-8") +
                            "&device_longitude=" + URLEncoder.encode(longitude, "UTF-8") +
                            "&device_latitude=" + URLEncoder.encode(latitude, "UTF-8") +
                            "&device_version=" + URLEncoder.encode(Devices.getDeviceVersion(), "UTF-8");


                    AuthorizationHttpResponse resp = HttpURLConnectionHelper.executePost(getString(R.string.authorization_endpoint), urlParameters);
                    Log.i("urlParams", urlParameters);
                    Log.i("url", url);


                    if (resp != null && resp.getResponseData() != null) //suppose to be myLoginCount
                    {
                        Log.i("Api response:", "Api Response Code:::" + resp.getResponseData().toString() + " second response");
                        /*Calendar c = Calendar.getInstance();
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        String formattedDate = df.format(c.getTime());*/

                        Log.e("HttpURLConnectionHelper.executePost", "Response from server; " + resp.getResponseData());

                        if (resp.getResponseCode() == 200) {
                            //insert into the database
                            AUTH_USER.initialRequest(getApplicationContext(), mEmail, mServiceID, url);
                            successMssg = resp.getResponseData();
                            return true;
                        } else if (resp.getResponseCode() == 401) { //device not yet authorized
                            // check if the user has sent authorization already
                            int mStatusCheck = AUTH_USER.AuthorizationState(getApplicationContext(), mEmail);
                            if (mStatusCheck >= 1) {
                                AUTH_USER.updateRequest(getApplicationContext(), mEmail, false, null, null, null);
                                // Log.i("update current email", "update goes here");
                                errorMssg = resp.getResponseData();
                            } else {
                                AUTH_USER.initialRequest(getApplicationContext(), mEmail, mServiceID, url);
                            }
                            // Log.i("Api response3:", "Api Response Code:::" + resp.toString() + " 3rd response" + " RCode:" + resp.getResponseCode() + " ; ResponseMessage:" + resp.getResponseData());
                            errorMssg = resp.getResponseData();
                            return false;
                        } else if (resp.getResponseCode() == 403) {
                            errorMssg = resp.getResponseData();
                            return false;
                        } else if (resp.getResponseCode() == 201) { //the user has been authorized before
                            errorMssg = "You have been authorized already on this device";
                            return false;
                        } else if (resp.getResponseCode() == 406) {
                            errorMssg = resp.getResponseData();
                            return false;
                        } else if (resp.getResponseCode() == 400) {
                            errorMssg = resp.getResponseData();
                            return false;
                        } else if (resp.getResponseCode() == 408) {
                            errorMssg = resp.getResponseData();
                            return false;
                        } else if (resp.getResponseCode() == 500) {
                            errorMssg = resp.getResponseData();
                            return false;
                        } else{
                            /*if(resp.getResponseCode()>0)
                            {
                                errorMssg =resp.getResponseData();
                            }
                            else{*/
                            errorMssg = "Authorization Error. " + resp.getResponseData() + " Code " + String.valueOf(resp.getResponseCode());
                            //}
                            return false;
                        }
                    }

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                errorMssg = getString(R.string.no_internet);// "Check your Internet Connection";

            }
            // TODO: register the new account here.
            return false;

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
                Log.i("AuthorizationLog", successMssg + "Finished Authorization");
                Toast.makeText(getApplicationContext(), "Authentication Status: " + successMssg, Toast.LENGTH_LONG).show();

                Intent intent = new Intent(getApplicationContext(), AuthorizeSuccessActivity.class);
                intent.putExtra("authStatus", successMssg);
                startActivity(intent);
            } else {
                Log.i("AuthorizationLog", "Failed Authorization");
                //mServiceIDView.setError(getString(R.string.error_invalid_auth_detail));
                //mServiceIDView.requestFocus();
                mServiceIDView.setError(errorMssg);
                mServiceIDView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }

    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        //GenericHelper.turnGPSOn(getApplicationContext());
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
}