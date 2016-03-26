package com.ensoft.mob.waterbiller.WebServices;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.ensoft.mob.waterbiller.DB.DBFirstSync;
import com.ensoft.mob.waterbiller.DB.DBStreet;
import com.ensoft.mob.waterbiller.DB.DataDB;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ebuka on 08/09/2015.
 */
public class InsertOrUpdateSync {
    DataDB dataDB = new DataDB();
    Calendar timer = Calendar.getInstance();
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");

    private static final String TAG = "InsertOrUpdateSync";
    //JSON Node Names
    public static final String TAG_TABLE_DATA = "table_data";
    public static final String TAG_TABLE_ASSIGNMENTS = "assignments";
    public static final String TAG_TABLE_USERS = "users";
    public static final String TAG_TABLE_STREETS = "synch_streets";
    public static final String TAG_TABLE_BUILDINGS = "synch_buildings";
    public static final String TAG_TABLE_ACCOUNTS = "synch_account_numbers";
    public static final String TAG_TABLE_PROFILES = "synch_profiles";
    public static final String TAG_TABLE_PROFILE_TYPES = "synch_profile_types";
    public static final String TAG_TABLE_ZONES = "ZONES";
    //public static final String TAG_TABLE_METER_ACCOUNTTYPE = "";
    public static final String TAG_TABLE_AREACODES = "synch_areacodes";
    public static final String TAG_TABLE_CHALLENGETYPE = "synch_meter_reading_challenge_types";
    public static final String TAG_TABLE_STREET_AREACODES = "_streets_areacodes";
    public static final String TAG_TABLE_METER_TYPE = "synch_meter_types";
    public static final String TAG_TABLE_METER_STATUS = "synch_meter_status";
    public static final String TAG_TABLE_BUILDINGS_CATEGORIES = "synch_building_categories";
    public static final String TAG_TABLE_CONSUMER_TYPES = "synch_consumer_types";
    public static final String TAG_TABLE_METER_INSTALLATION_TYPES = "synch_meter_installation_types";
    public static final String TAG_TABLE_ACCOUNT_BILLING = "synch_account_billing";
    public static final String TAG_TABLE_METER_READING = "synch_meter_reading";
    public static final String TAG_TABLE_PAYMENT_ITEMS = "synch_payment_items";
    public static final String TAG_TABLE_ACCESS_CONTROL = "access_control";

    JSONArray jsonArray = null;

    public InsertOrUpdateSync()
    {

    }
    public boolean sync_first_datByTable(Context context, String tTable, String jsonData){
       boolean issuccess = false;
           if(jsonData != null){
               try {
                   JSONObject jsonReader = new JSONObject(jsonData);
                   JSONObject tableData = jsonReader.getJSONObject(tTable);
               } catch (JSONException e) {
                   e.printStackTrace();
               }

           }

        return issuccess;
    }



    public JSONObject testSomething()
    {
        JSONArray responseJson = new JSONArray();
        for (int i = 0; i<3; i++)
        {
            JSONObject o = new JSONObject();
            try {
                o.put("status","1");
                o.put("idbuilding","235362");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            responseJson.put(o);
        }

        Log.e("TestOutput",responseJson.toString());
        JSONObject jo = new JSONObject();
        try {
            jo.put("_building",responseJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jo;
    }

    public JSONArray push_data_to_Assignment(Context context,JSONArray jsonArray)
    {
        //DBFirstSync dbFirstSync = new DBFirstSync(context);
        JSONArray responseJson = new JSONArray();

        //check if we have data in jsonArray_ASSIGN
        if(jsonArray.length()>0) {
            //loop tru the jsonarray to get json data

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = null;

                try {
                    c = jsonArray.getJSONObject(i);
                    ContentValues contentValues = new ContentValues();
                    //contentValues.put("id_assignment",c.getString("id_assignment"));
                    contentValues.put("users_id",c.getString("users_id"));
                    contentValues.put("street_areacodes_id",c.getString("street_areacodes_id"));
                    contentValues.put("assignment_id",c.getString("assignment_id"));
                    contentValues.put("assigned_by",c.getString("assigned_by"));
                    contentValues.put("assigned_on",c.getString("assigned_on"));
                    contentValues.put("service_id",c.getString("service_id"));

                    if(c.getString("assignment_id") != null)
                    {
                        JSONObject ob = new JSONObject();
                        // TODO: 19/09/2015 Call insert
                        long lastInsertId = dataDB.myConnection(context).onInsertOrUpdate(contentValues,TAG_TABLE_ASSIGNMENTS);
                        Log.e(TAG, TAG_TABLE_ASSIGNMENTS + "done" + String.valueOf(lastInsertId));
                        if(lastInsertId > 0){
                            String getStatus = dataDB.getRecordStatusByTableAndID(context, TAG_TABLE_ASSIGNMENTS, "status", "assignment_id", c.getString("assignment_id"));
                            if (getStatus != null) {
                                ob.put("assignment_id", c.getString("assignment_id"));
                                ob.put("status", getStatus);
                            }

                        }
                        responseJson.put(ob);

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
        return  responseJson;
    }

    public JSONArray push_data_to_Street(Context context,JSONArray jsonArray)
    {
        JSONArray responseJson = new JSONArray();
        if(jsonArray != null && jsonArray.length()>0)
        {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = null;

                try {
                    c = jsonArray.getJSONObject(i);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("street",c.getString("street"));
                    contentValues.put("city_id",c.getString("city_id"));
                    contentValues.put("latitude",c.getString("latitude"));
                    contentValues.put("longitude",c.getString("longitude"));
                    contentValues.put("street_id",c.getString("street_id"));
                    contentValues.put("area_code_id",c.getString("area_code_id"));
                    contentValues.put("city",c.getString("city"));
                    contentValues.put("lga",c.getString("lga"));
                    contentValues.put("zone",c.getString("zone"));
                    contentValues.put("mark_delete",c.getString("mark_delete"));
                    if(c.getString("street_id") != null)
                    {
                        JSONObject ob = new JSONObject();
                        // TODO: 19/09/2015 Call insert
                        long lastInsertId = dataDB.myConnection(context).onInsertOrUpdate(contentValues,TAG_TABLE_STREETS);

                        Log.e(TAG, TAG_TABLE_STREETS + "done" + String.valueOf(lastInsertId));
                        if(lastInsertId > 0){
                            ob.put("street_id", c.getString("street_id"));
                            ob.put("synch_status", "1");
                        }
                        else{
                            ob.put("street_id", c.getString("street_id"));
                            ob.put("synch_status", "0");
                        }
                        responseJson.put(ob);

                    }

                }
                catch(JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return responseJson;
    }
    public JSONArray push_data_to_Areacodes(Context context,JSONArray jsonArray)
    {
        Log.e("push_data_to_Areacodes","Just entered to save areacodes pushed to mobile");
        JSONArray responseJson = new JSONArray();
        if(jsonArray != null && jsonArray.length()>0)
        {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = null;

                try {
                    c = jsonArray.getJSONObject(i);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("area_name",c.getString("area_name"));
                    contentValues.put("area_code",c.getString("area_code"));
                    contentValues.put("area_code_id",c.getString("area_code_id"));
                    contentValues.put("mark_delete",c.getString("mark_delete"));
                    contentValues.put("service_id",c.getString("service_id"));
                    //contentValues.put("",c.getString("authorization_id"));
                    if(c.getString("area_code_id") != null)
                    {
                        JSONObject ob = new JSONObject();
                        // TODO: 19/09/2015 Call insert for areacodes
                        long lastInsertId = dataDB.myConnection(context).onInsertOrUpdate(contentValues,TAG_TABLE_AREACODES);

                        Log.e(TAG, TAG_TABLE_AREACODES + "done" + String.valueOf(lastInsertId));
                        if(lastInsertId > 0){
                                ob.put("area_code_id", c.getString("area_code_id"));
                                ob.put("synch_status", "1");
                        }
                        else{
                            ob.put("area_code_id", c.getString("area_code_id"));
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
    public JSONArray push_data_to_Building(Context context,JSONArray jsonArray)
    {
        JSONArray responseJson = new JSONArray();
        if (jsonArray != null && jsonArray.length() > 0)
        {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = null;

                try {
                    c = jsonArray.getJSONObject(i);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("building_name",c.getString("building_name") != null ? c.getString("building_name") : "");
                    contentValues.put("building_no",c.getString("building_number") != null ? c.getString("building_number") : "");
                    contentValues.put("building_image",c.getString("building_image") != null ? c.getString("building_image") : "");
                    contentValues.put("building_category_id",c.getString("building_category_id") != null ? c.getString("building_category_id") : "");
                    contentValues.put("street_id",c.getString("street_id"));
                    contentValues.put("state_id",c.getString("state_id"));
                    contentValues.put("latitude",c.getString("latitude") != null ? c.getString("latitude") : "");
                    contentValues.put("longitude",c.getString("longitude") != null ? c.getString("longitude") : "");
                    contentValues.put("building_id",c.getString("building_id") != null ? c.getString("building_id") : "");
                    contentValues.put("mark_delete",c.getString("mark_delete"));
                    contentValues.put("service_id", c.getString("service_id"));
                    if(c.getString("building_id") != null)
                    {
                        JSONObject ob = new JSONObject();
                        // TODO: 19/09/2015 Call insert for building
                        long lastInsertId = dataDB.myConnection(context).onInsertOrUpdate(contentValues,TAG_TABLE_BUILDINGS);

                        Log.e(TAG, TAG_TABLE_BUILDINGS + "done" + String.valueOf(lastInsertId));
                        if(lastInsertId > 0){
                            ob.put("building_id", c.getString("building_id"));
                            ob.put("synch_status", "1");
                        }
                        else
                        {
                            ob.put("building_id", c.getString("building_id"));
                            ob.put("synch_status", "0");
                        }
                        responseJson.put(ob);

                    }
                }
                catch(JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return responseJson;
    }
    public JSONArray push_data_to_BuildingCategory(Context context,JSONArray jsonArray)
    {
        JSONArray responseJson = new JSONArray();
        if (jsonArray != null && jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = null;

                try {
                    c = jsonArray.getJSONObject(i);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("building_category",c.getString("building_category"));
                    contentValues.put("building_category_id",c.getString("building_category_id"));
                    contentValues.put("mark_delete",c.getString("mark_delete"));
                    contentValues.put("service_id",c.getString("service_id"));

                    if(c.getString("building_category_id") != null)
                    {
                        JSONObject ob = new JSONObject();
                        // TODO: 19/09/2015 Call insert for building categories
                        if(dataDB.myConnection(context).deleteRecords(TAG_TABLE_BUILDINGS_CATEGORIES))
                        {
                            long lastInsertId = dataDB.myConnection(context).onInsertOrUpdate(contentValues, TAG_TABLE_BUILDINGS_CATEGORIES);

                            Log.e(TAG, TAG_TABLE_BUILDINGS_CATEGORIES + "done" + String.valueOf(lastInsertId));
                            if(lastInsertId > 0){

                                ob.put("building_category_id", c.getString("building_category_id"));
                                ob.put("synch_status", "1");

                            }
                            else
                            {
                                ob.put("building_category_id", c.getString("building_category_id"));
                                ob.put("synch_status", "0");
                            }
                        }

                        responseJson.put(ob);

                    }
                }
                catch(JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return responseJson;
    }
    public JSONArray push_data_to_Zones(Context context,JSONArray jsonArray)
    {
        JSONArray responseJson = new JSONArray();
        if(jsonArray != null && jsonArray.length()>0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = null;

                try {
                    c = jsonArray.getJSONObject(i);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("zone_id",c.getString("zone_id"));
                    contentValues.put("zone",c.getString("zone"));
                    contentValues.put("service_id",c.getString("service_id"));

                    if(c.getString("zone_id") != null)
                    {
                        JSONObject ob = new JSONObject();
                        // TODO: 19/09/2015 Call insert for zones
                        long lastInsertId = dataDB.myConnection(context).onInsertOrUpdate(contentValues,TAG_TABLE_ZONES);

                        Log.e(TAG, TAG_TABLE_ZONES + "done" + String.valueOf(lastInsertId));
                        if(lastInsertId > 0){
                            String getStatus = dataDB.getRecordStatusByTableAndID(context, TAG_TABLE_ZONES, "status", "zone_id", c.getString("zone_id"));
                            if (getStatus != null) {
                                ob.put("zone_id", c.getString("zone_id"));
                                ob.put("synch_status", getStatus);
                            }
                        }
                        responseJson.put(ob);

                    }

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return responseJson;
    }
    public JSONArray push_data_to_ChallengeType(Context context,JSONArray jsonArray)
    {
        JSONArray responseJson = new JSONArray();
        if(jsonArray != null && jsonArray.length()>0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = null;

                try {
                    c = jsonArray.getJSONObject(i);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("challenge_type_id",c.getString("challenge_type_id"));
                    contentValues.put("challenge",c.getString("challenge"));
                    contentValues.put("mark_delete", c.getString("mark_delete"));

                    if(c.getString("challenge_type_id") != null)
                    {
                        JSONObject ob = new JSONObject();
                        // TODO: 19/09/2015 Call insert for Challenge Type
                        long lastInsertId = dataDB.myConnection(context).onInsertOrUpdate(contentValues,"challengetype");

                        Log.e(TAG, TAG_TABLE_CHALLENGETYPE + "done" + String.valueOf(lastInsertId));
                        if(lastInsertId > 0){
                                ob.put("challenge_type_id", c.getString("challenge_type_id"));
                                ob.put("synch_status", "1");
                        }
                        else
                        {
                            ob.put("challenge_type_id", c.getString("challenge_type_id"));
                            ob.put("synch_status", "0");
                        }
                        responseJson.put(ob);

                    }

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return responseJson;
    }
    public JSONArray push_data_to_meter_status(Context context,JSONArray jsonArray)
    {
        JSONArray responseJson = new JSONArray();
        if(jsonArray != null && jsonArray.length()>0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = null;

                try {
                    c = jsonArray.getJSONObject(i);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("meter_status_id",c.getString("meter_status_id"));
                    contentValues.put("status",c.getString("status"));
                    contentValues.put("mark_delete",c.getString("mark_delete"));

                    if(c.getString("status") != null && c.getString("meter_status_id") != null)
                    {
                        JSONObject ob = new JSONObject();
                        // TODO: 19/09/2015 Call insert for Meter Status
                        long lastInsertId = dataDB.myConnection(context).onInsertOrUpdate(contentValues,"_meter_status");

                        Log.e(TAG, TAG_TABLE_METER_TYPE + "done" + String.valueOf(lastInsertId));
                        if(lastInsertId > 0){

                            ob.put("meter_status_id", c.getString("meter_status_id"));
                            ob.put("synch_status", "1");

                        }
                        else{
                            ob.put("meter_status_id", c.getString("meter_status_id"));
                            ob.put("synch_status", "0");
                        }
                        responseJson.put(ob);

                    }

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return responseJson;
    }
    public JSONArray push_data_to_meter_type(Context context,JSONArray jsonArray)
    {
        JSONArray responseJson = new JSONArray();
        if(jsonArray != null && jsonArray.length()>0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = null;

                try {
                    c = jsonArray.getJSONObject(i);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("meter_type_id",c.getString("meter_type_id"));
                    contentValues.put("meter_type",c.getString("meter_type"));
                    contentValues.put("mark_delete",c.getString("mark_delete"));

                    if(c.getString("meter_type") != null && c.getString("meter_type_id") != null)
                    {
                        JSONObject ob = new JSONObject();
                        // TODO: 19/09/2015 Call insert for Meter Type
                        long lastInsertId = dataDB.myConnection(context).onInsertOrUpdate(contentValues,"_meter_types");

                        Log.e(TAG, TAG_TABLE_METER_TYPE + "done" + String.valueOf(lastInsertId));
                        if(lastInsertId > 0){
                            //String getStatus = dataDB.getRecordStatusByTableAndID(context, TAG_TABLE_METER_TYPE, "synch_status", "meter_type", c.getString("meter_type"));
                            //if (getStatus != null) {
                            ob.put("meter_type_id", c.getString("meter_type_id"));
                            ob.put("synch_status", "1");
                            //}
                        }
                        else{
                            ob.put("meter_type_id", c.getString("meter_type_id"));
                            ob.put("synch_status", "0");
                        }
                        responseJson.put(ob);

                    }

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return responseJson;
    }
    public JSONArray push_data_to_consumer_types(Context context,JSONArray jsonArray)
    {
        JSONArray responseJson = new JSONArray();
        if(jsonArray != null && jsonArray.length()>0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = null;

                try {
                    c = jsonArray.getJSONObject(i);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("consumer_type_id",c.getString("consumer_type_id"));
                    contentValues.put("consumer_type",c.getString("consumer_type"));
                    //contentValues.put("service_id",c.getString("service_id"));
                    contentValues.put("mark_delete",c.getString("mark_delete"));

                    if(c.getString("consumer_type") != null && c.getString("consumer_type_id") != null)
                    {
                        JSONObject ob = new JSONObject();
                        // TODO: 19/09/2015 Call insert for ConsumerTypes
                        long lastInsertId = dataDB.myConnection(context).onInsertOrUpdate(contentValues,"_consumer_types");

                        Log.e(TAG, TAG_TABLE_CONSUMER_TYPES + "done" + String.valueOf(lastInsertId));
                        if(lastInsertId > 0){
                            //String getStatus = dataDB.getRecordStatusByTableAndID(context, TAG_TABLE_METER_TYPE, "synch_status", "meter_type", c.getString("meter_type"));
                            //if (getStatus != null) {
                            ob.put("consumer_type_id", c.getString("consumer_type_id"));
                            ob.put("synch_status", "1");
                            //}
                        }
                        else{
                            ob.put("consumer_type_id", c.getString("consumer_type_id"));
                            ob.put("synch_status", "0");
                        }
                        responseJson.put(ob);

                    }

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return responseJson;
    }
    public JSONArray push_data_to_meter_installation_type(Context context,JSONArray jsonArray)
    {
        JSONArray responseJson = new JSONArray();
        if(jsonArray != null && jsonArray.length()>0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = null;

                try {
                    c = jsonArray.getJSONObject(i);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("meter_installation_type_id",c.getString("meter_installation_type_id"));
                    contentValues.put("meter_installation_type",c.getString("meter_installation_type"));
                    //contentValues.put("service_id",c.getString("service_id"));
                    contentValues.put("mark_delete",c.getString("mark_delete"));

                    if(c.getString("meter_installation_type") != null && c.getString("meter_installation_type") != null)
                    {
                        JSONObject ob = new JSONObject();
                        // TODO: 19/09/2015 Call insert for Meter Installation Type
                        long lastInsertId = dataDB.myConnection(context).onInsertOrUpdate(contentValues,"_meter_installation_types");

                        Log.e(TAG, TAG_TABLE_METER_INSTALLATION_TYPES + "done" + String.valueOf(lastInsertId));
                        if(lastInsertId > 0){
                            //String getStatus = dataDB.getRecordStatusByTableAndID(context, TAG_TABLE_METER_TYPE, "synch_status", "meter_type", c.getString("meter_type"));
                            //if (getStatus != null) {
                            ob.put("meter_installation_type_id", c.getString("meter_installation_type_id"));
                            ob.put("synch_status", "1");
                            //}
                        }
                        else{
                            ob.put("meter_installation_type_id", c.getString("meter_installation_type_id"));
                            ob.put("synch_status", "0");
                        }
                        responseJson.put(ob);

                    }

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return responseJson;
    }
    public JSONArray push_data_to_Account(Context context,JSONArray jsonArray)
    {
        JSONArray responseJson = new JSONArray();
        if(jsonArray != null && jsonArray.length()>0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = null;

                try {
                    c = jsonArray.getJSONObject(i);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("account_number",c.getString("account_number"));
                    contentValues.put("profile_id",c.getString("profile_id"));
                    contentValues.put("billing_type_id",c.getString("billing_type_id"));
                    contentValues.put("valve_id",c.getString("valve_id"));
                    contentValues.put("building_id",c.getString("building_id"));
                    contentValues.put("meter_no",c.getString("meter_no"));
                    contentValues.put("service_id",c.getString("service_id"));
                    contentValues.put("user_id", c.getString("user_id"));
                    contentValues.put("status", c.getString("status"));
                    contentValues.put("mark_delete",c.getString("mark_delete"));

                    if(c.getString("account_number") != null && c.getString("profile_id") != null)
                    {
                        JSONObject ob = new JSONObject();
                        // TODO: 19/09/2015 Call insert for zones
                        long lastInsertId = dataDB.myConnection(context).onInsertOrUpdate(contentValues,TAG_TABLE_ACCOUNTS);

                        Log.e(TAG, TAG_TABLE_ACCOUNTS + "done" + String.valueOf(lastInsertId));
                        if(lastInsertId > 0){
                            //String getStatus = dataDB.getRecordStatusByTableAndID(context, TAG_TABLE_ACCOUNTS, "synch_status", "account_no_id", c.getString("account_no_id"));
                            //if (getStatus != null) {
                                ob.put("account_number", c.getString("account_number"));
                                ob.put("synch_status", "1");
                            //}
                        }
                        else
                        {
                            ob.put("account_number", c.getString("account_number"));
                            ob.put("synch_status", "0");
                        }
                        responseJson.put(ob);

                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return responseJson;
    }
    public JSONArray push_data_to_Profile(Context context,JSONArray jsonArray)
    {
        JSONArray responseJson = new JSONArray();
        if (jsonArray != null && jsonArray.length() > 0)
        {
            for(int i=0; i<jsonArray.length(); i++)
            {
                try{
                    JSONObject c = jsonArray.getJSONObject(i);
                    ContentValues contentValues = new ContentValues();
                    //contentValues.put("idprofile",c.getString("idprofile"));
                    contentValues.put("first_name",c.getString("first_name"));
                    contentValues.put("middle_name",c.getString("middle_name"));
                    contentValues.put("last_name",c.getString("last_name"));
                    contentValues.put("address",c.getString("address"));
                    contentValues.put("service_id",c.getString("service_id"));
                    contentValues.put("nationality",c.getString("nationality"));
                    contentValues.put("country_code",c.getString("country_code"));
                    contentValues.put("email",c.getString("email"));
                    contentValues.put("phone",c.getString("phone"));
                    contentValues.put("profile_id",c.getString("profile_id"));
                    contentValues.put("profile_type_id", c.getString("profile_type_id"));
                    contentValues.put("photo", c.getString("photo"));
                    contentValues.put("mark_delete",c.getString("mark_delete"));
                    contentValues.put("corporate_name",c.getString("corporate_name"));
                    contentValues.put("state_id",c.getString("state_id"));
                    contentValues.put("rcc_number",c.getString("rcc_number"));
                    contentValues.put("name", c.getString("name"));
                    contentValues.put("city", c.getString("city"));
                    contentValues.put("lga", c.getString("lga"));
                    contentValues.put("zone", c.getString("zone"));

                    if(c.getString("profile_id") != null)
                    {
                        JSONObject ob = new JSONObject();
                        // TODO: 19/09/2015 Call insert for Profile
                        long lastInsertId = dataDB.myConnection(context).onInsertOrUpdate(contentValues,TAG_TABLE_PROFILES);

                        Log.e(TAG, TAG_TABLE_PROFILES + "done" + String.valueOf(lastInsertId));
                        if(lastInsertId > 0){
                                ob.put("profile_id", c.getString("profile_id"));
                                ob.put("synch_status", "1");
                        }
                        else{
                            ob.put("profile_id", c.getString("profile_id"));
                            ob.put("synch_status", "0");
                        }
                        responseJson.put(ob);

                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }

        return responseJson;
    }

    public JSONArray push_data_to_ProfileType(Context context, JSONArray jsonArray)
    {
        JSONArray responseJson = new JSONArray();
        // TODO: 28/11/2015 check if we have data in jsonArray
        if(jsonArray.length() > 0)
        {
            // TODO: 28/11/2015 loop to get from jsonArray
            for (int i = 0; i<jsonArray.length(); i++)
            {
                try{
                    JSONObject c = jsonArray.getJSONObject(i);
                    // TODO: 16/09/2015 Build ContentValues to update profile_types and set authorized to 1
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("profile_type_id",c.getString("profile_type_id"));
                    contentValues.put("profile_type",c.getString("profile_type"));
                    contentValues.put("user_id",c.getString("user_id"));
                    contentValues.put("mark_delete",c.getString("mark_delete"));

                    if(c.getString("profile_type_id") != null)
                    {
                        JSONObject ob = new JSONObject();
                        // TODO: 19/09/2015 Call insert
                        long lastInsertId = dataDB.myConnection(context).onInsertOrUpdate(contentValues,TAG_TABLE_PROFILE_TYPES);
                        Log.e(TAG, TAG_TABLE_PROFILE_TYPES + "done in profile_types" + String.valueOf(lastInsertId));
                        if(lastInsertId != -1){
                            ob.put("profile_type_id", c.getString("profile_type_id"));
                            ob.put("synch_status", "1");
                        }
                        else{
                            ob.put("profile_type_id", c.getString("profile_type_id"));
                            ob.put("synch_status", "0");
                        }
                        responseJson.put(ob);

                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return responseJson;
    }

    public JSONArray push_data_to_account_billing(Context context, JSONArray jsonArray)
    {
        JSONArray responseJson = new JSONArray();
        //check if we have data in jsonArray
        if(jsonArray.length()>0) {
            //repeat to get data from jsonarray
            for(int i=0; i<jsonArray.length(); i++) {
                try {
                    JSONObject c = jsonArray.getJSONObject(i);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("meter_no",c.getString("meter_no"));
                    contentValues.put("account_number", c.getString("account_number"));
                    contentValues.put("previous_reading",c.getString("previous_reading"));
                    contentValues.put("current_reading",c.getString("current_reading"));
                    contentValues.put("date_previous_reading",c.getString("date_previous_reading"));
                    contentValues.put("date_current_reading",c.getString("date_current_reading"));
                    contentValues.put("previous_total_units",c.getString("previous_total_units"));
                    contentValues.put("total_units",c.getString("total_units"));
                    contentValues.put("rate",c.getString("rate"));
                    contentValues.put("amountDue",c.getString("amountDue"));
                    contentValues.put("reading_timestamp",c.getString("reading_timestamp"));
                    contentValues.put("building_id",c.getString("building_id"));
                    contentValues.put("building_category_id",c.getString("building_category_id"));
                    contentValues.put("area_code_id",c.getString("area_code_id"));
                    contentValues.put("previous_charge",c.getString("previous_charge"));
                    contentValues.put("previous_rate",c.getString("previous_rate"));
                    contentValues.put("billing_no",c.getString("billing_no"));
                    contentValues.put("invoice_date",c.getString("invoice_date"));
                    contentValues.put("billing_period",c.getString("billing_period"));
                    contentValues.put("due_date",c.getString("due_date"));
                    contentValues.put("payment_status",c.getString("payment_status"));
                    contentValues.put("batch",c.getString("batch"));
                    //contentValues.put("user_id", c.getString("user_id"));
                    contentValues.put("officer_user_id", c.getString("officer_user_id"));
                    contentValues.put("service_id", c.getString("service_id"));
                    contentValues.put("mark_delete",c.getString("mark_delete"));


                    if(c.getString("billing_no") != null && c.getString("account_number") != null && c.getString("meter_no") != null)
                    {
                        JSONObject ob = new JSONObject();
                        // TODO: 19/09/2015 Call insert
                        long lastInsertId = dataDB.myConnection(context).onInsertOrUpdate(contentValues,TAG_TABLE_ACCOUNT_BILLING);
                        Log.e(TAG, TAG_TABLE_ACCOUNT_BILLING + "done in account_billing" + String.valueOf(lastInsertId));
                        if(lastInsertId != -1){

                            ob.put("billing_no", c.getString("billing_no"));
                            ob.put("synch_status", "1");

                            // TODO: 16/09/2015 Build ContentValues to update account_billing and set authorized to 1

                        }
                        else{
                            ob.put("billing_no", c.getString("billing_no"));
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

    public JSONArray push_data_to_Payment_Items(Context context, JSONArray jsonArray)
    {
        JSONArray responseJson = new JSONArray();
        // TODO: 28/11/2015 check if we have data in jsonArray
        if(jsonArray.length() > 0)
        {
            // TODO: 28/11/2015 loop to get from jsonArray
            for (int i = 0; i<jsonArray.length(); i++)
            {
                try{
                    JSONObject c = jsonArray.getJSONObject(i);
                    // TODO: 16/09/2015 Build ContentValues to update payment_items and set authorized to 1
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("service_id",c.getString("service_id"));
                    contentValues.put("item_number",c.getString("item_number"));
                    contentValues.put("item_code",c.getString("item_code"));
                    contentValues.put("item_name",c.getString("item_name"));
                    contentValues.put("mark_delete", c.getString("mark_delete"));

                    if(c.getString("item_code") != null && c.getString("item_name") != null)
                    {
                        JSONObject ob = new JSONObject();
                        // TODO: 19/09/2015 Call insert
                        long lastInsertId = dataDB.myConnection(context).onInsertOrUpdate(contentValues,"_payment_items");
                        Log.e(TAG, TAG_TABLE_PAYMENT_ITEMS + "done in payment_items" + String.valueOf(lastInsertId));
                        if(lastInsertId != -1){
                            ob.put("item_code", c.getString("item_code"));
                            ob.put("synch_status", "1");
                        }
                        else{
                            ob.put("item_code", c.getString("item_code"));
                            ob.put("synch_status", "0");
                        }
                        responseJson.put(ob);

                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return responseJson;
    }

    public JSONArray push_data_to_AccessControl(Context context, JSONArray jsonArray)
    {
        JSONArray responseJson = new JSONArray();
        // TODO: 28/11/2015 check if we have data in jsonArray
        if(jsonArray.length() > 0) {
            // TODO: 28/11/2015 loop to get from jsonArray
            for (int i = 0; i < jsonArray.length(); i++) {
                try{
                    JSONObject c = jsonArray.getJSONObject(i);
                    // TODO: 16/09/2015 Build ContentValues to update access_control and set authorized to 1
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("user_id",c.getString("user_id"));
                    contentValues.put("can_create_customer_profile",c.getString("can_create_customer_profile"));
                    contentValues.put("can_view_customer_profile",c.getString("can_view_customer_profile"));
                    contentValues.put("can_handle_zone",c.getString("can_handle_zone"));
                    contentValues.put("can_handle_area_cluster",c.getString("can_handle_area_cluster"));
                    contentValues.put("can_handle_street",c.getString("can_handle_street"));
                    contentValues.put("can_do_enumeration",c.getString("can_do_enumeration"));
                    contentValues.put("can_do_meter_reading",c.getString("can_do_meter_reading"));
                    contentValues.put("can_view_bill",c.getString("can_view_bill"));
                    contentValues.put("can_make_payment",c.getString("can_make_payment"));
                    contentValues.put("can_view_payment_report",c.getString("can_view_payment_report"));
                    contentValues.put("can_do_ireport",c.getString("can_do_ireport"));
                    contentValues.put("can_view_ireport",c.getString("can_view_ireport"));
                    contentValues.put("can_view_notification",c.getString("can_view_notification"));
                    contentValues.put("can_create_assign_task",c.getString("can_create_assign_task"));
                    contentValues.put("can_view_task",c.getString("can_view_task"));
                    contentValues.put("service_id",c.getString("service_id"));

                    if(c.getString("user_id") != null) {
                        JSONObject ob = new JSONObject();
                        // TODO: 13/03/2016 Delete the existing access record
                        dataDB.myConnection(context).deleteRecords("access_control");
                        // TODO: 19/09/2015 Call insert
                        long lastInsertId = dataDB.myConnection(context).onInsertOrUpdate(contentValues,"access_control");
                        Log.e(TAG, TAG_TABLE_ACCESS_CONTROL + "done in access_control" + String.valueOf(lastInsertId));
                        if(lastInsertId != -1){
                            ob.put("user_id", c.getString("user_id"));
                            ob.put("synch_status", "1");
                        }
                        else{
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
                    contentValues.put("password",c.getString("password"));
                    contentValues.put("phone",c.getString("phone"));
                    contentValues.put("group_id", c.getString("group_id"));
                    contentValues.put("zone_id",c.getString("zone_id"));

                    if(c.getString("user_id") != null)
                    {
                        JSONObject ob = new JSONObject();
                        // TODO: 19/09/2015 Call insert
                        long lastInsertId = dataDB.myConnection(context).onInsertOrUpdate(contentValues,TAG_TABLE_USERS);
                        Log.e(TAG, TAG_TABLE_USERS + "done in users" + String.valueOf(lastInsertId));
                        if(lastInsertId != -1){
                            //String getStatus = dataDB.getRecordStatusByTableAndID(context, TAG_TABLE_USERS, "status", "users_id", c.getString("users_id"));
                            //if (getStatus != null) {
                                ob.put("users_id", c.getString("user_id"));
                                ob.put("sync_status", "1");
                            //}

                            // TODO: 16/09/2015 Build ContentValues to update client_table and set authorized to 1

                        }
                        else{
                            ob.put("user_id", c.getString("user_id"));
                            ob.put("sync_status", "0");
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

    public boolean sync_first_data(Context context,String jsonData)
    {
        DBStreet dbStreet = new DBStreet(context);
        DBFirstSync dbFirstSync = new DBFirstSync(context);
        boolean itSynced = false;
        if(jsonData != null)
        {
            try{
                Log.e("responseTry", "started processResponse");
                JSONObject jsonReader = new JSONObject(jsonData);
                JSONObject tableData = jsonReader.getJSONObject(TAG_TABLE_DATA);
                JSONArray jsonArray_ASSIGN = tableData.getJSONArray(TAG_TABLE_ASSIGNMENTS);
                JSONArray jsonArray_USERS = tableData.getJSONArray(TAG_TABLE_USERS);
                JSONArray jsonArray_STREETS = tableData.getJSONArray(TAG_TABLE_STREETS);
                JSONArray jsonArray_BUILDINGS = tableData.getJSONArray(TAG_TABLE_BUILDINGS);
                JSONArray jsonArray_ACCOUNT = tableData.getJSONArray(TAG_TABLE_ACCOUNTS);
                JSONArray jsonArray_PROFILES = tableData.getJSONArray(TAG_TABLE_PROFILES);
                JSONArray jsonArray_ZONES = tableData.getJSONArray(TAG_TABLE_ZONES);
                JSONArray jsonArray_AREACODES = tableData.getJSONArray(TAG_TABLE_AREACODES);
                JSONArray jsonArray_BUILDING_CATEGORY = tableData.getJSONArray(TAG_TABLE_BUILDINGS_CATEGORIES);
                JSONArray jsonArray_PAYMENT_ITEMS = tableData.getJSONArray(TAG_TABLE_PAYMENT_ITEMS);

                //check if we have data in jsonArray_ASSIGN
                if(jsonArray_ASSIGN.length()>0)
                {
                    //loop tru the jsonarray to get json data
                    for(int i=0; i<jsonArray_ASSIGN.length(); i++)
                    {
                        JSONObject c = jsonArray_ASSIGN.getJSONObject(i);
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("id_assignment",c.getString("id_assignment"));
                        contentValues.put("users_id",c.getString("users_id"));
                        contentValues.put("street_areacodes_id",c.getString("street_areacodes_id"));
                        contentValues.put("assignment_id",c.getString("assignment_id"));
                        contentValues.put("assigned_by",c.getString("assigned_by"));
                        contentValues.put("assigned_on",c.getString("assigned_on"));
                        contentValues.put("service_id",c.getString("service_id"));
                        contentValues.put("session_id",c.getString("session_id"));
                        contentValues.put("status",c.getString("status"));
                        contentValues.put("registered_by",c.getString("registered_by"));
                        contentValues.put("registered_on",c.getString("registered_on"));
                        contentValues.put("authorized",c.getString("authorized"));
                        contentValues.put("authorized_by",c.getString("authorized_by"));
                        contentValues.put("authorized_on",c.getString("authorized_on"));
                        contentValues.put("last_disabled_by",c.getString("last_disabled_by"));
                        contentValues.put("last_disabled_on",c.getString("last_disabled_on"));
                        contentValues.put("last_disable_reason",c.getString("last_disable_reason"));
                        contentValues.put("last_enabled_by",c.getString("last_enabled_by"));
                        contentValues.put("last_enabled_on",c.getString("last_enabled_on"));
                        contentValues.put("last_enable_reason",c.getString("last_enable_reason"));

                        boolean insertOrUpdateValues = dbFirstSync.
                                syncToTables(TAG_TABLE_ASSIGNMENTS, contentValues, "id_assignment", c.getString("id_assignment"));
                        Log.e(TAG,TAG_TABLE_ASSIGNMENTS + "done" + String.valueOf(insertOrUpdateValues));

                        /*String id_assignment = c.getString("id_assignment");
                        String users_id = c.getString("users_id");
                        String street_areacodes_id = c.getString("street_areacodes_id");
                        String assignment_id = c.getString("assignment_id");
                        String assigned_by = c.getString("assigned_by");
                        String assigned_on = c.getString("assigned_on");
                        String service_id = c.getString("service_id");
                        String session_id = c.getString("session_id");
                        String status = c.getString("status");
                        String registered_by = c.getString("registered_by");
                        String registered_on = c.getString("registered_on");
                        String authorized = c.getString("authorized");
                        String authorized_by = c.getString("authorized_by");
                        String authorized_on = c.getString("authorized_on");
                        String last_disabled_by = c.getString("last_disabled_by");
                        String last_disabled_on = c.getString("last_disabled_on");
                        String last_disable_reason = c.getString("last_disable_reason");
                        String last_enabled_by = c.getString("last_enabled_by");
                        String last_enabled_on = c.getString("last_enabled_on");
                        String last_enable_reason = c.getString("last_enable_reason");*/
                    }
                }

                // if we have data in jsonArray_USERS
                if(jsonArray_USERS.length()>0)
                {
                    //repeat to get data from jsonarray
                    for(int i=0; i<jsonArray_USERS.length(); i++)
                    {
                        JSONObject c = jsonArray_USERS.getJSONObject(i);

                        /*String id_users = c.getString("id_users");
                        String first_name = c.getString("first_name");
                        String middle_name = c.getString("middle_name");
                        String last_name = c.getString("last_name");
                        String email = c.getString("email");
                        String password = c.getString("password");
                        String phone = c.getString("phone");
                        String zone_id = c.getString("zone_id");
                        String group_id = c.getString("group_id");
                        String created_by = c.getString("created_by");
                        String created_on = c.getString("created_on");
                        String inactive = c.getString("inactive");
                        String service_id = c.getString("service_id");
                        String users_id = c.getString("users_id");*/

                        ContentValues contentValues = new ContentValues();
                        contentValues.put("id_users",c.getString("id_users"));
                        contentValues.put("first_name",c.getString("first_name"));
                        contentValues.put("middle_name",c.getString("middle_name"));
                        contentValues.put("last_name",c.getString("last_name"));
                        contentValues.put("email",c.getString("email"));
                        contentValues.put("password",c.getString("password"));
                        contentValues.put("phone",c.getString("phone"));
                        contentValues.put("zone_id",c.getString("zone_id"));
                        contentValues.put("group_id",c.getString("group_id"));
                        contentValues.put("created_by",c.getString("created_by"));
                        contentValues.put("created_on",c.getString("created_on"));
                        contentValues.put("inactive",c.getString("inactive"));
                        contentValues.put("service_id",c.getString("service_id"));
                        contentValues.put("users_id",c.getString("users_id"));

                        boolean insertOrUpdateValues = dbFirstSync.
                                syncToTables(TAG_TABLE_USERS, contentValues, "id_users", c.getString("id_users"));
                        Log.e(TAG, TAG_TABLE_USERS + "done" + String.valueOf(insertOrUpdateValues));

                    }
                }

                // if we have data in jsonArray_STREETS
                if(jsonArray_STREETS.length()>0)
                {
                    //repeat to get data from jsonarray
                    for(int i=0; i<jsonArray_STREETS.length(); i++)
                    {
                        JSONObject c = jsonArray_STREETS.getJSONObject(i);

                        /*String idstreet = c.getString("idstreet");
                        String street = c.getString("street");
                        String city_id = c.getString("city_id");
                        String latitude = c.getString("latitude");
                        String longitude = c.getString("longitude");
                        String street_id = c.getString("street_id");
                        String service_id = c.getString("service_id");
                        String session_id = c.getString("session_id");
                        String status = c.getString("status");
                        String registered_by = c.getString("registered_by");
                        String registered_on = c.getString("registered_on");
                        String authorized = c.getString("authorized");
                        String authorized_by = c.getString("authorized_by");
                        String authorized_on = c.getString("authorized_on");
                        String last_disabled_by = c.getString("last_disabled_by");
                        String last_disabled_on = c.getString("last_disabled_on");
                        String last_disable_reason = c.getString("last_disable_reason");
                        String last_enabled_by = c.getString("last_enabled_by");
                        String last_enabled_on = c.getString("last_enabled_on");
                        String last_enable_reason = c.getString("last_enable_reason");*/

                        ContentValues contentValues = new ContentValues();
                        contentValues.put("idstreet",c.getString("idstreet"));
                        contentValues.put("street",c.getString("street"));
                        contentValues.put("city_id",c.getString("city_id"));
                        contentValues.put("latitude",c.getString("latitude"));
                        contentValues.put("longitude",c.getString("longitude"));
                        contentValues.put("street_id",c.getString("street_id"));
                        contentValues.put("service_id",c.getString("service_id"));
                        contentValues.put("session_id",c.getString("session_id"));
                        contentValues.put("status",c.getString("status"));
                        contentValues.put("registered_by",c.getString("registered_by"));
                        contentValues.put("registered_on",c.getString("registered_on"));
                        contentValues.put("authorized",c.getString("authorized"));
                        contentValues.put("authorized_by",c.getString("authorized_by"));
                        contentValues.put("last_disabled_by",c.getString("last_disabled_by"));
                        contentValues.put("last_disabled_on",c.getString("last_disabled_on"));
                        contentValues.put("last_disable_reason",c.getString("last_disable_reason"));
                        contentValues.put("last_enabled_by",c.getString("last_enabled_by"));
                        contentValues.put("last_enabled_on",c.getString("last_enabled_on"));
                        contentValues.put("last_enable_reason",c.getString("last_enable_reason"));

                        boolean insertOrUpdateValues = dbFirstSync.
                                syncToTables(TAG_TABLE_STREETS, contentValues, "idstreet", c.getString("idstreet"));
                        Log.e(TAG, TAG_TABLE_STREETS + "done" + String.valueOf(insertOrUpdateValues));
                    }
                }

                //if we have data in jsonArray_BUILDING_CATEGORIES
                if(jsonArray_BUILDING_CATEGORY.length()>0)
                {
                    //iterate to get data from jsonArray
                    for(int i=0; i<jsonArray_BUILDING_CATEGORY.length(); i++)
                    {
                        JSONObject c = jsonArray_BUILDING_CATEGORY.getJSONObject(i);
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("idbuilding_category",c.getString("idbuilding_category"));
                        contentValues.put("building_category",c.getString("building_category"));
                        contentValues.put("building_categories_id",c.getString("building_categories_id"));
                        contentValues.put("service_id",c.getString("service_id"));

                        boolean insertOrUpdateValues = dbFirstSync.
                                syncToTables(TAG_TABLE_BUILDINGS_CATEGORIES, contentValues, "idbuilding_category", c.getString("idbuilding_category"));
                        Log.e(TAG, TAG_TABLE_BUILDINGS_CATEGORIES + "done" + String.valueOf(insertOrUpdateValues));

                    }
                }

                // if we have data in jsonArray_BUILDINGS
                if(jsonArray_BUILDINGS.length()>0)
                {
                    //repeat to get data from jsonarray
                    for(int i=0; i<jsonArray_BUILDINGS.length(); i++)
                    {
                        JSONObject c = jsonArray_BUILDINGS.getJSONObject(i);

                        ContentValues contentValues = new ContentValues();
                        contentValues.put("idbuilding",c.getString("idbuilding"));
                        contentValues.put("building_name",c.getString("building_name"));
                        contentValues.put("building_image",c.getString("building_image"));
                        contentValues.put("building_categories_id",c.getString("building_categories_id"));
                        contentValues.put("street_id",c.getString("street_id"));
                        contentValues.put("",c.getString(""));
                        contentValues.put("state_id",c.getString("state_id"));
                        contentValues.put("service_id",c.getString("service_id"));
                        contentValues.put("authorization_id",c.getString("authorization_id"));
                        contentValues.put("latitude",c.getString("latitude"));
                        contentValues.put("longitude",c.getString("longitude"));
                        contentValues.put("building_id",c.getString("building_id"));
                        contentValues.put("status",c.getString("status"));
                        contentValues.put("registered_by",c.getString("registered_by"));
                        contentValues.put("registered_on",c.getString("registered_on"));
                        contentValues.put("authorized",c.getString("authorized"));
                        contentValues.put("authorized_by",c.getString("authorized_by"));
                        contentValues.put("authorized_on",c.getString("authorized_on"));
                        contentValues.put("last_disabled_by",c.getString("last_disabled_by"));
                        contentValues.put("last_disabled_on",c.getString("last_disabled_on"));
                        contentValues.put("last_disable_reason",c.getString("last_disable_reason"));
                        contentValues.put("last_enabled_by",c.getString("last_enabled_by"));
                        contentValues.put("last_enabled_on",c.getString("last_enabled_on"));
                        contentValues.put("last_enable_reason",c.getString("last_enable_reason"));

                        boolean insertOrUpdateValues = dbFirstSync.
                                syncToTables(TAG_TABLE_BUILDINGS, contentValues, "idbuilding", c.getString("idbuilding"));
                        Log.e(TAG, TAG_TABLE_BUILDINGS + "done" + String.valueOf(insertOrUpdateValues));

                        /*String idbuilding = c.getString("idbuilding");
                        String building_name = c.getString("building_name");
                        String building_image = c.getString("building_image");
                        String building_categories_id = c.getString("building_categories_id");
                        String street_id = c.getString("street_id");
                        String state_id = c.getString("state_id");
                        String service_id = c.getString("service_id");
                        String authorization_id = c.getString("authorization_id");
                        String latitude = c.getString("latitude");
                        String longitude = c.getString("longitude");
                        String building_id = c.getString("building_id");
                        String status = c.getString("status");
                        String registered_by = c.getString("registered_by");
                        String registered_on = c.getString("registered_on");
                        String authorized = c.getString("authorized");
                        String authorized_by = c.getString("authorized_by");
                        String authorized_on = c.getString("authorized_on");
                        String last_disabled_by = c.getString("last_disabled_by");
                        String last_disabled_on = c.getString("last_disabled_on");
                        String last_disable_reason = c.getString("last_disable_reason");
                        String last_enabled_by = c.getString("last_enabled_by");
                        String last_enabled_on = c.getString("last_enabled_on");
                        String last_enable_reason = c.getString("last_enable_reason");*/

                    }
                }

                // if we have data in jsonArray_ACCOUNT
                if(jsonArray_ACCOUNT.length()>0)
                {
                    //repeat to get data from jsonarray
                    for(int i=0; i<jsonArray_ACCOUNT.length(); i++)
                    {
                        JSONObject c = jsonArray_ACCOUNT.getJSONObject(i);

                        ContentValues contentValues = new ContentValues();
                        contentValues.put("id_account_no",c.getString("id_account_no"));
                        contentValues.put("account_number",c.getString("account_number"));
                        contentValues.put("profile_id",c.getString("profile_id"));
                        contentValues.put("billing_type_id",c.getString("billing_type_id"));
                        contentValues.put("valve_id",c.getString("valve_id"));
                        contentValues.put("building_id",c.getString("building_id"));
                        contentValues.put("service_id",c.getString("service_id"));
                        contentValues.put("session_id",c.getString("session_id"));
                        contentValues.put("account_no_id",c.getString("account_no_id"));
                        contentValues.put("status",c.getString("status"));
                        contentValues.put("registered_by",c.getString("registered_by"));
                        contentValues.put("registered_on",c.getString("registered_on"));
                        contentValues.put("authorized",c.getString("authorized"));
                        contentValues.put("authorized_by",c.getString("authorized_by"));
                        contentValues.put("authorized_on",c.getString("authorized_on"));
                        contentValues.put("last_disabled_by",c.getString("last_disabled_by"));
                        contentValues.put("last_disabled_on",c.getString("last_disabled_on"));
                        contentValues.put("last_disable_reason",c.getString("last_disable_reason"));
                        contentValues.put("last_enabled_by",c.getString("last_enabled_by"));
                        contentValues.put("last_enabled_on",c.getString("last_enabled_on"));
                        contentValues.put("last_enable_reason",c.getString("last_enable_reason"));

                        boolean insertOrUpdateValues = dbFirstSync.
                                syncToTables(TAG_TABLE_ACCOUNTS, contentValues, "id_account_no", c.getString("id_account_no"));
                        Log.e(TAG, TAG_TABLE_ACCOUNTS + "done" + String.valueOf(insertOrUpdateValues));

                        /*String id_account_no = c.getString("id_account_no");
                        String account_number = c.getString("account_number");
                        String profile_id = c.getString("profile_id");
                        String billing_type_id = c.getString("billing_type_id");
                        String valve_id = c.getString("valve_id");
                        String building_id = c.getString("building_id");
                        String service_id = c.getString("service_id");
                        String session_id = c.getString("session_id");
                        String account_no_id = c.getString("account_no_id");
                        String status = c.getString("status");
                        String registered_by = c.getString("registered_by");
                        String registered_on = c.getString("registered_on");
                        String authorized = c.getString("authorized");
                        String authorized_by = c.getString("authorized_by");
                        String authorized_on = c.getString("authorized_on");
                        String last_disabled_by = c.getString("last_disabled_by");
                        String last_disabled_on = c.getString("last_disabled_on");
                        String last_disable_reason = c.getString("last_disable_reason");
                        String last_enabled_by = c.getString("last_enabled_by");
                        String last_enabled_on = c.getString("last_enabled_on");
                        String last_enable_reason = c.getString("last_enable_reason");*/
                    }
                }

                // if we have data in jsonArray_PROFILES
                if(jsonArray_PROFILES.length()>0)
                {
                    //repeat to get data from jsonarray
                    for(int i=0; i<jsonArray_PROFILES.length(); i++)
                    {
                        JSONObject c = jsonArray_PROFILES.getJSONObject(i);

                        ContentValues contentValues = new ContentValues();
                        contentValues.put("idprofile",c.getString("idprofile"));
                        contentValues.put("first_name",c.getString("first_name"));
                        contentValues.put("middle_name",c.getString("middle_name"));
                        contentValues.put("last_name",c.getString("last_name"));
                        contentValues.put("address",c.getString("address"));
                        contentValues.put("state",c.getString("state"));
                        contentValues.put("nationality",c.getString("nationality"));
                        contentValues.put("email",c.getString("email"));
                        contentValues.put("phone",c.getString("phone"));
                        contentValues.put("service_id",c.getString("service_id"));
                        contentValues.put("session_id",c.getString("session_id"));
                        contentValues.put("profile_id",c.getString("profile_id"));
                        boolean insertOrUpdateValues = dbFirstSync.
                                syncToTables(TAG_TABLE_PROFILES, contentValues, "idprofile", c.getString("idprofile"));
                        Log.e(TAG, TAG_TABLE_PROFILES + "done" + String.valueOf(insertOrUpdateValues));

                        /*String idprofile = c.getString("idprofile");
                        String first_name = c.getString("first_name");
                        String middle_name = c.getString("middle_name");
                        String last_name = c.getString("last_name");
                        String address = c.getString("address");
                        String state = c.getString("state");
                        String nationality = c.getString("nationality");
                        String email = c.getString("email");
                        String phone = c.getString("phone");
                        String service_id = c.getString("service_id");
                        String session_id = c.getString("session_id");
                        String profile_id = c.getString("profile_id");*/

                    }
                }

                // if we have data in jsonArray_ZONES
                if(jsonArray_AREACODES.length()>0)
                {
                    //repeat to get data from jsonarray
                    for(int i=0; i<jsonArray_AREACODES.length(); i++)
                    {
                        JSONObject c = jsonArray_AREACODES.getJSONObject(i);

                        ContentValues contentValues = new ContentValues();
                        contentValues.put("id_area_codes",c.getString("id_area_codes"));
                        contentValues.put("area_name",c.getString("area_name"));
                        contentValues.put("area_code",c.getString("area_code"));
                        contentValues.put("service_id",c.getString("service_id"));
                        contentValues.put("session_id",c.getString("session_id"));
                        contentValues.put("area_code_id",c.getString("area_code_id"));
                        contentValues.put("status",c.getString("status"));
                        contentValues.put("registered_by",c.getString("registered_by"));
                        contentValues.put("registered_on",c.getString("registered_on"));
                        contentValues.put("authorized",c.getString("authorized"));
                        contentValues.put("authorized_by",c.getString("authorized_by"));
                        contentValues.put("authorized_on",c.getString("authorized_on"));
                        contentValues.put("last_disabled_by",c.getString("last_disabled_by"));
                        contentValues.put("last_disabled_on",c.getString("last_disabled_on"));
                        contentValues.put("last_disable_reason",c.getString("last_disable_reason"));
                        contentValues.put("last_enabled_by",c.getString("last_enabled_by"));
                        contentValues.put("last_enabled_on",c.getString("last_enabled_on"));
                        contentValues.put("last_enable_reason",c.getString("last_enable_reason"));
                        boolean insertOrUpdateValues = dbFirstSync.
                                syncToTables(TAG_TABLE_AREACODES, contentValues, "id_area_codes", c.getString("id_area_codes"));
                        Log.e(TAG, TAG_TABLE_AREACODES + "done" + String.valueOf(insertOrUpdateValues));

                        /*String id_area_codes = c.getString("id_area_codes");
                        String area_name = c.getString("area_name");
                        String area_code = c.getString("area_code");
                        String service_id = c.getString("service_id");
                        String session_id = c.getString("session_id");
                        String area_code_id = c.getString("area_code_id");
                        String status = c.getString("status");
                        String registered_by = c.getString("registered_by");
                        String registered_on = c.getString("registered_on");
                        String authorized = c.getString("authorized");
                        String authorized_by = c.getString("authorized_by");
                        String authorized_on = c.getString("authorized_on");
                        String last_disabled_by = c.getString("last_disabled_by");
                        String last_disabled_on = c.getString("last_disabled_on");
                        String last_disable_reason = c.getString("last_disable_reason");
                        String last_enabled_by = c.getString("last_enabled_by");
                        String last_enabled_on = c.getString("last_enabled_on");
                        String last_enable_reason = c.getString("last_enable_reason");*/

                    }
                }

                if(jsonArray_PAYMENT_ITEMS.length()>0)
                {
                    //iterate to get data from jsonArray
                    for(int i=0; i<jsonArray_PAYMENT_ITEMS.length(); i++)
                    {
                        JSONObject c = jsonArray_PAYMENT_ITEMS.getJSONObject(i);
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("payment_code",c.getString("payment_code"));
                        contentValues.put("payment_item",c.getString("payment_item"));
                        contentValues.put("mark_delete",c.getString("mark_delete"));

                        boolean insertOrUpdateValues = dbFirstSync.
                                syncToTables(TAG_TABLE_PAYMENT_ITEMS, contentValues, "payment_code", c.getString("payment_code"));
                        Log.e(TAG, TAG_TABLE_PAYMENT_ITEMS + "done" + String.valueOf(insertOrUpdateValues));

                    }
                }
                itSynced = true;

            }
            catch (JSONException e) {
                Log.e("responseCatch", "using JSON CATCH");
                e.printStackTrace();
                itSynced = false;
            }

        }
        return itSynced;
    }
}
