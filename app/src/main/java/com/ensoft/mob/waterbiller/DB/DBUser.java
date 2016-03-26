package com.ensoft.mob.waterbiller.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ensoft.mob.waterbiller.Models.currentUserDetail;

import java.io.IOException;

/**
 * Created by Ebuka on 09/08/2015.
 */
public class DBUser {
    DBConnection myDBconnection;
    private static final String TAG = "DBUser";

    //private final Context context;
    private DataDB db;
    /*public DBUser(Context ctx)
    {
        this.context = ctx;
        db = new DataDB();
    }*/

    String name;
    public int login(Context context, String email, String passw)
    {

        String[] args = {email,passw};
        Log.i("LoginCount", email + ":" + passw);
        myDBconnection = new DBConnection(context);
        try {
            myDBconnection.createDataBase();
        } catch (IOException e) {
        }

        if(myDBconnection.checkDataBase()){
            myDBconnection.openDataBase();
            SQLiteDatabase db = myDBconnection.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email=? AND password=? LIMIT 1", args); //id_users,email,first_name,last_name,middle_name,phone,service_id,authorization_id //id_users,email,first_name,last_name,middle_name,phone,password
            int k = cursor.getCount();
            Log.i(TAG,Integer.toString(k));
            try{
                if (cursor.moveToFirst()) {
                    currentUserDetail userD = new currentUserDetail();
                    do {
                        userD.setUserid(Integer.toString(cursor.getInt(0)));
                        Log.i(TAG, cursor.getString(0));
                        userD.setEmail(cursor.getString(1));
                        Log.i(TAG, cursor.getString(1));
                        userD.setFirstname(cursor.getString(2));
                        Log.i(TAG, cursor.getString(2));
                        userD.setLastname(cursor.getString(3));
                        Log.i(TAG, cursor.getString(3));
                        userD.setMiddlename(cursor.getString(4));
                        Log.i(TAG, cursor.getString(4));
                        userD.setPhonenumber(cursor.getString(5));
                        Log.i(TAG, cursor.getString(5));
                        //userD.setServiceid(cursor.getString(6));
                        //Log.i(TAG, cursor.getString(6));
                        //userD.setAuthorizationid(cursor.getString(7));
                        //Log.i(TAG, cursor.getString(7));
                    } while (cursor.moveToNext());
                }
            }finally{
                cursor.close();
            }
            /*while(cursor.moveToNext()){

                name = cursor.getString(0);
            }*/

            myDBconnection.close();

            return k;
        }else {
            return 0;
        }
    }
}
