package com.ensoft.mob.waterbiller;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ensoft.mob.waterbiller.DB.DataDB;
import com.ensoft.mob.waterbiller.Models.CoordinateModel;
import com.loopj.android.http.Base64;

import java.util.Calendar;


public class iReportActivity extends AppCompatActivity {
    private static final String TAG = "iReportActivity" ;
    CoordinateModel coordinateModel = new CoordinateModel();
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    // if GPS is enabled
    boolean isGPSEnabled = false;
    // if Network is enabled
    boolean isNetworkEnabled = false;
    DataDB dataDB = new DataDB();
    MyApp app;
    EditText zone_name;
    EditText area_name;
    EditText street_name;
    EditText building_no;
    EditText description;
    TextView situation_image;
    ImageView imagePhoto;
    Button report_button;

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
        setContentView(R.layout.activity_i_report);

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
        actionBar.setTitle(Html.fromHtml("<font color='#ffffff' fontSize='10'>AquaBiller : iReport</font>"));

        app = ((MyApp) getApplication());

        zone_name = (EditText)findViewById(R.id.zone_name);
        area_name = (EditText)findViewById(R.id.area_name);
        street_name = (EditText)findViewById(R.id.street_name);
        building_no = (EditText)findViewById(R.id.building_no);
        description = (EditText)findViewById(R.id.description);
        situation_image = (TextView)findViewById(R.id.situation_image);
        imagePhoto = (ImageView)findViewById(R.id.imagePhoto);
        report_button = (Button)findViewById(R.id.report_button);

        // set the profile image
        if (app.getSituationImage() == null) {
            System.out.println("No Image captured yet : ");
        }
        else {
            System.out.println("Yes Image taken : ");
            byte[] imageData = app.getSituationImage();
            String base64String = Base64.encodeToString(imageData, Base64.NO_WRAP);
            byte[] decoded64String = Base64.decode(base64String, Base64.NO_WRAP);

            Bitmap bmp = BitmapFactory.decodeByteArray(decoded64String, 0, decoded64String.length);
            imagePhoto.setImageBitmap(bmp);
            description.setText(app.getIreport_description());
            building_no.setText(app.getIreport_building());
            street_name.setText(app.getIreport_street());
            area_name.setText(app.getIreport_area());
            zone_name.setText(app.getIreport_zone());
        }
        imagePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                app.setIreportData(zone_name.getText().toString(),area_name.getText().toString(),street_name.getText().toString(),building_no.getText().toString(),description.getText().toString());
                app.setActivity(iReportActivity.this);
                Intent intent = new Intent(getApplicationContext(),CameraUseActivity.class);
                startActivity(intent);
            }
        });

        report_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] ImgData = app.getSituationImage();
                boolean cancel = false;
                View focusView = null;
                zone_name.setError(null);
                area_name.setError(null);
                street_name.setError(null);
                description.setError(null);
                situation_image.setError(null);

                String _zone = zone_name.getText().toString();
                String _area = area_name.getText().toString();
                String _street = street_name.getText().toString();
                String _buildingno = building_no.getText().toString();
                String _description = description.getText().toString();
                String _serviceid = dataDB.myConnection(getApplicationContext()).selectColumnFromTableWithLimit("service_id","client_table",1);

                if (imagePhoto.getDrawable() == null || ImgData == null) {
                    situation_image.setError("Situation Image can not be empty");
                    focusView = situation_image;
                    Toast.makeText(getApplicationContext(), "Situation Image is required", Toast.LENGTH_LONG).show();
                    cancel = true;
                }
                if(TextUtils.isEmpty(_zone))
                {
                    zone_name.setError(getString(R.string.error_field_required));
                    focusView = zone_name;
                    cancel=true;
                }
                if(TextUtils.isEmpty(_area))
                {
                    area_name.setError(getString(R.string.error_field_required));
                    focusView = area_name;
                    cancel=true;
                }
                if(TextUtils.isEmpty(_street))
                {
                    street_name.setError(getString(R.string.error_field_required));
                    focusView = street_name;
                    cancel=true;
                }
                if(TextUtils.isEmpty(_description))
                {
                    description.setError(getString(R.string.error_field_required));
                    focusView = description;
                    cancel=true;
                }
                if(cancel){
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
                        else {
                            ContentValues contentValue = new ContentValues();
                            contentValue.put("service_id", app.getServiceid() != null ? String.valueOf(app.getServiceid()) : _serviceid);
                            contentValue.put("zone",_zone);
                            contentValue.put("area",_area);
                            contentValue.put("street",_street);
                            contentValue.put("building",_buildingno);
                            contentValue.put("description",_description);
                            contentValue.put("longitude",_longitude);
                            contentValue.put("latitude",_latitude);
                            contentValue.put("user_id",app.getUserid());
                            Calendar c = Calendar.getInstance();
                            int date = c.get(Calendar.DATE);

                            contentValue.put("registered_on",date);
                            long k = dataDB.myConnection(getApplicationContext()).countRecords("ireport");
                            long ireport_id = Integer.valueOf(String.valueOf(k + 1) + app.getUserid());
                            contentValue.put("ireport_id",ireport_id);
                            String base64String = Base64.encodeToString(ImgData, Base64.NO_WRAP);
                            contentValue.put("situation_image",base64String);

                            long ins_cont = dataDB.myConnection(getApplicationContext()).onInsert(contentValue, "ireport");
                            if (ins_cont > 0) {

                                Log.e("Inserted ireport Data", contentValue.toString());
                                AlertDialog.Builder adb = new AlertDialog.Builder(
                                        iReportActivity.this);
                                adb.setTitle("Info!");
                                adb.setIcon(R.drawable.logo);
                                adb.setMessage("You have succeeded in creating situation report. Thank you!");
                                adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        app.setIreportData(null, null, null, null, null);
                                        app.setSituationImage(null);
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        iReportActivity.this.finish();
                                        startActivity(intent);
                                    }
                                });
                                adb.show();
                            }
                            else{
                                Log.e("Unable to save ireport", contentValue.toString());
                                Toast.makeText(getApplicationContext(), "Error occured trying to save data", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_i_report, menu);
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
