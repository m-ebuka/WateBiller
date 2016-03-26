package com.ensoft.mob.waterbiller;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.Spinner;
import android.widget.Toast;

import com.ensoft.mob.waterbiller.DB.DBMeterEnumeration;
import com.ensoft.mob.waterbiller.DB.DataDB;

import java.util.ArrayList;

public class ProfileExistActivity extends AppCompatActivity {
    Button individaulReg;
    Button btnNext;
    Spinner profileType;
    Spinner streetList;
    Spinner buildingList;
    String selected_profileType;
    int selected_profileId;
    String selected_street;
    int selected_streetId;
    String selected_building;
    String selected_buildingId;
    String selected_buildingNo;
    MyApp app;

    private ArrayList<String> profileType_data_array;
    private ArrayList<String> streetList_data_array;
    private ArrayList<String> buildingList_data_array;

    DBMeterEnumeration dbMeterEnumeration;
    DataDB dataDB = new DataDB();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_exist);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_action_back);
        actionBar.setTitle("Aqua Biller");
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#5D617A"));
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setTitle(Html.fromHtml("<font color='#ffffff' fontSize='10'>AquaBiller : Choose Profile</font>"));
        app = ((MyApp)getApplication());
        dbMeterEnumeration = new DBMeterEnumeration();
        populateSpinnerProfileType();// populate spinner profile type
        populateSpinnerStreetList(); //populate spinner street list
        //populateSpinnerBuildingList(); //populate spinner building list
        btnNext = (Button) findViewById(R.id.mSelectProfileType);

        btnNext.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Log.e("Parameters", selected_profileType + ',' + selected_profileId);
                //Toast.makeText(getApplicationContext(), String.valueOf(selected_profileId), Toast.LENGTH_LONG).show();
                if(selected_street != null && selected_building != null)
                {
                    app.setActiveStreet(selected_streetId);
                    app.setActiveBuilding(selected_buildingId);
                    app.setProfileTypeId(String.valueOf(selected_profileId));
                    //app.setActiveBuildingNo(selected_buildingNo);

                    if(selected_profileId == 1){
                        Intent intent = new Intent(getApplicationContext(), individual_profile.class);
                        // intent.putExtra("profileTypeId", String.valueOf(selected_profileId));
                        app.setProfileTypeId(String.valueOf(selected_profileId));
                        ProfileExistActivity.this.finish();
                        startActivity(intent);
                    }
                    else{

                        Intent intent = new Intent(getApplicationContext(), corperate_profile.class);
                        // intent.putExtra("profileTypeId", String.valueOf(selected_profileId));
                        app.setProfileTypeId(String.valueOf(selected_profileId));
                        ProfileExistActivity.this.finish();
                        startActivity(intent);
                    }
                }
                else{
                    if(streetList == null && streetList.getSelectedItem() ==null){
                        Toast.makeText(getApplicationContext(), "Street is required", Toast.LENGTH_LONG).show();
                    }
                    if(buildingList == null && buildingList.getSelectedItem() ==null){
                        Toast.makeText(getApplicationContext(), "Building is required", Toast.LENGTH_LONG).show();
                    }
                }


            }
        });
/*
        mNext = (Button) findViewById(R.id.mSelectProfileType);
        corperateReg = (Button) findViewById(R.id.buttonCorpertionReg);

             individaulReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "Individual Registration", Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(), individual_profile.class));
                            }
        });

        */

    }

    void populateSpinnerStreetList()
    {
        streetList = (Spinner)findViewById(R.id.streetList);
        streetList_data_array = new ArrayList<String>();
        streetList_data_array = dbMeterEnumeration.fetchAllStreets(getApplicationContext());
        if(!streetList_data_array.isEmpty() && streetList_data_array.size()>0) {
            ArrayAdapter dataAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, streetList_data_array);
            streetList.setAdapter(dataAdapter);
            selected_street = streetList.getSelectedItem().toString();
            selected_streetId = Integer.valueOf(dataDB.myConnection(getApplicationContext()).selectFromTable("street_id","_streets","street",selected_street));
            //selected_streetId = ((int) streetList.getSelectedItemId());

            populateSpinnerBuildingList(selected_streetId);
        }
        streetList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_street = streetList.getSelectedItem().toString();
                selected_streetId = Integer.valueOf(dataDB.myConnection(getApplicationContext()).selectFromTable("street_id", "_streets", "street", selected_street));
                Log.e("SelectedStreetID", String.valueOf(selected_streetId));
                Log.e("SelectedStreet", selected_street);

                populateSpinnerBuildingList(selected_streetId);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    void populateSpinnerBuildingList(int selected_streetid)
    {
        buildingList = (Spinner)findViewById(R.id.buildingList);
        buildingList_data_array = new ArrayList<String>();
        buildingList_data_array = dbMeterEnumeration.fetchAllBuildings(getApplicationContext(), selected_streetid);
        if(!buildingList_data_array.isEmpty() && buildingList_data_array.size()>0) {
            ArrayAdapter dataAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, buildingList_data_array);
            buildingList.setAdapter(dataAdapter);
            selected_building = buildingList.getSelectedItem().toString();
            selected_buildingId = dataDB.myConnection(getApplicationContext()).selectFromTable("building_id", "_buildings", "building_no", selected_building);
            //selected_buildingNo = dataDB.myConnection(getApplicationContext()).selectFromTable("building_no","_buildings","building_name",selected_building);
        }
        buildingList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_building = buildingList.getSelectedItem().toString();
                selected_buildingId = dataDB.myConnection(getApplicationContext()).selectFromTable("building_id", "_buildings", "building_no", selected_building);
                //selected_buildingNo = dataDB.myConnection(getApplicationContext()).selectFromTable("building_no", "_buildings", "building_name", selected_building);

                Log.e("SelectedBuildingID", String.valueOf(selected_buildingId));
                Log.e("SelectedBuilding", selected_building);
                //Log.e("SelectedBuildingNo", selected_buildingNo);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    void populateSpinnerProfileType()
    {

        profileType = (Spinner)findViewById(R.id.profileType);
        profileType_data_array = new ArrayList<String>();
        profileType_data_array = dbMeterEnumeration.fetchAllProfileType(getApplicationContext());
        if(!profileType_data_array.isEmpty() && profileType_data_array.size()>0) {
            ArrayAdapter dataAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, profileType_data_array);
            profileType.setAdapter(dataAdapter);
            selected_profileType = profileType.getSelectedItem().toString();
            selected_profileId = dbMeterEnumeration.fetchProfileTypeIdByName(getApplicationContext(), selected_profileType);
        }
        profileType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_profileType = profileType.getSelectedItem().toString();
                selected_profileId = dbMeterEnumeration.fetchProfileTypeIdByName(getApplicationContext(), selected_profileType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile_exist, menu);
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
}
