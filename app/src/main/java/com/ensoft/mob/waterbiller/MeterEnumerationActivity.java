package com.ensoft.mob.waterbiller;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.Spinner;
import android.widget.Toast;

import com.ensoft.mob.waterbiller.DB.DBChallenges;
import com.ensoft.mob.waterbiller.DB.DBConnection;
import com.ensoft.mob.waterbiller.DB.DBMeterEnumeration;
import com.ensoft.mob.waterbiller.DB.DataDB;
import com.ensoft.mob.waterbiller.Devices.Devices;
import com.ensoft.mob.waterbiller.Models.CoordinateModel;
import com.ensoft.mob.waterbiller.Models.currentUserDetail;
import com.ensoft.mob.waterbiller.helpers.GenericHelper;
import com.loopj.android.http.Base64;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MeterEnumerationActivity extends AppCompatActivity {
    private static final String TAG = "MeterEnumerationActivity" ;
    DataDB dataDB = new DataDB();
    CoordinateModel coordinateModel = new CoordinateModel();
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    // if GPS is enabled
    boolean isGPSEnabled = false;
    // if Network is enabled
    boolean isNetworkEnabled = false;

    EditText areaName;
    EditText streetName;
    EditText houseNumber;
    EditText meterNumber;
    EditText serialNumber;
    Spinner spinnerMeterType;
    //Spinner spinnerMeterStatus;
    Spinner spinnerConsumerTypes;
    Spinner spinnerMeterInstallation;
    Button mSaveMeterEnumeration;
    String serviceId = "";
    String _METERS_TABLE = "meters";
   // String selected_challengeType;
    //int selected_challengeTypeId;

    String selected_meterType;
    int selected_meterTypeId;

    String selected_meterStatus;
    int selected_meterStatusId;

    String selected_installationType;
    int selected_installationTypeId;

    String selected_consumerType;
    int selected_consumerTypeId;

   // DBChallenges dbChallenges;
    DBMeterEnumeration dbMeterEnumeration;
    MyApp app;

    private ArrayList<String> data_array;
    private ArrayList<String> status_data_array;
    private ArrayList<String> ins_data_array;
    private ArrayList<String> consumer_data_array;

    Calendar timer = Calendar.getInstance();
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");

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
        setContentView(R.layout.activity_meter_enumeration);
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
        actionBar.setTitle(Html.fromHtml("<font color='#ffffff' fontSize='10'>AquaBiller : New Meter</font>"));
        dbMeterEnumeration = new DBMeterEnumeration();
        app = ((MyApp) getApplication());

        areaName = (EditText) findViewById(R.id.mAreaName);
        streetName = (EditText) findViewById(R.id.mStreetName);
        houseNumber = (EditText) findViewById(R.id.mHouseNumber);
        meterNumber = (EditText) findViewById(R.id.mMeterNumber);
        //serialNumber = (EditText) findViewById(R.id.mSerialNumber);
        mSaveMeterEnumeration = (Button) findViewById(R.id.mSaveMeterEnumeration);

        areaName.setEnabled(false);
        streetName.setEnabled(false);
        houseNumber.setEnabled(false);

        areaName.setText(app.getActiveAreaName() != null ? app.getActiveAreaName() : "");
        streetName.setText(app.getActiveStreetName() != null ? app.getActiveStreetName() : "");
        houseNumber.setText(app.getActiveBuildingNo() != null ? app.getActiveBuildingNo() : "");


        populateSpinnerMeterType(); // populate meter type spinner
        populateSpinnerConsumerType(); //populate spinner for consumer type
        //populateSpinnerMeterStatus(); // populate meter status spinner
        populateSpinnerMeterInstallation(); // populate meter installation spinner

        mSaveMeterEnumeration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSavingMeterEnumeration(); // attempt to report challenges
            }
        });

       }

    void populateSpinnerConsumerType()
    {

        spinnerConsumerTypes = (Spinner)findViewById(R.id.mConsumerType);
        consumer_data_array = new ArrayList<String>();
        consumer_data_array = dbMeterEnumeration.fetchAllConsumerTypes(getApplicationContext());
        if(!consumer_data_array.isEmpty() && consumer_data_array.size()>0) {
            ArrayAdapter dataAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, consumer_data_array);
            spinnerConsumerTypes.setAdapter(dataAdapter);
            selected_consumerType = spinnerConsumerTypes.getSelectedItem().toString();
            selected_consumerTypeId = Integer.parseInt(dataDB.myConnection(getApplicationContext()).selectFromTable("consumer_type_id", "_consumer_types", "consumer_type", selected_consumerType));
        }
        spinnerConsumerTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_consumerType = spinnerConsumerTypes.getSelectedItem().toString();
                selected_consumerTypeId = Integer.parseInt(dataDB.myConnection(getApplicationContext()).selectFromTable("consumer_type_id", "_consumer_types", "consumer_type", selected_consumerType));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    void populateSpinnerMeterType()
    {
        spinnerMeterType = (Spinner)findViewById(R.id.mMeterType);
        data_array = new ArrayList<String>();
        data_array = dbMeterEnumeration.fetchAllAccountType(getApplicationContext());
        if(!data_array.isEmpty() && data_array.size() > 0) {
            ArrayAdapter dataAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, data_array);
            spinnerMeterType.setAdapter(dataAdapter);
            selected_meterType = spinnerMeterType.getSelectedItem().toString();
            selected_meterTypeId = dbMeterEnumeration.fetchAccountTypeIdByName(getApplicationContext(), selected_meterType);
        }
        spinnerMeterType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_meterType = spinnerMeterType.getSelectedItem().toString();

                selected_meterTypeId = dbMeterEnumeration.fetchAccountTypeIdByName(getApplicationContext(), selected_meterType);
                     }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    void populateSpinnerMeterInstallation()
    {
        spinnerMeterInstallation = (Spinner)findViewById(R.id.mMeterInstallationType);
        ins_data_array = new ArrayList<String>();
        ins_data_array = dbMeterEnumeration.fetchAllMeterInstallation(getApplicationContext());
        if(!ins_data_array.isEmpty() && ins_data_array.size()>0) {
            ArrayAdapter dataAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, ins_data_array);
            spinnerMeterInstallation.setAdapter(dataAdapter);
            selected_installationType = spinnerMeterInstallation.getSelectedItem().toString();
            //app.setChallengeTypeName(selected_installationType);
            selected_installationTypeId = dbMeterEnumeration.fetchMeterInstallationIdByName(getApplicationContext(), selected_installationType);
            //app.setChallengeTypeId(selected_installationTypeId);
        }
        spinnerMeterInstallation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_installationType = spinnerMeterInstallation.getSelectedItem().toString();
                // app.setChallengeTypeName(selected_installationType);
                selected_installationTypeId = dbMeterEnumeration.fetchMeterInstallationIdByName(getApplicationContext(), selected_installationType);
                //  app.setChallengeTypeId(selected_installationTypeId);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /*void populateSpinnerMeterInstallationTypes()
    {
        spinnerMeterStatus = (Spinner)findViewById(R.id.mMeterStatus);
        status_data_array = new ArrayList<String>();
        status_data_array = dbMeterEnumeration.fetchAllMeterStatus(getApplicationContext());
        ArrayAdapter dataAdapter = new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,status_data_array);
        spinnerMeterStatus.setAdapter(dataAdapter);
        selected_meterStatus = spinnerMeterStatus.getSelectedItem().toString();
        selected_meterStatusId = dbMeterEnumeration.fetchMeterStatusIdByName(getApplicationContext(), selected_meterStatus);

        spinnerMeterStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_meterStatus = spinnerMeterStatus.getSelectedItem().toString();
              //  app.setChallengeTypeName(selected_meterStatus);
                selected_meterStatusId = dbMeterEnumeration.fetchMeterStatusIdByName(getApplicationContext(), selected_meterStatus);
              //  app.setChallengeTypeId(selected_meterStatusId);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }*/


    public void attemptSavingMeterEnumeration(){
        meterNumber.setError(null);
        //serialNumber.setError(null);
        areaName.setError(null);
        streetName.setError(null);
        houseNumber.setError(null);

        View focusView = null;
        if(TextUtils.isEmpty(meterNumber.getText().toString())){
            meterNumber.setError("Meter number is required");
            focusView = meterNumber;
            return;
        }
        else if(TextUtils.isEmpty(areaName.getText().toString())){
            areaName.setError("Area is required.");
            focusView = areaName;
            return;
        }
        else if(TextUtils.isEmpty(streetName.getText().toString())){
            streetName.setError("Street is required.");
            focusView = streetName;
            return;
        }
        else if(TextUtils.isEmpty(houseNumber.getText().toString())){
            houseNumber.setError("Building Number is required.");
            focusView = houseNumber;
            return;
        }
        else if(app.getActiveBuilding().isEmpty()){
            houseNumber.setError("Active Building not set.");
            focusView = houseNumber;
            return;
        }
        else if(selected_consumerTypeId == 0 && selected_installationTypeId == 0 && selected_meterTypeId == 0){
            Toast.makeText(getApplicationContext(), "Some vital fields are left empty",Toast.LENGTH_LONG).show();
            return;
        }
        else{
            // TODO: 23/09/2015 Process saving form data to database
            //String mMeterNo = meterNumber.getText().toString();
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
                    Toast.makeText(MeterEnumerationActivity.this, "Error: Location not set", Toast.LENGTH_LONG).show();
                }
                else{
                    // TODO: 23/09/2015 Process saving form data to database
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("meter_no",meterNumber.getText().toString());
                    contentValues.put("account_type_id",selected_meterTypeId); //selected_meterType
                    //String bu = "SELECT building_id FROM _buildings where building_no =" + houseNumber + " AND street_id=" + app.getActiveStreet() + " LIMIT 1;";
                    contentValues.put("building_id", app.getActiveBuilding());
                    contentValues.put("consumer_type_id",selected_consumerTypeId);
                    contentValues.put("meter_installation_type_id",selected_installationTypeId);
                    String s = "SELECT service_id,email FROM client_table LIMIT 1;";
                    Cursor cu = dataDB.myConnection(getApplicationContext()).selectAllFromTable(s, true);
                    String s_id=""; String myemail = "";
                    if(cu.getCount() > 0)
                    {
                        if (cu.moveToFirst()) {
                            do {
                                s_id = cu.getString(0);
                                myemail = cu.getString(1);
                            }while (cu.moveToNext());
                        }
                    }
                    contentValues.put("service_id", app.getServiceid() != null ? String.valueOf(app.getServiceid()) : s_id);
                    contentValues.put("authorization_id", app.getAuthorizationid()!=null && !app.getAuthorizationid().isEmpty() ? app.getAuthorizationid() : myemail + Devices.getDeviceUUID(getApplicationContext()) + Devices.getDeviceIMEI(getApplicationContext()));
                    contentValues.put("latitude",_latitude);
                    contentValues.put("longitude",_longitude);
                    long enum_meter = dataDB.myConnection(getApplicationContext()).onInsert(contentValues, "meters");
                    if (enum_meter > 0) {
                        app.setActiveMeterNo(meterNumber.getText().toString());
                        Intent intent = new Intent(getApplicationContext(), NewMeterSuccessActivity.class);
                        intent.putExtra("addedMeter", String.valueOf(enum_meter));
                        MeterEnumerationActivity.this.finish();
                        startActivity(intent);
                    }

                }
            }
            else {
                GenericHelper.askUserToOpenGPS(MeterEnumerationActivity.this);
            }




            //DBConnection dbConnection = new DBConnection(getApplicationContext());
       //  System.out.println("Meter Type: " + selected_meterType + " Meter Type Id: " + selected_meterTypeId + " Meter Status: "
        //         + selected_meterStatus + " selected status id: " + selected_meterStatusId + " selected installation type: ");
            /*String tMeterNo = meterNumber.getText().toString();
            int buildingId = app.getActiveBuilding();
            String formattedDate = df.format(timer.getTime());
            String authorizationId = app.getEmail() + Devices.getDeviceUUID(getApplicationContext()) + Devices.getDeviceIMEI(getApplicationContext());
            Cursor cursor = dbConnection.selectColumnsFromTable("service_id", "client_table", "email", app.getEmail());
            if(cursor.moveToFirst()){
                while (cursor.isAfterLast() == false){
                    serviceId = cursor.getString(0);
                    cursor.moveToNext();
                }
                cursor.close();
            }
            if(!tMeterNo.isEmpty() && !tSerialNo.isEmpty()
                    && !selected_meterType.isEmpty() && !selected_meterStatus.isEmpty()
                    && !String.valueOf(buildingId).isEmpty()
                    )
            {
                ContentValues insertData = new ContentValues();
                insertData.put("meter_no",tMeterNo);
                insertData.put("serial_no",tSerialNo);
                insertData.put("building_id", buildingId);
                insertData.put("meter_status_id", selected_meterStatusId);
                insertData.put("installation_date", formattedDate);
                insertData.put("service_id",serviceId);
                insertData.put("authorization_id", authorizationId);
                Cursor cursor3 = dbConnection.selectColumnsFromTableOrderBy("id_meter", _METERS_TABLE, "ORDER BY id_meter DESC LIMIT 1");
                if(cursor3.moveToFirst()){
                    while (cursor3.isAfterLast() == false){
                        int id_meter = cursor3.getInt(0)+ 1;
                        insertData.put("meter_id",id_meter);
                         cursor3.moveToNext();
                    }
                    cursor3.close();
                }
                else {
                    insertData.put("meter_id",1);
                }
                Log.e("Challenges data", insertData.toString());
                Toast.makeText(getApplicationContext(), "Meter Enumeration Submitted Successfully", Toast.LENGTH_LONG).show();


            }*/
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_meter_enumeration, menu);
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
