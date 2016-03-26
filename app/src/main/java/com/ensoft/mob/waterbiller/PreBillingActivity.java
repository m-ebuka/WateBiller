package com.ensoft.mob.waterbiller;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.ensoft.mob.waterbiller.DB.DBMeterEnumeration;
import com.ensoft.mob.waterbiller.DB.DataDB;

import java.util.ArrayList;

public class PreBillingActivity extends AppCompatActivity {
    DBMeterEnumeration dbMeterEnumeration;
    DataDB dataDB = new DataDB();
    MyApp app;
    Button mMoveNextScreenBilling;
    Spinner spinnerAllActiveAreaCodes;
    Spinner spinnerAllStreetList;
    Spinner spinnerAllBuildingList;
    EditText editMeterNumber;
    int selected_area_code_id;
    String selected_area_name;
    int selected_street_id;
    String selected_street_name;
    int selected_building_category_id;
    String selected_building_category_name;
    int selected_building_id;
    String selected_building_no;
    String selected_buildingName;
    String selected_meterno;

    private ArrayList<String> areaList_data_array;
    private ArrayList<String> streetList_data_array;
    private ArrayList<String> buildingList_data_array;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_billing);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_action_back);
        actionBar.setTitle("Aqua Biller");
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#5D617A"));
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setTitle(Html.fromHtml("<font color='#ffffff' fontSize='10'>AquaBiller : Pre Billing</font>"));
        app = ((MyApp)getApplication());
        dbMeterEnumeration = new DBMeterEnumeration();

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mMoveNextScreenBilling = (Button)findViewById(R.id.mMoveNextScreenBilling);
        editMeterNumber = (EditText)findViewById(R.id.editMeterNumber);
        populateSpinnerAreaList();

        mMoveNextScreenBilling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean cancel = false;
                View focusView = null;
                editMeterNumber.setError(null);
                if(selected_area_name != null || selected_building_no != null || selected_street_name != null)
                {
                    if(TextUtils.isEmpty(selected_meterno))
                    {
                        editMeterNumber.setError("No assigned meter for the selected building");
                        focusView = editMeterNumber;
                        cancel=true;
                        //Toast.makeText(getApplicationContext(), "No assigned meter for the selected building", Toast.LENGTH_LONG).show();
                        //return;
                    }
                    if(selected_building_category_id <= 0)
                    {
                        editMeterNumber.setError("Couldn't get Building Category for the selected building");
                        focusView = editMeterNumber;
                        cancel=true;
                        //Toast.makeText(getApplicationContext(), "Couldn't get Building Category for the selected building", Toast.LENGTH_LONG).show();
                        ///return;
                    }

                    Intent intent = new Intent(getApplicationContext(), BillingActivity.class);
                    PreBillingActivity.this.finish();
                    startActivity(intent);
                }
                else {
                    editMeterNumber.setError("All fields are required");
                    focusView = editMeterNumber;
                    cancel=true;
                    //Toast.makeText(getApplicationContext(), "All fields are required", Toast.LENGTH_LONG).show();
                    //return;
                }
                if(cancel){
                    // form field with an error.
                    focusView.requestFocus();
                }
                else{
                    //go ahead now
                    app.setActiveStreet(selected_street_id);
                    app.setActiveAreaCode(selected_area_code_id);
                    app.setActiveBuildingCategoryId(String.valueOf(selected_building_category_id));
                    app.setActiveMeterNo(selected_meterno);
                    app.setActiveBuilding(String.valueOf(selected_building_id));
                    app.setActiveBuildingName(selected_buildingName);
                    app.setActiveBuildingNo(selected_building_no);
                    app.setActiveBuildingCategoryName(selected_building_category_name);

                    PreBillingActivity.this.finish();
                    startActivity(new Intent(getApplicationContext(), BillingActivity.class));
                }
            }
        });
    }

    void populateSpinnerAreaList()
    {
        spinnerAllActiveAreaCodes = (Spinner)findViewById(R.id.spinnerAllActiveAreaCodes);
        areaList_data_array = new ArrayList<String>();
        areaList_data_array = dbMeterEnumeration.fetchAllAreaCodes(getApplicationContext());
        if(!areaList_data_array.isEmpty() && areaList_data_array.size()>0)
        {
            ArrayAdapter dataAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, areaList_data_array);
            spinnerAllActiveAreaCodes.setAdapter(dataAdapter);
            selected_area_name = spinnerAllActiveAreaCodes.getSelectedItem().toString();
            selected_area_code_id = Integer.valueOf(dataDB.myConnection(getApplicationContext()).selectFromTable("area_code_id", "_areacodes", "area_name", selected_area_name));

            populateSpinnerStreetList(String.valueOf(selected_area_code_id));
        }
        spinnerAllActiveAreaCodes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_area_name = spinnerAllActiveAreaCodes.getSelectedItem().toString();
                selected_area_code_id = Integer.valueOf(dataDB.myConnection(getApplicationContext()).selectFromTable("area_code_id", "_areacodes", "area_name", selected_area_name));

                Log.e("SelectedAreaName", String.valueOf(selected_area_name));
                Log.e("SelectedAreaCodeID", String.valueOf(selected_area_code_id));

                populateSpinnerStreetList(String.valueOf(selected_area_code_id));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    void populateSpinnerStreetList(String selected_areacode_id)
    {
        Log.e("Selected Areacode_id", selected_areacode_id);
        spinnerAllStreetList = (Spinner)findViewById(R.id.spinnerAllStreetList);
        streetList_data_array = new ArrayList<String>();
        streetList_data_array = dbMeterEnumeration.fetchAllStreetsByAreaCodeId(getApplicationContext(), selected_areacode_id);
        if(!streetList_data_array.isEmpty() && streetList_data_array.size()>0) {
            ArrayAdapter dataAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, streetList_data_array);
            Log.e("streetList_data_array",streetList_data_array.toString());
            if(dataAdapter != null) {
                spinnerAllStreetList.setAdapter(dataAdapter);
            }
            selected_street_name = spinnerAllStreetList.getSelectedItem().toString();
            selected_street_id = Integer.valueOf(dataDB.myConnection(getApplicationContext()).selectFromTable("street_id","_streets","street",selected_street_name));

            populateSpinnerBuildingList(selected_street_id);
        }
        spinnerAllStreetList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_street_name = spinnerAllStreetList.getSelectedItem().toString();
                selected_street_id = Integer.valueOf(dataDB.myConnection(getApplicationContext()).selectFromTable("street_id", "_streets", "street", selected_street_name));
                Log.e("SelectedStreetID", String.valueOf(selected_street_id));
                Log.e("SelectedStreet", selected_street_name);

                populateSpinnerBuildingList(selected_street_id);

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    void populateSpinnerBuildingList(int selected_streetid)
    {
        spinnerAllBuildingList = (Spinner)findViewById(R.id.spinnerAllBuildingList);
        buildingList_data_array = new ArrayList<String>();
        buildingList_data_array = dbMeterEnumeration.fetchAllBuildings(getApplicationContext(), selected_streetid);
        if(!buildingList_data_array.isEmpty() && buildingList_data_array.size()>0) {
            ArrayAdapter dataAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, buildingList_data_array);
            spinnerAllBuildingList.setAdapter(dataAdapter);
            selected_building_no = spinnerAllBuildingList.getSelectedItem().toString();
            selected_building_id = Integer.valueOf(dataDB.myConnection(getApplicationContext()).selectFromTable("building_id", "_buildings", "building_no", selected_building_no));
            selected_building_category_id = Integer.valueOf(dataDB.myConnection(getApplicationContext()).selectFromTable("building_category_id", "_buildings", "building_no", selected_building_no));
            selected_meterno = dataDB.myConnection(getApplicationContext()).selectFromTable("meter_no", "meters", "building_id", String.valueOf(selected_building_id));
            selected_buildingName = dataDB.myConnection(getApplicationContext()).selectFromTable("building_name", "_buildings", "building_no", selected_building_no);
            selected_building_category_name = dataDB.myConnection(getApplicationContext()).selectFromTable("building_category", "_building_categories", "building_category_id", String.valueOf(selected_building_category_id));
            editMeterNumber.setText(selected_meterno);

            //selected_buildingNo = dataDB.myConnection(getApplicationContext()).selectFromTable("building_no","_buildings","building_name",selected_building);
        }
        spinnerAllBuildingList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_building_no = spinnerAllBuildingList.getSelectedItem().toString();
                selected_building_id = Integer.valueOf(dataDB.myConnection(getApplicationContext()).selectFromTable("building_id", "_buildings", "building_no", selected_building_no));
                selected_building_category_id = Integer.valueOf(dataDB.myConnection(getApplicationContext()).selectFromTable("building_category_id", "_buildings", "building_no", selected_building_no));
                selected_meterno = dataDB.myConnection(getApplicationContext()).selectFromTable("meter_no", "meters", "building_id", String.valueOf(selected_building_id));
                editMeterNumber.setText(selected_meterno);
                selected_buildingName = dataDB.myConnection(getApplicationContext()).selectFromTable("building_name", "_buildings", "building_no", selected_building_no);
                selected_building_category_name = dataDB.myConnection(getApplicationContext()).selectFromTable("building_category", "_building_categories", "building_category_id", String.valueOf(selected_building_category_id));

                Log.e("SelectedBuildingID", String.valueOf(selected_building_id));
                Log.e("SelectedBuilding", selected_building_no);
                Log.e("SelectedBuildingName", selected_buildingName);
                Log.e("SelectedBuildingCategory", String.valueOf(selected_building_category_id));
                Log.e("SelectedBuildingCategoryName", selected_building_category_name);
                Log.e("MeterNo", String.valueOf(selected_meterno));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

}
