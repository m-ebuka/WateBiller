package com.ensoft.mob.waterbiller.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ensoft.mob.waterbiller.MyApp;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by TechVibes on 24-Sep-15.
 */
public class DBMeterEnumeration {
    MyApp app;
    DBConnection myDBconnection;
    private static final String TAG = "DBBuildingCategory";
    private DataDB db = new DataDB();

    public static final String KEY_ROWID = "_id";
    public static final String KEY_ID = "id_building_category";
    public static final String KEY_NAME = "building_category";

    public int fetchAccountTypeIdByName(Context context, String meterType)
    {
        String[] args = {meterType};
        int meter_typeId = 0;
        //String sql = "SELECT account_type_id FROM _account_types WHERE account_type=? LIMIT 1;";
        String sql = "SELECT meter_type_id FROM _meter_types WHERE meter_type=? LIMIT 1;";
        myDBconnection = new DBConnection(context);
        try {
            myDBconnection.createDataBase();
        } catch (IOException e) {
        }
        if(myDBconnection.checkDataBase()){
            myDBconnection.openDataBase();
            SQLiteDatabase db = myDBconnection.getWritableDatabase();
            Cursor cursor = db.rawQuery(sql, args);
            Log.i(TAG, "Result from meter type query by name; " + cursor.toString());
            if (cursor.moveToFirst()) {
                while (cursor.isAfterLast() == false) {
                    Log.w("METER TYPE ID", "here: " + cursor.getString(0));
                    String s = cursor.getString(0);
                    meter_typeId = Integer.parseInt(s);
                    cursor.moveToNext();
                }
            }

            myDBconnection.close();
            return meter_typeId;
        }
        return meter_typeId;
    }

    public int fetchConsumerTypeIdByName(Context context, String meterType)
    {
        String[] args = {meterType};
        int challenge_typeId = 0;
        //String sql = "SELECT account_type_id FROM _account_types WHERE account_type=? LIMIT 1;";
        String sql = "SELECT meter_type_id FROM _meter_types WHERE meter_type=? LIMIT 1;";
        myDBconnection = new DBConnection(context);
        try {
            myDBconnection.createDataBase();
        } catch (IOException e) {
        }
        if(myDBconnection.checkDataBase()){
            myDBconnection.openDataBase();
            SQLiteDatabase db = myDBconnection.getWritableDatabase();
            Cursor cursor = db.rawQuery(sql, args);
            Log.i(TAG, "Result from meter type query by name; " + cursor.toString());
            if (cursor.moveToFirst()) {
                while (cursor.isAfterLast() == false) {
                    Log.w("METER TYPE ID", "here: " + cursor.getString(0));
                    String s = cursor.getString(0);
                    challenge_typeId = Integer.parseInt(s);
                    cursor.moveToNext();
                }
            }

            myDBconnection.close();
            return challenge_typeId;
        }
        return challenge_typeId;
    }

    public ArrayList<String> fetchAllConsumerTypes(Context context) {
        String sql = "SELECT consumer_type_id AS _id, consumer_type FROM _consumer_types WHERE mark_delete=0 GROUP BY consumer_type ORDER BY consumer_type_id ASC;";

        ArrayList<String> my_array = new ArrayList<String>();
        Cursor allrows = db.myConnection(context).selectAllFromTable(sql,true);

            if (allrows.moveToFirst()) {
                do {
                    String ID = allrows.getString(0);
                    String KEY_ID = allrows.getString(0);
                    String KEY_NAME = allrows.getString(1);

                    my_array.add(KEY_NAME);

                } while (allrows.moveToNext());
            }


        return my_array;
    }
    public ArrayList<String> fetchAllStates(Context context) {
        String sql = "SELECT id AS _id, name FROM states ORDER BY name;";

        ArrayList<String> my_array = new ArrayList<String>();
        Cursor allrows = db.myConnection(context).selectAllFromTable(sql,true);

        if (allrows.moveToFirst()) {
            do {
                String ID = allrows.getString(0);
                String KEY_ID = allrows.getString(0);
                String KEY_NAME = allrows.getString(1);

                my_array.add(KEY_NAME);

            } while (allrows.moveToNext());
        }


        return my_array;
    }

    public int fetchStateIdByName(Context context, String stateName)
    {
        String[] args = {stateName};
        int challenge_typeId = 0;
        String sql = "SELECT id FROM states WHERE name=? LIMIT 1;";
        myDBconnection = new DBConnection(context);
        try {
            myDBconnection.createDataBase();
        } catch (IOException e) {
        }
        if(myDBconnection.checkDataBase()){
            myDBconnection.openDataBase();
            SQLiteDatabase db = myDBconnection.getWritableDatabase();
            Cursor cursor = db.rawQuery(sql, args);
            //Log.i(TAG, "Result from meter status query by name; " + cursor.toString());
            if (cursor.moveToFirst()) {
                while (cursor.isAfterLast() == false) {
                    //Log.w("meter status ID", "here: " + cursor.getString(0));
                    String s = cursor.getString(0);
                    challenge_typeId = Integer.parseInt(s);
                    cursor.moveToNext();
                }
            }
            myDBconnection.close();
            return challenge_typeId;
        }
        return challenge_typeId;
    }

    public ArrayList<String> fetchAllBuildings(Context context, int streetid)
    {
        ArrayList<String> my_array = new ArrayList<String>();
        Cursor allrows = db.myConnection(context).selectColumnsFromTableBy("building_no", "_buildings", "street_id", String.valueOf(streetid), "building_no");
        //Cursor allrows = db.myConnection(context).selectColumnsFromTableOrderBy("building_no","_buildings","ASC");
        if (allrows.moveToFirst()) {
            do {

                String KEY_NAME = allrows.getString(0);
                Log.e("fetchAllBuilding","building_no " + KEY_NAME);

                my_array.add(KEY_NAME);

            } while (allrows.moveToNext());
        }
        return my_array;
    }

    public ArrayList<String> fetchAllAreaCodes(Context context) {
        String sql = "SELECT area_code_id AS _id, area_name FROM _areacodes WHERE mark_delete = 0 GROUP BY area_name ORDER BY area_code_id ASC;";

        ArrayList<String> my_array = new ArrayList<String>();
        Cursor allrows = db.myConnection(context).selectAllFromTable(sql, true);

        if (allrows.moveToFirst()) {
            do {
                String ID = allrows.getString(0);
                Log.e("fetchAllAreas","area_code_id " + ID);
                String KEY_ID = allrows.getString(0);
                String KEY_NAME = allrows.getString(1);
                Log.e("fetchAllArea_Codes","area_name " + KEY_NAME);

                my_array.add(KEY_NAME);

            } while (allrows.moveToNext());
        }


        return my_array;
    }
    public ArrayList<String> fetchAllStreetsByAreaCodeId(Context context, String area_code_id) {
        String sql = "SELECT street_id AS _id, street FROM _streets WHERE mark_delete = 0 AND area_code_id = "+ "'" + area_code_id + "'" + " GROUP BY street ORDER BY street_id ASC;";

        ArrayList<String> my_array = new ArrayList<String>();
        Cursor allrows = db.myConnection(context).selectAllFromTable(sql, true);

        if (allrows.moveToFirst()) {
            do {
                String ID = allrows.getString(0);
                Log.e("fetchAllStreet","idstreet " + ID);
                String KEY_ID = allrows.getString(0);
                String KEY_NAME = allrows.getString(1);
                Log.e("fetchAllStreet","streetname " + KEY_NAME);

                my_array.add(KEY_NAME);

            } while (allrows.moveToNext());
        }


        return my_array;
    }
    public ArrayList<String> fetchAllStreets(Context context) {
        String sql = "SELECT street_id AS _id, street FROM _streets WHERE mark_delete = 0 GROUP BY street ORDER BY street_id ASC;";

        ArrayList<String> my_array = new ArrayList<String>();
        Cursor allrows = db.myConnection(context).selectAllFromTable(sql,true);

        if (allrows.moveToFirst()) {
            do {
                String ID = allrows.getString(0);
                Log.e("fetchAllStreet","idstreet " + ID);
                String KEY_ID = allrows.getString(0);
                String KEY_NAME = allrows.getString(1);
                Log.e("fetchAllStreet","streetname " + KEY_NAME);

                my_array.add(KEY_NAME);

            } while (allrows.moveToNext());
        }


        return my_array;
    }
        public ArrayList<String> fetchAllProfileType(Context context) {
        String sql = "SELECT profile_type_id AS _id, profile_type FROM profile_types ORDER BY profile_type_id;";

        ArrayList<String> my_array = new ArrayList<String>();
        Cursor allrows = db.myConnection(context).selectAllFromTable(sql,true);

        if (allrows.moveToFirst()) {
            do {
                String ID = allrows.getString(0);
                Log.e("fetchAllProfileType","Profile_type_id " + ID);
                String KEY_ID = allrows.getString(0);
                String KEY_NAME = allrows.getString(1);
                Log.e("fetchAllProfileType","Profile_type " + KEY_NAME);

                my_array.add(KEY_NAME);

            } while (allrows.moveToNext());
        }


        return my_array;
    }

    public int fetchProfileTypeIdByName(Context context, String profileType)
    {
        String[] args = {profileType};
        int challenge_typeId = 0;
        String sql = "SELECT profile_type_id FROM profile_types WHERE profile_type=? LIMIT 1;";
        myDBconnection = new DBConnection(context);
        try {
            myDBconnection.createDataBase();
        } catch (IOException e) {
        }
        if(myDBconnection.checkDataBase()){
            myDBconnection.openDataBase();
            SQLiteDatabase db = myDBconnection.getWritableDatabase();
            Cursor cursor = db.rawQuery(sql, args);
            //Log.i(TAG, "Result from meter status query by name; " + cursor.toString());
            if (cursor.moveToFirst()) {
                while (cursor.isAfterLast() == false) {
                    //Log.w("meter status ID", "here: " + cursor.getString(0));
                    String s = cursor.getString(0);
                    challenge_typeId = Integer.parseInt(s);
                    cursor.moveToNext();
                }
            }
            myDBconnection.close();
            return challenge_typeId;
        }
        return challenge_typeId;
    }

    public ArrayList<String> fetchAllAccountType(Context context) {
       // String sql = "SELECT account_type_id AS _id, account_type FROM _account_types;";
        String sql = "SELECT meter_type_id AS _id, meter_type FROM _meter_types;";
        myDBconnection = new DBConnection(context);
        try {
            myDBconnection.createDataBase();
        } catch (IOException e) {
        }
        ArrayList<String> my_array = new ArrayList<String>();
        if(myDBconnection.checkDataBase()){
            myDBconnection.openDataBase();
            SQLiteDatabase db = myDBconnection.getWritableDatabase();
            Cursor allrows = db.rawQuery(sql, null);
            System.out.println("Meter Count : " + allrows.getCount());

            if (allrows.moveToFirst()) {
                do {

                    String ID = allrows.getString(0);
                    String KEY_ID = allrows.getString(0);
                    String KEY_NAME = allrows.getString(1);

                    my_array.add(KEY_NAME);

                } while (allrows.moveToNext());
            }
            allrows.close();
            myDBconnection.close();

        }else{
            return null;
        }

        return my_array;
    }

    public int fetchMeterStatusIdByName(Context context, String meterStatusType)
    {
        String[] args = {meterStatusType};
        int challenge_typeId = 0;
        String sql = "SELECT idmeter_status FROM _meter_status WHERE status=? LIMIT 1;";
        myDBconnection = new DBConnection(context);
        try {
            myDBconnection.createDataBase();
        } catch (IOException e) {
        }
        if(myDBconnection.checkDataBase()){
            myDBconnection.openDataBase();
            SQLiteDatabase db = myDBconnection.getWritableDatabase();
            Cursor cursor = db.rawQuery(sql, args);
            Log.i(TAG, "Result from meter status query by name; " + cursor.toString());
            if (cursor.moveToFirst()) {
                while (cursor.isAfterLast() == false) {
                    Log.w("meter status ID", "here: " + cursor.getString(0));
                    String s = cursor.getString(0);
                    challenge_typeId = Integer.parseInt(s);
                    cursor.moveToNext();
                }
            }
            myDBconnection.close();
            return challenge_typeId;
        }
        return challenge_typeId;
    }


    public ArrayList<String> fetchAllMeterStatus(Context context) {
        String sql = "SELECT idmeter_status AS _id, status FROM _meter_status;";
        myDBconnection = new DBConnection(context);
        try {
            myDBconnection.createDataBase();
        } catch (IOException e) {
        }
        ArrayList<String> my_array = new ArrayList<String>();
        if(myDBconnection.checkDataBase()){
            myDBconnection.openDataBase();
            SQLiteDatabase db = myDBconnection.getWritableDatabase();
            Cursor allrows = db.rawQuery(sql, null);
            System.out.println("meter status count : " + allrows.getCount());

            if (allrows.moveToFirst()) {
                do {

                    String ID = allrows.getString(0);
                    String KEY_ID = allrows.getString(0);
                    String KEY_NAME = allrows.getString(1);

                    my_array.add(KEY_NAME);

                } while (allrows.moveToNext());
            }
            allrows.close();
            myDBconnection.close();

        }else{
            return null;
        }

        return my_array;
    }


    public int fetchMeterInstallationIdByName(Context context, String installationType)
    {
        String[] args = {installationType};
        int challenge_typeId = 0;
        String sql = "SELECT meter_installation_type_id FROM _meter_installation_types WHERE meter_installation_type=? LIMIT 1;";
        myDBconnection = new DBConnection(context);
        try {
            myDBconnection.createDataBase();
        } catch (IOException e) {
        }
        if(myDBconnection.checkDataBase()){
            myDBconnection.openDataBase();
            SQLiteDatabase db = myDBconnection.getWritableDatabase();
            Cursor cursor = db.rawQuery(sql, args);
            Log.i(TAG, "Result from meter installation query by name; " + cursor.toString());
            if (cursor.moveToFirst()) {
                while (cursor.isAfterLast() == false) {
                    Log.w("meter installation id", "here: " + cursor.getString(0));
                    String s = cursor.getString(0);
                    challenge_typeId = Integer.parseInt(s);
                    cursor.moveToNext();
                }
            }

            myDBconnection.close();
            return challenge_typeId;
        }
        return challenge_typeId;
    }


    public ArrayList<String> fetchAllMeterInstallation(Context context) {
        String sql = "SELECT meter_installation_type_id AS _id, meter_installation_type FROM _meter_installation_types;";
        myDBconnection = new DBConnection(context);
        try {
            myDBconnection.createDataBase();
        } catch (IOException e) {
        }
        ArrayList<String> my_array = new ArrayList<String>();
        if(myDBconnection.checkDataBase()){
            myDBconnection.openDataBase();
            SQLiteDatabase db = myDBconnection.getWritableDatabase();
            Cursor allrows = db.rawQuery(sql, null);
            System.out.println("METER INSTALLATION COUNT : " + allrows.getCount());

            if (allrows.moveToFirst()) {
                do {

                    String ID = allrows.getString(0);
                    String KEY_ID = allrows.getString(0);
                    String KEY_NAME = allrows.getString(1);

                    my_array.add(KEY_NAME);

                } while (allrows.moveToNext());
            }
            allrows.close();
            myDBconnection.close();

        }else{
            return null;
        }

        return my_array;
    }

}
