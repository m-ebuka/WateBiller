package com.ensoft.mob.waterbiller;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ensoft.mob.waterbiller.helpers.GenericHelper;
import com.ensoft.mob.waterbiller.helpers.rResult;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ebuka on 05/08/2015.
 */
public class DataAPI {
    //VolleyApplication volley;
    public String api_output;
    //rResult res = new rResult();

    public void API_JSON(Context context, String url, final TextView mTextViewJson,  JSONObject jsonParams)
    {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url,
                jsonParams,
                new
                        Response.Listener<JSONObject>() {
                            public void onResponse(JSONObject response) {
                                mTextViewJson.setText(response.toString());
                            }
                        },
                new Response.ErrorListener(){
                    public void
                    onErrorResponse(VolleyError error){
                        mTextViewJson.setText(error.toString());
                    }
                }
        );
        VolleyApplication.getsInstance().getmRequestQueue().add(request);

    }

    public void API_POST(Context context, String url,  final Map<String, String> params) {
        //if (GenericHelper.checkInternetConenction(context)) {
            RequestQueue queue = Volley.newRequestQueue(context);
            StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                // public String api_output;

                @Override
                public void onResponse(String response) {
                    // mPostCommentResponse.requestCompleted();
                    Log.i("ResultTAG", response);
                    api_output = response;
                    rResult.setResult(api_output);

                    // getResponse(response);
                    //mTextView.setText(api_output);
                }
            }, new Response.ErrorListener() {
                // public String api_output;
                @Override
                public void onErrorResponse(VolleyError error) {
                    //  mPostCommentResponse.requestEndedWithError(error);
                    Log.i("ResultTAG", error.toString());
                    api_output = error.toString();
                    rResult.setResult(api_output);
                    // getResponse(api_output);
                    //mTextView.setText(api_output);
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                /*Map<String, String> params = new HashMap<String, String>();
                params.put("username", "oderichsam@gmail.com");
                params.put("device_uuid", "1234567");
                params.put("device_model", "1223shd12");
                params.put("device_platform", "adroid");
                params.put("device_version", "gg234");*/
                    return params;
                }

                ;

            };
            queue.add(sr);
        //}
    }

}

/*

        Map<String, String> params = new HashMap<String, String>();
        params.put("username", "oderichsam@gmail.com");
        params.put("device_uuid", "1234567");
        params.put("device_model", "1223shd12");
        params.put("device_platform", "adroid");
        params.put("device_version", "gg234");

        String url = "http://payment.titsallglobalschools.org/webapi/";
        d_api.API_POST(this, url, mTextView, params);


        // json
        Map<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("username", "oderichsam@gmail.com");
        jsonParams.put("device_uuid", "1234567");
        jsonParams.put("device_model", "1223shd12");
        jsonParams.put("device_platform", "adroid");
        jsonParams.put("device_version", "gg234");
        String url2 = "http://payment.titsallglobalschools.org/webapi/test_api.php/";

        JSONObject obj = new JSONObject(jsonParams);

        d_api.API_JSON(this, url2, mTextViewJson, obj);
                Map<String, String> params = new HashMap<String, String>();
        params.put("username", "oderichsam@gmail.com");
        params.put("device_uuid", "1234567");
        params.put("device_model", "1223shd12");
        params.put("device_platform", "adroid");
        params.put("device_version", "gg234");
           // return params;
       // };
        String url = "http://payment.titsallglobalschools.org/webapi/";
        d_api.API_POST(this, url, mTextView, params);
        */