package com.live.dreamiptv.v2.Home;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.live.dreamiptv.v2.Live.LiveMainActivity;
import com.live.dreamiptv.v2.Login.LoginActivity;
import com.live.dreamiptv.v2.R;
import com.live.dreamiptv.v2.StreamInfo;
import com.live.dreamiptv.v2.Utilities;
import com.live.dreamiptv.v2.Vod.VodMainActivity;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class HomeActivity extends Activity {
    private Context mContext;
    private boolean allowUnregister;
    private long mDownloadReference;
    private MyWebReceiver mReceiver;
    private DownloadManager mDownloadManager;
    private static final String LOG_TAG = "AppUpgrade";
    public static final String HOME_ACTIVITY_RECORD_TEXT = "HomeActivityRecord";
    public static final String HOME_ACTIVITY_BUNDLE_TEXT = "HomeActivityBundle";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mContext = HomeActivity.this;
        allowUnregister = false;
        Button liveBtn = (Button) findViewById(R.id.liveBtn);
        liveBtn.setSelected(true);
        liveBtn.requestFocus();
        liveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, ArrayList<StreamInfo>> liveChannelsRecord = LoginActivity.getBundledChannelInfo(HomeActivity.this);
                Bundle bundle = new Bundle();
                bundle.putSerializable(HOME_ACTIVITY_RECORD_TEXT, liveChannelsRecord);
                startActivity(new Intent(mContext, LiveMainActivity.class).putExtra(HOME_ACTIVITY_BUNDLE_TEXT, bundle));
            }
        });
        Button vodBtn = (Button) findViewById(R.id.vodBtn);
        vodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, ArrayList<StreamInfo>> videosRecord = LoginActivity.getBundledVideoInfo(HomeActivity.this);
                Bundle bundle = new Bundle();
                bundle.putSerializable(HOME_ACTIVITY_RECORD_TEXT, videosRecord);
                startActivity(new Intent(mContext, VodMainActivity.class).putExtra(HOME_ACTIVITY_BUNDLE_TEXT, bundle));
            }
        });

//        SharedPreferences prefs = getSharedPreferences(Utilities.STORE_TIME_PREF_TEXT, MODE_PRIVATE);
//        String storedTime = prefs.getString(Utilities.STORE_TIME_TEXT, "ddMMYYYY");
//        String currentTime = Utilities.getCurrentTime();
//        if (storedTime.equals("ddMMYYYY")) {
//            storedTime = "2000/00/00 00:00:00";
//        }
//        if (Utilities.isWeekPassed(Utilities.getTimeDifference(currentTime, storedTime))) {
//            //Broadcast mReceiver for our Web Request
//            IntentFilter filter = new IntentFilter(MyWebReceiver.PROCESS_RESPONSE);
//            filter.addCategory(Intent.CATEGORY_DEFAULT);
//            mReceiver = new MyWebReceiver();
//            registerReceiver(mReceiver, filter);
//
//            //Broadcast mReceiver for the download manager
//            filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
//            registerReceiver(mDownloadReceiver, filter);
//
//            Intent msgIntent = new Intent(this, AppUpdateService.class);
//            msgIntent.putExtra(AppUpdateService.REQUEST_STRING, Utilities.UPDATE_APP_URL);
//            startService(msgIntent);
//            allowUnregister = true;
//        }
    }

    public static HashMap<String, ArrayList<StreamInfo>> getBundledChannelFileInfo(Activity activity) {
        HashMap<String, ArrayList<StreamInfo>> channelFileInfoList = new HashMap<>();
        Intent intent = activity.getIntent();
        if (intent.hasExtra(HOME_ACTIVITY_BUNDLE_TEXT)) {
            Bundle bundle = intent.getBundleExtra(HOME_ACTIVITY_BUNDLE_TEXT);
            channelFileInfoList = (HashMap<String, ArrayList<StreamInfo>>) bundle.getSerializable(HOME_ACTIVITY_RECORD_TEXT);
        }
        return channelFileInfoList;
    }

    @Override
    public void onDestroy() {
        if (allowUnregister) {
            this.unregisterReceiver(mReceiver);
            this.unregisterReceiver(mDownloadReceiver);
            allowUnregister = false;
        }
        super.onDestroy();
    }

    public class MyWebReceiver extends BroadcastReceiver {
        public static final String PROCESS_RESPONSE = "com.as400samplecode.intent.action.PROCESS_RESPONSE";

        @Override
        public void onReceive(Context context, Intent intent) {
            final String versionCode = intent.getStringExtra(AppUpdateService.RESPONSE_VERSION_CODE);
            final String newAppUrl = intent.getStringExtra(AppUpdateService.RESPONSE_NEW_APP_URL);
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    if (Integer.parseInt(versionCode) > Utilities.getVersionCode(mContext) && isFileAvailable(newAppUrl)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                        builder.setMessage("There is a newer version of Dreamiptv available, click OK to upgrade now?")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        mDownloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(newAppUrl));
                                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
                                        request.setAllowedOverRoaming(false);
                                        request.setTitle("My Android App Download");
                                        request.setDestinationInExternalFilesDir(HomeActivity.this, Environment.DIRECTORY_DOWNLOADS, "MyAndroidApp.apk");
                                        mDownloadReference = mDownloadManager.enqueue(request);
                                        dialog.cancel();
                                    }
                                })
                                .setNegativeButton("Remind Me Later", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        SharedPreferences.Editor editor = getSharedPreferences(Utilities.STORE_TIME_PREF_TEXT, MODE_PRIVATE).edit();
                                        editor.putString(Utilities.STORE_TIME_TEXT, Utilities.getCurrentTime());
                                        editor.apply();
                                        dialog.cancel();
                                    }
                                });
                        builder.create().show();
                    } else {
                        HomeActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Utilities.showLongToast(HomeActivity.this, "Application Version Code: " + versionCode);
                            }
                        });
                    }
                }
            });
        }
    }

    private BroadcastReceiver mDownloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (mDownloadReference == referenceId) {
                try {
                    Log.v(LOG_TAG, "Downloading of the new app version complete");
                    Intent installIntent = new Intent(Intent.ACTION_VIEW);
                    installIntent.setDataAndType(mDownloadManager.getUriForDownloadedFile(mDownloadReference),
                            "application/vnd.android.package-archive");
                    installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(installIntent);
                } catch (Exception e) {
                    final String errorMessage = e.getMessage();
                    HomeActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Utilities.showLongToast(HomeActivity.this, errorMessage);
                        }
                    });
                    Log.e("@DownloadAppUpdate", errorMessage);
                }
            }
        }
    };

    private boolean isFileAvailable(String fileUrl) {
        try {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection con = (HttpURLConnection) new URL(fileUrl).openConnection();
            con.setRequestMethod("HEAD");
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
