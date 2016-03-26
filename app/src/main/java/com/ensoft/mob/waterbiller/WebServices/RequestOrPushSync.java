package com.ensoft.mob.waterbiller.WebServices;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ensoft.mob.waterbiller.DB.AuthorizeUser;
import com.ensoft.mob.waterbiller.DB.DBCheckAuthorization;
import com.ensoft.mob.waterbiller.DB.DBConnection;
import com.ensoft.mob.waterbiller.DB.DataDB;
import com.ensoft.mob.waterbiller.Devices.Devices;
import com.ensoft.mob.waterbiller.Models.AuthorizationHttpResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Ebuka on 13/09/2015.
 */
public class RequestOrPushSync {
    AuthorizeUser authorizeUser = new AuthorizeUser();
    DBCheckAuthorization dbCheckAuthorization = new DBCheckAuthorization();
    DataDB dataDB = new DataDB();

    public AuthorizationHttpResponse requestDataSync(Context context, String email, String service_id, String authentication_code)
    {
        boolean isSuccess = false;AuthorizationHttpResponse resp = null;
        String authorization_id = email + Devices.getDeviceUUID(context) + Devices.getDeviceIMEI(context);
        String urlDecoded = dataDB.getSyncUrl(context);
        //consume the initial_url
        try {
            Log.e("RequestDataProcessor", "am here in try to post data for second request");
            Log.i("Api consume:", "Api request:::" + urlDecoded);
            String urlParameter = "service_id=" + URLEncoder.encode(service_id, "UTF-8")
                    + "&authentication_code=" + URLEncoder.encode(authentication_code,"UTF-8")
                    + "&authorization_id=" + URLEncoder.encode(authorization_id,"UTF-8");
            resp = HttpURLConnectionHelper.executePost(urlDecoded, urlParameter);
            Log.i("Api consume:", "Api request:::" + urlDecoded);
            Log.i("Api response:", "Api response here:::" + resp.getResponseData());


            isSuccess= true;
        } catch (UnsupportedEncodingException e) {
            Log.e("responseCatch", "using HttpURLConnectionHelper CATCH");
            e.printStackTrace();
        }
        return resp;
    }

    public  void processSynchResponseFromServer(Context context, byte[] syncResponse,String TABLE_NAME)
    {
        if(syncResponse != null)
        {
            Log.e("ResponseFromServer","Yes response gotten from server in byte " + syncResponse);
            String decodedDataUsingUTF8=null;
            try {
                decodedDataUsingUTF8 = new String(syncResponse, "UTF-8");  // Best way to decode using "UTF-8"
                Log.e("ResponseFromServer", "Yes response gotten from server after conversion " + decodedDataUsingUTF8);
                //TODO PROCESS THE JSON RESPONSE HERE
                JSONObject jsonReader = new JSONObject(decodedDataUsingUTF8);
                if(jsonReader != null)
                {
                    Log.e("ResponseFromServer", "Yes response gotten from server " + jsonReader);
                    JSONObject SYNC_RESULT = jsonReader.getJSONObject("SYNC_RESULT");
                    if(SYNC_RESULT != null)
                    {
                        Log.e("ResponseFromServer", "Yes response gotten from server " + SYNC_RESULT);
                        JSONArray jsonArray_TableResult = SYNC_RESULT.getJSONArray(TABLE_NAME);
                        if(jsonArray_TableResult != null)
                        {
                            Log.e("ResponseFromServer", "Yes response gotten from server " + jsonArray_TableResult);
                        }
                    }
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else
        {
            Log.e("ResponseFromServer","No response gotten from server");
        }
    }

    public boolean processSyncPush(Context context, byte[] syncResponse,String TABLE_NAME)
    {
        if(syncResponse != null)
        {
            Log.e("mResponse", "got a response"+ syncResponse);
            String decodedDataUsingUTF8=null;
            try {
                decodedDataUsingUTF8 = new String(syncResponse, "UTF-8");  // Best way to decode using "UTF-8"
                System.out.println("Text Decryted using UTF-8 : " + decodedDataUsingUTF8);
                //TODO PROCESS THE JSON RESPONSE HERE
                DBConnection myDBconnection;
                myDBconnection = new DBConnection(context);
                try {
                    myDBconnection.createDataBase();
                } catch (IOException e) {
                }
                if(myDBconnection.checkDataBase()){
                    myDBconnection.openDataBase();
                    SQLiteDatabase db = myDBconnection.getWritableDatabase();
                    ContentValues updateData = new ContentValues();

                    JSONObject jsonReader = new JSONObject(decodedDataUsingUTF8);
                    JSONObject SYNC_RESULT = jsonReader.getJSONObject("SYNC_RESULT");
                    JSONArray jsonArray_TableResult = SYNC_RESULT.getJSONArray(TABLE_NAME);

                    if(TABLE_NAME.equals("mobile_synch_buildings"))
                    {
                        for(int i=0; i<jsonArray_TableResult.length(); i++)
                        {
                            Log.e("UpdateResponse","Trying to update the sync_status of mobile_synch_buildings");
                            JSONObject c = jsonArray_TableResult.getJSONObject(i);
                            updateData.put("synch_status", c.getString("synch_status"));
                            String numRetry = dataDB.myConnection(context).selectFromTable("number_of_retry","mobile_synch_buildings","building_id",c.getString("building_id"));
                            if(numRetry != null) {
                                Log.i("num_of_retry update", "num_of_retry is found");
                                int cKount = Integer.valueOf(numRetry) + 1;
                                updateData.put("number_of_retry", cKount);
                            }
                            String building_id = c.getString("building_id");
                            //String[] arg = new String[]{meter_no};
                            Log.e("synch_status update", c.getString("synch_status"));
                            Log.i("building_id update", building_id);
                            //int updatedRec = db.update(TABLE_NAME, updateData, "building_id=" + building_id.trim(), null);
                            long updatedRec = dataDB.myConnection(context).onUpdateOrIgnore(updateData,TABLE_NAME,"building_id",building_id);
                            if (updatedRec > 0)
                            {
                                Log.e("synch_status update", "Just updated response for synch_status of mobile_synch_buildings");
                            }
                        }
                    }
                    if(TABLE_NAME.equals("mobile_synch_meter_readings") || TABLE_NAME.equals("mobile_synch_meter_reading"))
                    {
                        TABLE_NAME = "mobile_synch_meter_readings";
                        for(int i=0; i<jsonArray_TableResult.length(); i++)
                        {
                            JSONObject c = jsonArray_TableResult.getJSONObject(i);
                            updateData.put("synch_status",c.getString("synch_status"));
                            // TODO: 25/09/2015 we suppose to use meter_reading_id and not meter_no bcos is not unique. So ask the server to respond with the required fields
                            String numRetry = dataDB.myConnection(context).selectFromTable("number_of_retry", "mobile_synch_meter_readings", "meter_no", c.getString("meter_no"));
                            if(numRetry != null) {
                                int cKount = Integer.valueOf(numRetry) + 1;
                                updateData.put("number_of_retry", cKount);
                            }
                            //updateData.put("number_of_retry", "1");
                            String meter_no = c.getString("meter_no");
                            //String[] arg = new String[]{meter_no};
                            Log.e("mobile_synch_meter_readings synch_status", c.getString("synch_status")); //mobile_synch_meter_readings
                            Log.i("meter_no", meter_no);
                            db.update(TABLE_NAME, updateData, "meter_no=" + meter_no.trim(), null);
                        }
                    }
                    if(TABLE_NAME.equals("mobile_meter_reading_challenges"))
                    {
                        for(int i=0; i<jsonArray_TableResult.length(); i++)
                        {
                            JSONObject c = jsonArray_TableResult.getJSONObject(i);
                            updateData.put("synch_status",c.getString("synch_status"));
                            String numRetry = dataDB.myConnection(context).selectFromTable("number_of_retry","mobile_meter_reading_challenges","id_location_coordinate",c.getString("id_location_coordinate"));
                            if(numRetry != null) {
                                int cKount = Integer.valueOf(numRetry) + 1;
                                updateData.put("number_of_retry", cKount);
                            }
                            String id_location_coordinate = c.getString("id_location_coordinate");
                            //String[] arg = new String[]{meter_no};
                            Log.e(" mobile_meter_reading_challenges synch_status", c.getString("synch_status"));
                            Log.i("id_location_coordinate", id_location_coordinate);
                            db.update(TABLE_NAME, updateData, "id_location_coordinate=" + id_location_coordinate.trim(), null);
                        }
                    }

                    if(TABLE_NAME.equals("mobile_synch_profile"))
                    {
                        for(int i=0; i<jsonArray_TableResult.length(); i++)
                        {
                            JSONObject c = jsonArray_TableResult.getJSONObject(i);
                            if(c.getString("synch_status") != null && c.getString("profile_id") != null)
                            {
                                updateData.put("synch_status",c.getString("synch_status"));
                                String numRetry = dataDB.myConnection(context).selectFromTable("number_of_retry","mobile_synch_profile","profile_id",c.getString("profile_id"));
                                if(numRetry != null) {
                                    int cKount = Integer.valueOf(numRetry) + 1;
                                    updateData.put("number_of_retry", cKount);
                                }
                                String profile_id = c.getString("profile_id");
                                Log.e(" mobile_synch_profile synch_status", c.getString("synch_status"));
                                Log.i("profile_id", profile_id);
                                db.update(TABLE_NAME, updateData, "profile_id=" + profile_id, null);
                            }

                        }
                    }

                    if(TABLE_NAME.equals("mobile_synch_meters"))
                    {
                        for(int i=0; i<jsonArray_TableResult.length(); i++)
                        {
                            JSONObject c = jsonArray_TableResult.getJSONObject(i);
                            updateData.put("synch_status",c.getString("synch_status"));
                            String numRetry = dataDB.myConnection(context).selectFromTable("number_of_retry","mobile_synch_meters","meter_no",c.getString("meter_no"));
                            if(numRetry != null) {
                                int cKount = Integer.valueOf(numRetry) + 1;
                                updateData.put("number_of_retry", cKount);
                            }
                            String meter_no = c.getString("meter_no");
                            //String[] arg = new String[]{meter_no};
                            Log.e(" mobile_synch_meters synch_status", c.getString("synch_status"));
                            Log.i("meter_no", meter_no);
                            db.update(TABLE_NAME, updateData, "meter_no=" + meter_no.trim(), null);
                        }
                    }
                    if(TABLE_NAME.equals("mobile_synch_ireport"))
                    {
                        for(int i=0; i<jsonArray_TableResult.length(); i++)
                        {
                            JSONObject c = jsonArray_TableResult.getJSONObject(i);
                            updateData.put("synch_status",c.getString("synch_status"));
                            String numRetry = dataDB.myConnection(context).selectFromTable("number_of_retry","mobile_synch_ireport","ireport_id",c.getString("ireport_id"));
                            if(numRetry != null) {
                                int cKount = Integer.valueOf(numRetry) + 1;
                                updateData.put("number_of_retry", cKount);
                            }
                            String ireport_id = c.getString("ireport_id");
                            //String[] arg = new String[]{meter_no};
                            Log.e(" mobile_synch_ireport synch_status", c.getString("synch_status"));
                            Log.i("ireport_id", ireport_id);
                            db.update(TABLE_NAME, updateData, "ireport_id=" + ireport_id.trim(), null);
                        }
                    }
                    myDBconnection.close();
                }
                return true;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        return false;
    }
}
