package com.live.dreamiptv.v2.Home;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.live.dreamiptv.v2.Network.AppController;

import org.json.JSONException;
import org.json.JSONObject;

public class AppUpdateService extends IntentService {
    private static final String LOG_TAG = "@AppUpdateService";
    public static final String REQUEST_STRING = "myRequest";
    public static final String RESPONSE_VERSION_CODE = "version_code";
    public static final String RESPONSE_NEW_APP_URL = "new_app_url";

    public AppUpdateService() {
        super("AppUpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String requestString = intent.getStringExtra(REQUEST_STRING);
        Log.v(LOG_TAG, requestString);
        loadLatestAppInfoAndStartReceiver(requestString);
    }

    private void loadLatestAppInfoAndStartReceiver(String url) {
        JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                UpdateAppInfo newAppInfo = new UpdateAppInfo();
                try {
                    String newAppUrl = response.getString("url");
                    int newAppVersionCode = Integer.parseInt(response.getString("version"));
                    newAppInfo.setAppUrl(newAppUrl);
                    newAppInfo.setAppVersionCode(newAppVersionCode);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(HomeActivity.MyWebReceiver.PROCESS_RESPONSE);
                broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                broadcastIntent.putExtra(RESPONSE_VERSION_CODE, newAppInfo.getAppVersionCode() + "");
                broadcastIntent.putExtra(RESPONSE_NEW_APP_URL, newAppInfo.getAppUrl());
                sendBroadcast(broadcastIntent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("@MyWebService", error.getMessage());
            }
        });
        AppController.getInstances().addToRequestQueue(jsonObject);
    }
}

