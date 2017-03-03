/**
 *
 */
package com.android.sensortest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.Random;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import static android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.content.Context;
import android.view.LayoutInflater;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.content.res.Resources;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.InterruptedException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import android.os.IBinder;
import java.io.InputStream;


/**
 * @author wangxiaoyong
 *
 */
public class  WakeLock extends SensorCheckListItemChild {
    private static final String FULL_WAKE_LOCK = "FULL_WAKE_LOCK";
    private static final String SCREEN_BRIGHT_WAKE_LOCK = "SCREEN_BRIGHT_WAKE_LOCK";
    private static final String SCREEN_DIM_WAKE_LOCK = "SCREEN_DIM_WAKE_LOCK";
    private static final String PARTIAL_WAKE_LOCK = "PARTIAL_WAKE_LOCK";
    private static final String PROXIMITY_SCREEN_OFF_WAKE_LOCK = "PROXIMITY_SCREEN_OFF_WAKE_LOCK";

    private TextView mTextView;
    private LinearLayout mLinearLayout;
    private final Resources mResources;
    private final String[] mDumpsysPower = {"/system/bin/dumpsys power"};
    private final String mMatchString = "ws=";
    private Handler mWakeLockHandler;
    private CommandExecution mCommandExecution = new CommandExecution();
    private CommandExecution.CommandResult mCommandResult = new CommandExecution.CommandResult();
    private final Runnable mCommandExecutionDumpsysPower = new Runnable() {
        @Override  
        public void run() {
            mCommandResult = mCommandExecution.execCommand(mDumpsysPower, false);
            if (mCommandResult.successMsgList != null) {
                Log.d(TAG, "mCommandResult.successMsg:"+mCommandResult.successMsgList);
                mHandler.post(mUpdateWakeLockPowerStateRunable);
            }
        }
    };

    private final Runnable mUpdateWakeLockPowerStateRunable = new Runnable() {
        @Override  
        public void run() {
            for (int index = 0; index < mCommandResult.successMsgList.size();index++) {
                String infostring = mCommandResult.successMsgList.get(index);
                Log.d(TAG, "updateWakeLockPowerStateRunable infostring:"+infostring);
                if(infostring !=null && (infostring.indexOf(mMatchString) != -1)) {
                    TextView tv = new TextView(mContext);
                    tv.setText(getWakeLockStateString(infostring)+infostring);
                    tv.setSingleLine(false);
                    tv.setTextColor(R.color.black);
                    tv.setBackgroundResource(R.drawable.textviewbackground);
                    mLinearLayout.addView(tv);
                }
            }
        }
    };

    private String getWakeLockStateString(String srcString) {
        String result;
        if (srcString.indexOf(PARTIAL_WAKE_LOCK) != -1) {
            result = mResources.getString(R.string.partialwakeLock);
        } else if (srcString.indexOf(FULL_WAKE_LOCK) != -1
                    || srcString.indexOf(SCREEN_BRIGHT_WAKE_LOCK) != -1
                    || srcString.indexOf(SCREEN_DIM_WAKE_LOCK) != -1) {
            result = mResources.getString(R.string.screenonwakelock);
        } else if (srcString.indexOf(PROXIMITY_SCREEN_OFF_WAKE_LOCK) != -1) {
            result = mResources.getString(R.string.proximityscreenoffwakelock);
        } else {
            result = mResources.getString(R.string.wakelock);
        }
        return result;
    }

    public WakeLock(Context context, int layoutId) {
        super(context, layoutId);
        TAG="WakeLockState";
        mResources = context.getResources();
        mTextView = (TextView)LinearLayoutView.findViewById(R.id.wakelockstate);
        mLinearLayout = (LinearLayout)LinearLayoutView.findViewById(R.id.wakelockstatelayout);
        HandlerThread handlerThread = new HandlerThread("WakeLockTest");
        handlerThread.start();

        mWakeLockHandler = new Handler(handlerThread.getLooper());

    }

    @Override
    public void onCollapsed() {
        super.onCollapsed();
        mTextView.setVisibility(View.GONE);
        if (mCommandResult.successMsgList != null) {
            mCommandResult.successMsgList.removeAll(mCommandResult.successMsgList);
        }
        mLinearLayout.removeAllViews();
        Log.d(TAG,"onCollapsed");
    }

    @Override
    public void onExpanded() {
        super.onExpanded();
        mTextView.setVisibility(View.VISIBLE);
        Log.d(TAG,"onExpanded");
        mWakeLockHandler.post(mCommandExecutionDumpsysPower);
    }
}
