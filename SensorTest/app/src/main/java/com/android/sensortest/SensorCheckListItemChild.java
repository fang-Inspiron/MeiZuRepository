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
import java.util.Iterator;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import static android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.os.SystemClock;
import android.content.Context;
import android.view.LayoutInflater;
import android.os.Handler;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.provider.Settings;
import android.os.Message;


import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.ColorTemplate;
import android.graphics.Color;
import android.graphics.Typeface;

/**
 * @author wangxiaoyong
 *
 */
public class SensorCheckListItemChild extends LinearLayout {
    protected String TAG = "SensorCheckListItemChild";
    protected static final int MSG_UPDATE_VIEW = 1;

    protected Context mContext;
    protected SensorCheckListItemChildHandler mHandler;

    protected TextView mSensorCtsTestingText;
    protected LinearLayout mSensorCheckListTestResultLayout;
    protected View LinearLayoutView;

    protected class SensorCheckListItemChildHandler extends Handler {
        public SensorCheckListItemChildHandler() {
            super();
        }
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_VIEW:
                    Log.d(TAG, "handleMessage updateView:"+(View)msg.obj);
                    break;
            }
        }
    };

    public SensorCheckListItemChild(Context context, int layoutId) {
        super(context);
        mContext = context;
        mHandler = new SensorCheckListItemChildHandler();
        inflateLayout(mContext, layoutId);
    }

    private void inflateLayout(Context context,int layoutId) {
        LayoutInflater layoutInflater= (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayoutView = layoutInflater.inflate(layoutId, this, true);
    }

    protected void playSound() throws InterruptedException {
        MediaPlayer player = MediaPlayer.create(mContext, Settings.System.DEFAULT_NOTIFICATION_URI);
        if (player == null) {
            Log.e(TAG, "MediaPlayer unavailable.");
            return;
        }
        player.start();
        try {
            Thread.sleep(500);
        } finally {
            player.stop();
        }
    }

    protected void vibrate(int timeInMs) {
        Vibrator vibrator = (Vibrator) mContext.getSystemService(mContext.VIBRATOR_SERVICE);
        vibrator.vibrate(timeInMs);
    }

    public void onCollapsed() {

    }

    public void onExpanded() {
        //executeTests();
        try {
            //playSound();
            vibrate(500);
        } catch (Exception e){
        }
    }
}
