package com.ensoft.mob.waterbiller;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.ensoft.mob.waterbiller.Adapters.CustomGridViewAdapter;
import com.ensoft.mob.waterbiller.Broadcaster.MyBroadcastReceiver;
import com.ensoft.mob.waterbiller.DB.DataDB;
import com.ensoft.mob.waterbiller.Locations.GPSTracker;
import com.ensoft.mob.waterbiller.Models.currentUserDetail;
import com.ensoft.mob.waterbiller.WebServices.DataAPI;

import java.util.ArrayList;
import java.util.Calendar;


public class MainActivity extends ActionBarActivity {
    private static final String TAG = "MainActivity";
    MyApp app;
    DataDB data = new DataDB();
    DataAPI d_api = new DataAPI();
    TextView t;

    private TextView mTextView;
    //First We Declare Titles And Icons For Our Navigation Drawer List View
    //This Icons And Titles Are holded in an Array as you can see

    String TITLES[] = {"About Taxo Water Biller", "Notification", "Settings"};
    int ICONS[] = {R.drawable.ic_about_dark, R.drawable.ic_notification_dark, R.drawable.ic_settings_dark};

    //Similarly we Create a String Resource for the name and email in the header view
    //And we also create a int resource for profile picture in the header view

    String NAME = "Chukwuebuka Ngene";
    String EMAIL = "ebukaprof@android4devs.com";
    int PROFILE = R.drawable.logo;

    private Toolbar toolbar;                              // Declaring the Toolbar Object
    //private Spinner spinnerHospitalType;

    RecyclerView mRecyclerView;                           // Declaring RecyclerView
    RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View
    RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
    DrawerLayout Drawer;                                  // Declaring DrawerLayout

    ActionBarDrawerToggle mDrawerToggle;

    //Grid Layout
    GridView gridView;
    ArrayList<Item> gridArray = new ArrayList<Item>();
    CustomGridViewAdapter customGridAdapter;

    /*Spinner items*/
    /*public void addItemsToSpinnerHospitalType()
    {
        spinnerHospitalType = (Spinner) findViewById(R.id.spinnerHospital);
        List list = new ArrayList();
        list.add("Private");
        list.add("Public");
        list.add("Commercial");
        ArrayAdapter dataAdapter = new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,list);
        spinnerHospitalType.setAdapter(dataAdapter);
    }*/
    /*End spinner items*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        //set user detail from Global variable
        app = ((MyApp)getApplicationContext());
        t = (TextView)findViewById(R.id.hello_man);
        Log.i("Logon details", app.getFirstname() + " " + app.getLastname());
        t.setText("Hello " + app.getFirstname() + " " + app.getLastname());
        if(app.getUserid() == null)
        {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }

        //set grid view item
        //selectFromTable(String what, String _from, String _whereColumn, String _whereValue)

        Bitmap createProfileIcon = BitmapFactory.decodeResource(this.getResources(),R.drawable.ic_user);
        Bitmap viewProfileIcon = BitmapFactory.decodeResource(this.getResources(),R.drawable.ic_user);
        Bitmap enumerationIcon = BitmapFactory.decodeResource(this.getResources(),R.drawable.ic_factory);
        Bitmap existingMeterIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_meter_big);
        Bitmap billRecordIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_record_big);
        Bitmap makePaymentIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_report_big);
        Bitmap paymentReportIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_report_big);
        Bitmap iReportIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_notification_big);
        Bitmap iReportViewIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_notification_big);
        Bitmap notificationIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_notification_big);
        Bitmap taskIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_task_big);
        Bitmap createTaskIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_task_big);
        Bitmap guideIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_guide_big);
        Bitmap aboutIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_about_big);
        Bitmap settingsIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_settings_big);

        Log.e(TAG, "Userid: " + app.getUserid());

        String access_user_id = data.myConnection(getApplicationContext()).selectColumnFromTableWithLimit("user_id","access_control",1);
        Log.e(TAG, "Checkout Userid from access_control : " + access_user_id);
        if(Integer.valueOf(access_user_id) == Integer.valueOf(app.getUserid())){
            Log.e(TAG, "Checkout " + data.myConnection(getApplicationContext()).selectFromTable("can_create_customer_profile","access_control","user_id",app.getUserid()));

            if(Integer.valueOf(data.myConnection(getApplicationContext()).selectFromTable("can_create_customer_profile","access_control","user_id",app.getUserid())) == 1) {
                gridArray.add(new Item(createProfileIcon, "New Customer"));
            }
            Log.e(TAG, "Checkout can_do_enumeration " + data.myConnection(getApplicationContext()).selectFromTable("can_do_enumeration","access_control","user_id",app.getUserid()));

            if(Integer.valueOf(data.myConnection(getApplicationContext()).selectFromTable("can_do_enumeration","access_control","user_id",app.getUserid())) == 1) {
                gridArray.add(new Item(enumerationIcon, "Enumeration"));
            }
            Log.e(TAG, "Checkout can_do_meter_reading " + data.myConnection(getApplicationContext()).selectFromTable("can_do_meter_reading","access_control","user_id",app.getUserid()));

            if(Integer.valueOf(data.myConnection(getApplicationContext()).selectFromTable("can_do_meter_reading","access_control","user_id",app.getUserid())) == 1) {
                gridArray.add(new Item(existingMeterIcon, "Meter Reading"));
            }
            Log.e(TAG, "Checkout can_view_bill " + data.myConnection(getApplicationContext()).selectFromTable("can_view_bill","access_control","user_id",app.getUserid()));

            if(Integer.valueOf(data.myConnection(getApplicationContext()).selectFromTable("can_view_bill","access_control","user_id",app.getUserid())) == 1) {
                gridArray.add(new Item(billRecordIcon, "Bill Record"));
            }
            Log.e(TAG, "Checkout can_make_payment " + data.myConnection(getApplicationContext()).selectFromTable("can_make_payment","access_control","user_id",app.getUserid()));

            if(Integer.valueOf(data.myConnection(getApplicationContext()).selectFromTable("can_make_payment","access_control","user_id",app.getUserid())) == 1) {
                gridArray.add(new Item(makePaymentIcon, "Make Payment"));
            }
            Log.e(TAG, "Checkout can_view_payment_report " + data.myConnection(getApplicationContext()).selectFromTable("can_view_payment_report","access_control","user_id",app.getUserid()));

            if(Integer.valueOf(data.myConnection(getApplicationContext()).selectFromTable("can_view_payment_report","access_control","user_id",app.getUserid())) == 1) {
                gridArray.add(new Item(paymentReportIcon, "Pay Report"));
            }
            Log.e(TAG, "Checkout can_do_ireport " + data.myConnection(getApplicationContext()).selectFromTable("can_do_ireport","access_control","user_id",app.getUserid()));

            if(Integer.valueOf(data.myConnection(getApplicationContext()).selectFromTable("can_do_ireport","access_control","user_id",app.getUserid())) == 1) {
                gridArray.add(new Item(iReportIcon, "iReport"));
            }
            Log.e(TAG, "Checkout can_view_ireport " + data.myConnection(getApplicationContext()).selectFromTable("can_view_ireport","access_control","user_id",app.getUserid()));

            if(Integer.valueOf(data.myConnection(getApplicationContext()).selectFromTable("can_view_ireport","access_control","user_id",app.getUserid())) == 1) {
                gridArray.add(new Item(iReportViewIcon, "iReport View"));
            }
            Log.e(TAG, "Checkout can_view_notification " + data.myConnection(getApplicationContext()).selectFromTable("can_view_notification","access_control","user_id",app.getUserid()));

            if(Integer.valueOf(data.myConnection(getApplicationContext()).selectFromTable("can_view_notification","access_control","user_id",app.getUserid())) == 1) {
                gridArray.add(new Item(notificationIcon, "Notification"));
            }
            Log.e(TAG, "Checkout can_create_assign_task " + data.myConnection(getApplicationContext()).selectFromTable("can_create_assign_task","access_control","user_id",app.getUserid()));

            if(Integer.valueOf(data.myConnection(getApplicationContext()).selectFromTable("can_create_assign_task","access_control","user_id",app.getUserid())) == 1) {
                gridArray.add(new Item(createTaskIcon, "Assign Tasks"));
            }
            Log.e(TAG, "Checkout can_view_task " + data.myConnection(getApplicationContext()).selectFromTable("can_view_task","access_control","user_id",app.getUserid()));

            if(Integer.valueOf(data.myConnection(getApplicationContext()).selectFromTable("can_view_task","access_control","user_id",app.getUserid())) == 1) {
                gridArray.add(new Item(taskIcon, "View Tasks"));
            }
        }

        //gridArray.add(new Item(guideIcon,"Guide"));
        gridArray.add(new Item(aboutIcon,"About"));
        //gridArray.add(new Item(settingsIcon,"Settings"));



        gridView = (GridView) findViewById(R.id.gridView1);
        customGridAdapter = new CustomGridViewAdapter(this, R.layout.row_grid, gridArray);
        gridView.setAdapter(customGridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Toast.makeText(MainActivity.this, "You Clicked at " + ((TextView) v.findViewById(R.id.item_text)).getText(), Toast.LENGTH_SHORT).show();
                String clickItem = ((TextView) v.findViewById(R.id.item_text)).getText().toString();
                switch(clickItem){
                    case "New Customer":
                        startActivity(new Intent(getApplicationContext(), ProfileExistActivity.class));
                        break;
                    case "iReport":
                        startActivity(new Intent(getApplicationContext(), iReportActivity.class));
                        break;
                    case "About":
                        startActivity(new Intent(getApplicationContext(), AboutActivity.class));
                        break;
                    case "Enumeration":
                        //MainActivity.this.finish();
                        startActivity(new Intent(getApplicationContext(), StreetListActivity.class));
                        break;
                    case "Meter Reading":
                        startActivity(new Intent(getApplicationContext(), PreBillingActivity.class));
                        break;
                    case "Bill Record":
                        //startActivity(new Intent(getApplicationContext(), BillSearchActivity.class));
                        break;
                    case "Pay Report":
                        //startActivity(new Intent(getApplicationContext(), BillSearchActivity.class));
                        break;

                    default:
                        return;
                }
            }
        });

        // BroadCase Receiver Intent Object
        Intent alarmIntent = new Intent(getApplicationContext(), MyBroadcastReceiver.class);
        // Pending Intent Object
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Alarm Manager Object
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        // Alarm Manager calls BroadCast for every Ten seconds (10 * 1000), BroadCase further calls service to check if new records are inserted in
        // Remote MySQL DB
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + 5000, 10 * 1000000, pendingIntent);


        /*gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(
                        getApplicationContext(),
                        "You Clicked at " + ((TextView) view.findViewById(R.id.item_text))
                                .getText(), Toast.LENGTH_SHORT).show();

            }
        });*/
        /*end gridview*/

        //for GPS Location


        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView); // Assigning the RecyclerView Object to the xml View

        mRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size

        mAdapter = new MyAdapter(TITLES,ICONS,NAME,EMAIL,PROFILE,this);       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        // And passing the titles,icons,header view name, header view email,
        // and header view profile picture

        mRecyclerView.setAdapter(mAdapter);                              // Setting the adapter to RecyclerView

        mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager

        mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager


        Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);        // Drawer object Assigned to the view
        mDrawerToggle = new ActionBarDrawerToggle(this,Drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

        };
        Drawer.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
        mDrawerToggle.syncState();               // Finally we set the drawer toggle sync State


        //addItemsToSpinnerHospitalType();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
                Intent intent1 = new Intent(this, MySettingsActivity.class);
                this.startActivity(intent1);
                break;
            case R.id.action_about:
                //Toast.makeText(this,"The Item Clicked is: "+ id,Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(this, AboutActivity.class);
                this.startActivity(intent2);
                break;
            case R.id.action_logout:
                Intent intent3 = new Intent(this, LoginActivity.class);
                this.startActivity(intent3);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_bar) {//.action_settings

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
