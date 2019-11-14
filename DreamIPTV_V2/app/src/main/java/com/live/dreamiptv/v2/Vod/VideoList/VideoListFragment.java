package com.live.dreamiptv.v2.Vod.VideoList;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.live.dreamiptv.v2.R;
import com.live.dreamiptv.v2.StreamInfo;
import com.live.dreamiptv.v2.Vod.VideoPlay.VideoPlayFragment;

import java.util.ArrayList;

public class VideoListFragment extends Fragment implements VideoListBaseAdapter.SendLink {
    private Activity mActivity;
    private ListView mListView;
    private VideoListBaseAdapter mVideosAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.channel_video_combine_fragment, container, false);
        mListView = (ListView) view.findViewById(R.id.listViewChannelVideo);
        mListView.setItemsCanFocus(true);
        return view;
    }

    public void populateListView(ArrayList<StreamInfo> videoInfoList) {
        mVideosAdapter = new VideoListBaseAdapter(this, mActivity, videoInfoList);
        mListView.setAdapter(mVideosAdapter);
    }

    public void selectListViewFirstItem() {
        mListView.setSelection(0);
    }

    public void selectListViewLastItem(int position) {
        mListView.setSelection(position);
    }


    public void setFocusOnCurrentItem() {
        View v = mVideosAdapter.getCurrentListItem();
        if (v != null) {
            v.requestFocus();
        }
    }

    @Override
    public void sendStream(String videoLink) {
        VideoPlayFragment fragment = (VideoPlayFragment) getFragmentManager().findFragmentById(R.id.fragment_three_vod);
        if (fragment != null) {
            fragment.attainUrl(videoLink);
        }
    }

    @Override
    public void sendKeyRotate2ChannelListFirstItemEvent() {
        selectListViewFirstItem();
    }

    @Override
    public void sendKeyRotate2ChannelListLastItemEvent(int lastItemPosition) {
        selectListViewLastItem(lastItemPosition);
    }
}
