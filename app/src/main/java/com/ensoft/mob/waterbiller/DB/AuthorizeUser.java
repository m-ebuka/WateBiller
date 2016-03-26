package com.ensoft.mob.waterbiller.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.ensoft.mob.waterbiller.DB.DBConnection;
import com.ensoft.mob.waterbiller.Models.currentUserDetail;
import com.ensoft.mob.waterbiller.MyApp;
import com.ensoft.mob.waterbiller.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

/**
 * Created by TechVibes on 01-Sep-15.
 */
public class AuthorizeUser {
    //AuthorizeUser authorizeUser = new AuthorizeUser();
    DBCheckAuthorization dbCheckAuthorization = new DBCheckAuthorization();
    DBURL_Table dburl_table = new DBURL_Table();
    DataDB dataDB = new DataDB();
    MyApp appState;
    public static final String service_details = "service_details";
    public static final String CLIENT_NAME = "CLIENT_NAME";
    public static final String SERVICE_ID = "SERVICE_ID";
    public static final String INITIAL_URL = "INITIAL_URL";
    public static final String AUTHENTICATION_CODE = "AUTHENTICATION_CODE";
    public static final String TABLES = "SERVER_SYNC_TABLES";
    public static final String SYNCH_TABLE = "MOBILE_SYNCH_TABLE";
    public  static final String URL_VERSION_DATE = "URL_VERSION_DATE";

    //// INSERTING INTO TABLES
    public static final String tTableName = "seq_tables";
    public static final String seq_table = "seq_table";

    // INSERTING URL TABLE
    public  static  final String tUrlTableName = "url_table";

    // INSERTING INTO SYNCH_TABLE
    public static final String tSynchTableName = "seq_sync_server"; //seq_synch_table
    public static final String seq_synch_table = "seq_table_title"; //synch_table

    // INSERTING INTO CLIENT_TABLE
    public static final String tClientName = "client_name";
    public static final String tServiceId = "service_id";
    public static final String tAuthCode = "authorization_code";
    public static final String tClientTableName = "client_table";
    public static final String tEmail = "email";
    public static final String tService_code = "service_code";
    public static final String tAdd_request_url = "add_request_url";
    public static final String tRequest_date = "request_date";
    public static final String tIsFirstSyncDone = "isFirstSyncDone";

    // AUTHENTICATION PARAMETERS
    public static final String ServiceId = "service_id";
    public static final String AuthorizationId = "authorization_id";
    public static final String AuthenticationCode ="authentication_code";



    public void insertTables(Context context, String stringTables){
        // AuthorizeUser AUTH_USER = new AuthorizeUser();
        DBConnection myDBconnection;
        myDBconnection = new DBConnection(context);
        try {
            myDBconnection.createDataBase();
        } catch (IOException e) {
        }

        if(myDBconnection.checkDataBase()){
            myDBconnection.openDataBase();

            SQLiteDatabase db = myDBconnection.getWritableDatabase();


            db.delete(tTableName, null, null);

            ArrayList tListTables= new ArrayList(Arrays.asList(stringTables.split(",")));
            ContentValues tTables = new ContentValues();
            for(int i=0;i<tListTables.size();i++)
            {
                tTables.put(seq_table, tListTables.get(i).toString());
                db.insert(tTableName, null, tTables);
            }
            myDBconnection.close();
        }

    }


    public void insertSynchTables(Context context, String stringSynchTables){
        // AuthorizeUser AUTH_USER = new AuthorizeUser();
        DBConnection myDBconnection;
        myDBconnection = new DBConnection(context);
        try {
            myDBconnection.createDataBase();
        } catch (IOException e) {
        }

        if(myDBconnection.checkDataBase()){
            myDBconnection.openDataBase();

            SQLiteDatabase db = myDBconnection.getWritableDatabase();

            db.delete(tSynchTableName, null, null);

            ArrayList tListTables= new ArrayList(Arrays.asList(stringSynchTables.split(",")));
            ContentValues tTables = new ContentValues();
            for(int i=0;i<tListTables.size();i++)
            {
                tTables.put(seq_synch_table, tListTables.get(i).toString());
                db.insert(tSynchTableName, null, tTables);
            }
            myDBconnection.close();
        }
    }


    public boolean insertClientTable( Context context, String client_name, String service_id, String authorization_code){
        // AuthorizeUser AUTH_USER = new AuthorizeUser();
        DBConnection myDBconnection;
        myDBconnection = new DBConnection(context);
        try {
            myDBconnection.createDataBase();
        } catch (IOException e) {
        }
        if(myDBconnection.checkDataBase()){
            myDBconnection.openDataBase();
            SQLiteDatabase db = myDBconnection.getWritableDatabase();
            ContentValues insertData = new ContentValues();
            db.delete(tClientTableName, null, null);
            insertData.put(tClientName, client_name);
            insertData.put(tServiceId, service_id);
            insertData.put(tAuthCode, authorization_code);

            db.insert(tClientTableName, null, insertData);
            System.out.println(insertData);

            myDBconnection.close();
        }
        return true;
    }

    public boolean initialRequest(Context context, String temail, String service_code, String add_request_url){
        // AuthorizeUser AUTH_USER = new AuthorizeUser();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        String formattedDate = df.format(c.getTime());

        DBConnection myDBconnection;
        myDBconnection = new DBConnection(context);
        try {
            myDBconnection.createDataBase();
        } catch (IOException e) {
        }
        if(myDBconnection.checkDataBase()){
            myDBconnection.openDataBase();
            SQLiteDatabase db = myDBconnection.getWritableDatabase();
            ContentValues insertData = new ContentValues();

            db.delete(tClientTableName, null, null);
            //insertData.put(tClientName, client_name);
            // insertData.put(tServiceId, service_id);
            //insertData.put(tAuthCode, authorization_code);
            insertData.put(tEmail, temail);
            insertData.put(tService_code, service_code);
            insertData.put("authorized", "0");
            insertData.put(tAdd_request_url, add_request_url);
            insertData.put(tRequest_date, formattedDate);

            db.insert(tClientTableName, null, insertData);
            System.out.println("Inserted into client_table" + insertData);
            db.close();
            myDBconnection.close();
        }
        return true;
    }

    public static boolean updateRequest(Context context, String temail, ContentValues updateData){

        boolean isSucceed = false;
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
                db.update(tClientTableName, updateData, tEmail + "='" + temail + "'", null);
                System.out.println(updateData);
                isSucceed = true;
            }
            catch (Exception e)
            {
                isSucceed = false;
            }
            db.close();
            myDBconnection.close();
        }
        return isSucceed;
    }

    public boolean updateRequest(Context context, String temail,boolean isauthorized, String authcode,String clientname,String serviceid){
        // AuthorizeUser AUTH_USER = new AuthorizeUser();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        String formattedDate = df.format(c.getTime());
        DBConnection myDBconnection;
        myDBconnection = new DBConnection(context);
        try {
            myDBconnection.createDataBase();
        } catch (IOException e) {
        }
        if(myDBconnection.checkDataBase()){
            myDBconnection.openDataBase();
            SQLiteDatabase db = myDBconnection.getWritableDatabase();
            ContentValues insertData = new ContentValues();

            insertData.put(tRequest_date, formattedDate);

            if(isauthorized)
            {
                Log.e("AuthorizeUser", "inside checking if authorize is set");
                insertData.put("authorized", "1");
                insertData.put(tClientName, clientname);
                insertData.put(tAuthCode, authcode);
                insertData.put(tServiceId, serviceid);
                insertData.put(tIsFirstSyncDone, "1");
                db.update(tClientTableName, insertData, tEmail + "='" + temail + "'", null);
            }
            else{
                Log.e("AuthorizeUser", "after checking if authorize is set,update");
                db.update(tClientTableName, insertData, tEmail + "='" + temail + "'", null);
            }

            Log.i("updating client info", "successfully");

            System.out.println(insertData);
            db.close();
            myDBconnection.close();
        }
        return true;
    }

    public int AuthorizationState(Context context, String email)
    {
        Log.i("check state", "started successfully");
        String[] args = {email};
        DBConnection myDBconnection;
        myDBconnection = new DBConnection(context);
        try {
            myDBconnection.createDataBase();
        } catch (IOException e) {
        }

        if(myDBconnection.checkDataBase()) {
            myDBconnection.openDataBase();
            SQLiteDatabase db = myDBconnection.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT email FROM client_table WHERE email=? LIMIT 1", args);
            int k = cursor.getCount();
            Log.i("clientStatus", Integer.toString(k));
            try {
                if (cursor.moveToFirst()) {
                    currentUserDetail userD = new currentUserDetail();
                    do {
                        Log.i("Selected email", cursor.getString(0));

                    } while (cursor.moveToNext());
                }
            } finally {
                cursor.close();
            }

            myDBconnection.close();

            return k;
        }else {
            return 0;
        }
    }

    public boolean saveAuthorizationDetails(Context context, String data)
    {
        boolean isSuccess = false;
        try {
            Log.e("responseTry", "started processResponse");
            JSONObject jsonReader = new JSONObject(data);
            JSONObject servicedetail = jsonReader.getJSONObject(this.service_details);
            String clientName = servicedetail.getString(this.CLIENT_NAME);
            String service_id = servicedetail.getString(this.SERVICE_ID);
            String initial_url = servicedetail.getString(this.INITIAL_URL);

            String url_version_code = servicedetail.getString(this.URL_VERSION_DATE);
            String authentication_code = servicedetail.getString(this.AUTHENTICATION_CODE);
            String sync_tables = servicedetail.getString(this.SYNCH_TABLE);
            String tables = servicedetail.getString(this.TABLES);

            //fetch email from client_table
            String email = dbCheckAuthorization.fetchUserEmail(context);

            //checking
            if(clientName != null
                    && service_id != null
                    && initial_url != null
                    && url_version_code != null
                    && authentication_code != null
                    && sync_tables != null
                    && tables != null)
            {
                String urlDecoded = URLDecoder.decode(initial_url);
                Log.e("RequestDataProcessor", "am here to update and insert data in tables");
                //pass client data to be added

                //compile the data to save in the url_table
                ContentValues urlValuesToSave = new ContentValues();
                urlValuesToSave.put("req_initial_url", R.string.authorization_endpoint);
                urlValuesToSave.put("initial_url",urlDecoded);
                urlValuesToSave.put("url_version_date", url_version_code);

                ContentValues valuesToUpdate = new ContentValues();
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
                String formattedDate = df.format(c.getTime());
                valuesToUpdate.put("authorized", "1");
                valuesToUpdate.put("authorized_date", formattedDate);
                valuesToUpdate.put("authorization_code",authentication_code.trim());
                valuesToUpdate.put("client_name",clientName.trim());
                valuesToUpdate.put("service_id",service_id.trim());

                insertTables(context, tables);
                insertSynchTables(context, sync_tables);
                boolean isClientTableUpdated = dataDB.updateIntoTableByContentValue(context, "client_table", valuesToUpdate, "email", email);
                //authorizeUser.updateRequest(context, email, true, authentication_code.trim(), clientName.trim(), service_id.trim());
                boolean isURLSaved = dataDB.insertIntoTableByContentValue(context,"url_table",urlValuesToSave);

                if(isURLSaved)
                { Log.e("URL TABLE", "url saved successfully into url_table"); }

                //call the receiving sync here
                isSuccess = true;

            }
        } catch (JSONException e) {
            Log.e("responseCatch", "using JSON CATCH");
            e.printStackTrace();
        }
        return isSuccess;
    }

    public boolean insertAuthTableData(Context context, String columnName, String _tableName, String _tableValue){
        Log.e("_tableName","The table supplied; " + _tableName);
        boolean isSuccess = false;
        DBConnection myDBconnection;
        myDBconnection = new DBConnection(context);
        try {
            myDBconnection.createDataBase();
        } catch (IOException e) {
        }

        if(myDBconnection.checkDataBase()){
            myDBconnection.openDataBase();

            SQLiteDatabase db = myDBconnection.getWritableDatabase();

            db.delete(_tableName, null, null);

            ArrayList tListTables= new ArrayList(Arrays.asList(_tableValue.split(",")));
            ContentValues tTables = new ContentValues();
            for(int i=0;i<tListTables.size();i++)
            {
                tTables.put(columnName, tListTables.get(i).toString());
                db.insert(_tableName, null, tTables);
                Log.e("_tableName", "Saved; " + tListTables.get(i).toString() + " ;into : " + _tableName);
            }
            System.out.println(_tableValue);
            db.close();
            myDBconnection.close();
            return true;
        }

        return isSuccess;
    }

    public boolean insertAuthUrl(Context context, ContentValues contentValues, String _tableName)
    {
        boolean isSuccess = false;
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
                db.insertOrThrow(_tableName, null, contentValues);
                isSuccess = true;
            }
            catch (Exception e)
            {
                //db.update(_TABLENAME,_tableValues,_tableID +"="+ _tableID_Value,null);
                Log.e("Insert Exception","Exception on insert");
                isSuccess = false;
            }

            db.close();
            myDBconnection.close();
            return true;
        }
        return isSuccess;
    }

}
