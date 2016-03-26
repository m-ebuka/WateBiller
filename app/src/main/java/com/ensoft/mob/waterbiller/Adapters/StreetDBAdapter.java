package com.ensoft.mob.waterbiller.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ensoft.mob.waterbiller.DB.DBConnection;
import com.ensoft.mob.waterbiller.DB.DataDB;
import com.ensoft.mob.waterbiller.Models.Street;
import com.ensoft.mob.waterbiller.Models.currentUserDetail;
import com.ensoft.mob.waterbiller.MyApp;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Ebuka on 08/08/2015.
 */
public class StreetDBAdapter {
    MyApp app;
    DBConnection myDBconnection;
    public static final String KEY_ROWID = "_id";
    public static final String KEY_ID = "idstreet";
    public static final String KEY_NAME = "street_name";
    public static final String KEY_AREACODE = "area_code";
    public static final String KEY_CITYID = "idcity";
    public static final String KEY_CITYNAME = "city_name";
    public static final String KEY_LGAID = "id_lga";
    public static final String KEY_LGANAME = "name";
    public static final String KEY_ZONEID = "idzone";
    public static final String KEY_ZONENAME = "zone_name";
    public static final String KEY_TOTAL_ACCOUNT = "total_account";
    public static final String KEY_COLOR = "key_color";

    public static final int COL_KEY_ID = 0;
    public static final int COL_KEY_NAME = 1;
    public static final int COL_TOTAL_ACCOUNT = 2;
    public static final int COL_KEY_COLOR = 3;

    public static final String[] ALL_KEYS = new String[] {KEY_ROWID, KEY_NAME, KEY_TOTAL_ACCOUNT, KEY_COLOR};

    private static final String TAG = "Street DataAdapter";

    //private final Context context;
    private DataDB db;
    /*public StreetDBAdapter(Context ctx)
    {
        this.context = ctx;
        db = new DataDB();
    }*/

    public int fetchStreetByName(Context context, String street_name)
    {
        int streetid =0;
        Cursor cursor;
        app = ((MyApp)context);
        String[] args = {street_name};
        String sql = "SELECT street_id AS _id, _streets.* FROM _streets WHERE street=? LIMIT 1;";
        myDBconnection = new DBConnection(context);
        try {
            myDBconnection.createDataBase();
        } catch (IOException e) {
        }

        if(myDBconnection.checkDataBase()){
            myDBconnection.openDataBase();
            SQLiteDatabase db = myDBconnection.getWritableDatabase();
            cursor = db.rawQuery(sql, args);
            Log.i(TAG,"Result from street query by name; " + cursor.toString());
            while(cursor.moveToNext()){
                streetid = cursor.getInt(0);
                Log.w("STREETID","here; "+ Integer.toString(streetid) );
            }
            /*if(cursor != null)
            {
                cursor.moveToFirst();
            }*/

            myDBconnection.close();

            //return cursor;
        }
        return  streetid;
    }

    public String fetchStreetById(Context context, int streetid)
    {
        String streetname =null;
        Cursor cursor;
        app = ((MyApp)context);
        String[] args = {Integer.toString(streetid)};
        String sql = "SELECT * FROM _streets WHERE street_id=? LIMIT 1;";
        myDBconnection = new DBConnection(context);
        try {
            myDBconnection.createDataBase();
        } catch (IOException e) {
        }

        if(myDBconnection.checkDataBase()){
            myDBconnection.openDataBase();
            SQLiteDatabase db = myDBconnection.getWritableDatabase();
            cursor = db.rawQuery(sql, args);
            Log.i(TAG,"Result from street query by id; " + cursor.toString());
            while(cursor.moveToNext()){
                streetname = cursor.getString(1);
                Log.w("STREETNAME","here; "+ streetname );
            }

            /**/

            myDBconnection.close();

            //return cursor;
        }
        return streetname;
    }

    // public ArrayList<String> getAllStreetByUseridAndAreacode(Context context, String userid, int areacodeid) {
    public ArrayList<String> getAllStreetByUseridAndAreacode(Context context, int areacodeid) {
        // String[] args = {userid,Integer.toString(areacodeid)};
        String[] args = {Integer.toString(areacodeid)};
       /*
       String sql = "SELECT _streets.idstreet AS _id,_streets.idstreet,_streets.street,_streets.street_id FROM _streets, assignments,_streets_areacodes,_areacodes,_buildings, account_numbers" +
                " WHERE assignments.street_areacodes_id=_streets_areacodes.street_areacodes_id" +
                " AND _streets_areacodes.street_id=_streets.idstreet" +
                " AND assignments.users_id=? AND _streets_areacodes.area_codes_id=?" +
                " GROUP BY _streets.street_id;"; //_streets.idstreet,
                */
        //String sql = "SELECT _streets.street,_streets.street_id FROM _streets WHERE _streets.area_code_id=? GROUP BY _streets.street_id;";
        //String sql = "SELECT * FROM _streets WHERE _streets.area_code_id=? GROUP BY _streets.street_id;";
        String sql = "SELECT * FROM _streets GROUP BY _streets.street_id;";

        //String sql = "SELECT _street.idstreet AS _id, _street.idstreet,_streets_areacodes.id_street_areacodes AS total_account, _street.street_name, _city.idcity, _city.city_name, _lga.id_lga, _lga.name, _zones.idzone, _zones.zone_name, _areacodes.id_area_codes,_areacodes.area_code,_areacodes.area_name FROM _street, assignments, _streets_areacodes, _city, _lga, _zones, _areacodes WHERE _streets_areacodes.id_street_areacodes = assignment.id_street_areacodes AND _street.idstreet=_streets_areacodes.idstreet AND assignment.id_users=? AND _street.idcity = _city.idcity AND _city.id_lga=_lga.id_lga AND _lga.idzone=_zones.idzone AND _areacodes.id_area_codes=?;";
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
            System.out.println("COUNT : " + allrows.getCount());

            if (allrows.moveToFirst()) {
                do {
                    String ID = allrows.getString(6);
                    String IDSTREET = allrows.getString(6);
                    String STREETNAME = allrows.getString(1);
                    //String AREACODE = allrows.getString(3);
                    Log.w("STREETNAME",allrows.getString(1));
                    my_array.add(STREETNAME);


                } while (allrows.moveToNext());
            }
            allrows.close();
            myDBconnection.close();

        }else{
            return null;
        }

        return my_array;
    }

    public Cursor fetchAllStreetByUseridAndAreacode(Context context, int areacodeid) {
        Log.e("fetchAllStreetByUseridAndAreacode","area code id supplied: " +Integer.toString(areacodeid));
        String[] args = {Integer.toString(areacodeid)};
        /*String sql = "SELECT _streets.idstreet AS _id,_streets.idstreet,_streets.street,_streets.street_id FROM _streets, assignments,_streets_areacodes,_areacodes,_buildings, account_numbers" +
                " WHERE assignments.street_areacodes_id=_streets_areacodes.street_areacodes_id" +
                " AND _streets_areacodes.street_id=_streets.idstreet" +
                " AND assignments.users_id=? AND _streets_areacodes.area_codes_id=?" +
                " GROUP BY _streets.street_id;";*/
        /*String sql = "SELECT _streets.idstreet AS _id,_streets.idstreet,_streets.street,_streets.street_id FROM _streets,_streets_areacodes,_areacodes,_buildings, account_numbers" +
                " WHERE _streets_areacodes.street_id=_streets.idstreet" +
                " AND _streets_areacodes.area_code_id=?" +
                " GROUP BY _streets.street_id;";*/
        String sql = "SELECT street_id AS _id, street, latitude, longitude, area_code_id, city, lga FROM _streets " +
                "WHERE area_code_id = ? GROUP BY street_id;"; //mark_delete = 0 AND
        //String sql = "SELECT _street.idstreet AS _id, _street.idstreet,_streets_areacodes.id_street_areacodes AS total_account, _street.street_name, _city.idcity, _city.city_name, _lga.id_lga, _lga.name, _zones.idzone, _zones.zone_name, _areacodes.id_area_codes,_areacodes.area_code,_areacodes.area_name FROM _street, assignments, _streets_areacodes, _city, _lga, _zones, _areacodes WHERE _streets_areacodes.id_street_areacodes = assignment.id_street_areacodes AND _street.idstreet=_streets_areacodes.idstreet AND assignment.id_users=? AND _street.idcity = _city.idcity AND _city.id_lga=_lga.id_lga AND _lga.idzone=_zones.idzone AND _areacodes.id_area_codes=?;";
        myDBconnection = new DBConnection(context);
        //ArrayList<Street> streetArrayList = new ArrayList<Street>();
        //streetArrayList.clear();
        Cursor cursor;
        try {
            myDBconnection.createDataBase();
        } catch (IOException e) {
        }
        if(myDBconnection.checkDataBase()){
            myDBconnection.openDataBase();
            SQLiteDatabase db = myDBconnection.getWritableDatabase();
            //Cursor cursor = db.query("street", new String[] {KEY_ID,KEY_AREACODE,KEY_NAME,KEY_CITY},null,null,null,null,null);
            cursor = db.rawQuery(sql, args);
            Log.i(TAG, "Result from street query; " + cursor.toString());
            if(cursor != null)
            {
                cursor.moveToFirst();

            }

        }else{
            return null;
        }
        return cursor;
    }

    public Cursor fetchAllStreet(Context context)
    {
        /*SELECT _street.idstreet, _street.street_name, _city.idcity, _city.city_name, _lga.id_lga, _lga.name, _zones.idzone, _zones.zone_name
FROM _street, assignment, _streets_areacodes, _city, _lga, _zones
WHERE _streets_areacodes.id_street_areacodes = assignment.id_street_areacodes AND _street.idstreet=_streets_areacodes.idstreet AND assignment.id_users=1 AND _street.idcity = _city.idcity AND _city.id_lga=_lga.id_lga AND _lga.idzone=_zones.idzone;
        */

        Cursor cursor;
        app = ((MyApp)context);
        String[] args = {app.getUserid()};
        String sql = "SELECT _streets.idstreet AS _id,_streets.idstreet,_streets.street,_streets.street_id,_streets_areacodes.area_codes_id FROM _streets, assignments,_streets_areacodes,_areacodes,_buildings, account_numbers" +
                "WHERE assignments.street_areacodes_id=_streets_areacodes.street_areacodes_id" +
                "AND _streets_areacodes.street_id=_streets.idstreet" +
                "AND assignments.users_id=? AND _streets_areacodes.area_codes_id=?" +
                "GROUP BY _streets.street_id;";
        //String sql = "SELECT _street.idstreet AS _id, _street.idstreet,_streets_areacodes.id_street_areacodes AS total_account, _street.street_name, _city.idcity, _city.city_name, _lga.id_lga, _lga.name, _zones.idzone, _zones.zone_name, _areacodes.id_area_codes,_areacodes.area_code,_areacodes.area_name FROM _street, assignment, _streets_areacodes, _city, _lga, _zones, _areacodes WHERE _streets_areacodes.id_street_areacodes = assignment.id_street_areacodes AND _street.idstreet=_streets_areacodes.idstreet AND assignment.id_users=? AND _street.idcity = _city.idcity AND _city.id_lga=_lga.id_lga AND _lga.idzone=_zones.idzone AND _areacodes.id_area_codes=_streets_areacodes.id_area_codes;";
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
            Log.i(TAG, "Result from street query; " + cursor.toString());
            if(cursor != null)
            {
                cursor.moveToFirst();
            }

        }else{
            return null;
        }
        return  cursor;
    }

    public Cursor fetchStreets(Context context, String inputText) throws SQLException
    {
        Log.w(TAG, "INPUT STRING " +inputText);
        Cursor cursor = null;
        app = ((MyApp)context);
        String[] args = {app.getUserid(),Integer.toString(app.getActiveAreaCode())};
        if(inputText == null || inputText.length() == 0)
        {
            cursor = fetchAllStreet(context);
        }
        else
        {

            String sql = "SELECT _streets.idstreet AS _id,_streets.idstreet,_streets.street,_streets.street_id,_streets_areacodes.area_codes_id FROM _streets, assignments,_streets_areacodes,_areacodes,_buildings, account_numbers" +
                    "WHERE assignments.street_areacodes_id=_streets_areacodes.street_areacodes_id" +
                    "AND _streets_areacodes.street_id=_streets.idstreet" +
                    "AND assignments.users_id=? AND _streets_areacodes.area_codes_id=?" +
                    "GROUP BY _streets.street_id;";
            //Cursor cursor = db.query(true, SQLITE_TABLE, new String[] {KEY_ROWID, KEY_CODE, KEY_NAME, KEY_CONTINENT, KEY_REGION}, KEY_NAME + " like '%" + inputText + "%'", null, null, null, null, null);
            //String sql = "SELECT _street.idstreet AS _id, _street.idstreet,_streets_areacodes.id_street_areacodes AS total_account, _street.street_name, _city.idcity, _city.city_name, _lga.id_lga, _lga.name, _zones.idzone, _zones.zone_name, _areacodes.id_area_codes,_areacodes.area_code,_areacodes.area_name FROM _street, assignment, _streets_areacodes, _city, _lga, _zones, _areacodes WHERE _streets_areacodes.id_street_areacodes = assignment.id_street_areacodes AND _street.idstreet=_streets_areacodes.idstreet AND assignment.id_users=? AND _street.idcity = _city.idcity AND _city.id_lga=_lga.id_lga AND _lga.idzone=_zones.idzone AND _areacodes.id_area_codes=_streets_areacodes.id_area_codes;";
            myDBconnection = new DBConnection(context);
            try {
                myDBconnection.createDataBase();
            } catch (IOException e) {
            }
            if(myDBconnection.checkDataBase())
            {
                SQLiteDatabase db = myDBconnection.getWritableDatabase();
                //Cursor cursor = db.query(true, SQLITE_TABLE, new String[] {KEY_ROWID, KEY_CODE, KEY_NAME, KEY_CONTINENT, KEY_REGION}, KEY_NAME + " like '%" + inputText + "%'", null, null, null, null, null);
                cursor = db.rawQuery(sql, args);
                if(cursor != null)
                {
                    cursor.moveToFirst();
                }
            }
            else{ return null; }
        }
        return cursor;
    }

    public int numberOfAccountsInStreet(Context context, String street_id)
    {
        Cursor cursor;
        String[] args = {street_id};

        /*String sql = "SELECT count(account_numbers.account_number) AS total_account" +
                " FROM account_numbers,_buildings,_streets" +
                " WHERE account_numbers.building_id = _buildings.building_id" +
                " AND _buildings.street_id = _streets.street_id" +
                " AND _streets.street_id = ?;";*/

        String sql = "SELECT count(account_numbers.account_number) AS total_account \n" +
                "                FROM account_numbers,_buildings,_streets\n" +
                "                WHERE account_numbers.building_id = _buildings.building_id\n" +
                "                 AND _buildings.street_id = ?;";

        myDBconnection = new DBConnection(context);
        String acctNo = null; int acctKount =0;
        try {
            myDBconnection.createDataBase();
        } catch (IOException e) {
        }
        if(myDBconnection.checkDataBase())
        {
            SQLiteDatabase db = myDBconnection.getWritableDatabase();
            cursor = db.rawQuery(sql, args);
            if (cursor.moveToFirst()) {
                acctNo = cursor.getString(cursor.getColumnIndex("total_account"));

            }

            if(acctNo == null)
            {
                acctKount = 0;
            }
            else{
                acctKount = Integer.parseInt(cursor.getString(0));
            }

        }
        Log.e("Account Count for Street", "strCount;" + Integer.toString(acctKount));
        return acctKount;
    }

    public int numberOfBuildingsInStreet(Context context, String street_id)
    {
        Cursor cursor;
        String[] args = {street_id};

        String sql = "select count(*) from _buildings where street_id = ?;";

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

    public boolean deleteAllStreets(Context context)
    {
        int doneDelete = 0;
        myDBconnection = new DBConnection(context);
        try {
            myDBconnection.createDataBase();
        } catch (IOException e) {
        }
        if(myDBconnection.checkDataBase())
        {
            SQLiteDatabase db = myDBconnection.getWritableDatabase();
            //Cursor cursor = db.query(true, SQLITE_TABLE, new String[] {KEY_ROWID, KEY_CODE, KEY_NAME, KEY_CONTINENT, KEY_REGION}, KEY_NAME + " like '%" + inputText + "%'", null, null, null, null, null);
            doneDelete = db.delete("street",null,null);
            Log.w(TAG, Integer.toString(doneDelete));
            return  doneDelete > 0;

        }
        else{ return false; }
    }

}
