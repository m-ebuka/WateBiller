package com.ensoft.mob.waterbiller.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by Ebuka on 09/09/2015.
 */
public class DBStreet {
    DBConnection myDBconnection;
    private DataDB db;
    private static final String TAG = "DBStreet";
    Context _context;

    public DBStreet(Context context) {
        this._context = context;
    }



    public boolean insertIntoStreetForFirstSync(String _idstreet, String _street, String _city_id,
                                                String _latitude, String _longitude, String _street_id,
                                                String _service_id, String session_id, String _status,
                                                String _registered_by, String _registered_on,
                                                String _authorized, String _authorized_by,
                                                String _authorized_on, String _last_disabled_by,
                                                String _last_disabled_on, String _last_disable_reason,
                                                String _last_enabled_by, String _last_enabled_on, String _last_enable_reason)
    {
        Toast.makeText(_context, "Inserting into street table ", Toast.LENGTH_LONG).show();
        String sql =
                "INSERT or replace INTO _streets (idstreet,street,city_id,latitude,longitude,street_id,service_id," +
                        "session_id,status,registered_by,registered_on,authorized,authorized_by,authorized_on,last_disabled_on," +
                        "last_disable_reason,last_enabled_by,last_enabled_on,last_enable_reason) " +
                        "VALUES()" ;

        myDBconnection = new DBConnection(_context);
        try {
            myDBconnection.createDataBase();
        } catch (IOException e) {
        }
        if(myDBconnection.checkDataBase()){
            myDBconnection.openDataBase();
            SQLiteDatabase db = myDBconnection.getWritableDatabase();
            //Cursor cursor = db.rawQuery(sql, null);
            try{
                db.execSQL(sql);
                return true;
            }
            catch (Exception e)
            {
                Toast.makeText(_context, e.toString(), Toast.LENGTH_LONG).show();
            }

            myDBconnection.close();

        }
        return false;
    }

}
