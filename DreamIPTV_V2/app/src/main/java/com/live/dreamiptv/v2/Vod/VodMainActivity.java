package com.live.dreamiptv.v2.Vod;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.live.dreamiptv.v2.Home.HomeActivity;
import com.live.dreamiptv.v2.R;
import com.live.dreamiptv.v2.StreamInfo;
import com.live.dreamiptv.v2.Utilities;
import com.live.dreamiptv.v2.Vod.VideoCategoryList.VideoCategoryDetailFragment;
import com.live.dreamiptv.v2.Vod.VideoList.VideoListBaseAdapter;
import com.live.dreamiptv.v2.Vod.VideoList.VideoListFragment;
import com.live.dreamiptv.v2.Vod.VideoPlay.VideoPlayFragment;

import java.util.ArrayList;
import java.util.HashMap;

public class VodMainActivity extends AppCompatActivity {
    public static final String VIDEO_MAIN_ACTIVITY_RECORD_TEXT = "VideoMainActivityRecord";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vod_main);
        HashMap<String, ArrayList<StreamInfo>> mVideosRecord = HomeActivity.getBundledChannelFileInfo(VodMainActivity.this);
        Bundle bundle = new Bundle();
        bundle.putSerializable(VIDEO_MAIN_ACTIVITY_RECORD_TEXT, mVideosRecord);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        VideoCategoryDetailFragment videoCategoryDetailFragment = new VideoCategoryDetailFragment();
        videoCategoryDetailFragment.setArguments(bundle);
        fragmentTransaction.add(R.id.fragment_one_vod, videoCategoryDetailFragment, Utilities.FRAGMENT_ONE_TAG);
        fragmentTransaction.add(R.id.fragment_two_vod, new VideoListFragment(), Utilities.FRAGMENT_TWO_TAG);
        fragmentTransaction.add(R.id.fragment_three_vod, new VideoPlayFragment(), Utilities.FRAGMENT_THREE_TAG);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (VodUtils.isUserOnLargeScreen()) {
            VideoPlayFragment fragment = (VideoPlayFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_three_vod);
            if (fragment != null) {
                fragment.compressVideoView();
            }
        } else {
            VodUtils.setPreviousUrlStreaming("");
            this.finish();
            //super.onBackPressed();
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (VodUtils.isUserOnLargeScreen()) {
            if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
                VideoPlayFragment fragment = (VideoPlayFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_three_vod);
                if (fragment != null) {
                    fragment.performVideoPlayOrPause();
                }
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD) {
                VideoPlayFragment fragment = (VideoPlayFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_three_vod);
                if (fragment != null) {
                    fragment.performVideoFastForward();
                }
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_MEDIA_REWIND) {
                VideoPlayFragment fragment = (VideoPlayFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_three_vod);
                if (fragment != null) {
                    fragment.performVideoRewind();
                }
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                VideoListFragment fragment = (VideoListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_two_vod);
                if (fragment != null) {
                    fragment.setFocusOnCurrentItem();
                }
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_BACK) {
                return super.onKeyUp(keyCode, event);
            }
        }
        return super.onKeyUp(keyCode, event);
    }
}