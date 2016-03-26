package com.ensoft.mob.waterbiller;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ensoft.mob.waterbiller.DB.DBConnection;
import com.ensoft.mob.waterbiller.DB.DBMeterEnumeration;
import com.ensoft.mob.waterbiller.DB.DataDB;
import com.ensoft.mob.waterbiller.Models.CoordinateModel;
import com.ensoft.mob.waterbiller.R;
import com.ensoft.mob.waterbiller.helpers.GenericHelper;
import com.loopj.android.http.Base64;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class individual_profile extends AppCompatActivity {
    private static final String TAG = "Individual_ProfileActivity" ;
    CoordinateModel coordinateModel = new CoordinateModel();
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    // if GPS is enabled
    boolean isGPSEnabled = false;
    // if Network is enabled
    boolean isNetworkEnabled = false;
    Spinner states;
    EditText lastName;
    EditText firstName;
    EditText middleName;
    EditText address;
    EditText phoneNumber;
    EditText eemail;
    String profileTypeId;
    Button btnSave;
    TextView imageHolder;
    ImageView profileImage;
    MyApp app;
    Bitmap bmp;
    private Boolean imgIsSet;

    String selected_state;
    int selected_stateId;

    private ArrayList<String> state_data_array;
    DBMeterEnumeration dbMeterEnumeration;
    DataDB dataDB = new DataDB();

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_profile);

        // TODO: 22/09/2015 Initialize the Location awareness
        initializeLocationManager();
        try {
            // Getting GPS status
            isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if(isNetworkEnabled){
                mLocationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                        mLocationListeners[1]);
            }
            else{
                Log.e("GPS", "Network is disabled");
            }

        } catch (SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if(isGPSEnabled)
            {
                Log.e("GPS","GPS is enabled");
                mLocationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                        mLocationListeners[0]);
            }
            if (!isGPSEnabled) {
                Log.e("GPS","GPS is disabled");
                // so asking user to open GPS

            }

        } catch (SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_action_back);
        actionBar.setTitle("Aqua Biller");
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#5D617A"));
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setTitle(Html.fromHtml("<font color='#ffffff' fontSize='10'>AquaBiller : Individual Profile</font>"));

        app = ((MyApp) getApplication());
        dbMeterEnumeration = new DBMeterEnumeration();
        profileTypeId = app.getProfileTypeId();
       // populateSpinnerState();// populate the state spinner

        lastName = (EditText)findViewById(R.id.lastName);
        firstName = (EditText)findViewById(R.id.firstName);
        middleName = (EditText)findViewById(R.id.middleName);
        address = (EditText)findViewById(R.id.address);
        phoneNumber = (EditText)findViewById(R.id.phoneNumber);
        eemail = (EditText)findViewById(R.id.email);
        btnSave = (Button) findViewById(R.id.mIndvidualProfile);

        imageHolder = (TextView) findViewById(R.id.imageHolder);
        profileImage = (ImageView) findViewById(R.id.imagePhoto);

        // set the profile image
        if (app.getProfileImage() == null) {
            System.out.println("No Profile Picture taken yet : ");
        }
        else
        {
            System.out.println("Yes Profile Picture taken : ");
            byte[] imageData = app.getProfileImage();
            String base64String = Base64.encodeToString(imageData, Base64.NO_WRAP);
            byte[] decoded64String = Base64.decode(base64String, Base64.NO_WRAP);

            Bitmap bmp = BitmapFactory.decodeByteArray(decoded64String, 0, decoded64String.length);
            //Bitmap bmp = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            profileImage.setImageBitmap(bmp);
            lastName.setText(app.getProfileLastName());
            firstName.setText(app.getProfileFirstName());
            middleName.setText(app.getProfileMiddleName());
            address.setText(app.getProfileAddress());
            phoneNumber.setText(app.getProfilePhone());
            eemail.setText(app.getProfileEmail());
            profileTypeId = app.getProfileTypeId();
        }

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //showOptions(); //allow user to choose the application camera to use
                // used to set all the input fields that where entered before the camera activity was called
                app.setIndividualProfileData(lastName.getText().toString(), middleName.getText().toString(), firstName.getText().toString(), address.getText().toString(), phoneNumber.getText().toString(), eemail.getText().toString());
                app.setActivity(individual_profile.this);
                Intent intent = new Intent(getApplicationContext(),CameraUseActivity.class);
                startActivity(intent);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                byte[] ImgData = app.getProfileImage();
                boolean cancel = false;
                View focusView = null;
                lastName.setError(null);
                firstName.setError(null);
                middleName.setError(null);
                address.setError(null);
                phoneNumber.setError(null);
                eemail.setError(null);
                imageHolder.setError(null);

                String _lName = lastName.getText().toString();
                String _fName = firstName.getText().toString();
                String _mName = middleName.getText().toString();
                String _address = address.getText().toString();
                String _phoneNumber = phoneNumber.getText().toString();
                String _email = eemail.getText().toString();

                String s = "SELECT service_id,email FROM client_table LIMIT 1;";
                Cursor cu = dataDB.myConnection(getApplicationContext()).selectAllFromTable(s, true);
                String s_id="";
                if(cu.getCount() > 0)
                {
                    if (cu.moveToFirst()) {
                        //currentUserDetail userD = new currentUserDetail();
                        do {
                            s_id = cu.getString(0);
                            // myemail = cu.getString(1);
                        }while (cu.moveToNext());
                    }

                }

                if(TextUtils.isEmpty(_lName))
                {
                    lastName.setError(getString(R.string.error_field_required));
                    focusView = lastName;
                    cancel=true;
                }
                if(TextUtils.isEmpty(_address))
                {
                    address.setError(getString(R.string.error_field_required));
                    focusView = address;
                    cancel=true;
                }
                if(TextUtils.isEmpty(_phoneNumber))
                {
                    phoneNumber.setError(getString(R.string.error_field_required));
                    focusView = phoneNumber;
                    cancel=true;
                }
                if (!isEmailValid(_email) && !_email.isEmpty()) {
                    eemail.setError(getString(R.string.error_invalid_email));
                    focusView = eemail;
                    cancel = true;
                }
                /*
                if (profileTypeId.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Some Parameters are Missing", Toast.LENGTH_LONG).show();
                    cancel = true;
                }*/

                if (profileImage.getDrawable() == null || ImgData == null) {
                    imageHolder.setError("Profile Image can not be empty");
                    focusView = imageHolder;
                    Toast.makeText(getApplicationContext(), "Profile Image is required", Toast.LENGTH_LONG).show();
                    cancel = true;
                }

                if(cancel){
                    // form field with an error.
                    focusView.requestFocus();
                }
                else{
                    String _latitude = String.valueOf(coordinateModel.getLatitude());
                    String _longitude = String.valueOf(coordinateModel.getLongitude());
                    // TODO: 22/09/2015 check if gps is on
                    // Check if GPS is enabled
                    String provider = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
                    if(!provider.equals(""))
                    {
                        //GPS is enabled
                        Toast.makeText(getApplicationContext(), "GPS is enabled!", Toast.LENGTH_SHORT).show();
                        if(_longitude == null || _longitude.isEmpty() || _latitude == null || _latitude.isEmpty())
                        {
                            Toast.makeText(getApplicationContext(), "Error: Location not set", Toast.LENGTH_LONG).show();
                        }
                        else{
                            ContentValues contentValue = new ContentValues();
                            contentValue.put("service_id", app.getServiceid() != null ? String.valueOf(app.getServiceid()) : s_id);
                            contentValue.put("last_name",_lName);
                            contentValue.put("first_name",_fName);
                            contentValue.put("middle_name",_mName);
                            contentValue.put("address",_address);
                            contentValue.put("phone",_phoneNumber);
                            contentValue.put("email",_email);
                            contentValue.put("longitude",_longitude);
                            contentValue.put("latitude",_latitude);
                            contentValue.put("officer_id",app.getUserid());
                            String base64String = Base64.encodeToString(ImgData, Base64.NO_WRAP);
                            contentValue.put("photo",base64String);
                            contentValue.put("building_id",app.getActiveBuilding());
                            //contentValue.put("building_no",app.getActiveBuildingNo());
                            contentValue.put("street_id",app.getActiveStreet());
                            contentValue.put("profile_type_id",profileTypeId);
                            DBConnection dbConnection = new DBConnection(getApplicationContext());
                            Cursor cursor2 = dbConnection.selectColumnsFromTableOrderBy("idprofile", "profiles", "ORDER BY idprofile DESC LIMIT 1");
                            if (cursor2.moveToFirst()) {
                                while (cursor2.isAfterLast() == false) {
                                    int idMeterReading = cursor2.getInt(0) + 1;
                                    contentValue.put("profile_id", idMeterReading);
                                    cursor2.moveToNext();
                                }
                                cursor2.close();
                            } else {
                                contentValue.put("profile_id", 1);
                            }
                            long enum_building = dataDB.myConnection(getApplicationContext()).onInsert(contentValue, "profiles");
                            if (enum_building > 0) {

                                Toast.makeText(getApplicationContext(),"Profile Created Successfully", Toast.LENGTH_LONG).show();
                                app.setIndividualProfileData(null, null, null, null, null, null);
                                app.setProfileImage(null);
                                Log.e("Inserted Profile Data", contentValue.toString());
                                individual_profile.this.finish();
                                startActivity(new Intent(getApplicationContext(), ProfileExistActivity.class));
                            }
                            else{
                                Log.e("Unable to Insert Profile Data", contentValue.toString());
                            }
                        }
                    }
                    else {
                        GenericHelper.askUserToOpenGPS(individual_profile.this);
                    }

                }

            }
        });
    }
/*
    void populateSpinnerState()
    {

        states = (Spinner)findViewById(R.id.state);
        state_data_array = new ArrayList<String>();
        state_data_array = dbMeterEnumeration.fetchAllStates(getApplicationContext());
        if(!state_data_array.isEmpty() && state_data_array.size()>0) {
            ArrayAdapter dataAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, state_data_array);
            states.setAdapter(dataAdapter);
            selected_state = states.getSelectedItem().toString();
            selected_stateId = dbMeterEnumeration.fetchStateIdByName(getApplicationContext(),selected_state);
        }
        states.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_state = states.getSelectedItem().toString();
                selected_stateId = dbMeterEnumeration.fetchStateIdByName(getApplicationContext(), selected_state);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
*/
private boolean isEmailValid(String email) {
    //TODO: Replace this with your own logic
    return email.contains("@");
}
/*
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)
        {
            Bundle extras = data.getExtras();
            // TODO: 22-Sep-15 to read the picture from custume and device camera to the same place
            bmp = (Bitmap) extras.get("data");
            int bytes = bmp.getByteCount();
            ByteBuffer buffer = ByteBuffer.allocate(bytes); //Create a new buffer
            bmp.copyPixelsToBuffer(buffer); //Move the byte data to the buffer

            byte[] bmpArray = buffer.array(); //Get the underlying array containing the data.
            app.setProfileImage(bmpArray);
            profileImage.setImageBitmap(bmp);
            imgIsSet = true;
            lastName.setText(app.getProfileLastName());
            firstName.setText(app.getProfileFirstName());
            middleName.setText(app.getProfileMiddleName());
            address.setText(app.getProfileAddress());
            phoneNumber.setText(app.getProfilePhone());
            eemail.setText(app.getProfileEmail());
        }
    }
    */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_individual_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        //GenericHelper.turnGPSOn(getApplicationContext());
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
}
