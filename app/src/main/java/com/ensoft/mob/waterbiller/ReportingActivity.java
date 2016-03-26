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
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.ensoft.mob.waterbiller.DB.DBBuildingCategory;
import com.ensoft.mob.waterbiller.DB.DBChallenges;
import com.ensoft.mob.waterbiller.DB.DBConnection;
import com.ensoft.mob.waterbiller.DB.DataDB;
import com.ensoft.mob.waterbiller.Devices.Devices;
import com.ensoft.mob.waterbiller.Models.CoordinateModel;
import com.ensoft.mob.waterbiller.Models.currentUserDetail;
import com.ensoft.mob.waterbiller.helpers.GenericHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ReportingActivity extends AppCompatActivity {
    private static final String TAG = "Reporting";
    MyApp app;
    DataDB dataDB = new DataDB();
    boolean isNetworkEnabled = false;
    boolean isGPSEnabled = false;
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    CoordinateModel coordinateModel = new CoordinateModel();
    Spinner spinnerChallengeType;
    private ArrayList<String> data_array;
    DBChallenges dbChallenges;
    String latitude;
    String longitude;
    String selected_challengeType;
    int selected_challengeTypeId;
    EditText areaCode;
    EditText streetName;
    EditText buildingNumber;
    EditText challengeDescription;
    Button reportChallenge;
    String serviceId = "";
    int meterId = 100;
    String _METER_READING_CHALLENGES_TABLE = "meter_reading_challenges";
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
        setContentView(R.layout.activity_reporting);

        areaCode = (EditText) findViewById(R.id.area_name);
        streetName =(EditText) findViewById(R.id.street_name);
        buildingNumber = (EditText) findViewById(R.id.building_no);
        challengeDescription = (EditText) findViewById(R.id.challenge_description);
        reportChallenge = (Button) findViewById(R.id.report_button);

        areaCode.setEnabled(false);
        streetName.setEnabled(false);
        buildingNumber.setEnabled(false);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_action_back);
        actionBar.setTitle("Aqua Biller");
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#5D617A"));
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setTitle(Html.fromHtml("<font color='#ffffff' fontSize='9'>AquaBiller : Meter Reading Challenges</font>"));

        dbChallenges = new DBChallenges();
        app = ((MyApp) getApplication());

        areaCode.setText(app.getActiveAreaName() != null ? app.getActiveAreaName() : "");
        streetName.setText(app.getActiveStreetName() != null ? app.getActiveStreetName() : "");
        buildingNumber.setText(app.getActiveBuildingNo() != null ? app.getActiveBuildingNo() : "");

        populateSpinnerChallengeType(); //populate the spinner

        reportChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               attemptReporting(); // attempt to report challenges
                            }
        });
    }
    void populateSpinnerChallengeType()
    {
        spinnerChallengeType = (Spinner)findViewById(R.id.challenge_type);
        data_array = new ArrayList<String>();
        data_array = dbChallenges.fetchAllChallengeType(getApplicationContext());
        ArrayAdapter dataAdapter = new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,data_array);
        spinnerChallengeType.setAdapter(dataAdapter);
        selected_challengeType = spinnerChallengeType.getSelectedItem().toString();
          // app.setChallengeTypeName(selected_challengeType);
        selected_challengeTypeId = dbChallenges.fetchChallengeTypeIdByName(getApplicationContext(), selected_challengeType);
          // app.setChallengeTypeId(selected_challengeTypeId);

        spinnerChallengeType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_challengeType = spinnerChallengeType.getSelectedItem().toString();
                 //  app.setChallengeTypeName(selected_challengeType);
                selected_challengeTypeId = dbChallenges.fetchChallengeTypeIdByName(getApplicationContext(), selected_challengeType);
                  //  app.setChallengeTypeId(selected_challengeTypeId);

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    // TODO: 24-Sep-15 insert meter reading challenges to database
    public void attemptReporting(){
        challengeDescription.setError(null);
        View focusView = null;
      if(TextUtils.isEmpty(challengeDescription.getText().toString())){
          challengeDescription.setError("Report description is required");
          focusView = challengeDescription;
          return;
      }
        else{
          String _latitude = String.valueOf(coordinateModel.getLatitude());
          String _longitude = String.valueOf(coordinateModel.getLongitude());
          // Check if GPS is enabled
          String provider = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
          if(!provider.equals(""))
          {
              //GPS is enabled
              Toast.makeText(getApplicationContext(), "GPS is enabled!", Toast.LENGTH_SHORT).show();
              if(_longitude == null || _longitude.isEmpty() || _latitude == null || _latitude.isEmpty())
              {
                  Toast.makeText(ReportingActivity.this, "Error: Location not set", Toast.LENGTH_LONG).show();
              }
              else{
                  ContentValues inserData = new ContentValues();

                  inserData.put("meter_no",app.getActiveMeterNo());
                  //inserData.put("challenge_type_id", app.getChallengeTypeId());
                  String s = "SELECT service_id,email FROM client_table LIMIT 1;";
                  Cursor cu = dataDB.myConnection(getApplicationContext()).selectAllFromTable(s, true);
                  String s_id=""; String myemail = "";
                  if(cu.getCount() > 0)
                  {
                      if (cu.moveToFirst()) {
                          currentUserDetail userD = new currentUserDetail();
                          do {
                              s_id = cu.getString(0);
                              myemail = cu.getString(1);
                          }while (cu.moveToNext());
                      }

                  }

                  inserData.put("service_id", app.getServiceid() != null ? String.valueOf(app.getServiceid()) : s_id);
                  inserData.put("authorization_id", app.getAuthorizationid()!=null && !app.getAuthorizationid().isEmpty() ? app.getAuthorizationid() : myemail + Devices.getDeviceUUID(getApplicationContext()) + Devices.getDeviceIMEI(getApplicationContext()));
                  inserData.put("latitude",_latitude);
                  inserData.put("longitude",_longitude);
                  inserData.put("registered_by",app.getUserid());
                  inserData.put("registered_on", String.valueOf(timer.getTime()));
                  inserData.put("challenge_type_id", selected_challengeTypeId);
                  inserData.put("other_remark",challengeDescription.getText().toString());
                  Cursor cursor3 = dataDB.myConnection(getApplicationContext()).selectColumnsFromTableOrderBy("idmeter_reading_challenges", _METER_READING_CHALLENGES_TABLE, "ORDER BY idmeter_reading_challenges DESC LIMIT 1");
                  if(cursor3.moveToFirst()){
                      while (cursor3.isAfterLast() == false){
                          int idmeter_reading_challenges = cursor3.getInt(0)+ 1;
                          inserData.put("meter_reading_challenges_id",idmeter_reading_challenges);
                          cursor3.moveToNext();
                      }
                      cursor3.close();
                  }
                  else {
                      inserData.put("meter_reading_challenges_id",1);
                  }

                  Log.e("Challenges data", inserData.toString());
                  long insertResult = dataDB.myConnection(getApplicationContext()).onInsertOrUpdate(inserData, _METER_READING_CHALLENGES_TABLE);
                  if (insertResult > 0) {
                      challengeDescription.setText("");
                      Toast.makeText(getApplicationContext(), "Challenge Submitted Successfully", Toast.LENGTH_LONG).show();
                      Intent intent = new Intent(getApplicationContext(), ReportingSccessActivity.class);
                      ReportingActivity.this.finish();
                      startActivity(intent);
                  } else {
                      // TODO: 22-Sep-15 Show an error if all the required fields are not available
                      challengeDescription.setError("An error occurred");
                  }
              }
          }
          else
          {
              GenericHelper.askUserToOpenGPS(ReportingActivity.this);
          }

      }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reporting, menu);
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
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
}
