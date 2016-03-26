package com.ensoft.mob.waterbiller;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ensoft.mob.waterbiller.DB.AuthorizeUser;
import com.ensoft.mob.waterbiller.DB.DBConnection;
import com.ensoft.mob.waterbiller.DB.DataDB;
import com.ensoft.mob.waterbiller.Devices.Devices;
import com.ensoft.mob.waterbiller.Devices.LocationMavnager;
import com.ensoft.mob.waterbiller.Models.CoordinateModel;
import com.ensoft.mob.waterbiller.WebServices.PushAPI;
import com.ensoft.mob.waterbiller.helpers.GenericHelper;
import com.loopj.android.http.Base64;

import org.json.JSONObject;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BillingActivity extends AppCompatActivity {
    private static final String TAG = "Enumeration";
    DataDB dataDB = new DataDB();
    private Camera camera;
    private int cameraId = 0;
    private Boolean imgIsSet;
    Intent i;
    MyApp app;
    Bitmap bmp;
    EditText tMeterNumber;
    EditText tBuildingCategory;
    EditText tBuildingNumber;
    EditText tPreviousReadingValue;
    EditText tCurrentReadingValue;
    ImageView imgViewMeterBilling;
    Button sendbilling;
    TextView meterImage;
    boolean isNetworkEnabled = false;
    boolean isGPSEnabled = false;
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    CoordinateModel coordinateModel = new CoordinateModel();
    String _METER_READINGS_TABLE = "meter_readings";
    String _BUILDING_CATEGORIES_TABLE = "_building_categories";
    String latitude;
    String longitude;
    String prevReadingDate = "";
    Calendar timer = Calendar.getInstance();
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
    String idmeter_reading = "idmeter_reading";
    String s_id = "";

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
        setContentView(R.layout.activity_billing);
        app = ((MyApp) getApplication());

        //actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_action_back);
        actionBar.setTitle("Aqua Biller");
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#5D617A"));
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setTitle(Html.fromHtml("<font color='#ffffff' fontSize='10'>AquaBiller : Meter Reading</font>"));

        tBuildingCategory = (EditText) findViewById(R.id.editHouseCategory);
        tBuildingNumber = (EditText) findViewById(R.id.editHouseNumber);
        tMeterNumber = (EditText) findViewById(R.id.editMeterNumber);
        imgViewMeterBilling = (ImageView) findViewById(R.id.imagePhoto);
        tPreviousReadingValue = (EditText) findViewById(R.id.editPreviousReading);
        tCurrentReadingValue = (EditText) findViewById(R.id.editCurrentReading);
        sendbilling = (Button) findViewById(R.id.meter_reading_button);
        tBuildingCategory.setEnabled(false);
        tBuildingNumber.setEnabled(false);
        tMeterNumber.setEnabled(false);
        tPreviousReadingValue.setEnabled(false);
        meterImage = (TextView) findViewById(R.id.meter_image);


       /* PushAPI pushAPI =new PushAPI();
        JSONObject mobilMeterREadin = pushAPI.JsonPush(getApplicationContext(), "mobile_synch_meter_readings");
        System.out.println("MOBILE SYNCH METER READINGS: " + mobilMeterREadin);
        */

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

            PrefillData();

            final ImageButton fabButton = (ImageButton) findViewById(R.id.fab_addReport);
            fabButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fabButton.setSelected(!fabButton.isSelected());
                    Drawable drawable = fabButton.getDrawable();
                    if (drawable instanceof Animatable) {
                        ((Animatable) drawable).start();
                    }
                    Toast.makeText(getApplicationContext(),
                            "Report an issue/challenge", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), ReportingActivity.class));

                }
            });

            /*if (app.getCameraByteForMeterReading() == null) {
                System.out.println("No Picture : " + app.getCameraByteForMeterReading());
            } else {
                System.out.println("Yes Picture : " + app.getCameraByteForMeterReading());
                //--added to display imageView ---
                byte[] imageData = app.getCameraByteForMeterReading();
                System.out.println("Byte: " + imageData);
                Bitmap bmp = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                imgViewMeterBilling.setImageBitmap(bmp);
            }*/

            imgViewMeterBilling.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //showOptions(); //allow user to choose the application camera to use
                    app.setActivity(BillingActivity.this);
                    Intent intent = new Intent(getApplicationContext(),CameraUseActivity.class);
                    startActivity(intent);
                }
            });
            sendbilling.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    byte[] ImgData = app.getCameraByteForMeterReading();
                    tCurrentReadingValue.setError(null);
                    meterImage.setError(null);
                    //View focusView;
                    // TODO: 22-Sep-15 validating form inputs
                    if (TextUtils.isEmpty(tCurrentReadingValue.getText().toString())) {
                        tCurrentReadingValue.setError("Current Meter reading value can not be empty");
                       // focusView = tCurrentReadingValue;
                        return;
                    } else if (imgViewMeterBilling.getDrawable() == null || ImgData == null) {
                        meterImage.setError("Meter Image can not be empty");
                        Toast.makeText(getApplicationContext(), "Meter Image is required", Toast.LENGTH_LONG).show();
                       // focusView =meterImage;
                        return;
                    } else {
                        // TODO: 24-Sep-15 attempt meter reading submition
                        submitBilling(ImgData);
                    }
                }
            });

        }

    protected void PrefillData()
    {
        Log.e(TAG,"I AM HERE TO FILL DATA");
        try{
            String buildingCategoryId = app.getActiveBuildingCategoryId();
            Log.e("ActiveBuildingCategory", app.getActiveBuildingCategoryId());
            String buildingCategoryName = app.getActiveBuildingCategoryName();
            Log.e("ActiveBuildingCategoryName", buildingCategoryName);
            String meterNumber = app.getActiveMeterNo();
            Log.e("Active Meter", meterNumber);
            String buildingId =  app.getActiveBuilding();
            Log.e("Active Building ID", buildingId);
            String buildingName =  app.getActiveBuildingName();
            Log.e("Active BuildingName", buildingName);
            String buildingNumber =  app.getActiveBuildingNo();
            Log.e("Active Building Number", buildingNumber);

            if(!TextUtils.isEmpty(buildingCategoryName)) {
                Log.e("BuildingCategory2", buildingCategoryName);
                tBuildingCategory.setText(buildingCategoryName);
            }

            if(!TextUtils.isEmpty(meterNumber)){
                tMeterNumber.setText(meterNumber);
                ///SELECT current_reading, date_current_reading FROM meter_readings where meter_no = '234892' ORDER BY date_current_reading DESC LIMIT 1;
                String previousReading = dataDB.myConnection(getApplicationContext()).selectFromTableWithLimitAndOrder("current_reading", _METER_READINGS_TABLE, "meter_no", meterNumber, "1", "date_current_reading DESC");
                prevReadingDate = dataDB.myConnection(getApplicationContext()).selectFromTableWithLimitAndOrder("date_current_reading", _METER_READINGS_TABLE, "meter_no", meterNumber, "1", "date_current_reading DESC");

                Log.e(TAG,"PREVIOUS_READING " + previousReading +" here");
                Log.e(TAG,"PREVIOUS_READING_DATE " + prevReadingDate);
                if (TextUtils.isEmpty(previousReading) || previousReading == null) {
                    tPreviousReadingValue.setText("0.0");
                }
                else{
                    tPreviousReadingValue.setText(previousReading);
                }

            }

            if(buildingNumber != null){
                tBuildingNumber.setText(buildingNumber);
            }

            if (app.getCameraByteForMeterReading() == null) {
                System.out.println("No Meter Picture taken yet : ");
            }
            else
            {
                System.out.println("Yes Meter Picture taken : ");
                byte[] imageData = app.getCameraByteForMeterReading();
                String base64String = Base64.encodeToString(imageData, Base64.NO_WRAP);
                byte[] decoded64String = Base64.decode(base64String, Base64.NO_WRAP);

                Bitmap bmp = BitmapFactory.decodeByteArray(decoded64String, 0, decoded64String.length);
                //Bitmap bmp = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                imgViewMeterBilling.setImageBitmap(bmp);
            }
        }
        catch (Exception e)
        {
            Log.e("Accessing variables in MyApp from BillingActivity",e.toString());
        }

        System.out.println("Output from building " + app.getActiveBuildingCategoryName() + " category:" + app.getActiveBuildingCategoryId() + " Meter no" + app.getActiveMeterNo() + " house No: " + app.getActiveStreetName());


    }

    // TODO: 24-Sep-15 submit billing to the database
    public void submitBilling(byte[] ImgData){

        String buildingCat = tBuildingCategory.getText().toString();
        String meterNo = tMeterNumber.getText().toString();
        String buildingNo = tBuildingNumber.getText().toString();
        String previousReading = tPreviousReadingValue.getText().toString()!=null ? tPreviousReadingValue.getText().toString() : "";
        String currentReading = tCurrentReadingValue.getText().toString();
        int areaId = app.getActiveAreaCode();
        String officer_userid = app.getUserid();
        String buildingId = app.getActiveBuilding();
        String buildingCatId = app.getActiveBuildingCategoryId();
        String _serviceid = dataDB.myConnection(getApplicationContext()).selectColumnFromTableWithLimit("service_id", "client_table", 1);

        //String authorizationId = app.getEmail() + Devices.getDeviceUUID(getApplicationContext()) + Devices.getDeviceIMEI(getApplicationContext());

        /*DBConnection dbConnection = new DBConnection(getApplicationContext());
        Cursor cursor = dbConnection.selectColumnsFromTable("service_id", "client_table", "email", app.getEmail());

        if(cursor.moveToFirst()){
            while (cursor.isAfterLast() == false){
                String s_id = cursor.getString(0);
                cursor.moveToNext();
            }
            cursor.close();
        }*/

        String formattedDate = df.format(timer.getTime());
        ContentValues insertContent = new ContentValues();
        // service_id

        // TODO: 22-Sep-15 get the longitude and latitude

        latitude = coordinateModel.getLatitude();
        longitude = coordinateModel.getLongitude();

        // TODO: 22-Sep-15 check the required fields
        /*System.out.println("Current reading: " +
                currentReading +
                " Image: " + ImgData.toString() + " previouse reading: " + previousReading
                + " building No: " + buildingNo + " Meter No: " + meterNo + " building id: " + buildingId
                + " building category: " + buildingCat + " AreaId: " + areaId
                + " buildingCategoryId " + buildingCatId + " Logitude: " + longitude + " latitude: " + latitude);
*/
        if (    !meterNo.isEmpty()
                && !buildingNo.isEmpty()
                && !buildingCatId.isEmpty()
                && ImgData != null
                && !currentReading.isEmpty()
                && !String.valueOf(buildingId).isEmpty()
                ) {

            if (longitude == null || longitude.isEmpty() || latitude == null || latitude.isEmpty()) {
                Toast.makeText(BillingActivity.this, "Error: GPS Location not set", Toast.LENGTH_LONG).show();
                GenericHelper.askUserToOpenGPS(BillingActivity.this);
            } else {
                insertContent.put("meter_no", meterNo);
                insertContent.put("previous_reading", previousReading);
                insertContent.put("date_previous_reading", prevReadingDate);
                insertContent.put("current_reading", currentReading);
                insertContent.put("date_current_reading", formattedDate);
                // TODO: 08-Oct-15 used for the confirm billing activity
                app.setCurrentBillValue(Float.parseFloat(currentReading));
                String base64String = Base64.encodeToString(ImgData, Base64.NO_WRAP);
                insertContent.put("meter_image", base64String);
                insertContent.put("longitude", longitude);
                insertContent.put("latitude", latitude);
                insertContent.put("reading_timestamp", formattedDate);
                insertContent.put("building_id", buildingId);
                insertContent.put("building_categories_id", buildingCatId);
                insertContent.put("area_codes_id", areaId);
                insertContent.put("service_id", app.getServiceid() != null ? String.valueOf(app.getServiceid()) : _serviceid);
                insertContent.put("authorization_id", app.getAuthorizationid()!=null && !app.getAuthorizationid().isEmpty() ? app.getAuthorizationid() : app.getEmail() + Devices.getDeviceUUID(getApplicationContext()) + Devices.getDeviceIMEI(getApplicationContext()));
                long k = dataDB.myConnection(getApplicationContext()).countRecords(_METER_READINGS_TABLE);
                long meter_reading_id = Integer.valueOf(String.valueOf(k + 1) + app.getUserid());
                insertContent.put("meter_reading_id", meter_reading_id);
                insertContent.put("officer_userid", officer_userid);
                insertContent.put("synch_status", "0");
                Log.e("Billing content", insertContent.toString());
                long insertResult = dataDB.myConnection(getApplicationContext()).onInsertOrUpdate(insertContent, _METER_READINGS_TABLE);
                if (insertResult > 0) {
                    //Toast.makeText(getApplicationContext(),"Successful Billing Process", Toast.LENGTH_SHORT).show();
                    android.support.v7.app.AlertDialog.Builder adb = new android.support.v7.app.AlertDialog.Builder(
                            BillingActivity.this);
                    adb.setTitle("Info!");
                    adb.setIcon(R.drawable.logo);
                    adb.setMessage("Successful Meter Reading Process. Thank you!");
                    adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            app.setActiveStreet(0);
                            app.setActiveAreaCode(0);
                            app.setActiveBuildingCategoryId(null);
                            app.setActiveMeterNo(null);
                            app.setActiveBuilding(null);
                            app.setActiveBuildingName(null);
                            app.setActiveBuildingNo(null);
                            app.setActiveBuildingCategoryName(null);
                            app.setCameraByteForMeterReading(null);
                            tCurrentReadingValue.setText("");
                            imgIsSet = false;
                            Intent intent = new Intent(getApplicationContext(), PreBillingActivity.class);
                            BillingActivity.this.finish();
                            startActivity(intent);
                        }
                    });
                    adb.show();

                    //Initialize();
                } else {
                    // TODO: 22-Sep-15 Show an error if all the required fields are not available
                    tCurrentReadingValue.setError("Could not process meter reading");
                    Toast.makeText(getApplicationContext(), "Error: Could not process meter reading", Toast.LENGTH_LONG).show();

                }

        }


        } else {
            // TODO: 22-Sep-15 Show an error if all the required fields are not available
            tCurrentReadingValue.setError("Some fields are missing");
        }
    }


    protected void Initialize()
    {
       // tCurrentReadingValue.setText("");
        //tBuildingNumber.setText("");
        //tMeterNumber.setText("");
        app.setCameraByteForMeterReading(null);

        Drawable reset_image= getResources().getDrawable(R.drawable.camera_new);
        imgViewMeterBilling.setBackgroundDrawable(reset_image);
        tCurrentReadingValue.setText("");
        imgIsSet = false;
    }

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
            app.setCameraByteForMeterReading(bmpArray);
            imgViewMeterBilling.setImageBitmap(bmp);
            imgIsSet = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_billing, menu);
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

    private void showOptions() {

        final CharSequence[] items = { "Custom Camera", "Device App Camera", "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Camera Options!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Custom Camera")) {
                    //open the camera activity
                    app.setActivity(BillingActivity.this);
                    Intent intent = new Intent(getApplicationContext(),CameraUseActivity.class);
                    startActivity(intent);
                } else if (items[item].equals("Device App Camera")) {

                    i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(i, cameraId);

                    /*Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);*/
                } else if (items[item].equals("Cancel")) {
                       dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        //GenericHelper.turnGPSOn(getApplicationContext());
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

}
