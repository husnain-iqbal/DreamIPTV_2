package com.live.dreamiptv.v2.Live;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.live.dreamiptv.v2.Home.HomeActivity;
import com.live.dreamiptv.v2.Live.ChannelCategoryList.LiveCategoryDetailFragment;
import com.live.dreamiptv.v2.Live.ChannelList.ChannelDetailFragment;
import com.live.dreamiptv.v2.Live.ChannelLive.ChannelLiveFragment;
import com.live.dreamiptv.v2.R;
import com.live.dreamiptv.v2.StreamInfo;
import com.live.dreamiptv.v2.Utilities;

import java.util.ArrayList;
import java.util.HashMap;

public class LiveMainActivity extends AppCompatActivity {

    public static final String LIVE_MAIN_ACTIVITY_RECORD_TEXT = "LiveMainActivityRecord";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_live_main);
        HashMap<String, ArrayList<StreamInfo>> mLiveChannelsRecord = HomeActivity.getBundledChannelFileInfo(LiveMainActivity.this);
        Bundle bundle = new Bundle();
        bundle.putSerializable(LIVE_MAIN_ACTIVITY_RECORD_TEXT, mLiveChannelsRecord);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        LiveCategoryDetailFragment liveCategoryDetailFragment = new LiveCategoryDetailFragment();
        liveCategoryDetailFragment.setArguments(bundle);
        fragmentTransaction.add(R.id.fragment_one_live, liveCategoryDetailFragment, Utilities.FRAGMENT_ONE_TAG);
        fragmentTransaction.add(R.id.fragment_two_live, new ChannelDetailFragment(), Utilities.FRAGMENT_TWO_TAG);
        fragmentTransaction.add(R.id.fragment_three_live, new ChannelLiveFragment(), Utilities.FRAGMENT_THREE_TAG);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        LiveUtils.setPreviousUrlStreaming("");
        super.onBackPressed();
    }
}
