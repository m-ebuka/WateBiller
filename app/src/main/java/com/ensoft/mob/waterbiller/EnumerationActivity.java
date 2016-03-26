package com.ensoft.mob.waterbiller;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class EnumerationActivity extends AppCompatActivity {
    private static final String TAG = "Enumeration";
    private Camera camera;
    private int cameraId = 0;
    private Boolean imgIsSet;
    Intent i;
    Bitmap bmp;
    ImageView imgView;
    EditText tMeterNumber;
    EditText tStreetNumber;
    Button enumButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enumeration);


        imgView = (ImageView) findViewById(R.id.imagePhoto);
        enumButton = (Button) findViewById(R.id.enumeration_button);
        tMeterNumber = (EditText) findViewById(R.id.txt_meter_number);
        tStreetNumber = (EditText) findViewById(R.id.street_number);

        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i, cameraId);
            }
        });
        enumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!imgIsSet)
                {
                    Toast.makeText(getApplicationContext(),
                            "Error: Building is not yet captured", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(tMeterNumber.getText().toString()))
                {
                    tMeterNumber.setError("Meter number can not be empty");
                    return;
                }
                if(TextUtils.isEmpty(tStreetNumber.getText().toString()))
                {
                    tStreetNumber.setError("Street number can not be empty");
                    return;
                }
                Toast.makeText(getApplicationContext(),
                        "Successful Entry", Toast.LENGTH_SHORT).show();
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode == RESULT_OK)
        {
            Bundle extras = data.getExtras();
            bmp = (Bitmap) extras.get("data");
            imgView.setImageBitmap(bmp);
            imgIsSet = true;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_enumeration, menu);
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
