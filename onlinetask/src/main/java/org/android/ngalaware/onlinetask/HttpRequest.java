package org.android.ngalaware.onlinetask;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * This is class used to handle all http request
 * Created by AFin on 11/03/2016.
 */
public class HttpRequest extends AsyncTask<String, Integer, String> {

    //HttpRequest attribute
    private String url = "";
    private String requestType = "GET"; // Currently support GET and POST only. Default value will be "GET"
    private Activity activity;
    private int timeout = 15000; // timeout

    private boolean isNeedPostEncodeValue = false;
    private HashMap postEncodeValue;
    private int postEncodeType;
    private boolean isDebugModeActive;
    private String params = "";
    //Callback Handler
    private OnTaskCompleted listener;


    /**
     * Default Constructor of HTTP Request
     *
     * @param activity activity
     * @param listener OnlineTaskCallbacl
     * @param url URL address
     * @param requestType Request type "GET or POST"
     * @param isNeedPostEncodeValue Set TRUE if you need post encode value to url ex : username and password or something else
     * @param postEncodeValue Fill the value of Post Encode Value
     * @param postEncodeType 1. JSON Type (0). Http URL encoded (1)
     */
    public HttpRequest (Activity activity, OnTaskCompleted listener, String url, String requestType, boolean isNeedPostEncodeValue, HashMap postEncodeValue, int postEncodeType, boolean isDebugModeActive)
    {
        this.activity = activity;
        this.listener = listener;
        this.url      = url;
        this.requestType = requestType;
        this.isNeedPostEncodeValue = isNeedPostEncodeValue;
        this.postEncodeValue = postEncodeValue;
        this.postEncodeType = postEncodeType;
        this.isDebugModeActive = isDebugModeActive;
        OnlineTaskUtilities.getInstance().SetDebugMode(isDebugModeActive);
    }

    /**
     * This method will be grant all certificates from url
     * Please beware to accept all it.
     */
    @Override
    protected void onPreExecute ()
    {
        super.onPreExecute();

        // Accept all certificates from servers
        // Dummy trust manager that trusts all certificates
        TrustManager localTrustmanager = new X509TrustManager() {

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }
        };

        // Create SSLContext and set the socket factory as default
        try
        {
            SSLContext sslc = SSLContext.getInstance("TLS");
            sslc.init(null, new TrustManager[] { localTrustmanager },
                    new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslc
                    .getSocketFactory());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

    }

    /**
     * Modify timeout for each request
     *
     * Default value setted as 15 seconds or 15000 milliseconds
     * @param milliseconds Set timeout for request
     */
    public void SetTimeout (int milliseconds)
    {
        this.timeout = milliseconds;
    }

    /**
     * This is main function to handle HttpRequest
     *
     * @param strings string as params
     * @return string result for the result
     */
    @Override
    protected String doInBackground(String... strings) {
        String res = "";
        if (OnlineTaskUtilities.getInstance().GetDebugMode()) {
            Log.d(OnlineTaskUtilities.getInstance().DEBUG_TAGS, "Http Request - doInBackground process");
        }

        if (this.requestType.equals("GET") || this.requestType.equals("POST"))
        {
            res = startRequest(this.url, this.requestType);
        }
        else {
            if (OnlineTaskUtilities.getInstance().GetDebugMode())
            {
                Log.e(OnlineTaskUtilities.getInstance().ERROR_TAGS,"Unsupport request, currently only support GET or POST !");
            }
            res = "Unsupport request, currently only support GET or POST!";
        }
        return res;
    }

    /**
     * This method will be use to process after async task
     *
     * @param result result String after do in background process
     */
    @Override
    protected void onPostExecute(String result)
    {
        super.onPostExecute(result);
        // Setting up callback
        listener.OnTaskCompleted(result);
    }

    /**
     * Main method inside HttpRequest
     *
     * @param url url address
     * @param requestType Get or Post request
     * @return will return string after process
     */
    private String startRequest (String url, String requestType)
    {
        if (OnlineTaskUtilities.getInstance().GetDebugMode()) {
            Log.d(OnlineTaskUtilities.getInstance().DEBUG_TAGS, "Http Request - start request with type :" + this.requestType);
            Log.e(OnlineTaskUtilities.getInstance().DEBUG_TAGS, "Start Request to URL : " + url);
        }

        String response = "";
        URL validURL;
        try
        {
            validURL = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) validURL.openConnection();

            conn.setReadTimeout(this.timeout);
            conn.setConnectTimeout(this.timeout);

            if (requestType.toString().equals("POST")) {
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
            }
            else if (requestType.toString().equals("GET")) {
                conn.setRequestMethod("GET");
            }

            if (this.isNeedPostEncodeValue)
            {
                String [] keys = new String [postEncodeValue.size()];
                postEncodeValue.keySet().toArray(keys);

                if (OnlineTaskUtilities.getInstance().GetDebugMode()) {
                    for (int i = 0; i < keys.length; i++) {
                        Log.d (OnlineTaskUtilities.getInstance().DEBUG_TAGS, "keys["+i+"]"+keys[i]);
                    }
                }
                switch (this.postEncodeType) {
                    case 0 :
                    {
                        params = "{";
                        for (int i = 0; i < postEncodeValue.size(); i++)
                        {
                            if (i != 0)
                            {
                                params += ",";
                            }
                            params += "\"" + keys[i].toString() + "\": \""+ postEncodeValue.get(keys[i].toString()).toString() +"\"" ;
                        }
                        params += "}";

                        if (OnlineTaskUtilities.getInstance().GetDebugMode())
                        {
                            Log.e(OnlineTaskUtilities.getInstance().DEBUG_TAGS, "params : "+ params );
                        }
                        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    }
                    break;
                    case 1:
                    {
                        params = "";
                        for (int i = 0; i < postEncodeValue.size(); i++)
                        {
                            if (i != 0)
                            {
                                params += "&";
                            }
                            params += "" + keys[i].toString() + "="+ postEncodeValue.get(keys[i].toString()).toString()+"" ;
                        }
                        params += "";

                        if (OnlineTaskUtilities.getInstance().GetDebugMode())
                        {
                            Log.e(OnlineTaskUtilities.getInstance().DEBUG_TAGS, "params : "+ params );
                        }
                        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                        conn.setRequestProperty("Cache-Control", "no-cache");
                    }
                    break;
                    default:
                    {
                        if (OnlineTaskUtilities.getInstance().GetDebugMode())
                        {
                            Log.e(OnlineTaskUtilities.getInstance().ERROR_TAGS, "Unsupport post encode type, please try using JSON type (0) or URL Encode value (1)");
                        }
                    }
                    break;
                }

                OutputStream os = conn.getOutputStream();
                os.write(params.getBytes());
                os.flush();
            }

            int responseCode = -1;

            try {
                responseCode=conn.getResponseCode();
            }catch (Exception e)
            {
                if (OnlineTaskUtilities.getInstance().GetDebugMode())
                {
                    Log.e(OnlineTaskUtilities.getInstance().ERROR_TAGS, "Something goes wrong on HttpRequest - startRequest, try to check your internet connection !");
                }
                e.printStackTrace();
            }

            if (responseCode == HttpsURLConnection.HTTP_OK || responseCode == HttpsURLConnection.HTTP_CREATED)
            {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
            else {
                response = "{ \"error\": \"" + responseCode+ "-" + conn.getResponseMessage() +"\"}";
            }

            conn.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
            if (OnlineTaskUtilities.getInstance().GetDebugMode()) {
                Log.e(OnlineTaskUtilities.getInstance().ERROR_TAGS, "There's something while making Http request");
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (OnlineTaskUtilities.getInstance().GetDebugMode()) {
                Log.e(OnlineTaskUtilities.getInstance().ERROR_TAGS, "There's something while making Http request");
            }
        }
        return response;
    }
}
