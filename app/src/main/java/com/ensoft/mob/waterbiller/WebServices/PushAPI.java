package com.ensoft.mob.waterbiller.WebServices;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ensoft.mob.waterbiller.DB.AuthorizeUser;
import com.ensoft.mob.waterbiller.DB.DBConnection;
import com.ensoft.mob.waterbiller.DB.DataDB;
import com.ensoft.mob.waterbiller.Models.currentUserDetail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ebuka on 09-Sep-15.
 */
public class PushAPI {
    DataDB dataDB = new DataDB();

    public JSONObject JsonPushMobileSyncBuilding(Context context)
    {
        JSONObject userList = new JSONObject();
        JSONArray userListArray = new JSONArray();
        JSONObject userListObj = new JSONObject();
        //Map<String, String> userMap = new HashMap<String, String>();

        String sql = "SELECT building_image,building_name,building_category_id,street_id,building_id,user_id,service_id,authorization_id,session_id,latitude,longitude,building_owner_phone,building_owner_name,building_owner_email FROM mobile_synch_buildings WHERE synch_status = 0 LIMIT 5";
        Cursor cursor = dataDB.myConnection(context).selectAllFromTable(sql, true);
        Map<String, String> userMap = new HashMap<String, String>();
        try
        {
            int jj=0;
            if(cursor.getCount() > 0)
            {
                Log.e("JsonPushMobileSyncBuilding","Count greater than 0: " + cursor.getCount() + " Columns:" + cursor.getColumnCount());
                do {
                    //Log.e("JsonPushMobileSyncBuilding",cursor.getString(0));
                    Log.e("JsonPushMobileSyncBuilding",cursor.getString(1));
                    Log.e("JsonPushMobileSyncBuilding",cursor.getString(2));
                } while (cursor.moveToNext());
                for (Map.Entry<String, String> entry : userMap.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    JSONObject reader = new JSONObject(value);
                    userListArray.put(reader);

                }
                try {
                    userListObj.put("mobile_synch_buildings", userListArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                cursor.close();
                Log.e("Using Map Looping", userListObj.toString());
                return userListObj;
            }
            else{
                Log.e("JsonPushMobileSyncBuilding","Nothing to push");
            }
        } catch (JSONException e) {
            e.printStackTrace();

        } finally {
            cursor.close();
        }
        //myDBconnection.close();
        //}
        userListObj = null;
        return userListObj;


    }

    public JSONObject JsonPush(Context context, String Table_Name) {

        JSONObject userList = new JSONObject();
        JSONArray userListArray = new JSONArray();
        JSONObject userListObj = new JSONObject();

        /*DBConnection myDBconnection;

        myDBconnection = new DBConnection(context);
        try {
            myDBconnection.createDataBase();
        } catch (IOException e) {
        }

        if (myDBconnection.checkDataBase()) {
            myDBconnection.openDataBase();
            SQLiteDatabase db = myDBconnection.getWritableDatabase();*/
            String sql = "SELECT * FROM " + Table_Name + " WHERE synch_status = 0 LIMIT 5";
            Cursor cursor = dataDB.myConnection(context).selectAllFromTable(sql, true);
            //Cursor cursor = db.rawQuery("SELECT * FROM " + Table_Name + " WHERE synch_status = 0 LIMIT 5", null);

            int k = cursor.getColumnCount();
            int jj = 0;
            Log.i("clientStatus", Integer.toString(k));

            Map<String, String> userMap = new HashMap<String, String>();
            try {
                if (cursor.moveToFirst()) {
                    //while (cursor.isAfterLast() == false) {
                    do {
                        String columnn = "row" + String.valueOf(jj);
                        for (int i = 0; i < k; ++i) {
                            if(cursor.getColumnName(i).toLowerCase().contains("image")){
                                if (cursor.getString(i) == null) {
                                    userList.put(cursor.getColumnName(i), "");
                                }
                                else {
                                    userList.put(cursor.getColumnName(i), cursor.getString(i).toString());
                                }
                            }
                            else {
                                if (cursor.getString(i) == null) {
                                    userList.put(cursor.getColumnName(i), "");
                                }
                                else {
                                    userList.put(cursor.getColumnName(i), cursor.getString(i).toString());
                                }
                            }
                            // Log.i("ColumnIndex", i + " COLUMN: " +cursor.getColumnName(i) + "VALUE: " + cursor.getString(i));


                        }
                        userMap.put(columnn, userList.toString());
                        jj++;
                        //cursor.moveToNext();
                    } while (cursor.moveToNext());
                    //userListtt2.put(userList);
                    // Log.e("Using Mapping", userMap.toString());

                    for (Map.Entry<String, String> entry : userMap.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        // Log.i("Map Value", value);
                        JSONObject reader = new JSONObject(value);
                        userListArray.put(reader);
                        //Log.e("Using Map Looping", userListArray.toString());
                    }

                    try {
                        userListObj.put(Table_Name, userListArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    cursor.close();
                    //myDBconnection.close();
                    Log.e("Using Map Looping", userListObj.toString());
                    return userListObj;
                }
            } catch (JSONException e) {
                e.printStackTrace();

            } finally {
                cursor.close();
            }
            //myDBconnection.close();
        //}
        userListObj = null;
        return userListObj;

    }


    /*
    public JSONObject JsonPush(Context context, String Table_Name) {

        //Table_Name
        Log.e("Select4Table","Table to push here " + Table_Name);

        JSONObject userList = new JSONObject();
        JSONArray userListArray = new JSONArray();
        JSONObject userListObj = new JSONObject();

        DBConnection myDBconnection;

        myDBconnection = new DBConnection(context);
        try {
            myDBconnection.createDataBase();
        } catch (IOException e) {
        }
        //Cursor cursor = null;
        if (myDBconnection.checkDataBase()) {
            myDBconnection.openDataBase();
            SQLiteDatabase db = myDBconnection.getWritableDatabase();

            Cursor cursor = db.rawQuery("SELECT * FROM " + Table_Name + " WHERE synch_status=0 LIMIT 5", null);
            if(cursor != null && cursor.getCount() > 0 )
            {
                int k = cursor.getColumnCount();
                int jj = 0;
                Log.i("clientStatus", Integer.toString(k));

                Map<String, String> userMap = new HashMap<String, String>();
                try {
                    if (cursor.moveToFirst()) {
                        while (cursor.isAfterLast() == false) {
                            String columnn = "row" + String.valueOf(jj);
                            for (int i = 0; i < k; ++i) {
                                if (cursor.getString(i) == null) { //cursor.getString(i)
                                    userList.put(cursor.getColumnName(i), "");
                                    //Log.i("ColumnIndex", i + " COLUMN: " + cursor.getColumnName(i) + "VALUE: " + cursor.getString(i));
                                } else {
                                    userList.put(cursor.getColumnName(i), cursor.getString(i));
                                    // Log.i("ColumnIndex", i + " COLUMN: " +cursor.getColumnName(i) + "VALUE: " + cursor.getString(i));
                                }

                            }
                            userMap.put(columnn, userList.toString());
                            //userListtt.put(columnn, userList.toString());
                            jj++;
                            cursor.moveToNext();
                        }
                        //userListtt2.put(userList);
                        // Log.e("Using Mapping", userMap.toString());

                        for (Map.Entry<String, String> entry : userMap.entrySet()) {
                            String key = entry.getKey();
                            String value = entry.getValue();
                            // Log.i("Map Value", value);
                            JSONObject reader = new JSONObject(value);
                            userListArray.put(reader);
                            //Log.e("Using Map Looping", userListArray.toString());
                        }
                        // Log.i("usingJson Object", userListtt.toString());
                        // userListtt2.put(userListtt);
                        // Log.i("UserList Array", userListtt2.toString());
                        // userListArray.put(userListtt2);

                        try {
                            userListObj.put(Table_Name, userListArray);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //  String jsonStra = userListObj.toString();
                        // System.out.println("UserListArray: "+jsonStra);
                        cursor.close();
                        myDBconnection.close();
                        return userListObj;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    cursor.close();

                } finally {
                    cursor.close();
                }
            }
            else{
                Log.e("Select4Table","Nothing to synch here");
            }
            myDBconnection.close();
        }
        userListObj = null;
        return userListObj;

    }
    */
 }
