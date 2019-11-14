package com.live.dreamiptv.v2;

import java.io.Serializable;

/**
 * Created by Husnain Iqnal on 08-Jun-17.
 */
public class StreamInfo implements Serializable {

    private String mCategoryName;
    private String mStreamName;
    private String mStreamLink;
    private String mStreamType; // Channel, Video

    public StreamInfo(String categoryName, String streamName, String link, String streamType) {
        mCategoryName = categoryName;
        mStreamName = streamName;
        mStreamLink = link;
        mStreamType = streamType;
    }

    public String getCategoryName() {
        return mCategoryName;
    }

    public String getStreamName() {
        return mStreamName;
    }

    public String getStreamLink() {
        return mStreamLink;
    }

    public String getStreamType() {
        return mStreamType;
    }
}
