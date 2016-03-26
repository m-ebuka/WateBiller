package com.ensoft.mob.waterbiller.Services;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.ensoft.mob.waterbiller.CreatePasswordActivity;
import com.ensoft.mob.waterbiller.DB.AuthorizeUser;
import com.ensoft.mob.waterbiller.DB.DBCheckAuthorization;
import com.ensoft.mob.waterbiller.DB.DataDB;
import com.ensoft.mob.waterbiller.Devices.Devices;
import com.ensoft.mob.waterbiller.Devices.LocationMavnager;
import com.ensoft.mob.waterbiller.LoginActivity;
import com.ensoft.mob.waterbiller.MainActivity;
import com.ensoft.mob.waterbiller.Models.AuthorizationHttpResponse;
import com.ensoft.mob.waterbiller.Models.CoordinateModel;
import com.ensoft.mob.waterbiller.R;
import com.ensoft.mob.waterbiller.WebServices.HttpURLConnectionHelper;
import com.ensoft.mob.waterbiller.WebServices.InsertOrUpdateSync;
import com.ensoft.mob.waterbiller.WebServices.JSONParser;
import com.ensoft.mob.waterbiller.WebServices.PushAPI;
import com.ensoft.mob.waterbiller.WebServices.RequestOrPushSync;
import com.ensoft.mob.waterbiller.helpers.BuildHttpJsonResponse;
import com.ensoft.mob.waterbiller.helpers.GenericHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Ebuka on 06/09/2015.
 */
public class AuthorizationService extends Service {
    private static final String TAG = "AuthorizationService";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    // if GPS is enabled
    boolean isGPSEnabled = false;
    // if Network is enabled
    boolean isNetworkEnabled = false;
    String latitude = "Unknown",longitude="Unknown",email,service_code,client_name,service_id,authorization_code,authorized,isFirstSync,add_request_url,request_date,urlParameters;
    DBCheckAuthorization checkAuthorization = new DBCheckAuthorization();
    CoordinateModel coordinateModel = new CoordinateModel();
    DataDB dataDB = new DataDB();
    InsertOrUpdateSync firstInsertSyncData = new InsertOrUpdateSync();
    RequestOrPushSync requestOrPushSync = new RequestOrPushSync();
    AuthorizeUser authorizeUser = new AuthorizeUser();
    PushAPI pushAPI = new PushAPI();
    JSONParser jsonParser = new JSONParser();
    Calendar timer = Calendar.getInstance();
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");

    private class LocationListener implements android.location.LocationListener{
        Location mLastLocation;
        public LocationListener(String provider)
        {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }
        @Override
        public void onLocationChanged(Location location)
        {
            Log.e(TAG, "onLocationChanged: " + location);

            Log.e("Latitude: ", String.valueOf(location.getLatitude()));
            Log.e("Longitude: ", String.valueOf(location.getLongitude()));
            coordinateModel.setLatitude(String.valueOf(location.getLatitude()));
            coordinateModel.setLongitude(String.valueOf(location.getLongitude()));
            mLastLocation.set(location);
        }
        @Override
        public void onProviderDisabled(String provider)
        {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }
        @Override
        public void onProviderEnabled(String provider)
        {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }
    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.NETWORK_PROVIDER),
            new LocationListener(LocationManager.GPS_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        initializeLocationManager();
        try {
            // Getting GPS status
            isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if(isNetworkEnabled){
                mLocationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                        mLocationListeners[1]);
            }
            else{
                Log.e("GPS", "Network is disabled");
            }



        } catch (SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if(isGPSEnabled)
            {
                Log.e("GPS","GPS is enabled");
                mLocationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                        mLocationListeners[0]);
            }
            if (!isGPSEnabled) {
                Log.e("GPS","GPS is disabled");
                // so asking user to open GPS
                //GenericHelper.askUserToOpenGPS(getApplicationContext());
                //GenericHelper.checkIfGPSIsON(getApplicationContext());
            }

        } catch (SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }

        mTimer = new Timer();
        mTimer.schedule(timerTask, 15000, 40* 1000); //2000, 5 * 1000 //60*1000

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            Toast.makeText(this,"ServiceMan Running", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private Timer mTimer;

    TimerTask timerTask = new TimerTask() {

        @Override
        public void run() {

            if(GenericHelper.toggleGPS(getApplicationContext()))
            {

                Log.e("Log", "Running");
                //JSONObject buildJsonFromTable2 = pushAPI.JsonPush(getApplicationContext(), "mobile_synch_buildings");
                Cursor cursor = checkAuthorization.checkForAuthorizationRequest(getApplicationContext());
                if(cursor != null && cursor.getCount()>0)//&& cursor.getCount()>0
                {
                    Log.e("Log", "Running:authorization record found:" + cursor.getCount());
                    if (cursor.moveToFirst()) {
                        do {
                            client_name = cursor.getString(1);
                            service_id = cursor.getString(2);
                            authorization_code = cursor.getString(3);
                            authorized = cursor.getString(4);
                            Log.e("authorized found", cursor.getString(4));
                            email = cursor.getString(5);
                            Log.e("email found", cursor.getString(5));
                            service_code = cursor.getString(6);
                            add_request_url = cursor.getString(7);
                            Log.e("add_request_url found", cursor.getString(7) + ";");
                            request_date = cursor.getString(8);
                            Log.e("request_date found", cursor.getString(8) + ";");
                            isFirstSync = cursor.getString(10);
                            Log.e("isFirstSync found", cursor.getString(10) + ";");
                            //  Log.e("ClientData", "Cool " + isFirstSync + " " + authorized);
                            //cursor.moveToNext();
                        } while (cursor.moveToNext());
                    }

                    // TODO: 26/09/2015 get user location and push to database
                    if(service_code != null && email != null && coordinateModel.getLatitude() != null && coordinateModel.getLongitude() != null)
                    {
                        String authorization_id = email + Devices.getDeviceUUID(getApplicationContext()) + Devices.getDeviceIMEI(getApplicationContext());
                        ContentValues cV = new ContentValues();
                        //count number of rows in table
                        long tCount = dataDB.myConnection(getApplicationContext()).countRecords("location_coordinate");
                        if(tCount>=0) {
                            cV.put("latitude", coordinateModel.getLatitude());
                            cV.put("longitude", coordinateModel.getLongitude());
                            cV.put("authorization_id", authorization_id);
                            cV.put("service_code", service_code);
                            cV.put("location_coordinate_id", tCount + 1);
                            cV.put("date", timer.getTime().toString());
                            long myCoordt = dataDB.myConnection(getApplicationContext()).onInsert(cV, "location_coordinate");
                            if(myCoordt > 0)
                            {
                                Log.e("myCoordt", "saved user coordinates " + String.valueOf(myCoordt) + ". Coordinates " + coordinateModel.getLatitude() +" "+ coordinateModel.getLongitude() +";");
                            }
                        }
                    }

                    // TODO: 26/09/2015 Synchronization process
                    if((Integer.parseInt(authorized) == 0) && (Integer.parseInt(isFirstSync) == 0))
                    {

                        Log.e("AUTHORIZED & FIRSTSYNC", "User is not yet authorized and no sync found");
                        AuthorizationHttpResponse r1 = seekAuthorization();
                        if(r1 != null && r1.getResponseData() != null) //
                        {
                            Log.e("SEEK AUTHORIZATION","Something was returned after authorization." + r1.getResponseData() + ". status;" + r1.getResponseCode());
                            if(r1.getResponseCode() == 201){
                                Log.e("SEEK AUTHORIZATION", "User is now authorized and client_table need to be updated");

                                JSONObject jsonReader = null;
                                try {
                                    jsonReader = new JSONObject(r1.getResponseData());
                                    if(jsonReader != null)
                                    {
                                        Log.e("AUTH jsonReader","Service was able to read data from authorization response");
                                        JSONObject servicedetail = jsonReader.getJSONObject(authorizeUser.service_details);
                                        if(servicedetail != null && !servicedetail.toString().isEmpty())
                                        {
                                            Log.e("AUTH jsonReader","Got something from servicedetail");
                                            String clientName = servicedetail.getString(authorizeUser.CLIENT_NAME);
                                            String service_id = servicedetail.getString(authorizeUser.SERVICE_ID);
                                            String initial_url = servicedetail.getString(authorizeUser.INITIAL_URL);
                                            String url_version_date = servicedetail.getString(authorizeUser.URL_VERSION_DATE);
                                            String authentication_code = servicedetail.getString(authorizeUser.AUTHENTICATION_CODE);
                                            String mobile_sync_tables = servicedetail.getString(authorizeUser.SYNCH_TABLE);
                                            String server_tables = servicedetail.getString(authorizeUser.TABLES);
                                            if(clientName != null && !clientName.isEmpty()
                                                    && service_id != null && !service_id.isEmpty()
                                                    && initial_url != null && !initial_url.isEmpty()
                                                    && authentication_code != null && !authentication_code.isEmpty()
                                                    && url_version_date != null && !url_version_date.isEmpty())
                                            {
                                                Log.e("AUTH jsonReader","Got clientName:" + clientName);
                                                Log.e("AUTH jsonReader","Got service_id:" + service_id);
                                                Log.e("AUTH jsonReader","Got initial_url:" + initial_url);
                                                Log.e("AUTH jsonReader","Got url_version_date:" + url_version_date);
                                                Log.e("AUTH jsonReader","Got authentication_code:" + authentication_code);

                                                //make sure you get mobile_sync_tables and server_tables
                                                if(mobile_sync_tables != null && !mobile_sync_tables.isEmpty())
                                                {
                                                    Log.e("AUTH jsonReader","Got mobile_sync_tables:" + mobile_sync_tables);
                                                    // TODO: 16/09/2015 Build ContentValues to insert into the seq_sync_table
                                                    boolean insert_seq_snc_Table = authorizeUser.insertAuthTableData(getApplicationContext(), authorizeUser.seq_synch_table, authorizeUser.tSynchTableName, mobile_sync_tables);
                                                    if (insert_seq_snc_Table) {
                                                        Log.e("insert_seq_snc_Table","the following are saved in the seq_sync_table:" + mobile_sync_tables);
                                                    }
                                                }
                                                //make sure you get server_tables
                                                if(server_tables != null && !server_tables.isEmpty())
                                                {
                                                    Log.e("AUTH jsonReader","Got server_tables:" + server_tables);
                                                    // TODO: 16/09/2015 Build ContentValues to insert into the seq_tables
                                                    boolean insertSeqTable = authorizeUser.insertAuthTableData(getApplicationContext(), authorizeUser.seq_table, authorizeUser.tTableName, server_tables);
                                                    //Build Content values for insert into seq_sync_table
                                                    if (insertSeqTable) {
                                                        Log.e("insertSeqTable","the following are saved in the seq_table:" + server_tables);
                                                    }
                                                }

                                                // TODO: 16/09/2015 Build ContentValues to insert into url_table
                                                //String urlDecoded = URLDecoder.decode(initial_url);
                                                //compile the data to save in the url_table
                                                ContentValues urlValuesToSave = new ContentValues();
                                                urlValuesToSave.put("req_initial_url", add_request_url);
                                                urlValuesToSave.put("initial_url", URLDecoder.decode(initial_url));
                                                urlValuesToSave.put("url_version_date", url_version_date);
                                                boolean insertUrl = authorizeUser.insertAuthUrl(getApplicationContext(), urlValuesToSave, authorizeUser.tUrlTableName);
                                                if(insertUrl) {
                                                    Log.e("insertUrl","the following were inserted in the url_table:" + add_request_url +";" + URLDecoder.decode(initial_url) + ";" + url_version_date);
                                                }

                                                // TODO: 16/09/2015 Build ContentValues to update client_table and set authorized to 1
                                                String formattedDate = df.format(timer.getTime());
                                                ContentValues contentValues = new ContentValues();
                                                contentValues.put("client_name", clientName);
                                                contentValues.put("service_id", service_id);
                                                contentValues.put("authorization_code", authentication_code);
                                                contentValues.put("authorized", "1");
                                                contentValues.put("authorized_date", formattedDate);
                                                boolean updateClientTable = authorizeUser.updateRequest(getApplicationContext(), email, contentValues);
                                                if(updateClientTable){
                                                    Log.e("insertSeqTable","the following were updated in the client_table:" + clientName +";" + service_id + ";" + authentication_code +";" +formattedDate);
                                                    Log.i("AuthorizationDetail", " Calling sendNotification");
                                                    sendAuthorizationNotification("Authorization granted to " + email);
                                                }

                                            }


                                        }
                                    }

                                }
                                catch(JSONException e){
                                    e.printStackTrace();
                                }
                                //AuthorizeUser AUTH_USER = new AuthorizeUser();
                                //AUTH_USER.initialRequest(getApplicationContext(), mEmail, mServiceID, url);
                            }


                        }
                        else{
                            Log.e("SEEK AUTHORIZATION ", " Nothing was returned after authorization.");
                        }
                    }

                    if((Integer.parseInt(authorized) == 1) && (Integer.parseInt(isFirstSync) == 0))
                    {
                        Log.e("AUTHORIZED & FIRSTSYNC","Like user has been authorized. Now to sync");
                        String authorization_id = email + Devices.getDeviceUUID(getApplicationContext()) + Devices.getDeviceIMEI(getApplicationContext());
                        if(email != null && !email.isEmpty() && service_id != null && !service_id.isEmpty() && authorization_code != null && !authorization_code.isEmpty() && authorization_id != null && !authorization_id.isEmpty())
                        {
                            String urlDecoded = dataDB.getSyncUrl(getApplicationContext());
                            if(urlDecoded != null)
                            {
                                Log.e("URL Decoded", urlDecoded);

                                // TODO: 17/09/2015 action=send means the mobile expect data and action=push means mobile is sending data
                                //buid
                                JSONObject syncObj = BuildHttpJsonResponse.syncDataJson(service_id,authorization_id,authorization_code,"send",null,null);
                                if(syncObj != null)
                                {
                                    Log.e("DATA for SYNC",syncObj.toString());

                                    //post json to api
                                    AuthorizationHttpResponse firstSyncResponse = jsonParser.postMadeEasy(getApplicationContext(), urlDecoded, "POST", syncObj.toString());
                                    if(firstSyncResponse != null){
                                        String decodeFirstResponse = null;
                                        try {
                                            if(firstSyncResponse.getResponseByteData() != null && firstSyncResponse.getResponseCode() == 200){
                                                // TODO: 17/09/2015 decode byte data received
                                                decodeFirstResponse = new String(firstSyncResponse.getResponseByteData(), "UTF-8");
                                                Log.e("decoded sync data", decodeFirstResponse);

                                                // TODO: 17/09/2015 push sync data to database
                                                JSONObject ret = analyzeReceivedJson(getApplicationContext(), decodeFirstResponse);
                                                // TODO: 17/09/2015 return status response to server
                                                if(ret!=null)
                                                {
                                                    JSONObject bj = BuildHttpJsonResponse.syncDataJson(service_id,authorization_id,authorization_code,"response",null,ret);
                                                    if(bj != null)
                                                    {
                                                        Log.e("with response data", bj.toString());
                                                        AuthorizationHttpResponse fir = jsonParser.postMadeEasy(getApplicationContext(), urlDecoded, "POST", bj.toString());
                                                        if(fir != null){
                                                            //cut it here and allow request go again as far as isFirstSync is 0
                                                        }
                                                    }
                                                }
                                            }

                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                }

                            }
                        }
                    }
                    if(Integer.parseInt(authorized) == 1 && (Integer.parseInt(isFirstSync) == 1))
                    {
                        // TODO: 17/09/2015 action=send means the mobile expect data and action=push means mobile is sending data
                        Log.e("ServiceRunning","This user has been authorized and vital table sync done");
                        String authorization_id = email + Devices.getDeviceUUID(getApplicationContext()) + Devices.getDeviceIMEI(getApplicationContext());
                        if(email != null && !email.isEmpty() && service_id != null && !service_id.isEmpty() && authorization_code != null && !authorization_code.isEmpty() && authorization_id != null && !authorization_id.isEmpty()) {
                            String urlDecoded = dataDB.getSyncUrl(getApplicationContext());
                            if (urlDecoded != null) {
                                Log.e("URL Decoded on 1,1", urlDecoded);
                                // TODO: 20/09/2015 Process for pushing to the server
                                //iterate through the seq_sync_server and build table to send to server
                                Cursor mycursor = dataDB.myConnection(getApplicationContext()).selectAllFromTable("seq_sync_server","synch_status","0");
                                if(mycursor != null && mycursor.getCount() > 0)
                                {
                                    Log.e("seq_sync_server","Got table to sync");
                                    while(!mycursor.isAfterLast())
                                    {
                                        String syncTableName = mycursor.getString(1);
                                        Log.e("Table_to_Push",syncTableName);
                                        // TODO: 25/09/2015 process to build the table
                                        JSONObject buildJsonFromTable = pushAPI.JsonPush(getApplicationContext(), syncTableName);
                                        //JSONObject buildJsonFromTable = pushAPI.JsonPushMobileSyncBuilding(getApplicationContext());
                                        // TODO: 25/09/2015 pass json table result to build with authentication details
                                        if(buildJsonFromTable != null && buildJsonFromTable.length() > 0) {
                                            JSONObject jsonObj = BuildHttpJsonResponse.syncDataJson(service_id, authorization_id, authorization_code, "push", buildJsonFromTable, null);
                                            if (jsonObj != null) {
                                                Log.e("with push data", jsonObj.toString());
                                                AuthorizationHttpResponse responseFromServer = jsonParser.postMadeEasy(getApplicationContext(), urlDecoded, "POST", jsonObj.toString());
                                                if (responseFromServer.getResponseByteData() != null) {
                                                    Log.e("with response data", responseFromServer.getResponseByteData().toString());
                                                    // TODO: 20/09/2015 process the response gotten
                                                    boolean resultGottenAfterUpdatingResponse = requestOrPushSync.processSyncPush(getApplicationContext(), responseFromServer.getResponseByteData(), syncTableName);
                                                    //requestOrPushSync.processSynchResponseFromServer(getApplicationContext(), responseFromServer.getResponseByteData(), syncTableName);

                                                }
                                            }
                                        }

                                        mycursor.moveToNext();
                                    }
                                }
                                else {
                                    Log.e("seq_sync_server","Unable to get table to sync from seq_sync_server");
                                }

                                // TODO: 25/09/2015 Now to get from server after pushing process above

                                    // TODO: 17/09/2015 action=send means the mobile expect data and action=push means mobile is sending data
                                    //buid
                                    JSONObject syncObj = BuildHttpJsonResponse.syncDataJson(service_id,authorization_id,authorization_code,"send",null,null);
                                    if(syncObj != null) {
                                        Log.e("DATA for SYNC on 1,1", syncObj.toString());

                                        //post json to api
                                        AuthorizationHttpResponse firstSyncResponse = jsonParser.postMadeEasy(getApplicationContext(), urlDecoded, "POST", syncObj.toString());
                                        if (firstSyncResponse != null) {
                                            String decodeFirstResponse = null;
                                            try {
                                                if (firstSyncResponse.getResponseByteData() != null && firstSyncResponse.getResponseCode() == 200) {
                                                    // TODO: 17/09/2015 decode byte data received
                                                    Log.e("decodeFirstResponse","decode byte data received ");
                                                    decodeFirstResponse = new String(firstSyncResponse.getResponseByteData(), "UTF-8");
                                                    Log.e("decoded sync data", decodeFirstResponse);

                                                    // TODO: 17/09/2015 push sync data to database
                                                    JSONObject ret = analyzeReceivedJson(getApplicationContext(), decodeFirstResponse);
                                                    // TODO: 17/09/2015 return status response to server
                                                    if (ret != null) {
                                                        JSONObject bj = BuildHttpJsonResponse.syncDataJson(service_id, authorization_id, authorization_code, "response", null, ret);
                                                        if (bj != null) {
                                                            Log.e("with response data", bj.toString());
                                                            AuthorizationHttpResponse fir = jsonParser.postMadeEasy(getApplicationContext(), urlDecoded, "POST", bj.toString());
                                                            if (fir != null) {
                                                                //cut it here and allow request go again as far as isFirstSync is 0
                                                            }
                                                        }
                                                    }
                                                }

                                            } catch (UnsupportedEncodingException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }





                            /*String sync_buildingTable = "mobile_synch_buildings";
                            JSONObject pushBuilding = pushAPI.JsonPush(getApplicationContext(), sync_buildingTable);
                            JSONObject jsonObj = BuildHttpJsonResponse.syncDataJson(service_id, authorization_id, authorization_code, "push", pushBuilding, null);
                            Log.e("JSONObject",jsonObj.toString());
                            // TODO: 20/09/2015 process the response gotten
                            boolean respWent = requestOrPushSync.processSyncPush(getApplicationContext(), null, sync_buildingTable);

                            // TODO: 20/09/2015 Process for receiving from server
                            Log.e("SYNC"," Receiving data started");
                            JSONObject syncObj = BuildHttpJsonResponse.syncDataJson(service_id, authorization_id, authorization_code, "send", null, null);
                            if (syncObj != null) {
                                Log.e("DATA for SYNC", syncObj.toString());
                                JSONParser jsonParser = new JSONParser();
                                //post json to api
                                AuthorizationHttpResponse firstSyncResponse = jsonParser.postMadeEasy(getApplicationContext(), urlDecoded, "POST", syncObj.toString());
                                if (firstSyncResponse != null) {
                                    String decodeFirstResponse = null;
                                    try {
                                        if (firstSyncResponse.getResponseByteData() != null && firstSyncResponse.getResponseCode() == 200) {
                                            // TODO: 17/09/2015 decode byte data received
                                            decodeFirstResponse = new String(firstSyncResponse.getResponseByteData(), "UTF-8");
                                            Log.e("decoded sync data", decodeFirstResponse);

                                            // TODO: 17/09/2015 push sync data to database
                                            JSONObject ret = analyzeReceivedJson(getApplicationContext(), decodeFirstResponse);
                                            // TODO: 17/09/2015 return status response to server
                                            if (ret != null) {
                                                JSONObject bj = BuildHttpJsonResponse.syncDataJson(service_id, authorization_id, authorization_code, "response", null, ret);
                                                if (bj != null) {
                                                    Log.e("with response data", bj.toString());
                                                    AuthorizationHttpResponse fir = jsonParser.postMadeEasy(getApplicationContext(), urlDecoded, "POST", bj.toString());
                                                    if (fir != null) {
                                                        //cut it here and allow request go again as far as isFirstSync is 0
                                                    }
                                                }
                                            }

                                        }

                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }*/

                            }

                        }

                    }

                }

                else{
                    Log.e("Log", "Running:No authorization matching record found");
                }
                cursor.close();
            }
            else{
                Log.e("Log", "Running No GPS");
            }





        }
    };

    public JSONObject analyzeReceivedJson(Context context,String jsonData)
    {
        Log.e("AnalyzeData", "Analyze Received Data before saving");
        Log.e("JsonReader", jsonData);
        JSONArray workedJson = new JSONArray();
        JSONObject jsnObject = new JSONObject();

        JSONArray jsonArray_USERS = new JSONArray();
        JSONArray jsonArray_ASSIGN = new JSONArray();
        JSONArray jsonArray_STREETS  = new JSONArray();
        JSONArray jsonArray_BUILDINGS  = new JSONArray();
        JSONArray jsonArray_ACCOUNT  = new JSONArray();
        JSONArray jsonArray_PROFILES  = new JSONArray();
        JSONArray jsonArray_PROFILE_TYPES  = new JSONArray();
        JSONArray jsonArray_ZONES  = new JSONArray();
        JSONArray jsonArray_AREACODES  = new JSONArray();
        JSONArray jsonArray_BUILDING_CATEGORY  = new JSONArray();
        JSONArray jsonArray_ChallengeType = new JSONArray();
        JSONArray jsonArray_MeterType = new JSONArray();
        JSONArray jsonArray_MeterStatus = new JSONArray();
        JSONArray jsonArray_ConsumerTypes = new JSONArray();
        JSONArray jsonArray_InstallationTypes = new JSONArray();
        JSONArray jsonArray_AccountBilling = new JSONArray();
        JSONArray jsonArray_PaymentItems = new JSONArray();
        JSONArray jsonArray_AccessControl = new JSONArray();

        try{
            Log.e("data Try", "started process of user");
            JSONObject jsonReader = new JSONObject(jsonData);
            if(jsonReader != null)
            {
                jsonArray_USERS = jsonReader.getJSONArray(InsertOrUpdateSync.TAG_TABLE_USERS) != null ? jsonReader.getJSONArray(InsertOrUpdateSync.TAG_TABLE_USERS) : null;
                Log.e("TableData Gotten for user", jsonArray_USERS.toString() + " length;" + jsonArray_USERS.length());
            }
            if(jsonArray_USERS != null && jsonArray_USERS.length()>0)
            {
                Log.e("SYNC_SELECT","Sync Users");
                workedJson = push_data_to_User(context, jsonArray_USERS);
                if (workedJson != null && workedJson.length() > 0)
                {

                    Log.e("TestOutput Users",workedJson.toString());
                    try {
                        jsnObject.put(InsertOrUpdateSync.TAG_TABLE_USERS,workedJson);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        try{
            Log.e("data Try", "started process of assignment");
            JSONObject jsonReader = new JSONObject(jsonData);
            if(jsonReader != null)
            {
                jsonArray_ASSIGN = jsonReader.getJSONArray(InsertOrUpdateSync.TAG_TABLE_ASSIGNMENTS) != null ? jsonReader.getJSONArray(InsertOrUpdateSync.TAG_TABLE_ASSIGNMENTS) : null;
                Log.e("TableData Gotten for Assignment", jsonArray_ASSIGN.toString() + " length;" + jsonArray_ASSIGN.length());
            }
            if(jsonArray_ASSIGN != null && jsonArray_ASSIGN.length()>0)
            {
                Log.e("SYNC_SELECT","Sync Assignment");
                workedJson = firstInsertSyncData.push_data_to_Assignment(context, jsonArray_ASSIGN);
                if (workedJson != null && workedJson.length() > 0)
                {

                    Log.e("TestOutput Assignment",workedJson.toString());
                    try {
                        jsnObject.put(InsertOrUpdateSync.TAG_TABLE_ASSIGNMENTS,workedJson);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        try{
            Log.e("data Try", "started process of AreaCodes");
            JSONObject jsonReader = new JSONObject(jsonData);
            if(jsonReader != null)
            {
                jsonArray_AREACODES = jsonReader.getJSONArray(InsertOrUpdateSync.TAG_TABLE_AREACODES) != null ? jsonReader.getJSONArray(InsertOrUpdateSync.TAG_TABLE_AREACODES) : null;
                Log.e("TableData Gotten for AreaCodes", jsonArray_AREACODES.toString() + " length;" + jsonArray_AREACODES.length());
            }
            if(jsonArray_AREACODES != null && jsonArray_AREACODES.length() > 0)
            {
                Log.e("SYNC_SELECT","Sync AreaCodes");
                workedJson = firstInsertSyncData.push_data_to_Areacodes(context, jsonArray_AREACODES);
                if (workedJson != null && workedJson.length() > 0)
                {

                    Log.e("TestOutput AreaCodes",workedJson.toString());
                    try {
                        jsnObject.put(InsertOrUpdateSync.TAG_TABLE_AREACODES,workedJson);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        try{
            Log.e("data Try", "started process of Street");
            JSONObject jsonReader = new JSONObject(jsonData);
            if(jsonReader != null)
            {
                jsonArray_STREETS = jsonReader.getJSONArray(InsertOrUpdateSync.TAG_TABLE_STREETS) != null ? jsonReader.getJSONArray(InsertOrUpdateSync.TAG_TABLE_STREETS) : null;
                Log.e("TableData Gotten for Street", jsonArray_STREETS.toString() + " length;" + jsonArray_STREETS.length());
            }
            if(jsonArray_STREETS != null && jsonArray_STREETS.length() > 0)
            {
                Log.e("SYNC_SELECT","Sync Streets");
                workedJson = firstInsertSyncData.push_data_to_Street(context, jsonArray_STREETS);
                if (workedJson != null && workedJson.length() > 0)
                {

                    Log.e("TestOutput Streets",workedJson.toString());
                    try {
                        jsnObject.put(InsertOrUpdateSync.TAG_TABLE_STREETS,workedJson);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        try{
            Log.e("data Try", "started process of Building");
            JSONObject jsonReader = new JSONObject(jsonData);
            if(jsonReader != null)
            {
                jsonArray_BUILDINGS = jsonReader.getJSONArray(InsertOrUpdateSync.TAG_TABLE_BUILDINGS) != null ? jsonReader.getJSONArray(InsertOrUpdateSync.TAG_TABLE_BUILDINGS) : null;
                Log.e("TableData Gotten for Building", jsonArray_BUILDINGS.toString() + " length;" + jsonArray_BUILDINGS.length());
            }
            if(jsonArray_BUILDINGS != null && jsonArray_BUILDINGS.length() > 0)
            {
                Log.e("SYNC_SELECT","Sync Buildings");
                workedJson = firstInsertSyncData.push_data_to_Building(context, jsonArray_BUILDINGS);
                if (workedJson != null && workedJson.length() > 0)
                {

                    Log.e("TestOutput Buildings",workedJson.toString());
                    try {
                        jsnObject.put(InsertOrUpdateSync.TAG_TABLE_BUILDINGS,workedJson);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        try{
            Log.e("data Try", "started process of BuildingCategory");
            JSONObject jsonReader = new JSONObject(jsonData);
            if(jsonReader != null)
            {
                jsonArray_BUILDING_CATEGORY = jsonReader.getJSONArray(InsertOrUpdateSync.TAG_TABLE_BUILDINGS_CATEGORIES) != null ? jsonReader.getJSONArray(InsertOrUpdateSync.TAG_TABLE_BUILDINGS_CATEGORIES) : null;
                Log.e("TableData Gotten for BuildingCategory", jsonArray_BUILDING_CATEGORY.toString() + " length;" + jsonArray_BUILDING_CATEGORY.length());
            }
            if(jsonArray_BUILDING_CATEGORY != null && jsonArray_BUILDING_CATEGORY.length() > 0)
            {
                Log.e("SYNC_SELECT","Sync Building Categories");
                workedJson = firstInsertSyncData.push_data_to_BuildingCategory(context, jsonArray_BUILDING_CATEGORY);
                if (workedJson != null && workedJson.length() > 0)
                {

                    Log.e("TestOutput Building Categories",workedJson.toString());
                    try {
                        jsnObject.put(InsertOrUpdateSync.TAG_TABLE_BUILDINGS_CATEGORIES,workedJson);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        try{
            Log.e("data Try", "started process of Account");
            JSONObject jsonReader = new JSONObject(jsonData);
            if(jsonReader != null)
            {
                jsonArray_ACCOUNT = jsonReader.getJSONArray(InsertOrUpdateSync.TAG_TABLE_ACCOUNTS) != null ? jsonReader.getJSONArray(InsertOrUpdateSync.TAG_TABLE_ACCOUNTS) : null;
                Log.e("TableData Gotten for Account", jsonArray_ACCOUNT.toString() + " length;" + jsonArray_ACCOUNT.length());
            }
            if(jsonArray_ACCOUNT != null && jsonArray_ACCOUNT.length() > 0)
            {
                Log.e("SYNC_SELECT","Sync Account");
                workedJson = firstInsertSyncData.push_data_to_Account(context, jsonArray_ACCOUNT);
                if (workedJson != null && workedJson.length() > 0)
                {

                    Log.e("TestOutput Account",workedJson.toString());
                    try {
                        jsnObject.put(InsertOrUpdateSync.TAG_TABLE_ACCOUNTS,workedJson);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }


        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }


        try{
            Log.e("data try","started process of Profile_Type");
            JSONObject jsonReader = new JSONObject(jsonData);
            if(jsonReader != null)
            {
                jsonArray_PROFILE_TYPES = jsonReader.getJSONArray(InsertOrUpdateSync.TAG_TABLE_PROFILE_TYPES) != null
                        && jsonReader.getJSONArray(InsertOrUpdateSync.TAG_TABLE_PROFILE_TYPES).length()>0
                        ? jsonReader.getJSONArray(InsertOrUpdateSync.TAG_TABLE_PROFILE_TYPES) : null;
                Log.e("TableData Gotten for ProfileType", jsonArray_PROFILE_TYPES.toString() + " length;" + jsonArray_PROFILE_TYPES.length());
            }
            if(jsonArray_PROFILE_TYPES != null && jsonArray_PROFILE_TYPES.length()>0)
            {
                Log.e("SYNC_SELECT","Sync ProfileType");
                workedJson = firstInsertSyncData.push_data_to_ProfileType(context, jsonArray_PROFILE_TYPES);
                if (workedJson != null && workedJson.length() > 0)
                {

                    Log.e("TestOutput ProfileType",workedJson.toString());
                    try {
                        jsnObject.put(InsertOrUpdateSync.TAG_TABLE_PROFILE_TYPES,workedJson);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        try{
            Log.e("data Try", "started process of Profile");
            JSONObject jsonReader = new JSONObject(jsonData);
            if(jsonReader != null)
            {
                jsonArray_PROFILES = jsonReader.getJSONArray(InsertOrUpdateSync.TAG_TABLE_PROFILES) != null
                        && jsonReader.getJSONArray(InsertOrUpdateSync.TAG_TABLE_PROFILES).length()>0
                        ? jsonReader.getJSONArray(InsertOrUpdateSync.TAG_TABLE_PROFILES) : null;
                Log.e("TableData Gotten for Profile", jsonArray_PROFILES.toString() + " length;" + jsonArray_PROFILES.length());
            }
            if(jsonArray_PROFILES != null && jsonArray_PROFILES.length() > 0)
            {
                Log.e("SYNC_SELECT","Sync Profile");
                workedJson = firstInsertSyncData.push_data_to_Profile(context, jsonArray_PROFILES);
                if (workedJson != null && workedJson.length() > 0)
                {

                    Log.e("TestOutput Profile",workedJson.toString());
                    try {
                        jsnObject.put(InsertOrUpdateSync.TAG_TABLE_PROFILES,workedJson);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        try{
            Log.e("data Try", "started process of Zone");
            JSONObject jsonReader = new JSONObject(jsonData);
            if(jsonReader != null)
            {
                jsonArray_ZONES = jsonReader.getJSONArray(InsertOrUpdateSync.TAG_TABLE_ZONES) != null
                        && jsonReader.getJSONArray(InsertOrUpdateSync.TAG_TABLE_ZONES).length()>0
                        ? jsonReader.getJSONArray(InsertOrUpdateSync.TAG_TABLE_ZONES) : null;
                Log.e("TableData Gotten for Zone", jsonArray_ZONES.toString() + " length;" + jsonArray_ZONES.length());
            }
            if(jsonArray_ZONES != null && jsonArray_ZONES.length() > 0)
            {
                Log.e("SYNC_SELECT","Sync Zone");
                workedJson = firstInsertSyncData.push_data_to_Zones(context, jsonArray_ZONES);
                if (workedJson != null && workedJson.length() > 0)
                {

                    Log.e("TestOutput Zone",workedJson.toString());
                    try {
                        jsnObject.put(InsertOrUpdateSync.TAG_TABLE_ZONES,workedJson);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        try{
            Log.e("data Try", "started process of Challenge Type");
            JSONObject jsonReader = new JSONObject(jsonData);
            if(jsonReader != null)
            {
                jsonArray_ChallengeType = jsonReader.getJSONArray(InsertOrUpdateSync.TAG_TABLE_CHALLENGETYPE) != null
                        && jsonReader.getJSONArray(InsertOrUpdateSync.TAG_TABLE_CHALLENGETYPE).length()>0
                        ? jsonReader.getJSONArray(InsertOrUpdateSync.TAG_TABLE_CHALLENGETYPE) : null;
                Log.e("TableData Gotten for ChallengeType", jsonArray_ChallengeType.toString() + " length;" + jsonArray_ChallengeType.length());
            }
            if(jsonArray_ChallengeType != null && jsonArray_ChallengeType.length() > 0)
            {
                Log.e("SYNC_SELECT","Sync Challenge Type");
                workedJson = firstInsertSyncData.push_data_to_ChallengeType(context, jsonArray_ChallengeType);
                if (workedJson != null && workedJson.length() > 0)
                {

                    Log.e("TestOutput ChallengeType",workedJson.toString());
                    try {
                        jsnObject.put(InsertOrUpdateSync.TAG_TABLE_CHALLENGETYPE,workedJson);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        try{
            Log.e("data Try", "started process of Meter Account Type");
            JSONObject jsonReader = new JSONObject(jsonData);
            if(jsonReader != null)
            {
                jsonArray_MeterType = jsonReader.getJSONArray(InsertOrUpdateSync.TAG_TABLE_METER_TYPE) != null
                        && jsonReader.getJSONArray(InsertOrUpdateSync.TAG_TABLE_METER_TYPE).length()>0
                        ? jsonReader.getJSONArray(InsertOrUpdateSync.TAG_TABLE_METER_TYPE) : null;
                Log.e("TableData Gotten for MeterType", jsonArray_MeterType.toString() + " length;" + jsonArray_MeterType.length());
            }
            if(jsonArray_MeterType != null && jsonArray_MeterType.length() > 0)
            {
                Log.e("SYNC_SELECT","Sync Meter Type");
                workedJson = firstInsertSyncData.push_data_to_meter_type(context, jsonArray_MeterType);
                if (workedJson != null && workedJson.length() > 0)
                {

                    Log.e("TestOutput MeterType",workedJson.toString());
                    try {
                        jsnObject.put(InsertOrUpdateSync.TAG_TABLE_METER_TYPE,workedJson);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        try{
            Log.e("data Try", "started process of Meter Status");
            JSONObject jsonReader = new JSONObject(jsonData);
            if(jsonReader != null)
            {
                jsonArray_MeterStatus = jsonReader.getJSONArray(InsertOrUpdateSync.TAG_TABLE_METER_STATUS) != null
                        && jsonReader.getJSONArray(InsertOrUpdateSync.TAG_TABLE_METER_STATUS).length()>0
                        ? jsonReader.getJSONArray(InsertOrUpdateSync.TAG_TABLE_METER_STATUS) : null;
                Log.e("TableData Gotten for Meter Status", jsonArray_MeterStatus.toString() + " length;" + jsonArray_MeterStatus.length());
            }
            if(jsonArray_MeterStatus != null && jsonArray_MeterStatus.length() > 0)
            {
                Log.e("SYNC_SELECT","Sync Meter Status");
                workedJson = firstInsertSyncData.push_data_to_meter_status(context, jsonArray_MeterStatus);
                if (workedJson != null && workedJson.length() > 0)
                {

                    Log.e("TestOutput MeterStatus",workedJson.toString());
                    try {
                        jsnObject.put(InsertOrUpdateSync.TAG_TABLE_METER_STATUS,workedJson);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        try{
            Log.e("data Try", "started process of Consumer Types");
            JSONObject jsonReader = new JSONObject(jsonData);
            if(jsonReader != null)
            {
                jsonArray_ConsumerTypes = jsonReader.getJSONArray(InsertOrUpdateSync.TAG_TABLE_CONSUMER_TYPES) != null
                        && jsonReader.getJSONArray(InsertOrUpdateSync.TAG_TABLE_CONSUMER_TYPES).length()>0
                        ? jsonReader.getJSONArray(InsertOrUpdateSync.TAG_TABLE_CONSUMER_TYPES) : null;
                Log.e("TableData Gotten for Consumer Types", jsonArray_ConsumerTypes.toString() + " length;" + jsonArray_ConsumerTypes.length());
            }
            if(jsonArray_ConsumerTypes != null && jsonArray_ConsumerTypes.length() > 0)
            {
                Log.e("SYNC_SELECT","Sync Consumer Types");
                workedJson = firstInsertSyncData.push_data_to_consumer_types(context, jsonArray_ConsumerTypes);
                if (workedJson != null && workedJson.length() > 0)
                {

                    Log.e("TestOutput ConsumerTypes",workedJson.toString());
                    try {
                        jsnObject.put(InsertOrUpdateSync.TAG_TABLE_CONSUMER_TYPES,workedJson);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        try{
            Log.e("data Try", "started process of InstallationTypes");
            JSONObject jsonReader = new JSONObject(jsonData);
            if(jsonReader != null)
            {
                jsonArray_InstallationTypes = jsonReader.getJSONArray(InsertOrUpdateSync.TAG_TABLE_METER_INSTALLATION_TYPES) != null
                        && jsonReader.getJSONArray(InsertOrUpdateSync.TAG_TABLE_METER_INSTALLATION_TYPES).length()>0
                        ? jsonReader.getJSONArray(InsertOrUpdateSync.TAG_TABLE_METER_INSTALLATION_TYPES) : null;
                Log.e("TableData Gotten for InstallationTypes", jsonArray_InstallationTypes.toString() + " length;" + jsonArray_InstallationTypes.length());
            }
            if(jsonArray_InstallationTypes != null && jsonArray_InstallationTypes.length() > 0)
            {
                Log.e("SYNC_SELECT","Sync InstallationTypes");
                workedJson = firstInsertSyncData.push_data_to_meter_installation_type(context, jsonArray_InstallationTypes);
                if (workedJson != null && workedJson.length() > 0)
                {

                    Log.e("TestOutput InstallationTypes",workedJson.toString());
                    try {
                        jsnObject.put(InsertOrUpdateSync.TAG_TABLE_METER_INSTALLATION_TYPES,workedJson);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        try{
            Log.e("data Try", "started process of account_billing");
            JSONObject jsonReader = new JSONObject(jsonData);
            if(jsonReader != null)
            {
                jsonArray_AccountBilling = jsonReader.getJSONArray(InsertOrUpdateSync.TAG_TABLE_ACCOUNT_BILLING) != null
                        && jsonReader.getJSONArray(InsertOrUpdateSync.TAG_TABLE_ACCOUNT_BILLING).length()>0
                        ? jsonReader.getJSONArray(InsertOrUpdateSync.TAG_TABLE_ACCOUNT_BILLING) : null;
                Log.e("TableData Gotten for account_billing", jsonArray_AccountBilling.toString() + " length;" + jsonArray_AccountBilling.length());
            }
            if(jsonArray_AccountBilling != null && jsonArray_AccountBilling.length() > 0)
            {
                Log.e("SYNC_SELECT","Sync Account_billing");
                workedJson = firstInsertSyncData.push_data_to_account_billing(context, jsonArray_AccountBilling);
                if (workedJson != null && workedJson.length() > 0)
                {

                    Log.e("TestOutput account_billing",workedJson.toString());
                    try {
                        jsnObject.put(InsertOrUpdateSync.TAG_TABLE_ACCOUNT_BILLING,workedJson);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        try{
            Log.e("data Try", "started process of payment_items");
            JSONObject jsonReader = new JSONObject(jsonData);
            if(jsonReader != null)
            {
                jsonArray_PaymentItems = jsonReader.getJSONArray(InsertOrUpdateSync.TAG_TABLE_PAYMENT_ITEMS) != null
                        && jsonReader.getJSONArray(InsertOrUpdateSync.TAG_TABLE_PAYMENT_ITEMS).length()>0
                        ? jsonReader.getJSONArray(InsertOrUpdateSync.TAG_TABLE_PAYMENT_ITEMS) : null;
                Log.e("TableData Gotten for payment_items", jsonArray_PaymentItems.toString() + " length;" + jsonArray_PaymentItems.length());
            }
            if(jsonArray_PaymentItems != null && jsonArray_PaymentItems.length() > 0)
            {
                Log.e("SYNC_SELECT","Sync payment_items");
                workedJson = firstInsertSyncData.push_data_to_Payment_Items(context, jsonArray_PaymentItems);
                if (workedJson != null && workedJson.length() > 0)
                {

                    Log.e("TestOutput payment_items",workedJson.toString());
                    try {
                        jsnObject.put(InsertOrUpdateSync.TAG_TABLE_PAYMENT_ITEMS,workedJson);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        try{
            Log.e("data Try", "started process of access_control");
            JSONObject jsonReader = new JSONObject(jsonData);
            if(jsonReader != null)
            {
                jsonArray_AccessControl = jsonReader.getJSONArray(InsertOrUpdateSync.TAG_TABLE_ACCESS_CONTROL) != null ? jsonReader.getJSONArray(InsertOrUpdateSync.TAG_TABLE_ACCESS_CONTROL) : null;
                Log.e("TableData Gotten for access_control", jsonArray_AccessControl.toString() + " length;" + jsonArray_AccessControl.length());
            }
            if(jsonArray_AccessControl != null && jsonArray_AccessControl.length()>0)
            {
                Log.e("SYNC_SELECT","Sync access_control");
                workedJson = firstInsertSyncData.push_data_to_AccessControl(context, jsonArray_AccessControl);
                if (workedJson != null && workedJson.length() > 0)
                {
                    Log.e("TestOutput access_control",workedJson.toString());
                    try {
                        jsnObject.put(InsertOrUpdateSync.TAG_TABLE_ACCESS_CONTROL,workedJson);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }


        // TODO: 19/09/2015 Start checking for the one to process


        /*try {
            Log.e("data Try", "started processResponse");
            JSONObject jsonReader = new JSONObject(jsonData);
            JSONArray jr = jsonReader.getJSONArray(InsertOrUpdateSync.TAG_TABLE_USERS);
            if(jr != null && jr.length()>0){
                Log.e("TableData 22 Gotten", jr.toString() + " length;" + jr.length());
            }

            JSONArray jr2 = jsonReader.getJSONArray("assignment");
            Log.e("TableData 221 Gotten", jr2.toString());

            JSONObject tableData = jsonReader.getJSONObject(InsertOrUpdateSync.TAG_TABLE_DATA);
            Log.e("TableData Gotten",tableData.toString());
            Log.e("TableData 2 Gotten", tableData.getJSONArray("users").toString());
            JSONArray jsonArray_USERS = tableData.getJSONArray(InsertOrUpdateSync.TAG_TABLE_USERS) != null ? jsonReader.getJSONArray(InsertOrUpdateSync.TAG_TABLE_USERS) : null;
            JSONArray jsonArray_ASSIGN = tableData.getJSONArray(InsertOrUpdateSync.TAG_TABLE_ASSIGNMENTS) != null ? tableData.getJSONArray(InsertOrUpdateSync.TAG_TABLE_ASSIGNMENTS) : null;

            JSONArray jsonArray_STREETS = tableData.getJSONArray(InsertOrUpdateSync.TAG_TABLE_STREETS) != null ? tableData.getJSONArray(InsertOrUpdateSync.TAG_TABLE_STREETS) : null;
            JSONArray jsonArray_BUILDINGS = tableData.getJSONArray(InsertOrUpdateSync.TAG_TABLE_BUILDINGS) != null ? tableData.getJSONArray(InsertOrUpdateSync.TAG_TABLE_BUILDINGS) : null;
            JSONArray jsonArray_ACCOUNT = tableData.getJSONArray(InsertOrUpdateSync.TAG_TABLE_ACCOUNTS) != null ? tableData.getJSONArray(InsertOrUpdateSync.TAG_TABLE_ACCOUNTS) : null;
            JSONArray jsonArray_PROFILES = tableData.getJSONArray(InsertOrUpdateSync.TAG_TABLE_PROFILES) != null ? tableData.getJSONArray(InsertOrUpdateSync.TAG_TABLE_PROFILES) : null;
            JSONArray jsonArray_ZONES = tableData.getJSONArray(InsertOrUpdateSync.TAG_TABLE_ZONES) != null ? tableData.getJSONArray(InsertOrUpdateSync.TAG_TABLE_ZONES) : null;
            JSONArray jsonArray_AREACODES = tableData.getJSONArray(InsertOrUpdateSync.TAG_TABLE_AREACODES) != null ? tableData.getJSONArray(InsertOrUpdateSync.TAG_TABLE_AREACODES) : null;
            JSONArray jsonArray_BUILDING_CATEGORY = tableData.getJSONArray(InsertOrUpdateSync.TAG_TABLE_BUILDINGS_CATEGORIES) != null ? tableData.getJSONArray(InsertOrUpdateSync.TAG_TABLE_BUILDINGS_CATEGORIES) : null;


        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        return jsnObject;
    }

    public JSONArray push_data_to_User(Context context,JSONArray jsonArray)
    {
        JSONArray responseJson = new JSONArray();
        //check if we have data in jsonArray_ASSIGN
        if(jsonArray.length()>0) {
            //repeat to get data from jsonarray
            for(int i=0; i<jsonArray.length(); i++) {
                try {
                    JSONObject c = jsonArray.getJSONObject(i);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("user_id",c.getString("user_id"));
                    contentValues.put("first_name",c.getString("first_name"));
                    contentValues.put("middle_name",c.getString("middle_name"));
                    contentValues.put("last_name",c.getString("last_name"));
                    contentValues.put("email",c.getString("email"));
                    contentValues.put("phone",c.getString("phone"));
                    contentValues.put("zone_id",c.getString("zone_id"));
                    contentValues.put("group_id",c.getString("group_id"));
                    contentValues.put("focode",c.getString("fo_code"));
                    contentValues.put("inactive","0");

                    if(c.getString("user_id") != null)
                    {
                        JSONObject ob = new JSONObject();
                        //// TODO: 19/09/2015 Delete all records first
                        dataDB.myConnection(context).deleteRecords("users");
                        // TODO: 19/09/2015 Call insert
                        long lastInsertId = dataDB.myConnection(context).onInsertOrUpdate(contentValues,InsertOrUpdateSync.TAG_TABLE_USERS);
                        Log.e(TAG, InsertOrUpdateSync.TAG_TABLE_USERS + "done in users " + String.valueOf(lastInsertId));
                        if(lastInsertId > 0){

                            Log.e("SyncInsert","Inserted into users");
                            ob.put("user_id", c.getString("user_id"));
                            ob.put("synch_status", "1");
                            Log.e("SavedSync","Users Saved");
                            //}

                            // TODO: 16/09/2015 Build ContentValues to update client_table and set authorized to 1
                            String formattedDate = df.format(timer.getTime());
                            ContentValues cv = new ContentValues();
                            cv.put("isFirstSyncDone", "1");
                            cv.put("firstSyncDate", formattedDate);
                            boolean updateClientTable = authorizeUser.updateRequest(getApplicationContext(), email, cv);
                            if(updateClientTable){
                                //Log.e("insertSeqTable","the following were updated in the client_table:" + clientName +";" + service_id + ";" + authentication_code +";" +formattedDate);
                                Log.e("UserDetail", " Calling sendNotification");
                                sendAccessNotification("Access granted to " + email);
                            }
                        }
                        else{
                            Log.v("SavedSync","Users Not Saved");
                            ob.put("user_id", c.getString("user_id"));
                            ob.put("synch_status", "0");
                        }
                        responseJson.put(ob);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return responseJson;
    }

    public AuthorizationHttpResponse seekAuthorization()
    {
        AuthorizationHttpResponse resp = new AuthorizationHttpResponse();
        if(GenericHelper.checkInternetConenction(getApplicationContext()))
        {
            //GenericHelper.checkIfGPSIsON(getApplicationContext());
            if(coordinateModel.getLatitude() == null || coordinateModel.getLongitude() == null)
            {
                LocationMavnager man = new LocationMavnager();
                String locMan = man.getMyLoc(getApplicationContext());
                if(!locMan.equalsIgnoreCase("err")){
                    String[] splitedCoordinate = GenericHelper.splitString(locMan, ":");
                    Log.e("My LcMan", locMan);
                    Log.e("My LocMann", " latitude:"+splitedCoordinate[0] + " longitude:" + splitedCoordinate[1]);
                    latitude = splitedCoordinate[0];
                    longitude = splitedCoordinate[1];
                }
            }
            else{
                latitude = coordinateModel.getLatitude();
                longitude = coordinateModel.getLongitude();
            }

            if(email != null && service_code != null) {
                Log.d("EmailAndServicecode", "Got email and service code");
                try {
                    urlParameters = "email=" + URLEncoder.encode(email, "UTF-8") +
                            "&service_code=" + URLEncoder.encode(service_code.toUpperCase(), "UTF-8") +
                            "&device_uuid=" + URLEncoder.encode(Devices.getDeviceUUID(getApplicationContext()), "UTF-8") +
                            "&device_imei=" + URLEncoder.encode(Devices.getDeviceIMEI(getApplicationContext()), "UTF-8") +
                            "&device_name=" + URLEncoder.encode(Devices.getDeviceName(), "UTF-8") +
                            "&device_model=" + URLEncoder.encode(Devices.getDeviceModel(), "UTF-8") +
                            "&device_os=" + URLEncoder.encode(Devices.getDeviceAndroidID(getApplicationContext()), "UTF-8") +
                            "&device_longitude=" + URLEncoder.encode(longitude, "UTF-8") +
                            "&device_latitude=" + URLEncoder.encode(latitude, "UTF-8") +
                            "&device_version=" + URLEncoder.encode(Devices.getDeviceVersion(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.i("urlParams", urlParameters);

                resp = HttpURLConnectionHelper.executePost(add_request_url, urlParameters);//getString(R.string.authorization_endpoint)
                if(resp != null) {
                    Log.i("Api response:", "Api Response Code:::" + resp.getResponseData() + " second response");
                }

            }
            else
            {
                Log.d("EmailAndServicecode", "Could not get email and service code");
            }
        }
        else {
            //Toast.makeText(getApplicationContext(), "Device is offline at the moment", Toast.LENGTH_SHORT).show();
            Log.e("Authorization Service","Device is offline at the moment");
        }
        return resp;
    }


    public void onDestroy() {
        try {
            mTimer.cancel();
            timerTask.cancel();
            if (mLocationManager != null) {
                for (int i = 0; i < mLocationListeners.length; i++) {
                    try {
                        mLocationManager.removeUpdates(mLocationListeners[i]);
                    } catch (Exception ex) {
                        Log.i(TAG, "fail to remove location listners, ignore", ex);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Intent intent = new Intent("com.android.techtrainner");
        //intent.putExtra("yourvalue", "torestore");
        //sendBroadcast(intent);
    }

    public void notificationMan()
    {
        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
                resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mNotifyBuilder;
        NotificationManager mNotificationManager;
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Sets an ID for the notification, so it can be updated
        int notifyID = 9001;
        mNotifyBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setContentTitle("Notice")
                .setContentText("You've received new messages.")
                .setSmallIcon(R.drawable.logo);
        // Set pending intent
        mNotifyBuilder.setContentIntent(resultPendingIntent);
        // Set Vibrate, Sound and Light
        int defaults = 0;
        defaults = defaults | Notification.DEFAULT_LIGHTS;
        defaults = defaults | Notification.DEFAULT_VIBRATE;
        defaults = defaults | Notification.DEFAULT_SOUND;
        mNotifyBuilder.setDefaults(defaults);
        // Set the content for Notification
        mNotifyBuilder.setContentText(resultIntent.getStringExtra("intntdata"));
        // Set autocancel
        mNotifyBuilder.setAutoCancel(true);
        // Post a notification
        mNotificationManager.notify(notifyID, mNotifyBuilder.build());
    }

    //@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void sendAuthorizationNotification(String message) {


        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        int defaults = 0;
        defaults = defaults | Notification.DEFAULT_LIGHTS;
        defaults = defaults | Notification.DEFAULT_VIBRATE;
        defaults = defaults | Notification.DEFAULT_SOUND;
        Notification n  = new Notification.Builder(getApplicationContext())
                .setContentTitle("AquaBiller")
                .setContentText(message)
                .setTicker("New Message Alert!")
                .setSmallIcon(R.drawable.logo)
                .setContentIntent(pIntent)
                .setDefaults(defaults)
                .setAutoCancel(true).build();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, n);

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void sendAccessNotification(String message) {
         int MY_NOTIFICATION_ID=1;
        Intent myIntent = new Intent(getApplicationContext(), CreatePasswordActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                myIntent,
                Intent.FLAG_ACTIVITY_NEW_TASK);

       // Intent intent = new Intent(getApplicationContext(), CreatePasswordActivity.class);
        //PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        int defaults = 0;
        defaults = defaults | Notification.DEFAULT_LIGHTS;
        defaults = defaults | Notification.DEFAULT_VIBRATE;
        defaults = defaults | Notification.DEFAULT_SOUND;
        Notification n  = new Notification.Builder(getApplicationContext())
                .setContentTitle("AquaBiller")
                .setContentText(message)
                .setTicker("New Message Alert!")
                .setSmallIcon(R.drawable.logo)
                .setContentIntent(pIntent)
                .setDefaults(defaults)
                .setAutoCancel(true).build();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(MY_NOTIFICATION_ID, n);

    }





    private void toggleGPS(Context context, boolean enable) {
        String provider = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(provider.contains("gps") == enable) {
            return; // the GPS is already in the requested state
        }

        final Intent poke = new Intent();
        poke.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");
        poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
        poke.setData(Uri.parse("3"));
        context.sendBroadcast(poke);
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        //GenericHelper.turnGPSOn(getApplicationContext());
        toggleGPS(getApplicationContext(),true);
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

}


