package com.ensoft.mob.waterbiller;

import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.hardware.Camera;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
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

import com.ensoft.mob.waterbiller.Adapters.StreetDBAdapter;
import com.ensoft.mob.waterbiller.DB.DBAreaCodes;
import com.ensoft.mob.waterbiller.DB.DBBuildingCategory;
import com.ensoft.mob.waterbiller.DB.DataDB;
import com.ensoft.mob.waterbiller.Devices.Devices;
import com.ensoft.mob.waterbiller.Models.CoordinateModel;
import com.ensoft.mob.waterbiller.Models.currentUserDetail;
import com.ensoft.mob.waterbiller.helpers.GenericHelper;
import com.loopj.android.http.Base64;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class BuildingEnumerationActivity extends ActionBarActivity {
    private static final String TAG = "BuildingEnumerationActivity" ;


    MyApp app;
    DataDB dataDB = new DataDB();
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
    //String base64String = "";
    CoordinateModel coordinateModel = new CoordinateModel();
    StreetDBAdapter streetDBAdapter;
    DBAreaCodes dbAreaCodes;
    EditText mAreaCodeName;
    EditText mStreetName;
    ImageView imagePhoto;
    EditText mHouseName;
    EditText mHouseNumber;
    EditText mBuildingOwnerName;
    EditText mBuildingOwnerEmail;
    EditText mBuildingOwnerPhone;
    Button btn_save_enumeration;
    Spinner spinnerHouseCategory;
    TextView mhouseImage;
    private ArrayList<String> data_array;
    DBBuildingCategory dbBuildingCategory;
    String selected_buildingcategoryName;
    int selected_buildingCategoryId;
    int selected_streetId;
    int selected_areaCodeId;
    String building_no;
    String building_owner_name;
    String building_owner_phone;
    String building_owner_email;
    String errorMsg;
    private TextView mCreateStatus;
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
        setContentView(R.layout.activity_building_enumeration);

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

        app = ((MyApp)getApplication());
        streetDBAdapter = new StreetDBAdapter();
        dbAreaCodes = new DBAreaCodes();
        mAreaCodeName = (EditText)findViewById(R.id.editAreaName);
        mStreetName = (EditText)findViewById(R.id.editStreet);
        imagePhoto = (ImageView) findViewById(R.id.imagePhoto);
        mHouseName = (EditText) findViewById(R.id.editHouseName);
        mHouseNumber = (EditText)findViewById(R.id.editHouseNumber);
        mBuildingOwnerName = (EditText)findViewById(R.id.building_owner_name);
        mBuildingOwnerPhone = (EditText)findViewById(R.id.building_owner_phoneno);
        mBuildingOwnerEmail = (EditText)findViewById(R.id.building_owner_email);
        mhouseImage = (TextView)findViewById(R.id.houseImage);
        btn_save_enumeration = (Button) findViewById(R.id.enumeration_button);

        mAreaCodeName.setEnabled(false);
        mStreetName.setEnabled(false);
        dbBuildingCategory = new DBBuildingCategory();

        //actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_action_back);
        actionBar.setTitle("Aqua Biller");
        actionBar.setIcon(R.drawable.logo);
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#5D617A"));
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setTitle(Html.fromHtml("<font color='#ffffff' fontSize='10'>AquaBiller : New Building</font>"));

        populateSpinnerHouseCategory(); //populate the spinner

        try{
            if (app.getCameraByte() == null) {
                System.out.println("No Picture : " + app.getCameraByte());
            }
            else
            {

                //--added to display imageView ---
                byte[] imageData = app.getCameraByte();

                String base64String = Base64.encodeToString(imageData, Base64.NO_WRAP);
                byte[] decoded64String = Base64.decode(base64String, Base64.NO_WRAP);
                Log.e("BYTELENGTH","The size in byte is " + imageData.length);

                Log.e("BASE64LENGTH","The size in base64 is " + base64String.length());

                Bitmap bmp = BitmapFactory.decodeByteArray(decoded64String, 0, decoded64String.length);

                //Bitmap bmp = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);

                imagePhoto.setImageBitmap(bmp);
            }
            if(app.getActiveAreaName() != null && !app.getActiveAreaName().isEmpty())
            {
                String areaName = dbAreaCodes.getAreaCodeById(getApplicationContext(), app.getActiveAreaCode());
                mAreaCodeName.setText(areaName);
            }
            if(app.getActiveStreetName() != null && !app.getActiveStreetName().isEmpty())
            {
                String streetName = streetDBAdapter.fetchStreetById(getApplicationContext(), app.getActiveStreet());
                mStreetName.setText(streetName);
            }
        }
        catch (Exception e)
        {
            Log.e("Accessing variables in MyApp from BuildingEnumeration",e.toString());
        }


        imagePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open the camera activity
                app.setActivity(BuildingEnumerationActivity.this);
                Intent intent = new Intent(getApplicationContext(),CameraUseActivity.class);
                startActivity(intent);

            }
        });
                        btn_save_enumeration.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View view){
                //process adding new building and show success message

                boolean cancel = false;
                View focusView = null;

                mAreaCodeName.setError(null);
                mStreetName.setError(null);
                mHouseName.setError(null);
                mHouseNumber.setError(null);
                mhouseImage.setError(null);

                //app.setSampleByteImage(app.getCameraByte());
                byte[] _buildingImage = app.getCameraByte();
                String _areaName = mAreaCodeName.getText().toString();
                String _streetName = mStreetName.getText().toString();
                String _houseName = mHouseName.getText().toString();
                String _buildingNo = mHouseNumber.getText().toString();
                String _buildingOwnerName = mBuildingOwnerName.getText().toString();
                String _buildingOwnerEmail = mBuildingOwnerEmail.getText().toString();
                String _buildingOwnerPhone = mBuildingOwnerPhone.getText().toString();
                String _latitude = String.valueOf(coordinateModel.getLatitude());
                String _longitude = String.valueOf(coordinateModel.getLongitude());

                // TODO: 21/09/2015 Check for invalid data
                                //spinnerHouseCategory == null && spinnerHouseCategory.getSelectedItem() ==null
                if(TextUtils.isEmpty(selected_buildingcategoryName))
                {
                    focusView = spinnerHouseCategory;
                    cancel=true;
                    Toast.makeText(getApplicationContext(), "Building Category is required", Toast.LENGTH_LONG).show();
                }
                if(TextUtils.isEmpty(_areaName))
                {
                    mAreaCodeName.setError(getString(R.string.error_field_required));
                    focusView = mAreaCodeName;
                    cancel=true;
                }
                if(TextUtils.isEmpty(_streetName))
                {
                    mStreetName.setError(getString(R.string.error_field_required));
                    focusView = mStreetName;
                    cancel=true;
                }
                /*if (TextUtils.isEmpty(_houseName)) {
                    mHouseName.setError(getString(R.string.error_field_required));
                    focusView = mHouseName;
                    cancel = true;
                }*/
                if (TextUtils.isEmpty(_buildingNo)) {
                    mHouseNumber.setError(getString(R.string.error_field_required));
                    focusView = mHouseNumber;
                    cancel = true;
                }
                if(imagePhoto.getDrawable() == null)
                {
                    mBuildingOwnerPhone.setError("Building image not set");
                    focusView = imagePhoto;
                    cancel = true;
                }
                else{

                    if(_buildingImage == null)
                    {
                        mhouseImage.setError("Building image not set");
                        focusView = mhouseImage;
                        cancel = true;
                    }

                }


                if (cancel) {
                    // There was an error; don't attempt enumeration and focus the first
                    // form field with an error.
                    focusView.requestFocus();
                } else {
                    // Show a progress spinner, and kick off a background task to
                    // perform building enumeration.
                    // TODO: 22/09/2015 check if gps is on
                    // Check if GPS is enabled
                    String provider = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
                    if(!provider.equals(""))
                    {
                        //GPS is enabled
                        Toast.makeText(getApplicationContext(), "GPS is enabled!", Toast.LENGTH_SHORT).show();
                        if(_longitude == null || _longitude.isEmpty() || _latitude == null || _latitude.isEmpty())
                        {
                            Toast.makeText(BuildingEnumerationActivity.this, "Error: Location not set", Toast.LENGTH_LONG).show();
                        }
                        else{
                            // TODO: 23/09/2015 Process saving form data to database
                            ContentValues contentValue = new ContentValues();
                            //long numOfRec = dataDB.myConnection(getApplicationContext()).countRecords("_buildings") + 1;
                            String buildin_id = app.getActiveStreet()+"/"+_buildingNo;
                            contentValue.put("building_id",buildin_id);
                            contentValue.put("building_name", _houseName);
                            contentValue.put("building_no", _buildingNo);
                            Log.e("Byte Encode", "encode format: " + _buildingImage);
                            //MessageDigest md = MessageDigest.getInstance()
                            //String baseHex = GenericHelper.byteArrayToHexString(_buildingImage);
                            //Log.e("Hex Encode","encode format: " + baseHex);
                            //base64String = Base64.encodeToString(_buildingImage, Base64.DEFAULT);
                            //Log.e("Base64 Encode","encode format: " + base64String.toString());
                            String base64String = Base64.encodeToString(_buildingImage, Base64.NO_WRAP);
                            Log.e("BASE64LENGTH","The size in base64 is " + base64String.length());
                            contentValue.put("building_image", base64String);
                            contentValue.put("building_category_id", String.valueOf(selected_buildingCategoryId));
                            contentValue.put("street_id", String.valueOf(app.getActiveStreet()));
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

                            contentValue.put("service_id", app.getServiceid() != null ? String.valueOf(app.getServiceid()) : s_id);
                            contentValue.put("authorization_id", app.getAuthorizationid()!=null && !app.getAuthorizationid().isEmpty() ? app.getAuthorizationid() : myemail + Devices.getDeviceUUID(getApplicationContext()) + Devices.getDeviceIMEI(getApplicationContext()));
                            contentValue.put("latitude",_latitude);
                            contentValue.put("longitude",_longitude);
                            contentValue.put("registered_by",app.getUserid());
                            contentValue.put("registered_on", String.valueOf(timer.getTime()));
                            contentValue.put("building_owner_name",_buildingOwnerName);
                            contentValue.put("building_owner_phone",_buildingOwnerPhone);
                            contentValue.put("building_owner_email", _buildingOwnerEmail);
                            long enum_building = dataDB.myConnection(getApplicationContext()).onInsert(contentValue, "_buildings");
                                if (enum_building > 0) {
                                    app.setActiveBuildingNo(_buildingNo);
                                    app.setActiveBuilding(buildin_id);
                                    app.setActiveBuildingCategoryId(String.valueOf(selected_buildingCategoryId));
                                    Intent intent = new Intent(getApplicationContext(), NewBuildingSuccessActivity.class);
                                    intent.putExtra("addedBuilding", String.valueOf(enum_building));
                                    //_buildingImage.notify();
                                    app.setCameraByte(null);
                                    BuildingEnumerationActivity.this.finish();
                                    startActivity(intent);
                                }

                        }
                    }
                    else {
                        GenericHelper.askUserToOpenGPS(BuildingEnumerationActivity.this);

                    }

                }


            }
        });


    }

    public void ValidateBuildingEnumerationProcess()
    {
        boolean cancel = true;
        View focusView = null;
        // TODO: 21/09/2015 reset errors
        mAreaCodeName.setError(null);
        mStreetName.setError(null);
        mHouseNumber.setError(null);
        //store values from form element
        int _areacodeid = selected_areaCodeId;
        int _streetid = selected_streetId;
        int _buildingCat = selected_buildingCategoryId;
        byte[] _buildingImage = app.getCameraByte();
        String _areaName = mAreaCodeName.getText().toString();
        String _streetName = mStreetName.getText().toString();
        String _buildingNo = mHouseNumber.getText().toString();
        String _buildingOwnerName = mBuildingOwnerName.getText().toString();
        String _buildingOwnerEmail = mBuildingOwnerEmail.getText().toString();
        String _buildingOwnerPhone = mBuildingOwnerPhone.getText().toString();



        // TODO: 21/09/2015 Check for invalid data
        if (TextUtils.isEmpty(_buildingNo)) {
            mHouseNumber.setError(getString(R.string.error_field_required));
            focusView = mHouseNumber;
            cancel = true;
        }
        if(_buildingImage.length <= 0)
        {
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            //showProgress(true);
            //mCreateTask = new CreateBuildingEnumerationTask(_buildingImage,building_no,_areacodeid,_streetid,building_owner_name,building_owner_phone,building_owner_name,_buildingCat);
            //mCreateTask.execute((Void) null);
        }

    }

    void populateSpinnerHouseCategory()
    {
        spinnerHouseCategory = (Spinner)findViewById(R.id.spinnerHouseCategory);
        data_array = new ArrayList<String>();
        data_array = dbBuildingCategory.fetchAllBuildingCategory(getApplicationContext());
        if(!data_array.isEmpty() && data_array.size() > 0)
        {
            ArrayAdapter dataAdapter = new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,data_array);
            spinnerHouseCategory.setAdapter(dataAdapter);
            selected_buildingcategoryName = spinnerHouseCategory.getSelectedItem().toString();
            selected_buildingCategoryId = dbBuildingCategory.fetchBuildingCategoryIdByName(getApplicationContext(), selected_buildingcategoryName);
            Log.i("SELECTED BUILDINGCATEGORY", selected_buildingcategoryName +" BuildingCategory ID;" +selected_buildingCategoryId);
        }

        spinnerHouseCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_buildingcategoryName = spinnerHouseCategory.getSelectedItem().toString();
                selected_buildingCategoryId = dbBuildingCategory.fetchBuildingCategoryIdByName(getApplicationContext(), selected_buildingcategoryName);
                Log.i("SELECTED BUILDINGCATEGORY", selected_buildingcategoryName +" BuildingCategory ID;" +selected_buildingCategoryId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_building_enumeration, menu);
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

    private void showProgress(final boolean show)
    {
        ProgressDialog dialog;
        dialog = new ProgressDialog(BuildingEnumerationActivity.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setMax(100);
        dialog.show();
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        //GenericHelper.turnGPSOn(getApplicationContext());
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

}
