package com.ensoft.mob.waterbiller;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.ensoft.mob.waterbiller.Adapters.BaseStreetAdapter;
import com.ensoft.mob.waterbiller.Adapters.CustomStreetListAdapter;
import com.ensoft.mob.waterbiller.Adapters.StreetDBAdapter;
import com.ensoft.mob.waterbiller.DB.DBAreaCodes;
import com.ensoft.mob.waterbiller.DB.DataDB;
import com.ensoft.mob.waterbiller.Models.Street;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StreetListActivity extends ActionBarActivity {
    MyApp app;
    DataDB dataDB = new DataDB();
    Context context = StreetListActivity.this;
    DBAreaCodes dbAreaCodes = new DBAreaCodes();
    private StreetDBAdapter streetDBAdapter;
    private SimpleCursorAdapter dataAdapter;
    private Spinner spinnerAreaCodes;
    private ArrayList<String> my_array;
    private TableLayout tl;
    private TableRow tr;
    private TextView label,streetName,totalAcct;
    TextView mtextView2;
    ListView listView;

    ArrayList<Street> myList;


    // declare the color generator and drawable builder
    private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
    private TextDrawable.IBuilder mDrawableBuilder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_street_list);
        app = ((MyApp)getApplication());

        mtextView2 = (TextView)findViewById(R.id.textView2);
        listView = (ListView) findViewById(R.id.listView_Street);

        streetDBAdapter = new StreetDBAdapter();





        //actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_action_back);
        actionBar.setTitle("Aqua Biller");
        actionBar.setLogo(R.drawable.logo);
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#5D617A"));
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setTitle(Html.fromHtml("<font color='#ffffff'>AquaBiller : Street List</font>"));

        //populate spinner areacodes
        populateSpinnerAreaCodes();

        /*if(app.getActiveAreaCode() >= 1) {
            populateMe(String.valueOf(app.getActiveAreaCode()));
        }*/



    }

    // THIS FUNCTION SHOWS DATA FROM THE DATABASE
    private void populateMe(String areacode_id) {
        myList = new ArrayList<Street>();
        String sql = "SELECT * FROM _streets WHERE area_code_id = " +"'"+areacode_id+"'" + " AND mark_delete = 0 GROUP BY street ORDER BY street_id ASC;";
        Log.e("query to select street ", sql);
        Cursor myc = dataDB.myConnection(context).selectAllFromTable(sql,true);
        //Cursor myc = dataDB.myConnection(context).selectAllFromTable("_streets","mark_delete","0"); //.selectAllFromTable("_streets");
        if(myc != null && myc.getCount()>0)
        {
            Log.e("myc","street cursor count;"+myc.getCount());
            if (myc.moveToFirst()) {
                do {
                    Street street = new Street();
                    String idstreet = myc.getString(0);
                    Log.e("idstreet found", myc.getString(0));
                    String streetname = myc.getString(1);
                    Log.e("streetname found", myc.getString(1));
                    String latitude = myc.getString(3);
                    Log.e("latitude found", myc.getString(3) != null && !myc.getString(3).isEmpty() ? myc.getString(3) : "");
                    String longitude = myc.getString(4);
                    Log.e("longitude found", myc.getString(4) != null && !myc.getString(4).isEmpty() ? myc.getString(4) : "");
                    String area_code_id = myc.getString(5);
                    Log.e("area_code_id found", myc.getString(5));
                    String street_id = myc.getString(6);
                    Log.e("street_id found", myc.getString(6));

                    street.setStreetid(myc.getString(6));
                    street.setStreetname(myc.getString(1));
                    int cc = streetDBAdapter.numberOfAccountsInStreet(context, myc.getString(6));
                    int bc = streetDBAdapter.numberOfBuildingsInStreet(context, myc.getString(6));
                    street.setTotal_account(Integer.toString(cc));
                    street.setTotal_building(Integer.toString(bc));
                    myList.add(street);
                } while (myc.moveToNext());
            }
        }


        listView.setAdapter(new BaseStreetAdapter(context, myList));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Street clickedStreet = (Street) adapterView.getItemAtPosition(i);
                app.setActiveStreet(Integer.parseInt(clickedStreet.getStreetid()));
                app.setActiveStreetName(clickedStreet.getStreetname());
                Log.e("listView.setOnItemClickListener:", "Selected STREETID:"
                        + clickedStreet.getStreetid() + " Street Name:" + clickedStreet.getStreetname());

                Intent inK = new Intent(getApplicationContext(), BuildingListActivity.class);
                startActivity(inK);
            }
        });

        /*Cursor cursor = streetDBAdapter.fetchAllStreetByUseridAndAreacode(getApplicationContext(), app.getActiveAreaCode());
        Log.i("streetListActivity", "cursor count in StreetList; " + cursor.getCount());

        if(cursor.moveToFirst()) {

            do {
                Log.i("streetListActivity", "cursor string; " + cursor.getString(1));
                Street street = new Street();
                street.setStreetid(cursor.getString(0));
                street.setStreetname(cursor.getString(1));
                int cc = streetDBAdapter.numberOfAccountsInStreet(context, cursor.getString(0));
                int bc = streetDBAdapter.numberOfBuildingsInStreet(context,cursor.getString(0));
                street.setTotal_account(Integer.toString(cc));
                street.setTotal_building(Integer.toString(bc));
                myList.add(street);
            } while (cursor.moveToNext());
        }*/

    }

    private void populateSpinnerAreaCodes()
    {
        spinnerAreaCodes = (Spinner) findViewById(R.id.spinnerAllActiveAreaCodes);

        my_array = new ArrayList<String>();
            my_array = dbAreaCodes.getAllAreaByUserid(getApplicationContext(),app.getUserid());

        ArrayAdapter dataAdapter = new ArrayAdapter(this,R.layout.custom_spinner_item,my_array);
        //ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,R.layout.custom_spinner_item,my_array);
        dataAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerAreaCodes.setAdapter(dataAdapter);
        if(!my_array.isEmpty())
        {
            mtextView2.setVisibility(View.GONE);
            String areaname = spinnerAreaCodes.getSelectedItem().toString();
            Log.i("AREACODENAME",areaname);
            int idareacode = dbAreaCodes.getAreaCodeByName(getApplicationContext(), areaname);
            Log.i("AREACODEID",String.valueOf(idareacode));
            app.setActiveAreaCode(idareacode);
            app.setActiveAreaName(areaname);
        }
        else{
            mtextView2.setVisibility(View.VISIBLE);
            mtextView2.setText("No area/street cluster assigned to you yet.");
        }


        spinnerAreaCodes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String areaname = spinnerAreaCodes.getSelectedItem().toString();
                Log.i("AREANAME: ", areaname);
                int idareacode = Integer.valueOf(dataDB.myConnection(context).selectFromTable("area_code_id","_areacodes","area_name",areaname)); //dbAreaCodes.getAreaCodeByName(getApplicationContext(), areaname);
                Log.i("AREA_CODE_ID: ", String.valueOf(idareacode));
                app.setActiveAreaCode(idareacode);
                app.setActiveAreaName(areaname);

                populateMe(String.valueOf(idareacode)); //populate streets
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_street_list, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(item.getItemId()){
            case R.id.action_settings:
                Intent intent1 = new Intent(this, SettingsActivity.class);
                this.startActivity(intent1);
                break;//action_refresh
            case R.id.action_refresh:
                Intent intent5 = new Intent(this, StreetListActivity.class);
                finish();
                this.startActivity(intent5);
                break;
            case R.id.action_about:
                //Toast.makeText(this,"The Item Clicked is: "+ id,Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(this, AboutActivity.class);
                this.startActivity(intent2);
                break;
            case R.id.action_changepassword:
                Intent intent4 = new Intent(this,CreatePasswordActivity.class);
                this.startActivity(intent4);
                break;
            case R.id.action_logout:
                app.clear(); //clear temp data
                Intent intent3 = new Intent(this, LoginActivity.class);
                this.startActivity(intent3);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }
}
