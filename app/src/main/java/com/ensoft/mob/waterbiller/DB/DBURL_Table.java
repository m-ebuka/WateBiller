package com.ensoft.mob.waterbiller.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Ebuka on 09/09/2015.
 */
public class DBURL_Table {
    DBConnection myDBconnection;
    private DataDB db;

    public boolean saveIntoURLTable(Context context, ContentValues conValues)
    {
        DBConnection myDBconnection;
        myDBconnection = new DBConnection(context);
        try {
            myDBconnection.createDataBase();
        } catch (IOException e) {
        }
        if(myDBconnection.checkDataBase()){
            myDBconnection.openDataBase();
            SQLiteDatabase db = myDBconnection.getWritableDatabase();
            try{
                db.insertOrThrow("url_table",null,conValues);
                db.setTransactionSuccessful(); //necessary
                Toast.makeText(context, "url_table" + " Table insert done successful", Toast.LENGTH_LONG).show();
                return true;
            }
            catch (Exception e)
            {
                Toast.makeText(context, e.toString() +"Updated:", Toast.LENGTH_LONG).show();
                return false;
            }
            finally {
                db.endTransaction();
                myDBconnection.close();
            }



        }
        return false;
    }
}
