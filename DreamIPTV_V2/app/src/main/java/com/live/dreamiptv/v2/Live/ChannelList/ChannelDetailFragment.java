package com.live.dreamiptv.v2.Live.ChannelList;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.live.dreamiptv.v2.Live.ChannelLive.ChannelLiveFragment;
import com.live.dreamiptv.v2.R;
import com.live.dreamiptv.v2.StreamInfo;

import java.util.ArrayList;

/**
 * Edited by hp on 12/06/2017.
 */

public class ChannelDetailFragment extends Fragment implements ChannelDetailBaseAdapter.SendData {
    private Activity mActivity;
    private ListView mListView;

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

    public void populateListView(ArrayList<StreamInfo> channelList) {
        ChannelDetailBaseAdapter mChannelAdapter = new ChannelDetailBaseAdapter(this, mActivity, channelList);
        mListView.setAdapter(mChannelAdapter);
    }

    public void selectListViewFirstItem() {
        mListView.setSelection(0);
    }

    private void selectListViewLastItem(int position) {
        mListView.setSelection(position);
    }

    @Override
    public void sendStream(String channelLink) {
        ChannelLiveFragment fragment = (ChannelLiveFragment) getFragmentManager().findFragmentById(R.id.fragment_three_live);
        if (fragment != null) {
            fragment.getChannelLinkAndPlayChannel(channelLink);
        }
    }

    @Override
    public void sendKeyFullScreenBackEvent() {
        ChannelLiveFragment fragment = (ChannelLiveFragment) getFragmentManager().findFragmentById(R.id.fragment_three_live);
        if (fragment != null) {
            fragment.compressVideoView();
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
