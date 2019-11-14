package com.live.dreamiptv.v2.Vod;

/**
 * Created by Husnain Iqbal on 6/18/2017.
 */

public class VodUtils {

    private static boolean mUserOnLargeScreen;
    private static String previousUrlStreaming = "";

    public static boolean isUserOnLargeScreen() {
        return mUserOnLargeScreen;
    }

    public static void setUserOnLargeScreen(boolean userOnLargeScreen) {
        mUserOnLargeScreen = userOnLargeScreen;
    }

    public static String getPreviousUrlStreaming() {
        return previousUrlStreaming;
    }

    public static void setPreviousUrlStreaming(String previousUrl) {
        previousUrlStreaming = previousUrl;
    }
}
