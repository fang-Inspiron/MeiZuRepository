package com.android.sensortest;

import java.util.*;
import android.util.Log;
import java.text.*;

/*
*mDate | mTime | mPid  | mTid | mPriority | mLogTag  | mLogTextString
*12-22 16:57:13.918   974  1060 I PowerManagerService: Waking up from sleep (uid 1000)...
*/
public class SingleAndroidLog {
    /*
    private static final int WAKING_UP_LOG = 1;
    private static final int UNBLOCKED_SCREEN_LOG = 2;
    private static final int DELAYIN_SETPOWERMODE_LOG = 3;
    private static final int SET_DISPLAY_BRIGHTNESS_LOG = 4;
    */
    public int mLogType = -1;

    public String mLogTag;

    public String mDate;

    public String mTime;

    public String mPid;

    public String mTid;

    public String mPriority;

    public String mLogTextString;

    public int mTimeConsuming = 0; //ms

    public boolean mTurnScreenON = false; //ms

    @Override
    public String toString() {
        return "{SingleAndroidLog mDate=" + mDate + "\", mTime=" + mTime + ", mPid=" + mPid
                + ", mTid=" + mTid + ", mPriority=" + mPriority + ", mLogTag=" + mLogTag
                +", mLogTextString=" + Arrays.toString(mLogTextString.split(" "))+", mTimeConsuming="+mTimeConsuming+",mTurnScreenON="+mTurnScreenON+"}";
    }

    /**
     * Returns this log millisecond value. The value is the number of
     * milliseconds since Jan. 1, 1970, midnight GMT.
     *
     * @return the number of milliseconds since Jan. 1, 1970, midnight GMT.
     */
    public long getMilliseconds(){
        long milliseconds = -1;
        DateFormat format= new SimpleDateFormat("MM-dd HH:mm:ss.SSS");
        Date date = null;

        try {
            date = format.parse(mDate +" " +mTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date!=null) {
            milliseconds = date.getTime();
        }

        return milliseconds;
    }
}
