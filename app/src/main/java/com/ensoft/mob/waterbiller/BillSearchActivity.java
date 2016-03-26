package com.ensoft.mob.waterbiller;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class BillSearchActivity extends AppCompatActivity {
    MyApp app;
    private static final String TAG = "Bill Search";
    Button btnSearchBill;
    EditText txtAccountNo;
    EditText txtMeterNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_search);

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
        btnSearchBill = (Button)findViewById(R.id.btnBillSearch);
        txtAccountNo = (EditText)findViewById(R.id.editTextAccountNo);
        txtMeterNo = (EditText)findViewById(R.id.editTextMeterNo);

        //button Action
        btnSearchBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set search parameters
                app.setAccountNoSearchCriteria(txtAccountNo.getText().toString());
                app.setMeterNoSearchCrieria(txtMeterNo.getText().toString());

                //navigate to the Bill List Activity
                startActivity(new Intent(getApplicationContext(), BillListActivity.class));
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bill_search, menu);
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
