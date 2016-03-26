package com.ensoft.mob.waterbiller;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.ensoft.mob.waterbiller.Adapters.BaseBillingAdapter;
import com.ensoft.mob.waterbiller.Adapters.BaseBuildingAdapter;
import com.ensoft.mob.waterbiller.DB.DataDB;
import com.ensoft.mob.waterbiller.Models.Bill;
import com.ensoft.mob.waterbiller.Models.Building;
import com.ensoft.mob.waterbiller.Models.Street;

import java.util.ArrayList;

public class BillListActivity extends AppCompatActivity {
    ProgressDialog mProgressDialog;

    private int start = 0;
    private int limit = 5;

    MyApp app;
    DataDB dataDB = new DataDB();
    private static final String TAG = "Bill List";
    ListView billListView;
    TextView norecordTextView;
    private BaseBillingAdapter billingAdapter;
    ArrayList<Bill> myList = new ArrayList<Bill>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_list);

        //instantiate myApp class
        app = ((MyApp) getApplication());

        //actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_action_back);
        actionBar.setTitle("Aqua Biller");
        actionBar.setLogo(R.drawable.logo);
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#5D617A"));
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setTitle(Html.fromHtml("<font color='#ffffff'>AquaBiller : Search Bill</font>"));

        //instantiate controls
        //billListView = (ListView)findViewById(R.id.listView_bill);
        norecordTextView = (TextView)findViewById(R.id.textView2);

        // TODO: 09/10/2015 load listview
        // Execute RemoteDataTask AsyncTask
        new RemoteDataTask().execute();

    }

    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(BillListActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle("AquaBiller Load Bills");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            PopulateBillList(app.getAccountNoSearchCriteria(), app.getMeterNoSearchCrieria(), start, limit);

            return null;
        }
        @Override
        protected void onPostExecute(Void result) {

            //forPostExecute();
            billListView = (ListView) findViewById(R.id.listView_bill);
            //Create a button for load More
            Button btnLoadMore = new Button(BillListActivity.this);
            btnLoadMore.setText("Load More");
            //Adding loadmore to building listview at footer
            billListView.addFooterView(btnLoadMore);

            if(myList.size() > 0) {
                billListView.setAdapter(new BaseBillingAdapter(BillListActivity.this, myList));

                //listView action
                billListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        final Bill clickedBill = (Bill) adapterView.getItemAtPosition(i);
                        //app.setActiveBuildingNo(clickedBill.getBuilding_no());
                        //app.setActiveBuilding(clickedBill.getBuilding_id());
                        //app.setActiveBuildingName(clickedBill.getBuilding_name());
                        //app.setActiveBuildingCategoryId(clickedBill.getBuilding_category());

                        //System.out.println("ActiveBuilding1::" + clickedBill.getBuilding_id());
                        //app.setActiveBuilding(clickedBill.getBuilding_id());
                        //System.out.println("ActiveBuilding::" + app.getActiveBuilding());
                        final AlertDialog.Builder adb = new AlertDialog.Builder(
                                BillListActivity.this);
                        adb.setTitle("Print Option!");
                        adb.setIcon(R.drawable.logo);
                        adb.setMessage("Do you really want to print this bill");
                        adb.setPositiveButton("Yes Print", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //send to printer
                            }
                        });

                        adb.setNegativeButton("Cancel", null);
                        adb.show();


                    }
                });
                norecordTextView.setVisibility(View.INVISIBLE);
            }
            else{
                norecordTextView.setVisibility(View.VISIBLE);
                norecordTextView.setText("No matching bill record found.");
            }
            // Close the progressdialog
            mProgressDialog.dismiss();



            // TODO: 09/10/2015 listen to load more event
            btnLoadMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    // Starting a new async task
                    new LoadMoreDataTask().execute();
                }
            });

            // Create an OnScrollListener
            billListView.setOnScrollListener(new AbsListView.OnScrollListener() {

                @Override
                public void onScrollStateChanged(AbsListView view,
                                                 int scrollState) { // TODO Auto-generated method stub
                    int threshold = 1;
                    int count = billListView.getCount();

                    if (scrollState == SCROLL_STATE_IDLE) {
                        if (billListView.getLastVisiblePosition() >= count
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
            mProgressDialog = new ProgressDialog(BillListActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle("AquaBiller Load more Bills");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading more...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            PopulateBillList(app.getAccountNoSearchCriteria(), app.getMeterNoSearchCrieria(), start += 5, limit);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            // Locate listview last item
            int position = billListView.getLastVisiblePosition();
            // Pass the results into ListViewAdapter.java
            if(myList.size() > 0) {
                billListView.setAdapter(new BaseBillingAdapter(BillListActivity.this, myList));
            }
            // Show the latest retrived results on the top
            billListView.setSelectionFromTop(position, 0);
            // Close the progressdialog
            mProgressDialog.dismiss();

        }

    }

    private void PopulateBillList(String _acctNo, String _meterNo, int start, int limit)
    {
        //meter_no,amountDue,previous_reading,current_reading,rate,total_units,building_id,previous_charge,area_code_id,reading_timestamp,billing_no,billing_period,due_date,date_previous_reading,date_current_reading,invoice_date
        String sql = "SELECT * FROM account_billing ORDER BY invoice_date DESC LIMIT " + start +","+ limit +";";
        Cursor cursor = dataDB.myConnection(getApplicationContext()).selectAllFromTable(sql, true);
        if(cursor.moveToFirst()) {
            Log.i("BillListActivity", "PopulateBillList got something into list " + cursor.getCount());
            do {
                Bill bill = new Bill();
                bill.setMeter_no(cursor.getString(2));
                Log.i("BillListActivity", "PopulateBillList got meter no into list " + cursor.getString(2));
                bill.setAmountdue(cursor.getFloat(14));
                Log.i("BillListActivity", "PopulateBillList got amount into list " + cursor.getString(14));
                //String acctNo = dataDB.myConnection(getApplicationContext()).selectFromTable("account_number", "account_numbers", "meter_no", cursor.getString(0).toString());
                bill.setAccount_no(cursor.getString(3));
                Log.i("BillListActivity", "PopulateBillList got account_no into list " + cursor.getString(3));
                bill.setPrevious_reading(cursor.getFloat(4));
                bill.setCurrent_reading(cursor.getFloat(5));
                bill.setRate(cursor.getFloat(13));
                Log.i("BillListActivity", "PopulateBillList got rate into list " + cursor.getString(13));
                bill.setTotalunit(cursor.getInt(12));
                bill.setBuilding_id(cursor.getString(16));
                Log.i("BillListActivity", "PopulateBillList got building_id into list " + cursor.getString(16));
                String building_no = dataDB.myConnection(getApplicationContext()).selectFromTable("building_no", "_buildings", "building_id", cursor.getString(16).toString());
                bill.setBuilding_no(building_no);
                Log.i("BillListActivity", "PopulateBillList got building_no into list " + building_no);
                String profile_id = dataDB.myConnection(getApplicationContext()).selectFromTable("profile_id", "account_numbers", "account_number", cursor.getString(3).toString());
                Log.i("BillListActivity", "PopulateBillList got profile_id into list " + profile_id);
                String customer_name = dataDB.myConnection(getApplicationContext()).selectFromTable("name", "profiles", "profile_id", profile_id);
                Log.i("BillListActivity", "PopulateBillList got customer name into list " + customer_name);
                String corporate_name = dataDB.myConnection(getApplicationContext()).selectFromTable("corporate_name", "profiles", "profile_id", profile_id);
                Log.i("BillListActivity", "PopulateBillList got corporate name into list " + corporate_name);
                // TODO: 11/12/2015 check if to use name or corporate_name
                bill.setCustomer_name(customer_name != null || customer_name != "" ? customer_name : corporate_name);
                if(customer_name != null || customer_name != "")
                {
                    bill.setCustomer_name(customer_name);
                }
                else if(corporate_name != null || corporate_name != "")
                {
                    bill.setCustomer_name(corporate_name);
                }
                else{
                    bill.setCustomer_name("none");
                }
                bill.setPrevious_charge(cursor.getFloat(19));
                bill.setAreacodeid(cursor.getString(18));
                bill.setReading_timestamp(cursor.getString(9));
                bill.setBilling_no(cursor.getString(21));
                Log.i("BillListActivity", "PopulateBillList got billing_no into list " + cursor.getString(21));
                bill.setBilling_period(cursor.getString(24));
                bill.setBill_duedate(cursor.getString(25));
                bill.setDate_previous_reading(cursor.getString(6));
                bill.setDate_current_reading(cursor.getString(7));
                bill.setInvoice_date(cursor.getString(23));

                myList.add(bill);
            } while (cursor.moveToNext());
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bill_list, menu);
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
