package com.ensoft.mob.waterbiller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AuthorizeSuccessActivity extends AppCompatActivity {
  TextView showSuccess;
    Button auth_ok;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorize_success);
        showSuccess = (TextView) findViewById(R.id.show_success_mssg);
        Intent intent = getIntent();

        String authStatus = intent.getStringExtra("authStatus");
        showSuccess.setText(authStatus.toString()+". Please wait for authorization");
        Toast.makeText(getApplicationContext(), authStatus , Toast.LENGTH_LONG).show();

        auth_ok = (Button) findViewById(R.id.authorization_ok);


        auth_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity( new Intent(getApplicationContext(), LoginActivity.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_authorize_success, menu);
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
