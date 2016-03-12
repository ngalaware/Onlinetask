package android.com.asychronustaskexample;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.android.ngalaware.onlinetask.OnlineTask;
import org.android.ngalaware.onlinetask.OnlineTaskCallback;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Activity activity = this;
        String url = "your_url";
        String reqType = "POST";
        boolean needPostEncodeValue = false; // Optional
        String username = "username"; // can be void when needPostEncodeValue is false
        String password = "password"; // can be void when needPostEncodeValue is false
        // can be void when needPostEncodeValue is false
        HashMap reqProperties = new HashMap();
        reqProperties.put("grant_type", "password");
        reqProperties.put("username", username);
        reqProperties.put("password", password);

        // can be void when needPostEncodeValue is false
        int postEncodeType = 1;

        new OnlineTask(activity, url, reqType, needPostEncodeValue, reqProperties, 1, true).makeHttpRequest(new OnlineTaskCallback(){
            @Override
            public void OnTaskCompleted(String result)
            {
                Log.d("TAGS", result);
            }
        });
    }
}
