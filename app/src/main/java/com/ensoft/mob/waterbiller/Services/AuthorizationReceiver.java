package com.ensoft.mob.waterbiller.Services;

import android.content.BroadcastReceiver;
import com.ensoft.mob.waterbiller.Services.*;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.ensoft.mob.waterbiller.Services.AuthorizationService;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Ebuka on 06/09/2015.
 */
public class AuthorizationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Service Stops", "Ohhhhhhh");
        context.startService(new Intent(context, AuthorizationService.class));
    }

}
