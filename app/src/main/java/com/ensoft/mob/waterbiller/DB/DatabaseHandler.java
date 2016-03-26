package com.ensoft.mob.waterbiller.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

/**
 * Created by Ebuka on 04/08/2015.
 */
public class DatabaseHandler extends SQLiteOpenHelper {
    private static final String DATABASE_PATH= "data/data/com.ensoft.mob.waterbiller/databases/";
    private static final String DATABASE_NAME = "aqua_biller.db";
    private static final int DATABASE_VERSION = 1;
    private SQLiteDatabase dbObj;
    private final Context context;


    public DatabaseHandler(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public void createDB() throws IOException
    {
        this.getReadableDatabase(); //will create an empty database in the default system path of your application. We will overwrite this database with our database
        Log.i("Readable ends............","end");
        /*try
        {*/
            copyDB();
            Log.i("Copy db ends............", "end");
        /*}
        catch (IOException e)
        {
            throw  new Error("Error copying database");
        }*/

    }

    private boolean checkDB()
    {
        SQLiteDatabase checkDB = null;
        try
        {
            String path = DATABASE_PATH + DATABASE_NAME;
            Log.i("myPath ......", path);
            checkDB = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
            Log.i("myPath ......", path);
            if(checkDB())
            {
                Cursor c = checkDB.rawQuery("SELECT * FROM users", null);
                Log.i("Cursor....", c.getString(0));
                c.moveToFirst();
                String contents[] = new String[80];
                int flag=0;

                while(! c.isAfterLast())
                {
                    String temp="";
                    String s2=c.getString(0);
                    String s3=c.getString(1);
                    String s4=c.getString(2);
                    temp=temp+"\n Id:"+s2+"\tType:"+s3+"\tBal:"+s4;
                    contents[flag]=temp;
                    flag=flag+1;
                    Log.i("DB values.........",temp);
                    c.moveToNext();
                }
            }
            else
            {
                return false;
            }
        }
        catch(SQLiteException e)
        {
            e.printStackTrace();
        }
        if(checkDB != null){
            checkDB.close();
        }

        return checkDB != null ? true : false;
    }



    private void copyDB() {
        try {
            Log.i("inside copyDB....................","start");
            //will get the database file from the asset folder that we created
            InputStream ip =  context.getAssets().open(DATABASE_NAME+".db");
            Log.i("Input Stream....",ip+"");
            String op=  DATABASE_PATH + DATABASE_NAME;
            OutputStream output = new FileOutputStream( op);
            byte[] buffer = new byte[1024];
            int length;

            while ((length = ip.read(buffer))>0){
                output.write(buffer, 0, length);
                Log.i("Content.... ",length+"");
            }
            output.flush();
            output.close();
            ip.close();

        }

        catch (IOException e) {

            Log.v("error", e.toString());

        }
    }
    public void openDB() throws SQLException {
        String myPath = DATABASE_PATH + DATABASE_NAME;
        dbObj = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
        Log.i("open DB......",dbObj.toString());

    }

    @Override
    public synchronized void close() {

        if(dbObj != null)
            dbObj.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //sqLiteDatabase.execSQL(createDB);
        //createDB();
        //Log.i(TAG, "Database Create");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
