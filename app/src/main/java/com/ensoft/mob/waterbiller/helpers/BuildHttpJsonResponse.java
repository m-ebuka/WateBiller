package com.ensoft.mob.waterbiller.helpers;

import com.ensoft.mob.waterbiller.DB.AuthorizeUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Ebuka on 16/09/2015.
 */
public class BuildHttpJsonResponse {

    public static void buildResponse() throws JSONException {
        JSONObject jsonObj = new JSONObject(); //main object

        JSONObject jsonElement1 = new JSONObject();
        jsonElement1.put("userid-0011","1");
        jsonElement1.put("userid-0011","1");

        jsonObj.put("mobile_response",jsonElement1);

    }
    public static JSONObject syncDataJson(String service_id, String authorization_id, String authentication_code,String action,JSONObject pushData, JSONObject responseData){
        JSONObject syncDataJson = new JSONObject(); //parent node
        JSONObject jsonObj = new JSONObject(); //collection node object
        //JSONObject pushJsonData = new JSONObject(); //node object
        //JSONObject responseJsonData = new JSONObject();

        try {
            JSONObject jsonResponse = new JSONObject(); //for table

            JSONObject authJsonObject = new JSONObject();
            authJsonObject.put(AuthorizeUser.ServiceId,service_id);
            authJsonObject.put(AuthorizeUser.AuthenticationCode,authentication_code);
            authJsonObject.put(AuthorizeUser.AuthorizationId,authorization_id);
            authJsonObject.put("action",action);

            jsonObj.put("AUTH_DATA",authJsonObject);

            jsonObj.put("SYNC_DATA",pushData);


            //JSONObject rJson=new JSONObject(responseData);
            //jsonResponse.put("_building",responseData);
            jsonObj.put("RESPONSE",responseData);
            //jsonObj.put("RESPONSE",responseData);
            //JSONArray array=new JSONArray(jsonResponse);

            syncDataJson.put("REQ_DATA",jsonObj);
            return syncDataJson;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
