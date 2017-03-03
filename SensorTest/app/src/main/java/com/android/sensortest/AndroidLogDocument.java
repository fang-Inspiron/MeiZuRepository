/**
 *
 */
package com.android.sensortest;

import java.util.*;
import android.util.Log;


/**
 * @author wangxiaoyong
 * 
 */
public class AndroidLogDocument {
    private final String TAG = "AndroidLogDocument";

    private SingleAndroidLog mSingleAndroidLog = new SingleAndroidLog();

    public AndroidLogDocument() {
    }

    public void putSingleAndroidLog(String srcLog,String logTag, int logType) {
        logTag = logTag.trim();

        String[] spStr;
        String[] splitStr = srcLog.split(logTag);
        for (int index=0; index < splitStr.length; index++) {
            splitStr[index] = splitStr[index].trim();
        }


        SingleAndroidLog aSinglelog = new SingleAndroidLog();

        if (logTag.indexOf(":") != -1){
            aSinglelog.mLogTag = logTag.substring(0,logTag.length() - 1);
        } else {
            aSinglelog.mLogTag = logTag;
        }

        if (splitStr.length > 0) {
            //splitStr[0] = sub(splitStr[0]);
            spStr = splitStr[0].split(" ");
            List<String> infoStr = new ArrayList<String>();
            for (int index=0; index<spStr.length; index++) {
                if(!spStr[index].equals(" ") && !spStr[index].equals("")) {
                    infoStr.add(spStr[index]);
                }
            }
            //Log.d(TAG,""+infoStr);
            aSinglelog.mDate = infoStr.get(0).trim();
            aSinglelog.mTime = infoStr.get(1).trim();
            aSinglelog.mPid = infoStr.get(2).trim();
            aSinglelog.mTid = infoStr.get(3).trim();
            aSinglelog.mPriority = infoStr.get(4).trim();
            aSinglelog.mLogType = logType;
        }
        if (splitStr.length > 1) {
            aSinglelog.mLogTextString = splitStr[1];
        }
        Log.d(TAG, ""+aSinglelog);

        synchronized (mSingleAndroidLog) {
            mSingleAndroidLog = aSinglelog;
        }
    }

    @Override
    public String toString() {
        return ":"+mSingleAndroidLog+".\n";
    }
/*
    public int size() {
        return 1;
    }

    public boolean isEmpty() {
        return  (mSingleAndroidLog.size() <= 0);
    }
*/
    public SingleAndroidLog getSingleAndroidLog() {
        synchronized (mSingleAndroidLog) {
            //List<SingleAndroidLog> logList = (List)mSingleAndroidLog.clone();
            //return Collections.unmodifiableList(logList);
            return mSingleAndroidLog;
        }
    }
}
