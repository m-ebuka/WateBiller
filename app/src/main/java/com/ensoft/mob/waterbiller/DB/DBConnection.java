package com.ensoft.mob.waterbiller.DB;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
/**
 * Created by Ebuka on 05/08/2015.
 */
public class DBConnection extends SQLiteOpenHelper {
    //private static String DB_PATH = "";
    private static String DB_PATH = "/data/data/com.ensoft.mob.waterbiller/databases/";
    private static String DB_NAME = "abm0.db";
    private static final int DATABASE_VERSION = 80;
    private SQLiteDatabase myDataBase;
    private Context myContext = null;

    @SuppressLint("NewApi")
    public DBConnection(Context context) {
        //super(context, DB_NAME, null, DATABASE_VERSION);
        //this.myContext = context;

        super(context, DB_NAME, null, DATABASE_VERSION);
        this.myContext = context;
        //this.DB_PATH = this.myContext.getDatabasePath(DB_NAME).getAbsolutePath();
        Log.e("Path 1", DB_PATH);

    }

    public void createDataBase() throws IOException{
        boolean dbExist = checkDataBase();
        if(dbExist){
            //do nothing - database already exist
            Log.w("DB", "do nothing - database already exist ");
        }else{
            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();
            //this.close();
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }

    }

    public void copyDataBase() throws IOException{
        Log.w("DB", "Copying database... ");
        //Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public boolean checkDataBase(){
        SQLiteDatabase checkDB = null;
        try{
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
            Log.w("DB", "Checked and found database ");
        }catch(SQLiteException e){
            //database does't exist yet.
            Log.w("DB", "Database does not exist after checking ");
        }

        if(checkDB != null){
            checkDB.close();
        }

        return checkDB != null ? true : false;
    }

    public void openDataBase() throws SQLException{
        Log.w("DB", "Opening database... ");
        //Open the database
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);

    }

    @Override
    public synchronized void close() {
        if(myDataBase != null)
            myDataBase.close();
        super.close();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        /*try {
            createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        Log.w("DB", "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        //db.execSQL("DROP DATABASE IF EXISTS " + DB_NAME);
        //myContext.deleteDatabase(DB_NAME);
        //onCreate(db);
        if(newVersion>oldVersion){
            try {
                myContext.deleteDatabase(DB_NAME);
                //db.execSQL("DROP DATABASE IF EXISTS " + DB_NAME + ";");
                //db.execSQL("drop database abm0.db;");
                /*db.execSQL("DROP DATABASE IF EXISTS " + "aqua_biller2.db");
                db.execSQL("DROP DATABASE IF EXISTS " + "abm0.db");
                db.execSQL("DROP DATABASE IF EXISTS " + "aqua_biller_main56.db");
                db.execSQL("DROP DATABASE IF EXISTS " + "aqua_biller_main55.db");
                db.execSQL("DROP DATABASE IF EXISTS " + "aqua_biller11.db");
                db.execSQL("DROP DATABASE IF EXISTS " + "aqua_biller_main3.db");
                db.execSQL("DROP DATABASE IF EXISTS " + "aqua_biller20.db");
                db.execSQL("DROP DATABASE IF EXISTS " + "aqua_biller_main.db");
                db.execSQL("DROP DATABASE IF EXISTS " + "aqua_biller_main1.db");
                db.execSQL("DROP DATABASE IF EXISTS " + "aqua_biller_main2.db");
                db.execSQL("DROP DATABASE IF EXISTS " + "abm5.db");*/

                createDataBase();
                openDataBase();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public long onInsertOrUpdate(ContentValues values, String _tableName)
    {
        long id;
        Log.d("onInsertOrUpdate", "insertOrIgnore on " + values);
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            id=db.insertWithOnConflict(_tableName, null, values,
                    SQLiteDatabase.CONFLICT_REPLACE);
        } finally {
            db.close();
        }
        return id;
    }

    public long onUpdateOrIgnore(ContentValues values, String _tableName, String _fieldName, String _fieldValue)
    {
        long id;
        Log.d("onInsertOrUpdate", "insertOrIgnore on " + values);
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            //id=db.update(_tableName, values, _fieldName + "=?", new String[]{_fieldValue.toString()}); //new String[]{String.valueOf(_fieldValue)}
            id=db.update(_tableName, values, _fieldName + "='" + _fieldValue + "'", null);
        } finally {
            db.close();
        }
        return id;
    }

    public long onInsert(ContentValues values, String _tableName)
    {
        long isSuccess = 0;
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            isSuccess = db.insert(_tableName, null, values);
            db.close();
        }catch(Exception e){
            e.printStackTrace();
            isSuccess = 0;
        }
        myDataBase.close();
        return isSuccess;
    }

    public boolean onInsert(String query)
    {
        boolean isSuccess = false;
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL(query);
            db.close();
            isSuccess = true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            isSuccess = false;
        }
        return isSuccess;
    }

    /*public boolean onUpdate(ContentValues values, String _tableName)
    {
        boolean isSuccess = false;
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            //db.update(_tableName, null, values);

            db.close();
            isSuccess = true;
        }catch(Exception e){
            e.printStackTrace();
            isSuccess = false;
        }
        return isSuccess;
    }*/

    // count all records
    public long countRecords(String _tableName){

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT count(*) from " + _tableName, null);
        cursor.moveToFirst();

        long recCount = cursor.getInt(0);
        cursor.close();
        db.close();

        return recCount;
    }

    public Cursor selectAllFromTable(String _tableName)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * from " + _tableName, null);
        cursor.moveToFirst();

        cursor.close();
        db.close();

        return cursor;
    }

    public Cursor selectAllFromTable(String _from, String _fieldName, String _fieldValue)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT * FROM " + _from + " where " + _fieldName + " = '" +_fieldValue + "';";

        Cursor cursor = db.rawQuery(sql, null);
        if(cursor != null)
        {
            cursor.moveToFirst();
        }

        return cursor;
    }

    public Cursor selectAllFromTable(String sql,boolean yes)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        //String sql = "SELECT * FROM " + _from + " where " + _fieldName + " = '" +_fieldValue + "';";

        Cursor cursor = db.rawQuery(sql, null);
        if(cursor != null)
        {
            cursor.moveToFirst();
        }

        return cursor;
    }
    // TODO: 23-Sep-15 selects columns from a table with limit using an order by without a where clause
    public String selectColumnFromTableWithLimit(String _columns, String _table, int limit)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT " + _columns +" FROM " + _table + " LIMIT " +limit +";";
        String _what = null;
        Cursor cursor = db.rawQuery(sql, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Log.i("Selected field record", cursor.getString(0));
                    _what = cursor.getString(0);
                } while (cursor.moveToNext());

            }
        } finally {
            cursor.close();
        }

        return _what;
    }

    public String selectFromTable(String what, String _from, String _whereColumn, String _whereValue)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT " + what + " FROM " + _from + " where " + _whereColumn + " = '" +_whereValue + "';";
        String _what = null;
        Cursor cursor = db.rawQuery(sql, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Log.i("Selected field for record", cursor.getString(0));
                    _what = cursor.getString(0);
                } while (cursor.moveToNext());

            }
        } finally {
            cursor.close();
        }

        return _what;
    }


    // TODO: 23-Sep-15 selects colums from a table using a where clause
    public Cursor selectColumnsFromTable(String _columns, String _table, String _whereColumn, String _whereValue)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT " + _columns +" FROM " + _table + " where " + _whereColumn + " = '" + _whereValue + "';";

        Cursor cursor = db.rawQuery(sql, null);
        if(cursor != null)
        {
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }

    // TODO: 23-Sep-15 selects columns from a table using an order by without a where clause
    public Cursor selectColumnsFromTableOrderBy(String _columns, String _table)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT " + _columns +" FROM " + _table + ";";

        Cursor cursor = db.rawQuery(sql, null);
        if(cursor != null)
        {
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }



    // TODO: 23-Sep-15 selects columns from a table using an order by without a where clause
    public Cursor selectColumnsFromTableOrderBy(String _columns, String _table, String ORDER_BY)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT " + _columns +" FROM " + _table + " "+ ORDER_BY +";";

        Cursor cursor = db.rawQuery(sql, null);
        if(cursor != null)
        {
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }

    public String selectFromTableWithLimitAndOrder(String what, String _from, String _whereColumn, String _whereValue, String _limit, String _order_by)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT " + what + " FROM " + _from + " where " + _whereColumn + " = '" +_whereValue + "' ORDER BY " + _order_by + " LIMIT " + _limit + ";";
        Log.e("selectFromTableWithLimitAndOrder", sql);
        String _what = null;
        Cursor cursor = db.rawQuery(sql, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Log.i("Selected field for record", cursor.getString(0));
                    _what = cursor.getString(0);
                } while (cursor.moveToNext());

            }
        } finally {
            cursor.close();
        }

        return _what;
    }

    // TODO: 23-Sep-15  selects columns from a table using a where clause and order by
    public Cursor selectColumnsFromTableBy(String _columns, String _table, String _fieldName, String _fieldValue, String Order_by)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT " + _columns +" FROM " + _table + " where " + _fieldName + " = '" +_fieldValue + "' ORDER BY " + Order_by +";";
        Log.i("Sql query ", sql);
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor != null)
        {
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }



    // deletes all records
    public boolean deleteRecords(String _tableName){

        boolean isSuccess = false;
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("delete from "+ _tableName);
            db.close();
            isSuccess = true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            isSuccess = false;
        }
        return isSuccess;
    }

    // deletes records with id
    public boolean deleteRecords(String _tableName, String _fieldName, String _fieldValue)
    {
        boolean isSuccess = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("delete from " + _tableName + " where " + _fieldName + "=" + _fieldValue + ";");
            db.close();
            isSuccess = true;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            isSuccess = false;
        }
        return isSuccess;
    }


}
