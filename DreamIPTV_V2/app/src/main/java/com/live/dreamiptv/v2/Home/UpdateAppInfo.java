package com.live.dreamiptv.v2.Home;

/**
 * Created by Husnain Iqnal on 16-May-17.
 */
public class UpdateAppInfo {
    private String mAppUrl;
    private int mAppVersionCode;

    public String getAppUrl() {
        return mAppUrl;
    }

    public void setAppUrl(String url) {
        mAppUrl = url;
    }

    public int getAppVersionCode() {
        return mAppVersionCode;
    }

    public void setAppVersionCode(int versionCode) {
        mAppVersionCode = versionCode;
    }

    public UpdateAppInfo() {
    }

    public UpdateAppInfo(int versionCode, String appUrl) {
        mAppVersionCode = versionCode;
        mAppUrl = appUrl;
    }
}
