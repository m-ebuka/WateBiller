package com.ensoft.mob.waterbiller;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.ensoft.mob.waterbiller.Adapters.BaseBuildingAdapter;
import com.ensoft.mob.waterbiller.Adapters.BaseStreetAdapter;
import com.ensoft.mob.waterbiller.Adapters.BuildingAdapter;
import com.ensoft.mob.waterbiller.Adapters.StreetDBAdapter;
import com.ensoft.mob.waterbiller.DB.DataDB;
import com.ensoft.mob.waterbiller.Models.Building;
import com.ensoft.mob.waterbiller.Models.Street;
import com.ensoft.mob.waterbiller.helpers.GenericHelper;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

public class BuildingListActivity extends AppCompatActivity {
    public final static String METER_NO = null;
    public final static String BUILDING_NUMBER = null;
    public final static String BUILDING_TITLE = null;
    public final static String BUILDING_CATEGORY = null;
    public final static byte[] BUILDING_BYTE_IMAGE = null;

    private BuildingAdapter buildingDBAdapter;
    private StreetDBAdapter streetDBAdapter;
    private SimpleCursorAdapter dataAdapter;
    private Spinner spinnerStreets;
    private ArrayList<String> my_array;
    private TableLayout tl;
    private TableRow tr;
    private TextView houseNo,meterNo,accountNo;
    MyApp app;

    DataDB dataDB = new DataDB();
    ListView buildingListView;
    ArrayList<Building> myList = new ArrayList<Building>();
    ProgressDialog mProgressDialog;

    private int start = 0;
    private int limit = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_list);

        //buildingListView = (ListView) findViewById(R.id.listView_Building);
        buildingDBAdapter = new BuildingAdapter();
        streetDBAdapter = new StreetDBAdapter();

        app = ((MyApp)getApplication());
        try{
            if(app.getActiveAreaCode() >0 && app.getActiveStreet() > 0)
            {
                Log.e("The set Areacode and Street of BuildingList",Integer.toString(app.getActiveAreaCode()) + Integer.toString(app.getActiveStreet()));
                Log.e("The set AreacodeName and StreetName of BuildingList",app.getActiveAreaName() + app.getActiveStreetName());
            }
        }
        catch(Exception e)
        {

        }
        //actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_action_back);
        actionBar.setTitle("Aqua Biller");
        actionBar.setLogo(R.drawable.logo);
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#5D617A"));
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setTitle(Html.fromHtml("<font color='#ffffff'>AquaBiller : Building List</font>"));



        // TODO: 09/10/2015 load listview
        // Execute RemoteDataTask AsyncTask
        new RemoteDataTask().execute();

        // TODO: 09/10/2015 listen to load more event
        /*btnLoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // Starting a new async task
                new RemoteDataTask().execute();
            }
        });*/

        populateSpinnerStreet(); //populate spinnerStreet

        //populateBuildingListView(null, Integer.toString(app.getActiveStreet())); //populate the listView



       // Toast.makeText(BuildingListActivity.this, Integer.toString(app.getActiveStreet()), Toast.LENGTH_SHORT).show();



        final ImageButton fab1 = (ImageButton) findViewById(R.id.button_add_meter);
        final ImageButton fab2 = (ImageButton)findViewById(R.id.action_add_building);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab1.setSelected(!fab1.isSelected());
                //fab.setImageResource(fab.isSelected() ? R.drawable.animated_plus : R.drawable.animated_minus);
                Drawable drawable = fab1.getDrawable();
                if (drawable instanceof Animatable) {
                    ((Animatable) drawable).start();
                }
                Toast.makeText(getApplicationContext(),"Add meter to building", Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(getApplicationContext(), EnumerationActivity.class));

            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab2.setSelected(!fab2.isSelected());
                //fab.setImageResource(fab.isSelected() ? R.drawable.animated_plus : R.drawable.animated_minus);
                Drawable drawable = fab2.getDrawable();
                if (drawable instanceof Animatable) {
                    ((Animatable) drawable).start();
                }
                app.setCameraByte(null);
                Intent in = new Intent(getApplicationContext(),BuildingEnumerationActivity.class);
                startActivity(in);

            }
        });
    }

    private void populateSpinnerStreet()
    {
        spinnerStreets = (Spinner) findViewById(R.id.spinnerAllActiveStreets);
        my_array = new ArrayList<String>();
        //get all the streets by areacode passed from StreetListActivity

        my_array = streetDBAdapter.getAllStreetByUseridAndAreacode(getApplicationContext(), app.getActiveAreaCode());
        ArrayAdapter dataAdapter = new ArrayAdapter(this,R.layout.custom_spinner_item,my_array);
        dataAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerStreets.setAdapter(dataAdapter);

        spinnerStreets.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String streetname = spinnerStreets.getSelectedItem().toString();
                int idstreet = streetDBAdapter.fetchStreetByName(getApplicationContext(), streetname);
                //app.setActiveStreet(idstreet);
                //app.setActiveStreetName(streetname);

                //populateBuildingListView(null, Integer.toString(app.getActiveStreet()), start, limit);
                //populateBuildingListView(null, Integer.toString(idstreet));
                Log.e("spinnerStreets.setOnItemSelectedListener", " Street Name" + streetname + "Street id:" + Integer.toString(idstreet));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void populateBuildingListView(String inputText,String activeStreet,int start, int limit)
    {


        Cursor cursor = buildingDBAdapter.fetchAllBuilding(getApplicationContext(), activeStreet, inputText, start, limit);
        Log.i("buildingListActivity", "cursor count in BuildingList; " + cursor.getCount());
        if(cursor.moveToFirst()) {
            do {
                //Log.i("buildingListActivity", "cursor string; " + cursor.getString(2));
                Building building = new Building();
                building.setBuilding_no(cursor.getString(2));
                building.setBuilding_id(cursor.getString(12));
                String acctNumber = dataDB.myConnection(getApplicationContext()).selectFromTable("account_number","account_numbers","building_id",cursor.getString(12).toString());
                System.out.println("Account Number::"+acctNumber);
                building.setAccount_no(acctNumber);
                System.out.println("Building id::" + cursor.getString(12));
                String meterNo = dataDB.myConnection(getApplicationContext()).selectFromTable("meter_no","meters","building_id",cursor.getString(12).toString());
                System.out.println("Meter Number::"+meterNo);
                building.setMeter_no(meterNo);
                building.setBuilding_name(cursor.getString(1));
                building.setBuilding_category(cursor.getString(4));
                building.setLatitude(cursor.getString(10));
                building.setLongitude(cursor.getString(11));

                if(cursor.getString(3) != null && !cursor.getString(3).isEmpty()) {
                    Log.e("ImageByte Gotten", "I got building image byte " + cursor.getString(3) + "data:" + cursor.getString(3));

                    building.setBuilding_image(cursor.getString(3));
                }
                else{
                    Log.e("ImageByte Gotten","No building image byte gotten");
                }

                myList.add(building);
            } while (cursor.moveToNext());
        }



    }

    private void forPostExecute()
    {

    }

    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(BuildingListActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle("AquaBiller Load Buildings");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            //populateSpinnerStreet();
            populateBuildingListView(null, Integer.toString(app.getActiveStreet()), start, limit);
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {

            //forPostExecute();
            buildingListView = (ListView) findViewById(R.id.listView_Building);
            //Create a button for load More
            Button btnLoadMore = new Button(BuildingListActivity.this);
            btnLoadMore.setText("Load More");
            //Adding loadmore to building listview at footer
            buildingListView.addFooterView(btnLoadMore);

            if(myList.size() > 0) {
                buildingListView.setAdapter(new BaseBuildingAdapter(BuildingListActivity.this, myList));
            }
            // Close the progressdialog
            mProgressDialog.dismiss();

            buildingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    final Building clickedBuilding = (Building) adapterView.getItemAtPosition(i);
                    app.setActiveBuildingNo(clickedBuilding.getBuilding_no());
                    app.setActiveBuilding(clickedBuilding.getBuilding_id());
                    app.setActiveBuildingName(clickedBuilding.getBuilding_name());
                    app.setActiveBuildingCategoryId(clickedBuilding.getBuilding_category());
                    //app.setActiveLongitude(clickedBuilding.getLongitude() != null ? clickedBuilding.getLongitude() : "");
                    //app.setActiveLatitude(clickedBuilding.getLatitude() != null ? clickedBuilding.getLongitude() : "");

                    System.out.println("ActiveBuilding1::" + clickedBuilding.getBuilding_id());
                    app.setActiveBuilding(clickedBuilding.getBuilding_id());
                    System.out.println("ActiveBuilding::" + app.getActiveBuilding());
                    final AlertDialog.Builder adb = new AlertDialog.Builder(
                            BuildingListActivity.this);
                    adb.setTitle("Choice of Operation!");
                    adb.setIcon(R.drawable.logo);
                    adb.setMessage("Please choose an action to perform");
                    adb.setPositiveButton("Read Meter", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (clickedBuilding.getMeter_no() != null && !clickedBuilding.getMeter_no().isEmpty()) {
                                app.setActiveMeterNo(clickedBuilding.getMeter_no());
                                app.setActiveBuilding(clickedBuilding.getBuilding_id());
                                app.setActiveBuildingName(clickedBuilding.getBuilding_name());
                                app.setActiveBuildingNo(clickedBuilding.getBuilding_no());
                                app.setActiveBuildingCategoryId(clickedBuilding.getBuilding_category());

                                Intent inK = new Intent(getApplicationContext(), BillingActivity.class);
                                inK.putExtra("meterno", clickedBuilding.getMeter_no());
                                BuildingListActivity.this.finish();
                                startActivity(inK);

                            } else {
                                AlertDialog.Builder adb = new AlertDialog.Builder(
                                        BuildingListActivity.this);
                                adb.setTitle("Info!");
                                adb.setIcon(R.drawable.logo);
                                adb.setMessage("Oops..No Meter set for the selected building. Do you want to add meter?");
                                adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(getApplicationContext(), MeterEnumerationActivity.class);
                                        BuildingListActivity.this.finish();
                                        startActivity(intent);
                                    }
                                });
                                adb.setNegativeButton("No", null);
                                adb.show();
                            }
                        }
                    });
                    //adb.setNeutralButton("Add Profile", null); //was cancel
                    /*adb.setNeutralButton("Add Profile", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getApplicationContext(), ProfileExistActivity.class);
                            BuildingListActivity.this.finish();
                            startActivity(intent);
                        }
                    });*/
                    adb.setNegativeButton("Add Meter", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getApplicationContext(), MeterEnumerationActivity.class);
                            BuildingListActivity.this.finish();
                            startActivity(intent);
                        }
                    });
                    adb.show();


                }
            });


            // TODO: 09/10/2015 listen to load more event
            btnLoadMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    // Starting a new async task
                    new LoadMoreDataTask().execute();
                }
            });

            // Create an OnScrollListener
            buildingListView.setOnScrollListener(new AbsListView.OnScrollListener() {

                @Override
                public void onScrollStateChanged(AbsListView view,
                                                 int scrollState) { // TODO Auto-generated method stub
                    int threshold = 1;
                    int count = buildingListView.getCount();

                    if (scrollState == SCROLL_STATE_IDLE) {
                        if (buildingListView.getLastVisiblePosition() >= count
                                - threshold) {
                            // Execute LoadMoreDataTask AsyncTask
                            new LoadMoreDataTask().execute();
                        }
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem,
                                     int visibleItemCount, int totalItemCount) {
                    // TODO Auto-generated method stub

                }

            });

        }
    }


    private class LoadMoreDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(BuildingListActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle("AquaBiller Load more Buildings");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading more...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }
        @Override
        protected Void doInBackground(Void... params) {
            //int startv=start+7;
            populateBuildingListView(null, Integer.toString(app.getActiveStreet()),start +=7, limit);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            // Locate listview last item
            int position = buildingListView.getLastVisiblePosition();
            // Pass the results into ListViewAdapter.java
            if(myList.size() > 0) {
                buildingListView.setAdapter(new BaseBuildingAdapter(BuildingListActivity.this, myList));
            }
            // Show the latest retrived results on the top
            buildingListView.setSelectionFromTop(position, 0);
            // Close the progressdialog
            mProgressDialog.dismiss();

            //forPostExecute();
            //buildingListView = (ListView) findViewById(R.id.listView_Building);

            /*if(myList.size() > 0) {
                buildingListView.setAdapter(new BaseBuildingAdapter(BuildingListActivity.this, myList));
            }*/
            // Close the progressdialog
            //mProgressDialog.dismiss();





        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_building_list, menu);
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
