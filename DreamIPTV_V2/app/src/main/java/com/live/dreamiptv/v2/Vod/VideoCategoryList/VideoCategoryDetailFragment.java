package com.live.dreamiptv.v2.Vod.VideoCategoryList;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.live.dreamiptv.v2.R;
import com.live.dreamiptv.v2.StreamInfo;
import com.live.dreamiptv.v2.Utilities;
import com.live.dreamiptv.v2.Vod.VideoList.VideoListFragment;
import com.live.dreamiptv.v2.Vod.VodMainActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class VideoCategoryDetailFragment extends Fragment implements VideoCategoryBaseAdapter.SendData {
    private Activity mActivity;
    private ListView mListView;
    private VideoCategoryBaseAdapter mCategoryAdapter;
    private HashMap<String, ArrayList<StreamInfo>> mLiveChannelsRecord;

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
        mLiveChannelsRecord = (HashMap<String, ArrayList<StreamInfo>>) getArguments().getSerializable(VodMainActivity.VIDEO_MAIN_ACTIVITY_RECORD_TEXT);
        ArrayList<String> videoCategoryList = Utilities.getCategoryListFromVideoOrChannelsRecord(mLiveChannelsRecord);
        mCategoryAdapter = new VideoCategoryBaseAdapter(this, mActivity, videoCategoryList);
        mListView.setItemsCanFocus(true);
        mListView.setAdapter(mCategoryAdapter);
        mListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCategoryAdapter.setSelectedCategoryIndex(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return view;
    }

    public void selectListViewFirstItem() {
        mListView.setSelection(0);
    }

    public void selectListViewLastItem(int position) {
        mListView.setSelection(position);
    }

    @Override
    public void sendCategoryName(String categoryName) {
        VideoListFragment fragment = (VideoListFragment) getFragmentManager().findFragmentById(R.id.fragment_two_vod);
        if (fragment != null) {
            ArrayList<StreamInfo> videoInfoList = mLiveChannelsRecord.get(categoryName);
            fragment.populateListView(videoInfoList);
        }
    }

    @Override
    public void sendKeyRotate2VideoListFirstItemEvent() {
        VideoListFragment fragment = (VideoListFragment) getFragmentManager().findFragmentById(R.id.fragment_two_vod);
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
