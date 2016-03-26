package com.ensoft.mob.waterbiller.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ensoft.mob.waterbiller.DB.DBConnection;
import com.ensoft.mob.waterbiller.DB.DataDB;
import com.ensoft.mob.waterbiller.MyApp;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Ebuka on 25/08/2015.
 */
public class DBBuildingCategory {
    MyApp app;
    DBConnection myDBconnection;
    private static final String TAG = "DBBuildingCategory";
    private DataDB db;

    public static final String KEY_ROWID = "_id";
    public static final String KEY_ID = "id_building_category";
    public static final String KEY_NAME = "building_category";

    public int fetchBuildingCategoryIdByName(Context context, String categoryname)
    {
        String[] args = {categoryname};
        int building_categoryId = 0;
        String sql = "SELECT idbuilding_category FROM _building_categories WHERE building_category=? LIMIT 1;";
        myDBconnection = new DBConnection(context);
        try {
            myDBconnection.createDataBase();
        } catch (IOException e) {
        }
        if(myDBconnection.checkDataBase()){
            myDBconnection.openDataBase();
            SQLiteDatabase db = myDBconnection.getWritableDatabase();
            Cursor cursor = db.rawQuery(sql, args);
            Log.i(TAG,"Result from buildingcategory query by name; " + cursor.toString());
            while(cursor.moveToNext()){
                Log.w("BUILDING CATEGORY ID", "here: "+cursor.getString(0));
                String s = cursor.getString(0);
                building_categoryId = Integer.parseInt(s);
            }

            myDBconnection.close();

        }
        return building_categoryId;
    }

    public ArrayList<String> fetchAllBuildingCategory(Context context) {

        String sql = "SELECT building_category_id AS _id, building_category FROM _building_categories GROUP BY building_category;";
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
            System.out.println("BUILDING CATEGORY COUNT : " + allrows.getCount());

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
