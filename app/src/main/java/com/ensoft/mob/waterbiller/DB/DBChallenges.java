package com.ensoft.mob.waterbiller.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ensoft.mob.waterbiller.MyApp;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by TechVibes on 23-Sep-15.
 */
public class DBChallenges {
    MyApp app;
    DBConnection myDBconnection;
    private static final String TAG = "DBBuildingCategory";
    private DataDB db;

    public static final String KEY_ROWID = "_id";
    public static final String KEY_ID = "id_building_category";
    public static final String KEY_NAME = "building_category";

    public int fetchChallengeTypeIdByName(Context context, String challengType)
    {
        String[] args = {challengType};
        int challenge_typeId = 0;
        String sql = "SELECT idchallengetype FROM challengetype WHERE challenge=? LIMIT 1;";
        myDBconnection = new DBConnection(context);
        try {
            myDBconnection.createDataBase();
        } catch (IOException e) {
        }
        if(myDBconnection.checkDataBase()){
            myDBconnection.openDataBase();
            SQLiteDatabase db = myDBconnection.getWritableDatabase();
            Cursor cursor = db.rawQuery(sql, args);
            Log.i(TAG, "Result from challenge type query by name; " + cursor.toString());
            if (cursor.moveToFirst()) {
                while (cursor.isAfterLast() == false) {
                    Log.w("CHALLENGE TYPE ID", "here: " + cursor.getString(0));
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


    public ArrayList<String> fetchAllChallengeType(Context context) {
        String sql = "SELECT idchallengetype AS _id, challenge FROM challengetype;";
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
            System.out.println("CHALLENGE TYPE COUNT : " + allrows.getCount());

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
