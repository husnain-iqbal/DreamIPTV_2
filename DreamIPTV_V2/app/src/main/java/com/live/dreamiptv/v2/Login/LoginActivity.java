package com.live.dreamiptv.v2.Login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.live.dreamiptv.v2.Home.HomeActivity;
import com.live.dreamiptv.v2.R;
import com.live.dreamiptv.v2.StreamInfo;
import com.live.dreamiptv.v2.Utilities;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class LoginActivity extends Activity {
    private Context mContext;
    private EditText mUsernameEt;
    private EditText mPasswordEt;
    private CheckBox mSaveLoginCheckBox;
    private HashMap<String, ArrayList<StreamInfo>> mLiveChannelsInfoRecord;
    private HashMap<String, ArrayList<StreamInfo>> mVideosInfoRecord;
    private static final String LOGIN_PREFS = "LoginDataPrefs";
    public static final String LOGIN_ACTIVITY_CHANNEL_RECORD_TEXT = "LoginActivityChannelRecord";
    public static final String LOGIN_ACTIVITY_VIDEO_RECORD_TEXT = "LoginActivityVideoRecord";
    public static final String LOGIN_ACTIVITY_BUNDLE_TEXT = "LoginActivityBundle";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = LoginActivity.this;
        mLiveChannelsInfoRecord = new HashMap<>();
        mVideosInfoRecord = new HashMap<>();
        SharedPreferences prefs = getSharedPreferences(LOGIN_PREFS, MODE_PRIVATE);
        Boolean isLoginAlreadySaved = prefs.getBoolean(Utilities.REMEMBER_TEXT, false);
        if (isLoginAlreadySaved) {
            String username = prefs.getString(Utilities.USERNAME_TEXT, "default username");
            String password = prefs.getString(Utilities.PASSWORD_TEXT, "default password");
            String filePath = getDataFilePath(username, password);
            Log.i("@LoginActivity", filePath);
            new DataFileLoaderAsyncTask().execute(filePath);
        } else {
            setContentView(R.layout.activity_login);
            mUsernameEt = (EditText) findViewById(R.id.userName);
            mPasswordEt = (EditText) findViewById(R.id.userPass);
            mSaveLoginCheckBox = (CheckBox) findViewById(R.id.checkConfirm);
            Button loginBtn = (Button) findViewById(R.id.buttonLogin);
            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    String username = mUsernameEt.getText().toString().trim();
                    String password = mPasswordEt.getText().toString().trim();
                    if (username.isEmpty()) {
                        Utilities.showShortToast(mContext, "Username is Empty.");
                    } else if (password.isEmpty()) {
                        Utilities.showShortToast(mContext, "Password is Empty.");
                    } else {
                        String filePath = getDataFilePath(username, password);
                        Log.i("@LoginActivity", filePath);
                        new DataFileLoaderAsyncTask().execute(filePath);
                    }
                }
            });
        }
    }

    private String getDataFilePath(String username, String password) {
        return Utilities.URL_M3U + Utilities.USERNAME_TEXT + username + "&" + Utilities.PASSWORD_TEXT + password;
    }

    private void saveCredentials2Prefs() {
        SharedPreferences prefs = getSharedPreferences(LOGIN_PREFS, MODE_PRIVATE);
        Boolean isLoginAlreadySaved = prefs.getBoolean(Utilities.REMEMBER_TEXT, false);
        if (!isLoginAlreadySaved) {
            if (mSaveLoginCheckBox.isChecked()) {
                prefs.edit().putString(Utilities.USERNAME_TEXT, mUsernameEt.getText().toString().trim()).apply();
                prefs.edit().putString(Utilities.PASSWORD_TEXT, mPasswordEt.getText().toString().trim()).apply();
                prefs.edit().putBoolean(Utilities.REMEMBER_TEXT, true).apply();
            }
        }
    }

    private void openHomeActivityAndPassBundle() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(LOGIN_ACTIVITY_CHANNEL_RECORD_TEXT, mLiveChannelsInfoRecord);
        bundle.putSerializable(LOGIN_ACTIVITY_VIDEO_RECORD_TEXT, mVideosInfoRecord);
        startActivity(new Intent(LoginActivity.this, HomeActivity.class).putExtra(LOGIN_ACTIVITY_BUNDLE_TEXT, bundle));
        finish();
    }

//    private void readFileFromServer(final String url) {
//        AsyncTask.execute(new Runnable() {
//            @Override
//            public void run() {
//                InputStream is;
//                try {
//                    DefaultHttpClient httpClient = new DefaultHttpClient();
//                    HttpGet httpGet = new HttpGet(url);
//                    HttpResponse httpResponse = httpClient.execute(httpGet);
//                    HttpEntity httpEntity = httpResponse.getEntity();
//                    is = httpEntity.getContent();
//
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
//                    int counter = 0;
//                    String categoryName = "";
//                    String channelName = "";
//                    String channelLink;
//                    String line;
//                    if (reader.readLine().equalsIgnoreCase("error:Username or Password does not match")) {
//                        return;
//                    }
//                    while ((line = reader.readLine()) != null) {
//                        ArrayList<StreamInfo> channelInfoList = new ArrayList<>();
//                        counter++;
//                        if (counter != 1) {
//                            if (counter % 2 == 0) {
//                                line = line.replace("#EXTINF:-1 group-title=", "");
//                                line = line.replace("\"", "");
//                                String[] lineArray = line.split(",");
//                                categoryName = lineArray[0].trim();
//                                channelName = lineArray[1].trim();
//                            } else if (counter % 2 == 1) {
//                                channelLink = line.trim();
//                                String streamType = "";
//                                if (channelLink.contains(".m3u8")) {
//                                    if (mLiveChannelsInfoRecord.containsKey(categoryName)) {
//                                        channelInfoList = mLiveChannelsInfoRecord.get(categoryName);
//                                        channelInfoList.add(new StreamInfo(categoryName, channelName, channelLink, streamType));
//                                        mLiveChannelsInfoRecord.put(categoryName, channelInfoList);
//                                    } else {
//                                        channelInfoList.add(new StreamInfo(categoryName, channelName, channelLink, streamType));
//                                        mLiveChannelsInfoRecord.put(categoryName, channelInfoList);
//                                    }
//                                } else {
//                                    if (mVideosInfoRecord.containsKey(categoryName)) {
//                                        channelInfoList = mVideosInfoRecord.get(categoryName);
//                                        channelInfoList.add(new StreamInfo(categoryName, channelName, channelLink, streamType));
//                                        mVideosInfoRecord.put(categoryName, channelInfoList);
//                                    } else {
//                                        channelInfoList.add(new StreamInfo(categoryName, channelName, channelLink, streamType));
//                                        mVideosInfoRecord.put(categoryName, channelInfoList);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                    reader.close();
//                    is.close();
//                    openHomeActivityInUiThreadAndPassBundle();
//                } catch (Exception e) {
//                    Log.e("Buffer Error", "Error converting result " + e.toString());
//                }
//            }
//        });
//    }

    private class DataFileLoaderAsyncTask extends AsyncTask<String, Void, Boolean> {
        private String errorMessage;

        @Override
        protected Boolean doInBackground(String... params) {
            InputStream inputStream;
            try {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(params[0]);
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                inputStream = httpEntity.getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"), 8);
                int counter = 0;
                String categoryName = "";
                String channelName = "";
                String channelLink;
                String line;
                if (reader.readLine().equalsIgnoreCase("error:Username or Password does not match")) {
                    errorMessage = "error:Username or Password does not match";
                    return false;
                }
                while ((line = reader.readLine()) != null) {
                    ArrayList<StreamInfo> channelInfoList = new ArrayList<>();
                    counter++;
                    if (counter % 2 == 1) {
                        line = line.replace("#EXTINF:-1 group-title=", "");
                        line = line.replace("\"", "");
                        String[] lineArray = line.split(",");
                        categoryName = lineArray[0].trim();
                        channelName = lineArray[1].trim();
                    } else if (counter % 2 == 0) {
                        channelLink = line.trim();
                        String streamType = "";
                        if (channelLink.contains(".m3u8")) {
                            if (mLiveChannelsInfoRecord.containsKey(categoryName)) {
                                channelInfoList = mLiveChannelsInfoRecord.get(categoryName);
                                channelInfoList.add(new StreamInfo(categoryName, channelName, channelLink, streamType));
                                mLiveChannelsInfoRecord.put(categoryName, channelInfoList);
                            } else {
                                channelInfoList.add(new StreamInfo(categoryName, channelName, channelLink, streamType));
                                mLiveChannelsInfoRecord.put(categoryName, channelInfoList);
                            }
                        } else {
                            if (mVideosInfoRecord.containsKey(categoryName)) {
                                channelInfoList = mVideosInfoRecord.get(categoryName);
                                channelInfoList.add(new StreamInfo(categoryName, channelName, channelLink, streamType));
                                mVideosInfoRecord.put(categoryName, channelInfoList);
                            } else {
                                channelInfoList.add(new StreamInfo(categoryName, channelName, channelLink, streamType));
                                mVideosInfoRecord.put(categoryName, channelInfoList);
                            }
                        }
                    }
                }
                reader.close();
                inputStream.close();
            } catch (Exception e) {
                errorMessage = e.toString();
                Log.e("Buffer Error", "Error converting result " + errorMessage);
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean isDataFileLoaded) {
            if (isDataFileLoaded) {
                saveCredentials2Prefs();
                openHomeActivityAndPassBundle();
            } else {
                if (errorMessage == null) {
                    errorMessage = "Error Occurred";
                }
                Utilities.showShortToast(mContext, errorMessage);
            }
        }
    }

//    private void validateUser(final String userName, final String password) {
//        HashMap<String, String> params = new HashMap<String, String>();
//        params.put("user", userName);
//        params.put("pass", password);
//        GeneralStringRequest request = new GeneralStringRequest(Settings.URL_VALIDATE_USER, params, new Response.Listener<String>() {
//
//            @Override
//            public void onResponse(String response) {
//                closeProgressDialog();
//                if (!isClosed) {
//                    if (success) {
//                        showProgressDialog("Downloading Data");
//                        downloadFile(usernameField.getText().toString(), passwordField.getText().toString());
//                        if (rememberBox.getVisibility() == View.VISIBLE) {
//                            Appdata.saveUser(MainActivity.this, userName, password);
//                        }
//                    } else {
//                        Toast.makeText(MainActivity.this, "Please Provide correct username and password.", Toast.LENGTH_LONG).show();
//                    }
//                }
//            }
//        }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(MainActivity.this, "Some problem with backend server! Please try again.", Toast.LENGTH_LONG).show();
//
//            }
//        }, new Response.ParseListener<String>() {
//
//            @Override
//            public void onParse(String response) throws VolleyError {
//                try {
//                    if (JSONHelper.checkStatus(response).equalsIgnoreCase("success")) {
//                        success = true;
//                    } else {
//                        success = false;
//                    }
//                } catch (Exception e) {
//                    throw new VolleyError(e);
//                }
//
//            }
//        });
//
//
//        Appdata.getRequestQueue(this.getApplicationContext()).add(request);
//    }

    public static HashMap<String, ArrayList<StreamInfo>> getBundledChannelInfo(Activity activity) {
        HashMap<String, ArrayList<StreamInfo>> channelFileInfoRecord = new HashMap<>();
        Intent intent = activity.getIntent();
        if (intent.hasExtra(LOGIN_ACTIVITY_BUNDLE_TEXT)) {
            Bundle bundle = intent.getBundleExtra(LOGIN_ACTIVITY_BUNDLE_TEXT);
            channelFileInfoRecord = (HashMap<String, ArrayList<StreamInfo>>) bundle.getSerializable(LOGIN_ACTIVITY_CHANNEL_RECORD_TEXT);
        }
        return channelFileInfoRecord;
    }

    public static HashMap<String, ArrayList<StreamInfo>> getBundledVideoInfo(Activity activity) {
        HashMap<String, ArrayList<StreamInfo>> channelFileInfoRecord = new HashMap<>();
        Intent intent = activity.getIntent();
        if (intent.hasExtra(LOGIN_ACTIVITY_BUNDLE_TEXT)) {
            Bundle bundle = intent.getBundleExtra(LOGIN_ACTIVITY_BUNDLE_TEXT);
            channelFileInfoRecord = (HashMap<String, ArrayList<StreamInfo>>) bundle.getSerializable(LOGIN_ACTIVITY_VIDEO_RECORD_TEXT);
        }
        return channelFileInfoRecord;
    }
}
