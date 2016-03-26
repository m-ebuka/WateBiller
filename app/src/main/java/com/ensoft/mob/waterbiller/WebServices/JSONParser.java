package com.ensoft.mob.waterbiller.WebServices;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import com.ensoft.mob.waterbiller.Models.AuthorizationHttpResponse;
import com.ensoft.mob.waterbiller.helpers.GenericHelper;
import com.goebl.david.Webb;
import com.loopj.android.http.*;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

/**
 * Created by Ebuka on 08/09/2015.
 */
public class JSONParser {
    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";

    // constructor
    public JSONParser() {

    }



    // function get json from url
    // by making HTTP POST or GET mehtod
    public JSONObject makeHttpRequest(String url, String method,
                                      List<NameValuePair> params) {

        // Making HTTP request
        try {

            // check for request method
            if(method == "POST"){
                // request method is POST
                // defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(new UrlEncodedFormEntity(params));

                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();

            }else if(method == "GET"){
                // request method is GET
                DefaultHttpClient httpClient = new DefaultHttpClient();
                String paramString = URLEncodedUtils.format(params, "utf-8");
                url += "?" + paramString;
                HttpGet httpGet = new HttpGet(url);

                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        // return JSON String
        return jObj;

    }

    public void sendJsonObject(String objParams, String myurl) throws IOException {
        //URL url;
        URLConnection urlConn;
        DataOutputStream printout;
        DataInputStream input;
        URL url = null;
        url = new URL(myurl);
        urlConn = url.openConnection();
        urlConn.setDoInput(true);
        urlConn.setDoOutput(true);
        urlConn.setUseCaches(false);
        urlConn.setRequestProperty("Content-Type","application/json");
        urlConn.connect();

        // Send POST output.
        printout = new DataOutputStream(urlConn.getOutputStream ());
        printout.write(Integer.parseInt(URLEncoder.encode(objParams.toString(), "UTF-8")));
        printout.flush();
        printout.close();
    }

    public static HttpResponse makeJsonRequest(String url, JSONObject jsonObject) throws Exception
    {
        //instantiates httpclient to make request
        DefaultHttpClient httpclient = new DefaultHttpClient();

        //url with the post data
        HttpPost httpost = new HttpPost(url);

        //convert parameters into JSON object
        JSONObject holder = jsonObject;
        //passes the results to a string builder/entity
        StringEntity se = new StringEntity(holder.toString());
        //sets the post request as the resulting string
        httpost.setEntity(se);
        httpost.setHeader("Accept", "application/json");
        httpost.setHeader("Content-type", "application/json");

        //Handles what is returned from the page
        ResponseHandler responseHandler = new BasicResponseHandler();
        return (HttpResponse) httpclient.execute(httpost, responseHandler);
    }

    public void postJsonBody(String url,JSONObject obj) {
        // Create a new HttpClient and Post Header

        HttpParams myParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(myParams, 10000);
        HttpConnectionParams.setSoTimeout(myParams, 10000);
        HttpClient httpclient = new DefaultHttpClient(myParams );
        String json=obj.toString();

        try {

            HttpPost httppost = new HttpPost(url.toString());
            httppost.setHeader("Content-type", "text/html"); //application/json

            StringEntity se = new StringEntity(obj.toString());
            se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httppost.setEntity(se);

            HttpResponse response = httpclient.execute(httppost);
            String temp = EntityUtils.toString(response.getEntity());
            Log.i("tag", temp);


        } catch (ClientProtocolException e) {

        } catch (IOException e) {
        }
    }

    public AuthorizationHttpResponse postMadeEasy(Context context,String url,String method,String params)
    {
        final AuthorizationHttpResponse httpResponse = new AuthorizationHttpResponse();
        try{
            if(GenericHelper.checkInternetConenction(context)){
              //  AsyncHttpClient client = new AsyncHttpClient();
                SyncHttpClient client = new SyncHttpClient();

                if(method == "GET"){
                    client.get(url, new AsyncHttpResponseHandler() {

                        @Override
                        public void onStart() {
                            // called before request is started
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                            // called when response HTTP status is "200 OK"
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                            // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                        }

                        @Override
                        public void onRetry(int retryNo) {
                            // called when request is retried
                        }
                    });
                }
                if(method == "POST")
                {
                    //params = "{\"AUTH_DATA\":{\"action\":\"PUSH\",\"service_id\":\"234001\",\"authorization_id\":\"mimi@techvibesltd.com383748374801837383728373801\",\"authentication_code\":\"9915603a567f20556b752ff4ad0af28abf93c9de94994f0b6371531916b5db3dd0e8bf74012196024bbd46c151a405fa\",\"DATA\":{\"SYNC_DATA\":{\"mobile_synch_meter_readings\":[{\"date_previous_reading\":\"2015-01-23 25:03:01\",\"service_id\":\"2093348\",\"number_of_retry\":\"0\",\"idmeter_reading\":\"5\",\"session_id\":\"2039984840\",\"meter_no\":\"49039422\",\"back_up\":\"0\",\"building_id\":\"5\",\"last_synched_date\":\"2015-09-01 12:40:22\",\"synch_status\":\"0\",\"current_reading\":\"12209\",\"previous_reading\":\"33390\",\"user_id\":\"1\",\"meter_image\":\"iVBORw0KGgoAAAANS\",\"authorization_id\":\"1\",\"locked\":\"0\",\"date_current_reading\":\"2015-03-01 04:02:10\"},{\"date_previous_reading\":\"2015-02-03 12:01-22\",\"service_id\":\"2093894\",\"number_of_retry\":\"0\",\"idmeter_reading\":\"3\",\"session_id\":\"20124489\",\"meter_no\":\"2123090\",\"back_up\":\"0\",\"building_id\":\"3\",\"last_synched_date\":\"2015-09-01 12:40:22\",\"synch_status\":\"0\",\"current_reading\":\"700\",\"previous_reading\":\"600\",\"user_id\":\"1\",\"meter_image\":\"iVBORw0KGgoAAAANS\",\"authorization_id\":\"1\",\"locked\":\"0\",\"date_current_reading\":\"2015-03-03 12:12:20\"},{\"date_previous_reading\":\"2014-01-23 24:02:03\",\"service_id\":\"3012309\",\"number_of_retry\":\"0\",\"idmeter_reading\":\"4\",\"session_id\":\"32098012\",\"meter_no\":\"32409431\",\"back_up\":\"0\",\"building_id\":\"4\",\"last_synched_date\":\"2015-09-01 12:40:22\",\"synch_status\":\"0\",\"current_reading\":\"1208\",\"previous_reading\":\"800.4\",\"user_id\":\"1\",\"meter_image\":\"iVBORw0KGgoAAAANS\",\"authorization_id\":\"1\",\"locked\":\"0\",\"date_current_reading\":\"2014-04-02 11:12:10\"},{\"date_previous_reading\":\"2015-08-01 12:40:22\",\"service_id\":\"3212343\",\"number_of_retry\":\"0\",\"idmeter_reading\":\"1\",\"session_id\":\"20148902\",\"meter_no\":\"12309012\",\"back_up\":\"0\",\"building_id\":\"1\",\"last_synched_date\":\"2015-09-01 12:40:22\",\"synch_status\":\"0\",\"current_reading\":\"3006.5\",\"previous_reading\":\"3003.5\",\"user_id\":\"1\",\"meter_image\":\"iVBORw0KGgoAAAANS\",\"authorization_id\":\"1\",\"locked\":\"0\",\"date_current_reading\":\"2015-09-01 12:40:22\"},{\"date_previous_reading\":\"2015-06-02 13:20:23\",\"service_id\":\"3212343\",\"number_of_retry\":\"0\",\"idmeter_reading\":\"2\",\"session_id\":\"20148902\",\"meter_no\":\"8290123\",\"back_up\":\"0\",\"building_id\":\"2\",\"last_synched_date\":\"2015-09-01 12:40:22\",\"synch_status\":\"0\",\"current_reading\":\"4200\",\"previous_reading\":\"4000\",\"user_id\":\"1\",\"meter_image\":\"iVBORw0KGgoAAAANS\",\"authorization_id\":\"1\",\"locked\":\"0\",\"date_current_reading\":\"2015-07-02 13:20:23\"}]}}}}";
                    StringEntity se = null;
                    try {
                        se = new StringEntity(params.toString());
                    } catch (UnsupportedEncodingException e) {
                        // handle exceptions properly!
                    }
                    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "text/html"));

                    ByteArrayEntity entity = new ByteArrayEntity(params.getBytes("UTF-8"));
                    client.post(context, url, se, "application/json", new AsyncHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            httpResponse.setResponseCode(statusCode);
                            httpResponse.setResponseByteData(responseBody);
                            System.out.println("OnSuccess: " + responseBody + "StatusCode: " + statusCode);

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            httpResponse.setResponseCode(statusCode);
                            httpResponse.setResponseByteData(responseBody);
                            System.out.println("OnFailure: " + responseBody + "StatusCode: " + statusCode);
                        }
                    });
                }
            }
            else{
                System.out.println("No Internet Connection ");
                httpResponse.setResponseCode(0);
                httpResponse.setResponseData("No Internet Connection");
            }



        }
        catch (Exception e){
            System.out.println("Exception gotten; " + e.toString());
            httpResponse.setResponseCode(0);
            httpResponse.setResponseData("Exception gotten; " + e.toString());
        }
        return httpResponse;
    }

    public void testPostForSync() {
        /*String urlParameters  = "authorization_id=mimi@techvibesltd.com358429066656504358429066656504&authorization_code=9915603a567f20556b752ff4ad0af28abf93c9de94994f0b6371531916b5db3dd0e8bf74012196024bbd46c151a405fa&service_id=234001";
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        String request = "http://aquabiller.taxo-igr.com/webS/request.php/users/authenticate";
        URL url = new URL(request);
        HttpURLConnection conn= (HttpURLConnection)url.openConnection();
        conn.setDoOutput( true );
        conn.setInstanceFollowRedirects( false );
        conn.setRequestMethod("POST");
        conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty( "charset", "utf-8");
        conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
        conn.setUseCaches( false );
        try( DataOutputStream wr = new DataOutputStream( conn.getOutputStream())) {
            wr.write( postData );
        }*/

        /*String urlParameters  = "authorization_id=mimi@techvibesltd.com358429066656504358429066656504&authorization_code=9915603a567f20556b752ff4ad0af28abf93c9de94994f0b6371531916b5db3dd0e8bf74012196024bbd46c151a405fa&service_id=234001";
        URL url = new URL("http://aquabiller.taxo-igr.com/webS/request.php/users/authenticate");
        URLConnection conn = url.openConnection();

        conn.setDoOutput(true);

        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
        writer.write(urlParameters);
        writer.flush();
        String line;
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        writer.close();
        reader.close();*/

        Webb webb = Webb.create();
        //webb.setBaseUri("http://aquabiller.taxo-igr.com/webS/request.php/users/authenticate");
        //webb.setDefaultHeader(Webb.HDR_USER_AGENT, Const.UA);

        /*webb.post("http://aquabiller.taxo-igr.com/webS/request.php/users/authenticate")
                .param("authorization_id", "mimi@techvibesltd.com35842906665650435842906665650")
                .param("authorization_code", "9915603a567f20556b752ff4ad0af28abf93c9de94994f0b6371531916b5db3dd0e8bf74012196024bbd46c151a405fa")
                .param("service_id", "234001")
                .ensureSuccess()
                .asVoid();*/

// later we authenticate
        com.goebl.david.Response<JSONObject> response = webb
                .post("http://aquabiller.taxo-igr.com/webS/request.php/users/authenticate")
                .param("authorization_id","mimi@techvibesltd.com35842906665650435842906665650")
                .param("authorization_code", "9915603a567f20556b752ff4ad0af28abf93c9de94994f0b6371531916b5db3dd0e8bf74012196024bbd46c151a405fa")
                .param("service_id","234001")
                .ensureSuccess()
                .asJsonObject();

        JSONObject apiResult = response.getBody();
        if(apiResult != null)
        {
            Log.e("WEBB RESULT","response; " + apiResult);
        }
        else
        {
            Log.e("WEBB RESULT","Nothing was gotten after WEBB ");
        }
    }

    public void testPostForSync2()
    {
        Log.e("HttpClient RESULT","Want to test Webservice with HttpClient");
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://aquabiller.taxo-igr.com/webS/request.php/users/authenticate");

        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
            nameValuePairs.add(new BasicNameValuePair("authorization_id", "mimi@techvibesltd.com35842906665650435842906665650"));
            nameValuePairs.add(new BasicNameValuePair("authorization_code", "9915603a567f20556b752ff4ad0af28abf93c9de94994f0b6371531916b5db3dd0e8bf74012196024bbd46c151a405fa"));
            nameValuePairs.add(new BasicNameValuePair("service_id", "234001"));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            if(response != null)
            {
                Log.e("HttpClient RESULT","response; " + response);
            }
            else{
                Log.e("HttpClient RESULT","no response; ");
            }

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            Log.e("WEB Error", "Error occured at HttpClient; " + e.toString());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.e("WEB Error", "Error occured at HttpClient; " + e.toString());
        }
    }

}
