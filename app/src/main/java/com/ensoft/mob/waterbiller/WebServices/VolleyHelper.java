package com.ensoft.mob.waterbiller.WebServices;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ensoft.mob.waterbiller.Models.VolleyHttpResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ebuka on 05/09/2015.
 */
public class VolleyHelper {
    private static final String TAG = "VolleyHelper";
    public void SendStringPost(Context context, String url, final HashMap<String,String> _map)
    {
        final String reqResponse;
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        // Request a string response from the provided URL.
        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                createPostSuccessListener(),
                createPostErrorListener()) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();
                map = _map;
                //map.put("name", "Jon Doe");
                //map.put("age", "21");

                return map;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };

// Add the request to the RequestQueue.
        queue.add(request);
        //ApplicationController.getInstance().addToRequestQueue(request);
    }

    private static Response.Listener<String> createPostSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // TODO parse response
                VolleyHttpResponse httpResponse = new VolleyHttpResponse();
                httpResponse.setSuccessResponse(response);

            }
        };
    }

    private static Response.ErrorListener createPostErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error Response code: " + error.getMessage());
                VolleyHttpResponse httpResponse = new VolleyHttpResponse();
                NetworkResponse response = error.networkResponse;
                httpResponse.setErrorResponse(error.getMessage());

                if (response != null) {
                    httpResponse.setStatusCode(response.statusCode);
                }
            }
        };
    }

    public void SendJsonObjectPost(Context context, String url, HashMap<String,String> params)
    {
        /*HashMap<String,String> params = new HashMap<String,String>();
        params.put("device_uuid","dhejjy3782jns");*/

        JsonObjectRequest request = new JsonObjectRequest(
                url,
                new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            VolleyLog.v("Response:%n %s", response.toString(4));
                            VolleyHttpResponse httpResponse = new VolleyHttpResponse();
                            httpResponse.setSuccessJSONResponse(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                VolleyHttpResponse httpResponse = new VolleyHttpResponse();
                httpResponse.setErrorResponse(error.getMessage());
            }
        }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        // add the request object to the queue to be executed
        ApplicationController.getInstance().addToRequestQueue(request);
    }

    public void SendJsonArrayPost(Context context, String url)
    {
        JsonArrayRequest req = new JsonArrayRequest(url, new Response.Listener<JSONArray> () {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    VolleyLog.v("Response:%n %s", response.toString(4));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

// add the request object to the queue to be executed
        ApplicationController.getInstance().addToRequestQueue(req);
    }

    public void PostJson(String url, Map<String, String> jsonParams)
    {
        //Map<String, String> jsonParams = new HashMap<String, String>();
        //jsonParams.put("param1", youParameter);

        JsonObjectRequest myRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                new JSONObject(jsonParams),

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        VolleyLog.e("Error: ", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.e("Error: ", error.getMessage());
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("User-agent", "Mozilla/5.0");
                return headers;
            }
        };
        ApplicationController.getInstance().addToRequestQueue(myRequest, "tag");
    }
}
