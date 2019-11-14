package com.live.dreamiptv.v2.Live.ChannelCategoryList;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.live.dreamiptv.v2.Live.ChannelList.ChannelDetailFragment;
import com.live.dreamiptv.v2.Live.LiveMainActivity;
import com.live.dreamiptv.v2.R;
import com.live.dreamiptv.v2.StreamInfo;

import java.util.ArrayList;
import java.util.HashMap;


public class LiveCategoryDetailFragment extends Fragment implements LiveCategoryBaseAdapter.SendData {
    private Activity mActivity;
    private ListView mListView;
    private LiveCategoryBaseAdapter mCategoryAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.live_category_detail_fragment, container, false);
        mListView = (ListView) view.findViewById(R.id.list_live_categories);
        HashMap<String, ArrayList<StreamInfo>> liveChannelsRecord = (HashMap<String, ArrayList<StreamInfo>>) getArguments().getSerializable(LiveMainActivity.LIVE_MAIN_ACTIVITY_RECORD_TEXT);
        mCategoryAdapter = new LiveCategoryBaseAdapter(this, mActivity, liveChannelsRecord);
        mListView.setItemsCanFocus(true);
        mListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCategoryAdapter.setSelectedCategoryIndex(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mListView.setAdapter(mCategoryAdapter);
        return view;
    }

    private void selectListViewFirstItem() {
        mListView.setSelection(0);
    }

    private void selectListViewLastItem(int position) {
        mListView.setSelection(position);
    }

    @Override
    public void sendChannelList(ArrayList<StreamInfo> channelList) {
        ChannelDetailFragment fragment = (ChannelDetailFragment) getFragmentManager().findFragmentById(R.id.fragment_two_live);
        if (fragment != null) {
            fragment.populateListView(channelList);
        }
    }

    @Override
    public void sendKeyRotate2ChannelListFirstItemEvent() {
        ChannelDetailFragment fragment = (ChannelDetailFragment) getFragmentManager().findFragmentById(R.id.fragment_two_live);
        if (fragment != null) {
            fragment.selectListViewFirstItem();
        }
    }

    @Override
    public void sendKeyRotate2CategoryListFirstItemEvent() {
        selectListViewFirstItem();
    }

    @Override
    public void sendKeyRotate2CategoryListLastItemEvent(int lastItemPosition) {
        selectListViewLastItem(lastItemPosition);
    }
}
