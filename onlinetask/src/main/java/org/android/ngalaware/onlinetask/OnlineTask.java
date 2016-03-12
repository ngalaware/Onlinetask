package org.android.ngalaware.onlinetask;

import android.app.Activity;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by user on 11/03/2016.
 */
public class OnlineTask {
    private Activity activity;
    private String url;
    private String requestType;

    private boolean isNeedPostEncodeValue = false;
    private HashMap postEncodeValue;
    private int postEncodeType;

    private boolean isDebuggingModeActive = false;

    public OnlineTask ()
    {

    }
    public OnlineTask (Activity activity, String url, String requestType,boolean isNeedPostEncodeValue, HashMap postEncodeValue, int postEncodeType, boolean isDebuggingModeActive)
    {
        this.activity = activity;
        this.url = url;
        this.requestType= requestType;
        this.isNeedPostEncodeValue = isNeedPostEncodeValue;
        this.postEncodeValue = postEncodeValue;
        this.postEncodeType = postEncodeType;
        this.isDebuggingModeActive = isDebuggingModeActive;
    }

    public void makeHttpRequest (OnlineTaskCallback callback)
    {
        try {
            new HttpRequest(this.activity, callback,this.url,this.requestType, isNeedPostEncodeValue, this.postEncodeValue, this.postEncodeType, this.isDebuggingModeActive).execute("").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

}
