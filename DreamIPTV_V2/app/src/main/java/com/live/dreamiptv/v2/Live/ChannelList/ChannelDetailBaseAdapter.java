package com.live.dreamiptv.v2.Live.ChannelList;

import android.app.Activity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.live.dreamiptv.v2.Live.LiveUtils;
import com.live.dreamiptv.v2.R;
import com.live.dreamiptv.v2.StreamInfo;

import java.util.ArrayList;

class ChannelDetailBaseAdapter extends BaseAdapter {
    private boolean isLastItem;
    private boolean isFirstItem;
    //    private Activity mActivity;
    private SendData mSendData;
    private View mCurrentListItem;
    private LayoutInflater mLayoutInflater;
    private ArrayList<StreamInfo> mChannelList;

    public ChannelDetailBaseAdapter(SendData sendLink, Activity activity, ArrayList<StreamInfo> channelList) {
        isFirstItem = false;
        isLastItem = false;
//        mActivity = activity;
        mChannelList = new ArrayList<>();
        mChannelList = channelList;
        mSendData = sendLink;
        mLayoutInflater = LayoutInflater.from(activity.getApplicationContext());
    }

    private View getCurrentListItem() {
        return mCurrentListItem;
    }

    private void setCurrentListItem(View currentListItem) {
        mCurrentListItem = currentListItem;
    }

    @Override
    public int getCount() {
        return mChannelList.size();
    }

    @Override
    public Object getItem(int position) {
        return mChannelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;
        if (vi == null) {
            vi = mLayoutInflater.inflate(R.layout.channel_detail_row, parent, false);
            holder = new ViewHolder();
            holder.channelButton = (Button) vi.findViewById(R.id.channelId);
            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }
        holder.channelButton.setFocusable(true);
        holder.channelButton.setFocusableInTouchMode(true);
        StreamInfo item = mChannelList.get(position);
        holder.channelButton.setText(item.getStreamName());
        holder.channelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurrentListItem(v);
                StreamInfo videoInfo = mChannelList.get(position);
                mSendData.sendStream(videoInfo.getStreamLink());
                v.setSelected(true);
                v.setEnabled(true);
                v.setFocusableInTouchMode(true);
                v.requestFocus();
            }
        });
        holder.channelButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    setCurrentListItem(v);
                    checkIfFirstItem(position);
                    checkIfLastItem(position);
                }
            }
        });
        holder.channelButton.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) { //TODO: return true if listener has consumed the event
                int keyAction = event.getAction();
                if (keyAction == KeyEvent.ACTION_DOWN) {
                    if (!LiveUtils.isUserPresentOnLargeScreen()) {
                        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN && isLastItem) {
                            mSendData.sendKeyRotate2ChannelListFirstItemEvent();
                            return true;
                        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP && isFirstItem) {
                            mSendData.sendKeyRotate2ChannelListLastItemEvent(mChannelList.size() - 1);
                            return true;
                        }
                        return false;
                    } else {
                        setFocusOnCurrentItem();
                        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_BACK) {
                            mSendData.sendKeyFullScreenBackEvent();
//                            v.setPressed(true);
                        }
                        return true;
                    }
                }
                return false;
            }
        });
        return vi;
    }

    private void setFocusOnCurrentItem() {
        View v = getCurrentListItem();
        if (v != null) {
            v.requestFocus();
        }
    }

    private void checkIfLastItem(int itemPosition) {
        if (itemPosition + 1 == mChannelList.size()) {
            isLastItem = true;
        } else {
            isLastItem = false;
        }
    }

    private void checkIfFirstItem(int itemPosition) {
        if (itemPosition == 0) {
            isFirstItem = true;
        } else {
            isFirstItem = false;
        }
    }

    private static class ViewHolder {
        Button channelButton;
    }

    public interface SendData {
        void sendStream(String channelLink);

        void sendKeyFullScreenBackEvent();

        void sendKeyRotate2ChannelListFirstItemEvent();

        void sendKeyRotate2ChannelListLastItemEvent(int lastItemPosition);
    }
}
