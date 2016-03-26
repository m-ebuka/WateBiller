package com.ensoft.mob.waterbiller;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class BillingConfirmActivity extends AppCompatActivity {

    MyApp app;
    byte FONT_TYPE;
    private static BluetoothSocket btsocket;
    private static OutputStream btoutputstream;
    Button reading_confirm_button;
    EditText sconsumerName;
    EditText shouseNumber;
    EditText smeterNumber;
    EditText spreviousReading;
    EditText scurrentReading;
    EditText sunitUsed;
    EditText stotalAmount;

    /*protected void PrefillData()
    {
        String ti = app.getActiveBuilding();
        if(ti == 1)
        {
           // scurrentReading.setText(Integer.toString(app.getCurrentBillValue()));
            scurrentReading.setText(Float.toString(app.getCurrentBillValue()));
            sconsumerName.setText("Udom Eket Obasi");
            smeterNumber.setText("37352678");
            shouseNumber.setText("No 5");
            spreviousReading.setText("408");
            int prev = Integer.parseInt(spreviousReading.getText().toString());
            double solut = app.getCurrentBillValue() - prev;
            double solutAmount = solut * 150;
            sunitUsed.setText(String.valueOf(solut));
            stotalAmount.setText(String.valueOf(solutAmount));
        }
        else if(ti == 2)
        {
            //scurrentReading.setText(Integer.toString(app.getCurrentBillValue()));
            scurrentReading.setText(Float.toString(app.getCurrentBillValue()));
            sconsumerName.setText("John Boe Eket");
            smeterNumber.setText("000932678");
            shouseNumber.setText("CH08");
            spreviousReading.setText("750");
            int prev = Integer.parseInt(spreviousReading.getText().toString());
            double solut = app.getCurrentBillValue() - prev;
            double solutAmount = solut * 150;
            sunitUsed.setText(String.valueOf(solut));
            stotalAmount.setText(String.valueOf(solutAmount));
        }
        else
        {
           // scurrentReading.setText(Integer.toString(app.getCurrentBillValue()));
            scurrentReading.setText(Float.toString(app.getCurrentBillValue()));
            sconsumerName.setText("Chief Lukas Debaptist");
            smeterNumber.setText("870039996");
            shouseNumber.setText("CH013");
            spreviousReading.setText("350");
            int prev = Integer.parseInt(spreviousReading.getText().toString());
            double solut = app.getCurrentBillValue() - prev;
            double solutAmount = solut * 150;
            sunitUsed.setText(String.valueOf(solut));
            stotalAmount.setText(String.valueOf(solutAmount));
        }
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing_confirm);
        app = ((MyApp)getApplication());

        //actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_action_back);
        actionBar.setTitle("Aqua Biller");
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#5D617A"));
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setTitle(Html.fromHtml("<font color='#ffffff' fontSize='10'>AquaBiller : Billing Confirmation</font>"));

        reading_confirm_button = (Button)findViewById(R.id.reading_confirm_button);
        sconsumerName = (EditText)findViewById(R.id.editConsumerName);
        shouseNumber = (EditText)findViewById(R.id.editHouseNumber);
        smeterNumber = (EditText)findViewById(R.id.editMeterNumber);
        spreviousReading = (EditText)findViewById(R.id.editPreviousReading);
        scurrentReading = (EditText)findViewById(R.id.editCurrentReading);
        sunitUsed = (EditText)findViewById(R.id.editUnitUsed);
        stotalAmount = (EditText)findViewById(R.id.editAmount);

        sconsumerName.setEnabled(false);
        shouseNumber.setEnabled(false);
        smeterNumber.setEnabled(false);
        spreviousReading.setEnabled(false);
        scurrentReading.setEnabled(false);
        sunitUsed.setEnabled(false);
        stotalAmount.setEnabled(false);

        //PrefillData();

        reading_confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connect();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_billing_confirm, menu);
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
        if(id == R.id.action_logout)
        {
            Intent intent3 = new Intent(this, LoginActivity.class);
            this.startActivity(intent3);
        }

        return super.onOptionsItemSelected(item);
    }

    protected void connect() {
        if(btsocket == null){
            Intent BTIntent = new Intent(getApplicationContext(), BTDeviceList.class);
            this.startActivityForResult(BTIntent, BTDeviceList.REQUEST_CONNECT_BT);
        }
        else{

            OutputStream opstream = null;
            try {
                opstream = btsocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            btoutputstream = opstream;
            print_bt();

        }

    }


    private void print_bt() {
        try {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            btoutputstream = btsocket.getOutputStream();

            byte[] printformat = { 0x1B, 0x21, FONT_TYPE };
            btoutputstream.write(printformat);
            //get logo
            Drawable logo= getResources().getDrawable(R.drawable.logo);
            Bitmap bitmap = ((BitmapDrawable) logo).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] bitmapdata = stream.toByteArray();
            //convert logo to byte
            //btoutputstream.write(bitmapdata.toString().getBytes());
            String msg_header = "===========CRSWBL==========="; //message.getText().toString();
            String dot1 = "********************";
            String myName = "   Consumer Name: " + sconsumerName.getText().toString();
            String myPhone = "   Consumer Phone:   ";
            String meterNo = "Meter Number: " + smeterNumber.getText().toString();
            String prevReading = "Previous Reading: " + spreviousReading.getText().toString();
            String currReading = "Current Reading: " + scurrentReading.getText().toString();
            String unitUsed = "Unit Used:  " + sunitUsed.getText().toString();
            String currTariff = "Current Tariff:  N150";
            String currCharge = "Current Charge: " + stotalAmount.getText().toString();
            String arrearsCharge = "Arrears:   *************";
            String totalCharge = "Total Amount: " + stotalAmount.getText().toString();
            String msg_footer = "******Thank you********";
            btoutputstream.write(msg_header.getBytes());
            btoutputstream.write(dot1.getBytes());
            btoutputstream.write(0x0D);
            btoutputstream.write(myName.getBytes());
            btoutputstream.write(myPhone.getBytes());
            btoutputstream.write(meterNo.getBytes());
            btoutputstream.write(prevReading.getBytes());
            btoutputstream.write(currReading.getBytes());
            btoutputstream.write(0x0D);
            btoutputstream.write(unitUsed.getBytes());
            btoutputstream.write(currTariff.getBytes());
            btoutputstream.write(currCharge.getBytes());
            btoutputstream.write(arrearsCharge.getBytes());
            btoutputstream.write(totalCharge.getBytes());
            btoutputstream.write(msg_footer.getBytes());
            btoutputstream.write(0x0D);
            btoutputstream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if(btsocket!= null){
                btoutputstream.close();
                btsocket.close();
                btsocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            btsocket = BTDeviceList.getSocket();
            if(btsocket != null){
                print_bt();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
