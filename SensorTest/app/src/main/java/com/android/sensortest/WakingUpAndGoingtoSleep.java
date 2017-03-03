/**
 *
 */
package com.android.sensortest;

import java.util.*;

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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.InterruptedException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import android.os.IBinder;
import java.io.InputStream;
import android.os.Environment;
import com.android.sensortest.SingleAndroidLog;


/**
 * @author wangxiaoyong
 *
 */
public class  WakingUpAndGoingtoSleep extends SensorCheckListItemChild {
    private static final String WAKING_UP = "Waking up from sleep";
    private static final String UNBLOCKED_SCREEN = "Unblocked screen on after";
    private static final String DELAYIN_SETPOWERMODE = "Excessive delay in setPowerMode";
    private static final String DELAYIN_ENABLESENSOR = "Excessive delay in nativeEnableSensor";
    private static final String DELAYIN_DISABLESENSOR = "Excessive delay in nativeDisableSensor";
    private static final String DELAYIN_SETLIGHT = "Excessive delay setting light";
    private static final String SET_DISPLAY_BRIGHTNESS = "setDisplayBrightness";
    private static final String SET_DISPLAY_STATE = "setDisplayState";
    private static final String SET_DISPLAY_STATE_ON = "(id=0, state=ON)";
    private static final String SET_DISPLAY_STATE_OFF = "(id=0, state=OFF)";

    private static final String WAKING_UP_TAG = "PowerManagerService:";
    private static final String UNBLOCKED_SCREEN_TAG = "DisplayPowerController:";
    private static final String DELAYIN_SETPOWERMODE_TAG = "SurfaceControl:";
    private static final String DELAYIN_SENSOR_TAG = "SensorManager:";
    private static final String DISPLAY_BRIGHTNESS_TAG = "LocalDisplayAdapter:";
    private static final String SETLIGHT_TAG = "LightsService:";

    public static final int WAKING_UP_LOG_TYPE = 1;
    public static final int UNBLOCKED_SCREEN_LOG_TYPE = 2;
    public static final int SET_DISPLAY_STATE_TYPE = 3;
    public static final int DELAYIN_SETPOWERMODE_LOG_TYPE = 4;
    public static final int SET_DISPLAY_BRIGHTNESS_LOG_TYPE = 5;

    private List<List<AndroidLogDocument>> mWakeupScreenAndroidLog = new ArrayList<List<AndroidLogDocument>>();
    private boolean mDuringWakingUp = false;
    private boolean mSetPowerModeON = false;
    private List<AndroidLogDocument> mOneWakingupAndroidLogDocument = null;


    private final static String LOG_FILE_DIR = "/Android/data/Acom.android.sensortest";
    private final static String WAKEUP_LOG_FILE_NAME = "wakeuplog.log";

    private LinkedHashMap<Integer,Integer> mUnbLockedScreenTimeMap = new LinkedHashMap<Integer,Integer>();
    private LinkedHashMap<Integer,Integer> mPowerModeDelayTimeMap = new LinkedHashMap<Integer,Integer>();
    private LinkedHashMap<Integer,Integer> mOneWakeupConsumingTimeMap = new LinkedHashMap<Integer,Integer>();

    private SensorTestLineChart mSetPowerModeTimeLinechart;
    private LinkedHashMap<Integer,Integer> mSetPowerModeTimeMap = new LinkedHashMap<Integer,Integer>();

    private TextView mTextView;
    private LinearLayout mLinearLayout;
    private final Resources mResources;
    private final String[] mlogcatClearCommand = {"/system/bin/logcat -c"};
    private final String[] mLogcatCommand = {"/system/bin/logcat -v threadtime -b main -b system"};//{"/system/bin/logcat -v threadtime -b system"};

    private Handler mJobHandler;
    private CommandExecution mCommandExecution = new CommandExecution();
    private CommandExecution.CommandResult mCommandResult = new CommandExecution.CommandResult();
    private final Runnable mCommandExecutionDumpsysPower = new Runnable() {
        @Override
        public void run() {
            File logFile = getLogFile();
            if (logFile != null) {
                mCommandExecution.execCommandNoWait(mlogcatClearCommand, false);
                mCommandExecution.execCommandResultToFile(mLogcatCommand, false, logFile, 11, "Waking up from sleep");

                //Unblocked Screen Time
                analysisLogFile(logFile);
                mHandler.post(mUpdateTimeViewRunable);
            }
        }
    };

    private void classificationAllLog(String src) {
        if (src.indexOf(WAKING_UP) != -1 && !mDuringWakingUp && mOneWakingupAndroidLogDocument == null) {
            mDuringWakingUp = true;
            mOneWakingupAndroidLogDocument = new ArrayList<AndroidLogDocument>();

            //"Waking up from sleep";
            AndroidLogDocument androidLogDocument = new AndroidLogDocument();
            androidLogDocument.putSingleAndroidLog(src, WAKING_UP_TAG, WAKING_UP_LOG_TYPE);
            mOneWakingupAndroidLogDocument.add(androidLogDocument);
        }

        if (!mSetPowerModeON && mDuringWakingUp && mOneWakingupAndroidLogDocument != null
            && src.indexOf(SET_DISPLAY_STATE+SET_DISPLAY_STATE_ON) != -1) {
            //"Unblocked screen on after"
            mSetPowerModeON = true;
        }

        if (mDuringWakingUp && mOneWakingupAndroidLogDocument != null
            && src.indexOf(UNBLOCKED_SCREEN) != -1) {
            //"Unblocked screen on after"
            AndroidLogDocument androidLogDocument = new AndroidLogDocument();
            androidLogDocument.putSingleAndroidLog(src, UNBLOCKED_SCREEN_TAG, UNBLOCKED_SCREEN_LOG_TYPE);
            mOneWakingupAndroidLogDocument.add(androidLogDocument);
        }

        if (mDuringWakingUp && mSetPowerModeON && mOneWakingupAndroidLogDocument != null
            && src.indexOf(DELAYIN_SETPOWERMODE) != -1) {
            //"Excessive delay in setPowerMode"
            mSetPowerModeON = false;
            AndroidLogDocument androidLogDocument = new AndroidLogDocument();
            androidLogDocument.putSingleAndroidLog(src, DELAYIN_SETPOWERMODE_TAG, DELAYIN_SETPOWERMODE_LOG_TYPE);
            mOneWakingupAndroidLogDocument.add(androidLogDocument);
        }

        if (mDuringWakingUp && mOneWakingupAndroidLogDocument != null
            && src.indexOf(SET_DISPLAY_BRIGHTNESS) != -1
            && src.indexOf("setDisplayBrightness(id=0, brightness=0)") == -1) {
            //"setDisplayBrightness"
            mDuringWakingUp = false;
            mSetPowerModeON = false;
            AndroidLogDocument androidLogDocument = new AndroidLogDocument();
            androidLogDocument.putSingleAndroidLog(src, DISPLAY_BRIGHTNESS_TAG, SET_DISPLAY_BRIGHTNESS_LOG_TYPE);
            mOneWakingupAndroidLogDocument.add(androidLogDocument);

            mWakeupScreenAndroidLog.add(mOneWakingupAndroidLogDocument);
            mOneWakingupAndroidLogDocument = null;
        }
    }

    private void analysisLogFile(File logFile){
        if (logFile != null){
            InputStreamReader isr = null;
            BufferedReader br = null;
            FileInputStream inStream = null;
            try{
                inStream = new FileInputStream(logFile);
                isr = new InputStreamReader(inStream);
                br = new BufferedReader(isr);
                String str = "";
                while ((str = br.readLine()) != null) {
                    classificationAllLog(str);
                }
            } catch (Exception e){
                
            } finally {
                try {
                    if (isr != null) isr.close();
                    if (br != null) br.close();
                    if (inStream != null) inStream.close();
                    Log.d(TAG, "analysisLogFile success");
                } catch (IOException e) {
                    String errmsg = e.getMessage();
                    if (errmsg != null) {
                        Log.e(TAG, errmsg);
                    }
                    e.printStackTrace();
                }
            }

            AnalysisWakeupScreenConsumingTime();
        }
    }

    private void AnalysisWakeupScreenConsumingTime(){
        for (int index=0; index < mWakeupScreenAndroidLog.size(); index++) {

            List<AndroidLogDocument> oneWakingupAndroidLog = mWakeupScreenAndroidLog.get(index);
            long wakingUpStartTime = -1;
            for(int logindex = 0; logindex < oneWakingupAndroidLog.size(); logindex++) {

                AndroidLogDocument logDocument = oneWakingupAndroidLog.get(logindex);
                SingleAndroidLog log = logDocument.getSingleAndroidLog();

                if (log != null && log.mLogTextString != null
                    && log.mLogType == WAKING_UP_LOG_TYPE) {
                    wakingUpStartTime = log.getMilliseconds();
                }

                if (log != null && log.mLogTextString != null
                    && log.mLogType == DELAYIN_SETPOWERMODE_LOG_TYPE) {
                    String[] logStr = log.mLogTextString.split(" ");
                    if (logStr.length >= 5) {
                        String[] logTimeStr = (logStr[4]).split("ms");
                        log.mTimeConsuming = Integer.parseInt(logTimeStr[0]);
                        mPowerModeDelayTimeMap.put(mPowerModeDelayTimeMap.size(), log.mTimeConsuming);
                    }
                }

                if (log != null && log.mLogTextString != null
                    && log.mLogType == UNBLOCKED_SCREEN_LOG_TYPE) {
                    String[] logStr = log.mLogTextString.split(" ");
                    log.mTimeConsuming = Integer.parseInt(logStr[4]);
                    mUnbLockedScreenTimeMap.put(mUnbLockedScreenTimeMap.size(), log.mTimeConsuming);
                }

                if (log != null && log.mLogTextString != null
                    && log.mLogType == SET_DISPLAY_BRIGHTNESS_LOG_TYPE) {
                    if (wakingUpStartTime != -1) {
                        mOneWakeupConsumingTimeMap.put(mOneWakeupConsumingTimeMap.size(), (int)(log.getMilliseconds() - wakingUpStartTime));
                    }
                }
            }

            if (mPowerModeDelayTimeMap.size() == 0 || index > mPowerModeDelayTimeMap.size()-1) {
                mPowerModeDelayTimeMap.put(mPowerModeDelayTimeMap.size(), 0);
            }
        }
    }

    private File getLogFile() {
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
        if (!sdCardExist) {
            return null;
        }
        File sdDir = Environment.getExternalStorageDirectory();
        String path = sdDir.getPath() + LOG_FILE_DIR;
        File fileDir = new File(path);
        if (!fileDir.exists()) {
            Log.d(TAG, "fileDir.mkdirs()");
            fileDir.mkdirs();
        }
        File file = new File(fileDir, WAKEUP_LOG_FILE_NAME);
        Log.d(TAG, "getlogfile = " + file);
        return file;
    }

    private void deleteLogFile() {
        try {
            File logFile = getLogFile();
            if (logFile != null) {
                logFile.delete();
                Log.d(TAG, "deleteLogFile file = " + logFile);
            }
        } catch (Exception e){
        }
    }


    private final Runnable mUpdateTimeViewRunable = new Runnable() {
        @Override  
        public void run() {
            if (mUnbLockedScreenTimeMap.size() > 0) {
                SensorTestBarChart unbLockedScreenTimeBarchart = new SensorTestBarChart(mContext);
                unbLockedScreenTimeBarchart.setBarChartData(mUnbLockedScreenTimeMap
                    , mContext.getResources().getString(R.string.blockedscreenconsumingtime)
                    , null);
                unbLockedScreenTimeBarchart.ShowView();
                mLinearLayout.addView(unbLockedScreenTimeBarchart);
                mTextView.setText(mResources.getString(R.string.testcountnumber)
                    + mUnbLockedScreenTimeMap.size());
            }

            if (mPowerModeDelayTimeMap.size() > 0) {
                SensorTestBarChart powerModeDelayTimeBarchart = new SensorTestBarChart(mContext);
                powerModeDelayTimeBarchart.setBarChartData(mPowerModeDelayTimeMap
                    , mContext.getResources().getString(R.string.setdisplaypowermodeconsumingtime)
                    , null);
                powerModeDelayTimeBarchart.ShowView();
                mLinearLayout.addView(powerModeDelayTimeBarchart);
            }

            if (mOneWakeupConsumingTimeMap.size() > 0) {
                SensorTestBarChart wakeupConsumingTimeBarchart = new SensorTestBarChart(mContext);
                wakeupConsumingTimeBarchart.setLimitLine(500f, mContext.getResources().getString(R.string.wakeupscreentime));
                wakeupConsumingTimeBarchart.setBarChartData(mOneWakeupConsumingTimeMap
                    , mContext.getResources().getString(R.string.wakeupconsumingtime)
                    , null);
                wakeupConsumingTimeBarchart.ShowView();
                mLinearLayout.addView(wakeupConsumingTimeBarchart);
            }
            try {
                playSound();
                vibrate(500);
            } catch (Exception e){
            }
        }
    };

    public WakingUpAndGoingtoSleep(Context context, int layoutId) {
        super(context, layoutId);
        TAG="WakingUpAndGoingtoSleep";
        mResources = context.getResources();
        mTextView = (TextView)LinearLayoutView.findViewById(R.id.testcount);
        mLinearLayout = (LinearLayout)LinearLayoutView.findViewById(R.id.wakinguplayout);
        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();

        mJobHandler = new Handler(handlerThread.getLooper());
    }

    @Override
    public void onCollapsed() {
        super.onCollapsed();
        mDuringWakingUp = false;
        mSetPowerModeON = true;
        mOneWakingupAndroidLogDocument = null;
        if (mUnbLockedScreenTimeMap.size() > 0) {
            mUnbLockedScreenTimeMap.clear();
        }

        if (mPowerModeDelayTimeMap.size() > 0) {
            mPowerModeDelayTimeMap.clear();
        }

        if (mOneWakeupConsumingTimeMap.size() > 0) {
            mOneWakeupConsumingTimeMap.clear();
        }
        if (mWakeupScreenAndroidLog.size()>0){
            mWakeupScreenAndroidLog.removeAll(mWakeupScreenAndroidLog);
        }
        mLinearLayout.removeAllViews();
        deleteLogFile();
        Log.d(TAG,"onCollapsed");
    }

    @Override
    public void onExpanded() {
        //super.onExpanded();
        mTextView.setText(mResources.getString(R.string.testcountnumber) + "0");
        mTextView.setVisibility(View.VISIBLE);

        mJobHandler.removeCallbacks(mCommandExecutionDumpsysPower);
        deleteLogFile();
        mJobHandler.post(mCommandExecutionDumpsysPower);
        Log.d(TAG,"onExpanded");
    }
}
