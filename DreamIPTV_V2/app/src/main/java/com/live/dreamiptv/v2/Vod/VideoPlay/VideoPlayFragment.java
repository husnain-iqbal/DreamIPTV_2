package com.live.dreamiptv.v2.Vod.VideoPlay;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.live.dreamiptv.v2.Live.LiveUtils;
import com.live.dreamiptv.v2.R;
import com.live.dreamiptv.v2.Utilities;
import com.live.dreamiptv.v2.Vod.VideoList.VideoListBaseAdapter;
import com.live.dreamiptv.v2.Vod.VideoList.VideoListFragment;
import com.live.dreamiptv.v2.Vod.VodUtils;

public class VideoPlayFragment extends Fragment {
    private Activity mActivity;
    private Context mContext;
    private WebView mWebView;
    private SimpleExoPlayer mExoPlayer;
    private SimpleExoPlayerView mVideoView;
    private RelativeLayout mIpTvLogoImage;
    private RelativeLayout mFragmentsContainer;
    private RelativeLayout mVideoViewContainerFragment;
    private static int mSmallScreenVideoViewHeight;
    private long mCurrentVideoPosition;
    private ImageButton mPlayButton;
    private ImageButton mPauseButton;
    private ImageButton mFastForwardButton;
    private ImageButton mRewindButton;
    private static final String VIDEO_LINK_WEB_VIEW_PREFIX = "http://51.15.9.179/yt/downloader.php?videoid=";
    private static final String YOUTUBE_LINK_WEB_VIEW_PREFIX = "https://www.youtube.com/watch?v=";

    public long getCurrentVideoPosition() {
        return mCurrentVideoPosition;
    }

    public void setCurrentVideoPosition(long position) {
        mCurrentVideoPosition = position;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mContext = mActivity.getApplicationContext();
        VodUtils.setUserOnLargeScreen(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.video_view_exoplayer_layout, container, false);
        mWebView = (WebView) view.findViewById(R.id.web_view);
        mVideoView = (SimpleExoPlayerView) view.findViewById(R.id.video_view_exoplayer);
        mPlayButton = (ImageButton) view.findViewById(R.id.exo_play);
        mPauseButton = (ImageButton) view.findViewById(R.id.exo_pause);
        mRewindButton = (ImageButton) view.findViewById(R.id.exo_rew);
        mFastForwardButton = (ImageButton) view.findViewById(R.id.exo_ffwd);
        mIpTvLogoImage = (RelativeLayout) mActivity.findViewById(R.id.iptv_logo_image);
        mFragmentsContainer = (RelativeLayout) mActivity.findViewById(R.id.category_videos_fragments_container);
        mVideoViewContainerFragment = (RelativeLayout) mActivity.findViewById(R.id.fragment_three_vod);
        mSmallScreenVideoViewHeight = (int) mActivity.getResources().getDimension(R.dimen.video_view_small_screen_height);
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mExoPlayer != null) {
            mExoPlayer.release();
        }
    }

    public void attainUrl(String videoLink) {
        if (!VodUtils.getPreviousUrlStreaming().equals(videoLink)) {
            setCurrentVideoPosition(0);
            VodUtils.setPreviousUrlStreaming(videoLink);
            mWebView.loadUrl("about:blank");
            if (mExoPlayer != null) {
                mExoPlayer.release();
            }
            if (videoLink.contains(VIDEO_LINK_WEB_VIEW_PREFIX)) {
                playVideoInWebView(videoLink);
            } else {
                playVideoInExoPlayer(videoLink);
            }
        }
        if (!VodUtils.isUserOnLargeScreen()) {
            stretchVideoView();
        }
    }

    private void playVideoInWebView(String link) {
        mWebView.setVisibility(View.VISIBLE);
        mVideoView.setVisibility(View.GONE);
        link = link.replace(VIDEO_LINK_WEB_VIEW_PREFIX, YOUTUBE_LINK_WEB_VIEW_PREFIX);
        link += "?enablejsapi=1&autoplay=1";
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.loadUrl(link);
        mWebView.setWebViewClient(new WebViewClient());
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        if (android.os.Build.VERSION.SDK_INT > 16) {
            settings.setMediaPlaybackRequiresUserGesture(false);
        }

//        mWebView.setWebChromeClient(new WebChromeClient());
//        mWebView.setWebViewClient(new WebViewClient() {
//            public void onPageFinished(WebView view, String url) {
//                try {
//                    view.loadUrl("javascript:(function() { document.getElementsByTagName('video')[0].play(); })()");
////                    mWebView.loadUrl(url);
//                }catch (Exception e){
//                    Log.e("JavScriptException", e.getMessage());
//                }
//            }
//        });
//        WebSettings settings = mWebView.getSettings();
//        settings.setJavaScriptEnabled(true);
//        settings.setJavaScriptCanOpenWindowsAutomatically(true);
//        settings.setLoadWithOverviewMode(true);
//        settings.setUseWideViewPort(true);
//        settings.setLoadsImagesAutomatically(true);
//        settings.setPluginState(WebSettings.PluginState.ON);
//        settings.setAllowFileAccess(true);
//        if (android.os.Build.VERSION.SDK_INT > 16) {
//            settings.setMediaPlaybackRequiresUserGesture(false);
//        }
    }

    private void playVideoInExoPlayer(String link) {
        mWebView.setVisibility(View.GONE);
        mVideoView.setVisibility(View.VISIBLE);
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        mExoPlayer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector);
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(mContext, Util.getUserAgent(mContext, mActivity.getResources().getString(R.string.app_name)), bandwidthMeter);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        final MediaSource videoSource = new ExtractorMediaSource(Uri.parse(link), dataSourceFactory, extractorsFactory, null, null);
        mExoPlayer.prepare(videoSource, false, false);
        mExoPlayer.setPlayWhenReady(true);
        mExoPlayer.addListener(new ExoPlayer.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object o) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroupArray, TrackSelectionArray trackSelectionArray) {

            }

            @Override
            public void onLoadingChanged(boolean b) {
//                Log.d("Tag", b+"");
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                Log.d("Tag", playbackState + "");
                switch (playbackState) {
                    case ExoPlayer.STATE_IDLE:
                        mExoPlayer.prepare(videoSource);
                        mExoPlayer.seekTo(getCurrentVideoPosition());
                        mExoPlayer.setPlayWhenReady(true);
                        break;
                    case ExoPlayer.STATE_BUFFERING:
                        setCurrentVideoPosition(mExoPlayer.getCurrentPosition());
                        break;
                    case ExoPlayer.STATE_READY:
                        setCurrentVideoPosition(mExoPlayer.getCurrentPosition());
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
                            if (VodUtils.isUserOnLargeScreen()) {
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
                Log.d("Tag", "Position Discontinuity");
            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }
        });
        mVideoView.setPlayer(mExoPlayer);
        mVideoView.bringToFront();
        mVideoView.requestFocus();
        mVideoView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
//        final Handler handler = new Handler();
//        final Runnable r = new Runnable() {
//            public void run() {
//                handler.postDelayed(this, 1000);
//                int exoPlayerState = mExoPlayer.getPlaybackState();
//                if (exoPlayerState == ExoPlayer.STATE_IDLE) {
//                    mExoPlayer.prepare(videoSource);
//                    mExoPlayer.seekTo(getCurrentVideoPosition());
//                    mExoPlayer.setPlayWhenReady(true);
//                    Log.e("@VideoPlayFragment", "State idle");
//                } else if (exoPlayerState == ExoPlayer.STATE_BUFFERING || exoPlayerState == ExoPlayer.STATE_READY) {
//                    setCurrentVideoPosition(mExoPlayer.getCurrentPosition());
//                } else if (exoPlayerState == ExoPlayer.STATE_ENDED) {
//                }
//            }
//        };
//        handler.postDelayed(r, 1000);
    }

    public void performVideoPlayOrPause() {
        mVideoView.showController();
        if (mPlayButton.getVisibility() == View.VISIBLE) {
            mPlayButton.performClick();
            mPlayButton.requestFocus();
        } else {
            mPauseButton.performClick();
            mPauseButton.requestFocus();
        }
    }

    public void performVideoFastForward() {
        mVideoView.showController();
        mFastForwardButton.performClick();
        if (mPlayButton.getVisibility() == View.VISIBLE) {
            mPlayButton.requestFocus();
        } else {
            mPauseButton.requestFocus();
        }
    }

    public void performVideoRewind() {
        mVideoView.showController();
        mRewindButton.performClick();
        if (mPlayButton.getVisibility() == View.VISIBLE) {
            mPlayButton.requestFocus();
        } else {
            mPauseButton.requestFocus();
        }
    }

    public void compressVideoView() {
        if (mIpTvLogoImage.getVisibility() == View.GONE) {
            mIpTvLogoImage.setVisibility(View.VISIBLE);
        }
        if (mFragmentsContainer.getVisibility() == View.GONE) {
            mFragmentsContainer.setVisibility(View.VISIBLE);
        }
        mVideoViewContainerFragment.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, mSmallScreenVideoViewHeight));
        VodUtils.setUserOnLargeScreen(false);
        VideoListFragment fragment = (VideoListFragment) getFragmentManager().findFragmentById(R.id.fragment_two_vod);
        if (fragment != null) {
            fragment.setFocusOnCurrentItem();
        }
        clearFocusFromWebView();
    }

    private void stretchVideoView() {
        if (mIpTvLogoImage.getVisibility() == View.VISIBLE) {
            mIpTvLogoImage.setVisibility(View.GONE);
        }
        if (mFragmentsContainer.getVisibility() == View.VISIBLE) {
            mFragmentsContainer.setVisibility(View.GONE);
        }
        mVideoViewContainerFragment.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        VodUtils.setUserOnLargeScreen(true);
        setFocusOnWebView();
    }

    private void setFocusOnWebView() {
        if (mWebView.getVisibility() == View.VISIBLE) {
            mWebView.setFocusable(true);
            mWebView.requestFocus();
            mWebView.bringToFront();
        }
    }

    private void clearFocusFromWebView() {
        if (mWebView.getVisibility() == View.VISIBLE) {
            mWebView.setFocusable(true);
            mWebView.clearFocus();
        }
    }

    @Override
    public void onDestroy() {
        if (mExoPlayer != null) {
            mExoPlayer.release();
        }
        super.onDestroy();
    }
}