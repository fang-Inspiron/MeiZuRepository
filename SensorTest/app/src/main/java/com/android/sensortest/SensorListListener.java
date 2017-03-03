package com.android.sensortest;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;

public class SensorListListener {
    private static final String TAG = "SensorListListener";

    private Context mContext;
    private SensorManager mSensorManager;
    private List<Sensor> mSensorList;
    private Sensor mTriggerSensor;
    private List<SensorListItemChild> mSensorListView;

    public SensorListListener(Context context) {
        mContext = context;
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        mSensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        //sensorItemChild = new SensorListItemChild();
        mSensorListView = new ArrayList<SensorListItemChild>(); 
    }

    public int getSensorListNum() {
        if (mSensorList == null) {
            return 0;
        }

        return mSensorList.size();
    }

    public String getSensorName(int index) {
        if (index >=0 && index < mSensorList.size()) {
            return mSensorList.get(index).getName();
        }
        return null;
    }
    
    public void SetSensorListView(List<SensorListItemChild> list) {
        mSensorListView = list;
        for(int index = 0;index < getSensorListNum(); index++) {
            mSensorListView.get(index).setSensor(mSensorList.get(index));
        }
    }

    public boolean setSensorEnabled(int index, boolean enable) {
        mSensorListView.get(index).setSensorEnable(enable, true);
        return true;
    }
}
