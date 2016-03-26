package com.ensoft.mob.waterbiller.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ensoft.mob.waterbiller.DB.DBConnection;
import com.ensoft.mob.waterbiller.MyApp;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by Ebuka on 10/08/2015.
 */
public class BuildingAdapter {
    MyApp app;
    DBConnection myDBconnection;
    public static final String KEY_ROWID = "_id";
    public static final String KEY_ID = "id_meter";
    public static final String KEY_METER_NO = "meter_no";
    public static final String KEY_METER_SERIAL = "serial_no";
    public static final String KEY_BUILDINGNAME = "building_name";
    public static final String KEY_BUILDING_CATEGORY = "building_category";
    public static final String KEY_BUILDING_IMAGE = "building_image";
    private static final String TAG = "Building DataAdapter";

    /*public Cursor fetchAllBuilding(Context context,String activeStreet, String inputText)
    {
        app = ((MyApp)context);
        Cursor cursor;
        String[] args = {Integer.toString(app.getActiveStreet())};

        Log.w("INSERTED STREETID", Integer.toString(app.getActiveStreet()));
        String sql = "SELECT meter.id_meter AS _id, meter.id_meter, meter.meter_no, meter.serial_no, _building.idbuilding, _building.building_name, _building.building_image, _building_category.building_category\n" +
                "FROM meter, _building, _building_category WHERE meter.idbuilding=_building.idbuilding AND _building.idbuilding_category=_building_category.idbuilding_category AND _building.idstreet=?";
        myDBconnection = new DBConnection(context);
        try {
            myDBconnection.createDataBase();
        } catch (IOException e) {
        }

        if(myDBconnection.checkDataBase()){
            myDBconnection.openDataBase();
            SQLiteDatabase db = myDBconnection.getWritableDatabase();
            //Cursor cursor = db.query("street", new String[] {KEY_ID,KEY_AREACODE,KEY_NAME,KEY_CITY},null,null,null,null,null);
            cursor = db.rawQuery(sql, args);
            Log.i(TAG,"Result from building query; " + cursor.toString());
            if(cursor != null)
            {
                cursor.moveToFirst();
            }

        }else{
            return null;
        }

        return  cursor;
    }*/

    public String fetchBuildingById(Context context, int buildingid)
    {
        String buildingname =null;
        Cursor cursor;
        app = ((MyApp)context);
        String[] args = {Integer.toString(buildingid)};
        String sql = "SELECT idbuilding AS _id, _building.* FROM _building WHERE idbuilding=? LIMIT 1;";
        myDBconnection = new DBConnection(context);
        try {
            myDBconnection.createDataBase();
        } catch (IOException e) {
        }

        if(myDBconnection.checkDataBase()){
            myDBconnection.openDataBase();
            SQLiteDatabase db = myDBconnection.getWritableDatabase();
            cursor = db.rawQuery(sql, args);
            Log.i(TAG,"Result from building query by id; " + cursor.toString());
            if(cursor != null)
            {
                cursor.moveToFirst();
                while(cursor.moveToNext()){
                    buildingname = cursor.getString(2);
                }
            }
            /**/

            myDBconnection.close();

            //return cursor;
        }
        return buildingname;
    }

    //SELECT * FROM account_numbers WHERE account_numbers.mark_delete = 0 AND account_numbers.building_id = 1 LIMIT 1;

    public int getAccountNumberByBuildingID(Context context, String building_id)
    {
        Cursor cursor;
        String[] args = {building_id};

        String sql = "SELECT * FROM account_numbers WHERE account_numbers.mark_delete = 0 AND account_numbers.building_id = ? LIMIT 1;";

        myDBconnection = new DBConnection(context);
        String acctNo = null;
        int strtCount =0;
        try {
            myDBconnection.createDataBase();
        } catch (IOException e) {
        }
        if(myDBconnection.checkDataBase())
        {
            SQLiteDatabase db = myDBconnection.getWritableDatabase();
            cursor = db.rawQuery(sql, args);
            if (cursor.moveToFirst()) {
                acctNo = cursor.getString(0);

            }

            if(acctNo == null)
            {
                strtCount = 0;
            }
            else{
                strtCount = Integer.parseInt(cursor.getString(0));
            }
            Log.e("Building Count for Street", "strCount;" + Integer.toString(strtCount));

        }
        return strtCount;
    }

    public Cursor fetchAllBuilding(Context context, String _streetid, String inputText,int start,int limit)
    {
        Cursor cursor;
        String[] args = {_streetid};
        /*String sql = "SELECT account_numbers.building_id AS _id,account_numbers.account_no_id,account_numbers.account_number,account_numbers.meter_no,\n" +
                "account_numbers.building_id,_buildings.building_name,_buildings.building_no,_buildings.building_image,_streets.street,\n" +
                "_buildings.latitude,_buildings.longitude,_buildings.building_categories_id\n" +
                "FROM account_numbers,_buildings,_streets\n" +
                "WHERE account_numbers.building_id = _buildings.building_id\n" +
                "AND _buildings.street_id = ?\n" +
                "AND _streets.street_id = _buildings.street_id\n" +
                "GROUP BY _buildings.building_id";*/
        //String sql = "SELECT meters.id_meter AS _id, account_numbers.meter_no, _buildings.building_name, _buildings.building_image, account_numbers.building_id,  _buildings.building_id,  _building_categories.building_category\n" +
                //"account_numbers.account_number, _buildings.latitude,_buildings.longitude FROM meters, _buildings, _building_categories, account_numbers WHERE _buildings.building_id=account_numbers.building_id AND meters.meter_no=account_numbers.meter_no AND _buildings.building_categories_id=_building_categories.building_categories_id AND _buildings.street_id=?";
        String sql = "SELECT * FROM _buildings WHERE _buildings.mark_delete = 0 AND _buildings.street_id = ? ORDER BY _buildings.building_no ASC LIMIT " +start +","+ limit +";";
        myDBconnection = new DBConnection(context);
        try {
            myDBconnection.createDataBase();
        } catch (IOException e) {
        }

        if(myDBconnection.checkDataBase()){
            myDBconnection.openDataBase();
            SQLiteDatabase db = myDBconnection.getWritableDatabase();
            //Cursor cursor = db.query("street", new String[] {KEY_ID,KEY_AREACODE,KEY_NAME,KEY_CITY},null,null,null,null,null);
            cursor = db.rawQuery(sql, args);
            Log.i(TAG,"Result from building query; " + cursor.toString());
            if(cursor != null)
            {
                cursor.moveToFirst();
            }

        }else{
            return null;
        }
        return  cursor;
    }
}
