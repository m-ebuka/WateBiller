package com.ensoft.mob.waterbiller;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class BuildingMapLocationActivity extends AppCompatActivity implements GoogleMap.OnInfoWindowClickListener {
    private LatLng defaultLatLng = null;
    private GoogleMap map;
    MyApp app;
    private int zoomLevel = 17; //7
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_map_location);
        app = ((MyApp)getApplication());

        Bundle extras = getIntent().getExtras();
        Double mlatitude,mlongitude;

        if (extras != null) {
            Log.e("extrass","extrass not empty");
            mlatitude = Double.parseDouble(extras.getString("mlatitude").trim());
            mlongitude = Double.parseDouble(extras.getString("mlongitude").trim());
            defaultLatLng = new LatLng(mlatitude,mlongitude);
        }
        else{
            Log.e("extrass","extrass is empty");
            defaultLatLng = new LatLng(0.0023,1.320233);
        }

        //Log.e("getActiveLatitude","Latitude" + app.getActiveLatitude());
        /*if(!app.getActiveLatitude().isEmpty() && app.getActiveLatitude() != null) {
            double lat = Double.valueOf(app.getActiveLatitude());
            double lon = Double.valueOf(app.getActiveLongitude());
            defaultLatLng = new LatLng(lat,lon);
        }
        else {*/

        //}
        //actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_action_back);
        actionBar.setTitle("Aqua Biller");
        actionBar.setLogo(R.drawable.logo);
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#5D617A"));
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setTitle(Html.fromHtml("<font color='#ffffff'>AquaBiller : Building Map View</font>"));

        try {
            map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if (map!=null){
                map.getUiSettings().setCompassEnabled(true);
                map.setTrafficEnabled(true);
                map.setMyLocationEnabled(true);


                // Move the camera instantly to defaultLatLng.
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, zoomLevel));


                map.addMarker(new MarkerOptions().position(defaultLatLng)
                        .title(app.getActiveBuildingName() != null && app.getActiveBuildingNo() != null ? app.getActiveBuildingName().toString() + ":" + app.getActiveBuildingNo().toString() : "Building title")
                        .snippet(app.getActiveAreaName() != null && app.getActiveStreetName() != null ? app.getActiveAreaName().toString() + ":" + app.getActiveStreetName().toString() : "This is the snippet within the Window")
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.logo)));

                map.setOnInfoWindowClickListener(this);
            }


        }catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_building_map_location, menu);
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

    @Override
    public void onPause() {
        if (map != null){
            map.setMyLocationEnabled(false);
            map.setTrafficEnabled(false);
        }
        super.onPause();
    }
    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(BuildingMapLocationActivity.this,"snippet "+ marker.getSnippet(),Toast.LENGTH_LONG).show();
        /*Intent intent = new Intent(this, NewActivity.class);
        intent.putExtra("snippet", marker.getSnippet());
        intent.putExtra("title", marker.getTitle());
        intent.putExtra("position", marker.getPosition());
        startActivity(intent);*/
    }
}
