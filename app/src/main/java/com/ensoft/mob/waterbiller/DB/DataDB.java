package com.ensoft.mob.waterbiller.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.ensoft.mob.waterbiller.Devices.Devices;
import com.ensoft.mob.waterbiller.Models.currentUserDetail;
import com.ensoft.mob.waterbiller.MyApp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by Ebuka on 05/08/2015.
 */
public class DataDB {
    private static final String TAG = "DataDB";
    MyApp appState;
    DBConnection myDBconnection;
    //DataBaseHelper myDBconnection;
    String name;
    public void testDB(Context context) throws SQLException {
        myDBconnection = new DBConnection(context);
        try{
            myDBconnection.createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        myDBconnection.openDataBase();
    }
    public int login(Context context, String email, String passw)
    {

        String[] args = {email,passw};
        Log.i("LoginCount", email +":"+ passw);
        myDBconnection = new DBConnection(context);
        try {
            myDBconnection.createDataBase();
        } catch (IOException e) {
        }

        if(myDBconnection.checkDataBase()){
            myDBconnection.openDataBase();
            SQLiteDatabase db = myDBconnection.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT user_id,email,first_name,middle_name,last_name,phone,group_id FROM users WHERE email=? AND pwd=? LIMIT 1", args);
            int k = cursor.getCount();
            Log.i("LoginLog",Integer.toString(k));
            try{
                if (cursor.moveToFirst()) {
                    currentUserDetail userD = new currentUserDetail();
                    do {
                        userD.setUserid(Integer.toString(cursor.getInt(0)));
                        userD.setEmail(cursor.getString(1));
                        appState = ((MyApp)context);
                        appState.setUserid(Integer.toString(cursor.getInt(0)));
                        appState.setEmail(cursor.getString(1));
                        appState.setFirstname(cursor.getString(2));
                        appState.setMiddlename(cursor.getString(3));
                        appState.setLastname(cursor.getString(4));
                        appState.setPhone(cursor.getString(5));
                        appState.setUserGroup_id(Integer.toString(cursor.getInt(6)));
                        String service_id = myConnection(context).selectFromTable("service_id","client_table","email",cursor.getString(1));
                        if(service_id != null) {
                            appState.setServiceid(service_id);
                        }
                        appState.setAuthorizationid(email + Devices.getDeviceUUID(context) + Devices.getDeviceIMEI(context));

                        Log.i("LoginLog", cursor.getString(2));

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

    public void insertByQuery(Context context, String query)
    {
        if(query != null)
        {
            DBConnection myDBconnection;
            myDBconnection = new DBConnection(context);
            try {
                myDBconnection.createDataBase();
            } catch (IOException e) {
            }
            if(myDBconnection.checkDataBase()) {
                myDBconnection.openDataBase();
                SQLiteDatabase db = myDBconnection.getWritableDatabase();
                Cursor cursor = db.rawQuery(query, null);

                myDBconnection.close();
            }
        }
    }

    public String getRecordStatusByTableAndID(Context context, String _tableName, String _what, String _idField, String _idFieldValue)
    {
        //_tableName is the name of the table you want to search on
        //_what is what you are selecting from table
        //_idField is the column id for value compare
        //_idFieldValue is the value you want to use to compare
        String status = null;
        String args[] = {_idFieldValue};
        String sql = "SELECT " + _what + " FROM " + _tableName + " where " + _idField +" =? LIMIT 1";
        DBConnection myDBconnection;
        myDBconnection = new DBConnection(context);
        try {
            myDBconnection.createDataBase();
        } catch (IOException e) {
        }
        if(myDBconnection.checkDataBase()) {
            myDBconnection.openDataBase();
            SQLiteDatabase db = myDBconnection.getWritableDatabase();
            Cursor cursor = db.rawQuery(sql, args);
            try {
                if (cursor.moveToFirst()) {
                    do {
                        Log.i("Selected status for record", cursor.getString(0));
                        status = cursor.getString(0);
                    } while (cursor.moveToNext());

                }
            } finally {
                cursor.close();
            }

            myDBconnection.close();

        }
        return status;
    }

    public String getSyncUrl(Context context)
    {
        String sql = "SELECT initial_url FROM url_table ORDER BY idurl_table DESC LIMIT 1";
        String url=null;
        DBConnection myDBconnection;
        myDBconnection = new DBConnection(context);
        try {
            myDBconnection.createDataBase();
        } catch (IOException e) {
        }

        if(myDBconnection.checkDataBase()) {
            myDBconnection.openDataBase();
            SQLiteDatabase db = myDBconnection.getWritableDatabase();
            Cursor cursor = db.rawQuery(sql, null);
            try {
                if (cursor.moveToFirst()) {
                   do {
                        Log.i("Selected sync url", cursor.getString(0));
                        url = cursor.getString(0);
                    } while (cursor.moveToNext());

                }
            } finally {
                cursor.close();
            }

            myDBconnection.close();

        }
        return url;
    }

    /*public Cursor getClientInfo(Context context, String email)
    {
        String arg=null;
        String sql = "SELECT * FROM client_table WHERE email=? LIMIT 1";

        DBConnection myDBconnection;
        myDBconnection = new DBConnection(context);
        try {
            myDBconnection.createDataBase();
        } catch (IOException e) {
        }

        if(myDBconnection.checkDataBase()) {
            myDBconnection.openDataBase();
            SQLiteDatabase db = myDBconnection.getWritableDatabase();
            Cursor cursor = db.rawQuery(sql, null);
            try {
                if (cursor.moveToFirst()) {

                    do {
                        Log.i("Selected sync url", cursor.getString(0));
                        //url = cursor.getString(0);
                    } while (cursor.moveToNext());
                }
            } finally {
                cursor.close();
            }

            myDBconnection.close();

        }
        return ur;
        }
        */


    public JSONArray cursorToJsonArray(Context context)
    {
        myDBconnection = new DBConnection(context);
        try {
            myDBconnection.createDataBase();
        } catch (IOException e) {
        }

        JSONArray resultSet 	= new JSONArray();
        JSONObject returnObj 	= new JSONObject();
        if(myDBconnection.checkDataBase()){
            myDBconnection.openDataBase();
            SQLiteDatabase db = myDBconnection.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM _buildings LIMIT 2", null);
            int k = cursor.getCount();
            Log.i("LoginLog",Integer.toString(k));
            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {

                int totalColumn = cursor.getColumnCount();
                JSONObject rowObject = new JSONObject();

                for( int i=0 ;  i< totalColumn ; i++ )
                {
                    if( cursor.getColumnName(i) != null )
                    {

                        try
                        {

                            if( cursor.getString(i) != null )
                            {
                                Log.d("TAG_NAME", cursor.getString(i) );
                                rowObject.put(cursor.getColumnName(i) ,  cursor.getString(i) );
                            }
                            else
                            {
                                rowObject.put( cursor.getColumnName(i) ,  "" );
                            }
                        }
                        catch( Exception e )
                        {
                            Log.d("TAG_NAME", e.getMessage()  );
                        }
                    }

                }

                resultSet.put(rowObject);
                cursor.moveToNext();
            }

            cursor.close();
            Log.d("TAG_NAME", resultSet.toString());


        }
        return resultSet;

    }

    public Cursor getAllStreetAllocatedToUser(Context context)
    {
        String sql = "SELECT street_name FROM street";
        myDBconnection = new DBConnection(context);
        try {
            myDBconnection.createDataBase();
        } catch (IOException e) {
        }

        if(myDBconnection.checkDataBase()){
            myDBconnection.openDataBase();
            myDBconnection.close();
            SQLiteDatabase db = myDBconnection.getWritableDatabase();
            Cursor cursor = db.rawQuery(sql, null);
            /*while(cursor.moveToNext()){
                name = cursor.getString(0);
            }*/

            myDBconnection.close();

            return cursor;
        }else{
            return null;
        }
    }
    public Boolean openDataBase(Context context)
    {
        myDBconnection = new DBConnection(context);
        Boolean con = false;
        try {
            myDBconnection.createDataBase();
        } catch (IOException e) {
            con = false;
        }
        if(myDBconnection.checkDataBase())
        {
            myDBconnection.openDataBase();
            con = true;
        }
        return con;
    }
    public void closeDataBase(Context context)
    {
        myDBconnection.close();
    }
    public String getNameDB(Context context){
        myDBconnection = new DBConnection(context);
        //myDBconnection = new DataBaseHelper(context);
        try {
            myDBconnection.createDataBase();
        } catch (IOException e) {
        }

        if(myDBconnection.checkDataBase()){
            myDBconnection.openDataBase();
            myDBconnection.close();
            SQLiteDatabase db = myDBconnection.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT first_name,last_name FROM users", null);
            while(cursor.moveToNext()){
                name = "Hello " + cursor.getString(0) + " " + cursor.getString(1);
            }

            myDBconnection.close();

            return name;
        }else{
            return "ERROR";
        }
    }

    public boolean insertIntoTableByContentValue(Context context, String _TABLENAME, ContentValues contentValues)
    {
        Log.e(TAG,"Insert started");
        myDBconnection = new DBConnection(context);
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
                db.insertOrThrow(_TABLENAME, null, contentValues);
                db.setTransactionSuccessful(); //necessary
                Toast.makeText(context, _TABLENAME + " Table insert done successful", Toast.LENGTH_LONG).show();
                return true;
            }
            catch (Exception e)
            {
                //db.update(_TABLENAME,_tableValues,_tableID +"="+ _tableID_Value,null);
                Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                //return true;
            }
            finally {
                db.endTransaction();
                myDBconnection.close();
            }



        }
        return false;
    }

    public boolean updateIntoTableByContentValue(Context context, String _TABLENAME, ContentValues contentValues, String _tableID, String _tableID_Value)
    {
        Log.e(TAG,"update started");
        myDBconnection = new DBConnection(context);
        try {
            myDBconnection.createDataBase();
        } catch (IOException e) {
        }
        if(myDBconnection.checkDataBase()){
            myDBconnection.openDataBase();
            SQLiteDatabase db = myDBconnection.getWritableDatabase();

            try{
                db.update(_TABLENAME,contentValues,_tableID +"="+ _tableID_Value,null);
                db.setTransactionSuccessful(); //necessary
                Toast.makeText(context, _TABLENAME + " Table update done successful", Toast.LENGTH_LONG).show();
                return true;
            }
            catch (Exception e)
            {
                Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                return false;
            }
            finally {
                db.endTransaction();
                myDBconnection.close();
            }



        }
        return false;
    }

    public DBConnection myConnection(Context context) {
        myDBconnection = new DBConnection(context);
        try {
            myDBconnection.createDataBase();
        } catch (IOException e) {
        }
        if (myDBconnection.checkDataBase()) {
            myDBconnection.openDataBase();
        }
        return myDBconnection;
    }

}
