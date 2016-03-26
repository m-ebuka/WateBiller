package com.ensoft.mob.waterbiller.WebServices;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ensoft.mob.waterbiller.VolleyApplication;

import org.json.JSONObject;

/**
 * Created by Ebuka on 05/08/2015.
 */
public class DataAPI {
    //VolleyApplication volley;
    public String api_output;

    public void API_Get(Context context, String url)
    {
        //volley = new VolleyApplication();

        JsonObjectRequest request = new JsonObjectRequest(url, null,
                new
                        Response.Listener<JSONObject>() {
                            //                   @Override
                            public void onResponse(JSONObject response) {

                                api_output = response.toString();
                                Log.i("api output", api_output);

                                //mTextView.setText(response.toString());
                            }
                        },
                new Response.ErrorListener(){
                    //  @Override
                    public void onErrorResponse(VolleyError error){

                        api_output = error.toString();
                        Log.i("api output", api_output);

                        //mTextView.setText(error.toString());
                    }
                }
        );


        Log.i("api url", url);

        VolleyApplication.getsInstance().getmRequestQueue().add(request);
        //VolleyApplication.getsInstance().getmRequestQueue().add(request);
        //Log.i("api url",null);

    }


}
