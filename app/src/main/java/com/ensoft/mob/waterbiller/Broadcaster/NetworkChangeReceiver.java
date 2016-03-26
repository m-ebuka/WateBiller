package com.ensoft.mob.waterbiller.Broadcaster;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.ensoft.mob.waterbiller.helpers.GenericHelper;

/**
 * Created by Ebuka on 31/08/2015.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //check if device is connected to the internet
        if(GenericHelper.isOnline(context))
        {
            Toast.makeText(context, "Internet Connectivity is available", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(context, "Device is offline at the moment", Toast.LENGTH_SHORT).show();
        }
    }
}
