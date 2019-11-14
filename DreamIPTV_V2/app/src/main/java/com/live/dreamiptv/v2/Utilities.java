package com.live.dreamiptv.v2;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * Created by hp on 4/20/2017.
 */

public class Utilities {

    public static final String FRAGMENT_ONE_TAG = "1";
    public static final String FRAGMENT_TWO_TAG = "2";
    public static final String FRAGMENT_THREE_TAG = "3";
    public static final int DEVICE_SDK_VERSION = android.os.Build.VERSION.SDK_INT;
    public static final int LOLLIPOP_SDK_VERSION = Build.VERSION_CODES.LOLLIPOP;
    public static final int JELLY_BEAN_SDK_VERSION = android.os.Build.VERSION_CODES.JELLY_BEAN;

    public static final String STORE_TIME_PREF_TEXT = "storeTimePref";
    public static final String STORE_TIME_TEXT = "storeTime";

    public static final String USERNAME_TEXT = "username=";
    public static final String PASSWORD_TEXT = "password=";
    public static final String REMEMBER_TEXT = "rememberUser";
    public static final String URL_M3U = "http://smartiptv.dyndns.org/newplistcms/downloaduser?";

    public static final String MP4_TEST_URL = "http://www.ebookfrenzy.com/android_book/movie.mp4";
    public static final String MKV_TEST_URL = "http://go-dl.eve-files.com/media/0906/test.mkv";

    public static void showShortToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    @SuppressWarnings("NewApi, deprecation")
    public static void setListItemBackgroundBlue(Activity activity, View view) {
        if (Utilities.DEVICE_SDK_VERSION < Utilities.JELLY_BEAN_SDK_VERSION) {
            view.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.list_cell_bg_blue));
        } else {
            view.setBackground(activity.getResources().getDrawable(R.drawable.list_cell_bg_blue));
        }
    }

    @SuppressWarnings("NewApi, deprecation")
    public static void setListItemBackgroundGray(Activity activity, View view) {
        if (Utilities.DEVICE_SDK_VERSION < Utilities.JELLY_BEAN_SDK_VERSION) {
            view.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.list_cell_bg_gray));
        } else {
            view.setBackground(activity.getResources().getDrawable(R.drawable.list_cell_bg_gray));
        }
    }


    public static ArrayList<String> getCategoryListFromVideoOrChannelsRecord(HashMap<String, ArrayList<StreamInfo>> record) {
        ArrayList<String> categoryList = new ArrayList<>();
        Iterator it = record.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            categoryList.add(pair.getKey().toString());
//            it.remove();
        }
        return categoryList;
    }

    public static int getVersionCode(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException ex) {
            Log.v("@Utils-VersionCode", ex.getMessage());
        }
        return 0;
    }

    public static String getVersionName(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException ex) {
            Log.v("@Utils-VersionName", ex.getMessage());
        }
        return null;
    }

    public static String getCurrentTime() {
        DateFormat dateTimeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        return dateTimeFormat.format(cal.getTime());
    }

    public static long getTimeDifference(String time1, String time2) {
        try {
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
            Date date1 = dateTimeFormat.parse(time1);
            Date date2 = dateTimeFormat.parse(time2);
            long difference = date2.getTime() - date1.getTime();
            if (difference < 0) {
                difference *= -1;
            }
            return difference;
        } catch (Exception ex) {
            Log.e("@TimeParseException", ex.getMessage());
            return -1;
        }
    }

    public static boolean isWeekPassed(long timeDifference) {
        long weekTimeMs = 7 * 24 * 60 * 60 * 1000;
//        long weekTimeMs = 1000;
        return timeDifference / weekTimeMs >= 1;
    }

//    public static boolean isDeviceConnectedToInternet(Context mContext) {
//        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Network[] networks = connectivityManager.getAllNetworks();
//            NetworkInfo networkInfo;
//            for (Network mNetwork : networks) {
//                networkInfo = connectivityManager.getNetworkInfo(mNetwork);
//                return networkInfo.getState().equals(NetworkInfo.State.CONNECTED);
//            }
//        } else {
//            if (connectivityManager != null) {
//                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
//                if (info != null) {
//                    for (NetworkInfo anInfo : info) {
//                        return anInfo.getState() == NetworkInfo.State.CONNECTED;
//                    }
//                }
//            }
//        }
//        return false;
//    }
//
//    public static boolean isDeviceOnline(Context context) {
//        ConnectivityManager connectivityManager = (ConnectivityManager)
//                context.getSystemService(Context.CONNECTIVITY_SERVICE);
//
//        if (connectivityManager.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED ||
//                connectivityManager.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) {
//            return true;
//        } else if (connectivityManager.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED
//                || connectivityManager.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED) {
//            return false;
//        }
//
//        return false;
//    }
}
