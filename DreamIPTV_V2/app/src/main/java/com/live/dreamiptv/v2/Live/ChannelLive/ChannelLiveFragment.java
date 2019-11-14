package com.live.dreamiptv.v2.Live.ChannelLive;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.AdaptiveMediaSourceEventListener;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.live.dreamiptv.v2.Live.LiveUtils;
import com.live.dreamiptv.v2.R;

import java.io.IOException;

public class ChannelLiveFragment extends Fragment {
    private Activity mActivity;
    private Context mContext;
    private SimpleExoPlayer mExoPlayer;
    private SimpleExoPlayerView mVideoView;
    private RelativeLayout mIpTvLogoImage;
    private RelativeLayout mFragmentsContainer;
    private RelativeLayout mVideoViewContainerFragment;
    private int mSmallScreenVideoViewHeight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mContext = mActivity.getApplicationContext();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.channel_view_exoplayer_layout_android, container, false);
        mVideoView = (SimpleExoPlayerView) view.findViewById(R.id.channel_view_exoplayer);
        mIpTvLogoImage = (RelativeLayout) mActivity.findViewById(R.id.iptv_logo_image);
        mFragmentsContainer = (RelativeLayout) mActivity.findViewById(R.id.category_channel_fragments_container);
        mVideoViewContainerFragment = (RelativeLayout) mActivity.findViewById(R.id.fragment_three_live);
        mSmallScreenVideoViewHeight = (int) mActivity.getResources().getDimension(R.dimen.video_view_small_screen_height);
        return view;
    }

    public void getChannelLinkAndPlayChannel(String channelLink) {
        if (LiveUtils.getPreviousUrlStreaming().equals(channelLink)) {
            // Small screen - Large Screen Handling
            if (LiveUtils.isUserPresentOnLargeScreen()) {
                compressVideoView();
            } else {
                stretchVideoView();
            }
        } else {
//            channelLink = Utilities.MP4_TEST_URL;
            playVideo(channelLink);
        }
    }

    private void playVideo(String link) {
        if (mExoPlayer != null) {
            mExoPlayer.release();
        }
        LiveUtils.setPreviousUrlStreaming(link);
        Handler mainHandler = new Handler();
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
//        LoadControl loadControl = new DefaultLoadControl();
//        mExoPlayer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector, loadControl);
        mExoPlayer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector);
        DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter();
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(mContext, Util.getUserAgent(mContext, "Exo2"), defaultBandwidthMeter);
//        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        final HlsMediaSource hlsMediaSource = new HlsMediaSource(Uri.parse(link), dataSourceFactory, mainHandler, new AdaptiveMediaSourceEventListener() {
            @Override
            public void onLoadStarted(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs) {
            }

            @Override
            public void onLoadCompleted(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded) {
            }

            @Override
            public void onLoadCanceled(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded) {
            }

            @Override
            public void onLoadError(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded, IOException error, boolean wasCanceled) {
            }

            @Override
            public void onUpstreamDiscarded(int trackType, long mediaStartTimeMs, long mediaEndTimeMs) {
            }

            @Override
            public void onDownstreamFormatChanged(int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaTimeMs) {
            }
        });
        mExoPlayer.addListener(new ExoPlayer.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object o) {
            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroupArray, TrackSelectionArray trackSelectionArray) {
            }

            @Override
            public void onLoadingChanged(boolean b) {
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                Log.d("Tag", playbackState + "");
                switch (playbackState) {
                    case ExoPlayer.STATE_IDLE:
                        mExoPlayer.prepare(hlsMediaSource);
                        mExoPlayer.setPlayWhenReady(true);
                        break;
                    case ExoPlayer.STATE_BUFFERING:
                        break;
                    case ExoPlayer.STATE_READY:
                        break;
                    case ExoPlayer.STATE_ENDED:
                        break;
                }
            }

            @Override
            public void onPlayerError(ExoPlaybackException e) {
                String alertTitle = "Stream Error";
                String errorMsg = e.getMessage();
                if (errorMsg != null && !errorMsg.isEmpty()) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mActivity);
                    alertBuilder.setTitle(alertTitle);
                    alertBuilder.setMessage(errorMsg);
                    alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (LiveUtils.isUserPresentOnLargeScreen()) {
                                compressVideoView();
                            }
                        }
                    })
                            .create()
                            .show();
                }
            }

            @Override
            public void onPositionDiscontinuity() {
            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            }
        });
        mExoPlayer.prepare(hlsMediaSource);
        mVideoView.setPlayer(mExoPlayer);
        mVideoView.requestFocus();
        mExoPlayer.setPlayWhenReady(true);
        mVideoView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
        mVideoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (LiveUtils.isUserPresentOnLargeScreen()) {
                    compressVideoView();
                }
                return false;
            }
        });
    }

    public void compressVideoView() {
        if (mIpTvLogoImage.getVisibility() == View.GONE) {
            mIpTvLogoImage.setVisibility(View.VISIBLE);
        }
        if (mFragmentsContainer.getVisibility() == View.GONE) {
            mFragmentsContainer.setVisibility(View.VISIBLE);
        }
        mVideoViewContainerFragment.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, mSmallScreenVideoViewHeight));
        LiveUtils.setUserPresentOnLargeScreen(false);
    }

    private void stretchVideoView() {
        if (mIpTvLogoImage.getVisibility() == View.VISIBLE) {
            mIpTvLogoImage.setVisibility(View.GONE);
        }
        if (mFragmentsContainer.getVisibility() == View.VISIBLE) {
            mFragmentsContainer.setVisibility(View.GONE);
        }
        mVideoViewContainerFragment.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        LiveUtils.setUserPresentOnLargeScreen(true);
    }

    public void releasePlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.release();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }
}