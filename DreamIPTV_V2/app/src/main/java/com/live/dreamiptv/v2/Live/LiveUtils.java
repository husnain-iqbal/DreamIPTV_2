package com.live.dreamiptv.v2.Live;

import com.live.dreamiptv.v2.Utilities;

/**
 * Created by Husnain Iqbal on 6/18/2017.
 */

public class LiveUtils {

    private static String previousUrlStreaming = "";
    private static boolean mIsUserPresentOnLargeScreen;


    public static String getPreviousUrlStreaming() {
        return previousUrlStreaming;
    }

    public static void setPreviousUrlStreaming(String previousUrl) {
        previousUrlStreaming = previousUrl;
    }

    public static boolean isUserPresentOnLargeScreen() {
        return mIsUserPresentOnLargeScreen;
    }

    public static void setUserPresentOnLargeScreen(boolean presentOnLargeScreen) {
        mIsUserPresentOnLargeScreen = presentOnLargeScreen;
    }
}
