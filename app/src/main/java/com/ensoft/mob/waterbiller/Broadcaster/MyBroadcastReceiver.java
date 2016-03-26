package com.ensoft.mob.waterbiller.Broadcaster;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.widget.Toast;

import com.ensoft.mob.waterbiller.Services.SyncServices;
import com.ensoft.mob.waterbiller.helpers.GenericHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ebuka on 31/08/2015.
 */
public class MyBroadcastReceiver extends BroadcastReceiver {
    /**
     * This method is called when the BroadcastReceiver is receiving an Intent
     * broadcast.  During this time you can use the other methods on
     * BroadcastReceiver to view/modify the current result values.  This method
     * is always called within the main thread of its process, unless you
     * explicitly asked for it to be scheduled on a different thread using
     * {@link Context#registerReceiver(BroadcastReceiver,
     * IntentFilter, String, Handler)}. When it runs on the main
     * thread you should
     * never perform long-running operations in it (there is a timeout of
     * 10 seconds that the system allows before considering the receiver to
     * be blocked and a candidate to be killed). You cannot launch a popup dialog
     * in your implementation of onReceive().
     * <p/>
     * <p><b>If this BroadcastReceiver was launched through a &lt;receiver&gt; tag,
     * then the object is no longer alive after returning from this
     * function.</b>  This means you should not perform any operations that
     * return a result to you asynchronously -- in particular, for interacting
     * with services, you should use
     * {@link Context#startService(Intent)} instead of
     * {@link Context#bindService(Intent, ServiceConnection, int)}.  If you wish
     * to interact with a service that is already running, you can use
     * {@link #peekService}.
     * <p/>
     * <p>The Intent filters used in {@link Context#registerReceiver}
     * and in application manifests are <em>not</em> guaranteed to be exclusive. They
     * are hints to the operating system about how to find suitable recipients. It is
     * possible for senders to force delivery to specific recipients, bypassing filter
     * resolution.  For this reason, {@link #onReceive(Context, Intent) onReceive()}
     * implementations should respond only to known actions, ignoring any unexpected
     * Intents that they may receive.
     *
     * @param context The Context in which the receiver is running.
     * @param intent  The Intent being received.
     */
    static int kounter = 0;
    @Override
    public void onReceive(final Context context, Intent intent) {
        kounter++;
        Toast.makeText(context, "Broadcast Service Running for " + kounter + " times", Toast.LENGTH_SHORT).show();
        //Instantiate the http client
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        //check if device is connected to the internet
        if(GenericHelper.isOnline(context))
        {
            Toast.makeText(context, "Internet Connectivity is available", Toast.LENGTH_SHORT).show();
            final Intent intnt = new Intent(context, SyncServices.class);
            intnt.putExtra("intntdata", "Connected ");
            context.startService(intnt);

            //call for sync.
            /*client.post("", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    System.out.println(responseBody.toString());
                    // Create JSON object out of the response sent by webservice
                    //JSONObject obj = new JSONObject(responseBody);
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    if(statusCode == 404){
                        Toast.makeText(context, "404", Toast.LENGTH_SHORT).show();
                    }else if(statusCode == 500){
                        Toast.makeText(context, "500", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(context, "Error occured!", Toast.LENGTH_SHORT).show();
                    }
                }
            });*/
        }
        else{
            Toast.makeText(context, "Device is offline at the moment", Toast.LENGTH_SHORT).show();
        }
    }

}
