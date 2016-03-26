package com.ensoft.mob.waterbiller.WebServices;

import android.util.Log;

import com.ensoft.mob.waterbiller.Models.AuthorizationHttpResponse;

import org.apache.http.HttpStatus;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Ebuka on 11/08/2015.
 */
public class HttpURLConnectionHelper {
    private final String USER_AGENT = "Mozilla/5.0";
    private static final String TAG = "HttpURL";

    public StringBuffer sendGet(String url) throws Exception
    {
        //example : http://www.google.com/search?k=johndo
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        //adding header
        con.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode = con.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while((inputLine = in.readLine()) != null)
        {
            response.append(inputLine);

        }
        in.close();
        Log.w(TAG,"response from Get; " + response.toString());
        return response;

    }

    public StringBuffer sendPOST(String url, String urlparams) throws Exception
    {
        //urlparams example: firstname=ebuka&company=techvibes&num=1234567
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        //send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlparams);
        wr.flush();
        wr.close();

        //get response code
        int responseCode = con.getResponseCode();

        //fetch response string
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while((inputLine = in.readLine()) != null)
        {
            response.append(inputLine);
        }
        in.close();
        Log.w(TAG,"Response from POST request;" + response);
        return response;
    }

    public static AuthorizationHttpResponse executePost(String targetURL, String urlParameters)
    {
        AuthorizationHttpResponse httpResponse = new AuthorizationHttpResponse();
        Log.i("Post Started", "just started the post");
        URL url;
        HttpURLConnection connection = null;
        try {
            //Create connection
            Log.i("Response", "using the try root");
            url = new URL(targetURL);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "keep-alive");
            connection.setRequestProperty("SendChunked", "True");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Length", "" +
                    Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
             //Send request
            DataOutputStream wr = new DataOutputStream (connection.getOutputStream ());
            wr.writeBytes (urlParameters);
            wr.flush();
            wr.close();

            //Get the statuscode
            httpResponse.setResponseCode(connection.getResponseCode());
            //Get Response

           InputStream errorStream = connection.getErrorStream();

              if(errorStream == null){
                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuffer response = new StringBuffer();

                while((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();
                Log.i("First response", "value:" + response.toString());
                  httpResponse.setResponseData(response.toString());
                   //return response.toString();
                  return httpResponse;
                  }
            else {

                  BufferedReader rd = new BufferedReader(new InputStreamReader(errorStream));
                  String line;
                  StringBuffer response = new StringBuffer();

                  while((line = rd.readLine()) != null) {
                      response.append(line);
                      response.append('\r');
                  }
                  rd.close();
               Log.i("error stream: ", "the error " + errorStream);
                  httpResponse.setResponseData(response.toString());
                return httpResponse;
            }

        } catch (Exception e) {
      Log.i("Response", "using the catch root");
            e.printStackTrace();
            return null;

        } finally {
            if(connection != null) {
                connection.disconnect();
            }
        }
    }


}
