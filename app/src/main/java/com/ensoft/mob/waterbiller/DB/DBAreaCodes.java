package com.ensoft.mob.waterbiller.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.ensoft.mob.waterbiller.Adapters.StreetDBAdapter;
import com.ensoft.mob.waterbiller.Models.Street;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Ebuka on 13/08/2015.
 */
public class DBAreaCodes {
    DBConnection myDBconnection;
    public static final String KEY_ROWID = "_id";
    public static final String KEY_ID = "id_area_codes";
    public static final String KEY_NAME = "area_name";
    public static final String KEY_AREACODE = "area_code";
    private static final String TAG = "DBAreaCodes";

    //private final Context context;
    private DataDB db;

    public Cursor getAllAreaByUserid()
    {
        Cursor cursor;
        String sql = "";
        return null;
    }

    public int getAreaCodeByName(Context context,String areaname)
    {
        Log.w("AREAName", "here: "+areaname);
        String[] args = {areaname};
        int idareacode = 0;
        String sql = "SELECT * FROM _areacodes WHERE area_name=? LIMIT 1;";
        myDBconnection = new DBConnection(context);
        try {
            myDBconnection.createDataBase();
        } catch (IOException e) {
        }
        if(myDBconnection.checkDataBase()){
            myDBconnection.openDataBase();
            SQLiteDatabase db = myDBconnection.getWritableDatabase();
            Cursor cursor = db.rawQuery(sql, args);
            Log.i(TAG,"Result from street query by name; " + cursor.toString());
            while(cursor.moveToNext()){
                Log.w("AREACODE", "here: "+cursor.getString(0));
                String s = cursor.getString(5);
                idareacode = Integer.parseInt(s);
            }
            /*if(cursor != null)
            {
                cursor.moveToFirst();
            }*/
            /**/

            myDBconnection.close();

        }
        return idareacode;
    }

    public String getAreaCodeById(Context context,int id_areacode)
    {
        String[] args = {Integer.toString(id_areacode)};
        String areaname = null;
        String sql = "SELECT * FROM _areacodes WHERE area_code_id=? LIMIT 1;";
        myDBconnection = new DBConnection(context);
        try {
            myDBconnection.createDataBase();
        } catch (IOException e) {
        }
        if(myDBconnection.checkDataBase()){
            myDBconnection.openDataBase();
            SQLiteDatabase db = myDBconnection.getWritableDatabase();
            Cursor cursor = db.rawQuery(sql, args);
            Log.i(TAG,"Result from areacodes query by id; " + cursor.toString());

            while(cursor.moveToNext()){
                areaname = cursor.getString(1);
                Log.w("AREANAME","here; "+ areaname );
            }
            /*if(cursor != null)
            {
                cursor.moveToFirst();
                while(cursor.moveToNext()){
                    areaname = cursor.getString(1);
                }
            }*/
            /**/

            myDBconnection.close();
            Log.i("AREANAME IN DBAREACodes", areaname);

        }
        return areaname;
    }

    public ArrayList<String> getAllAreaByUserid(Context context, String userid) {
        //  public ArrayList<String> getAllAreaByUserid(Context context, String userid) {
        //  String[] args = {userid};

     /*
        String sql="SELECT _areacodes.id_area_codes" +
                " AS _id,_areacodes.id_area_codes, _areacodes.area_name, _areacodes.area_code" +
                " FROM _areacodes, assignments, _streets_areacodes" +
                " WHERE  assignments.street_areacodes_id=_streets_areacodes.street_areacodes_id" +
                " AND _streets_areacodes.area_codes_id=_areacodes.area_codes_id" +
                " AND assignments.users_id=?" +
                " GROUP BY _areacodes.id_area_codes";
        */
        /*String sql="SELECT _areacodes.area_code_id" +
                " AS _id,_areacodes.area_code_id, _areacodes.area_name, _areacodes.area_code" +
                " FROM _areacodes, _streets_areacodes" +
                " WHERE _streets_areacodes.area_codes_id=_areacodes.area_code_id" +
                " GROUP BY _areacodes.area_name";*/

        String sql="SELECT _areacodes.area_code_id" +
                " AS _id,_areacodes.area_code_id, _areacodes.area_name, _areacodes.area_code" +
                " FROM _areacodes" +
                " WHERE mark_delete = 0" +
                " GROUP BY _areacodes.area_name ORDER BY _areacodes.area_name ASC";

        myDBconnection = new DBConnection(context);
        try {
            myDBconnection.createDataBase();
        } catch (IOException e) {
        }

        ArrayList<String> my_array = new ArrayList<String>();
        if(myDBconnection.checkDataBase()){
            myDBconnection.openDataBase();
            SQLiteDatabase db = myDBconnection.getWritableDatabase();
            //  Cursor allrows = db.rawQuery(sql, args);
            Cursor allrows = db.rawQuery(sql, null);
            System.out.println("COUNT : " + allrows.getCount());

            if (allrows.moveToFirst()) {
                do {

                    String ID = allrows.getString(0);
                    Log.i("AREA_CODE_ID IN DBAREACodes", ID);
                    String AREANAME = allrows.getString(2);
                    Log.i("AREANAME IN DBAREACodes", AREANAME);
                    String AREACODE = allrows.getString(3);
                    Log.i("AREACODE IN DBAREACodes", AREACODE);
                    my_array.add(AREANAME);

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
