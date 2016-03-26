package com.ensoft.mob.waterbiller.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by Ebuka on 09/09/2015.
 */
public class DBFirstSync {
    DBConnection myDBconnection;
    //private DataDB db;
    private static final String TAG = "DBFirstSync";
    Context _context;

    public DBFirstSync(Context context) {
        this._context = context;
    }

    public boolean syncToTables(String _TABLENAME, ContentValues _tableValues, String _tableID, String _tableID_Value)
    {
        Log.e(TAG,"Synchroniztion started");
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
                //db.execSQL(sql);
                db.insertOrThrow(_TABLENAME,null,_tableValues);
                db.setTransactionSuccessful(); //necessary
                Toast.makeText(_context, _TABLENAME + " Table insert done successful", Toast.LENGTH_LONG).show();
                return true;
            }
            catch (Exception e)
            {
                db.update(_TABLENAME,_tableValues,_tableID +"="+ _tableID_Value,null);
                Toast.makeText(_context, e.toString() +"Updated:", Toast.LENGTH_LONG).show();
                //return true;
            }
            finally {
                db.endTransaction();
                myDBconnection.close();
            }



        }
        return false;
    }
}
