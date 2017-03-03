/**
 *
 */
package com.android.sensortest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.os.SystemClock;
import android.content.Context;
import android.view.LayoutInflater;

/**
 * @author wangxiaoyong
 */
public class SensorListItemChild extends LinearLayout {
    private static final String TAG = "SensorListListener";
    private String mTitle;
    private TextView mChildNoValueText;
    private DynamicLineChartView mDynamicPlotView;
    private LinearLayout mStatus;
    private CheckBox mCheckbox_x;
    private CheckBox mCheckbox_y;
    private CheckBox mCheckbox_z;
    private CheckBox mCheckbox_record;

    private RadioGroup mRadioGroup;
    private List<RadioButton> mRadioSensorDelayRate = new ArrayList<RadioButton>();
    private int mSensorDelayRate = SensorManager.SENSOR_DELAY_NORMAL;

    private Sensor mSensor;
    private boolean mSensorEnabled = false;
    private boolean mIsReceiveSensorValue = false;
    private SensorManager mSensorManager;
    private static float mScale;

    private static final int EVENT_REMOVE_HORIZON = 100;
    private static final int EVENT_VALUE_HORIZON = 10000;
    private Paint mTablePaint = new Paint();
    private float mRawMax = Float.MIN_VALUE;
    private int mTextSize = 16;
    private Paint mTextPaint = new Paint();

    private Context mContext;
    private boolean flag = false;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SensorEvent eve = (SensorEvent) msg.obj;
            new WriteFileThread(eve).start();
        }
    };

    class WriteFileThread extends Thread {
        SensorEvent event;

        public WriteFileThread(SensorEvent event) {
            this.event = event;
        }

        public void run() {
            writeFileToSD(event);
        }
    }

    private final SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(final SensorEvent event) {
            Log.d(TAG, "onSensorChanged mSensorEnabled:" + mSensorEnabled
                    + ",event.values[0]:" + event.values[0]);
            if (mSensorEnabled) {
                final long time = SystemClock.uptimeMillis();
                float sensorValue = event.values[0];
                handleLightSensorEvent(time, event);

                if (flag) {
                    Message msg = new Message();
                    msg.obj = event;
                    handler.sendMessage(msg);
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Not used.
        }
    };

    private final TriggerEventListener mTriggerEventListener = new TriggerEventListener() {
        @Override
        public void onTrigger(TriggerEvent event) {
            mIsReceiveSensorValue = true;
            updateView(event);
        }
    };

    private View.OnClickListener checkerOne = new View.OnClickListener() {
        public void onClick(View paramAnonymousView) {
            mDynamicPlotView.setSelection(mCheckbox_x.isChecked(), mCheckbox_y.isChecked(), mCheckbox_z.isChecked());
        }
    };

    public SensorListItemChild(Context context, String title, SensorManager sensormanager) {
        super(context);
        mTitle = title;
        mContext = context;
        mSensorManager = sensormanager;
        mScale = mContext.getResources().getDisplayMetrics().density;
        mTextSize = getTextScale(8);

        mTablePaint.setStrokeWidth(1.0F);
        mTablePaint.setStyle(Paint.Style.STROKE);
        mTablePaint.setAntiAlias(true);

        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setAntiAlias(true);
    }

    private int getTextScale(int textNum) {
        return (int) (textNum * mScale + 0.5f);
    }

    private void inflateLayout(Context context) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.sensor_list_child, this, true);

        mChildNoValueText = (TextView) view.findViewById(R.id.childNoValueYetText);
        mDynamicPlotView = (DynamicLineChartView) view.findViewById(R.id.dynamicPlotView);
        mStatus = (LinearLayout) view.findViewById(R.id.status);

        mCheckbox_x = (CheckBox) view.findViewById(R.id.value_x);
        mCheckbox_x.setChecked(true);
        mCheckbox_x.setOnClickListener(checkerOne);

        mCheckbox_y = (CheckBox) view.findViewById(R.id.value_y);
        mCheckbox_y.setChecked(true);
        mCheckbox_y.setOnClickListener(checkerOne);

        mCheckbox_z = (CheckBox) view.findViewById(R.id.value_z);
        mCheckbox_z.setChecked(true);
        mCheckbox_z.setOnClickListener(checkerOne);

        mCheckbox_record = (CheckBox) view.findViewById(R.id.value_record);
        mCheckbox_record.setChecked(false);
        mCheckbox_record.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCheckbox_record.isChecked()) {
                    flag = true;
                } else {
                    flag = false;
                }
            }
        });

        if (!isOneShotSensor()) {
            mRadioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);
            mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    // TODO Auto-generated method stub
                    for (int index = 0; index < mRadioSensorDelayRate.size(); index++) {
                        if (mRadioSensorDelayRate.get(index).getId() == checkedId && mSensorDelayRate != index) {
                            mSensorDelayRate = index;
                            setSensorEnable(false, false);
                            setSensorEnable(true, false);
                            Log.d(TAG, "checkedId =" + checkedId + ",mSensorDelayRate:" + mSensorDelayRate);
                        }
                    }
                }
            });

            mRadioSensorDelayRate.add((RadioButton) view.findViewById(R.id.RadioButton1));
            mRadioSensorDelayRate.add((RadioButton) view.findViewById(R.id.RadioButton2));
            mRadioSensorDelayRate.add((RadioButton) view.findViewById(R.id.RadioButton3));
            mRadioSensorDelayRate.add((RadioButton) view.findViewById(R.id.RadioButton4));
            mRadioSensorDelayRate.get(mSensorDelayRate).setChecked(true);
        }

        mDynamicPlotView.setCurveColor(mCheckbox_x.getTextColors().getDefaultColor(),
                mCheckbox_y.getTextColors().getDefaultColor(),
                mCheckbox_z.getTextColors().getDefaultColor());
        updateView();
    }

    public void updateView() {
        if (mIsReceiveSensorValue && mSensorEnabled) {
            mChildNoValueText.setVisibility(View.GONE);
            mStatus.setVisibility(View.VISIBLE);
            mDynamicPlotView.setVisibility(View.VISIBLE);
            mDynamicPlotView.invalidate();
            if (!isOneShotSensor()) {
                mRadioGroup.setVisibility(View.VISIBLE);
            }
            return;
        }
        mChildNoValueText.setVisibility(View.VISIBLE);
        mStatus.setVisibility(View.GONE);
        mDynamicPlotView.setVisibility(View.GONE);
        if (!isOneShotSensor()) {
            mRadioGroup.setVisibility(View.GONE);
        }
    }

    public void updateView(final SensorEvent event) {

        if (mIsReceiveSensorValue && mSensorEnabled) {
            mDynamicPlotView.setCurvePath(event);
            mCheckbox_x.setText(String.valueOf(event.values[0]));
            //mDynamicPlotView.setLinePoint((int)event.values[0], (int)event.values[1]);

            if (event.values.length > 1) {
                mCheckbox_y.setText(String.valueOf(event.values[1]));
            }
            if (event.values.length > 2) {
                mCheckbox_z.setText(String.valueOf(event.values[2]));
            }
            mChildNoValueText.setVisibility(View.GONE);
            mDynamicPlotView.setVisibility(View.VISIBLE);
            mStatus.setVisibility(View.VISIBLE);
            mDynamicPlotView.invalidate();
            if (!isOneShotSensor()) {
                mRadioGroup.setVisibility(View.VISIBLE);
            }
            return;
        }
        mChildNoValueText.setVisibility(View.VISIBLE);
        mDynamicPlotView.setVisibility(View.GONE);
        mStatus.setVisibility(View.GONE);
        if (!isOneShotSensor()) {
            mRadioGroup.setVisibility(View.GONE);
        }

    }

    public void updateView(TriggerEvent event) {
        if (mIsReceiveSensorValue && mSensorEnabled) {
            mDynamicPlotView.setCurvePath(event);
            mCheckbox_x.setText(String.valueOf(event.values[0]));
            if (event.values.length > 1) {
                mCheckbox_y.setText(String.valueOf(event.values[1]));
            }
            if (event.values.length > 2) {
                mCheckbox_z.setText(String.valueOf(event.values[2]));
            }
            mChildNoValueText.setVisibility(View.GONE);
            mStatus.setVisibility(View.VISIBLE);
            mDynamicPlotView.invalidate();
            if (!isOneShotSensor()) {
                mRadioGroup.setVisibility(View.VISIBLE);
            }
            return;
        }
        mChildNoValueText.setVisibility(View.VISIBLE);
        mStatus.setVisibility(View.GONE);
        mDynamicPlotView.setVisibility(View.GONE);
        mRadioGroup.setVisibility(View.GONE);
        if (!isOneShotSensor()) {
            mRadioGroup.setVisibility(View.GONE);
        }
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }

    public boolean isSensorEnable() {
        return mSensorEnabled;
    }

    public void setSensor(Sensor sensor) {
        mSensor = sensor;
        inflateLayout(mContext);
    }

    public Sensor getSensor() {
        return mSensor;
    }

    public int getSensorDelayRate() {
        return mSensorDelayRate;
    }

    public boolean isOneShotSensor() {
        Log.d(TAG, "mSensor:" + mSensor + ",ReportingMode:" + (mSensor != null && mSensor.getReportingMode() == Sensor.REPORTING_MODE_ONE_SHOT));
        return (mSensor != null && mSensor.getReportingMode() == Sensor.REPORTING_MODE_ONE_SHOT);
    }

    private void handleLightSensorEvent(long time, SensorEvent event) {
        Log.d(TAG, "onSensorChanged childNoValueText:" + mChildNoValueText);
        Log.d(TAG, "onSensorChanged checkbox_x:" + mCheckbox_x);
        Log.d(TAG, "onSensorChanged checkbox_y:" + mCheckbox_y);
        Log.d(TAG, "onSensorChanged checkbox_z:" + mCheckbox_z);
        Log.d(TAG, "onSensorChanged event ={" + Arrays.toString(event.values) + "}.");

        mIsReceiveSensorValue = true;
        updateView(event);
    }

    public void writeFileToSD(SensorEvent event) {
        //写入文件
        String SDPATH = Environment.getExternalStorageDirectory().getPath() + "/Android/data/Acom.android.sensortest";
        String fileName = mTitle + ".txt";
        System.out.println("~~~~~~~~~~~~~~~~" + fileName);
        File directory = new File(SDPATH);
        //如果目录不存在，创建目录
        if (!directory.exists() && !directory.isDirectory()) {
            directory.mkdir();
        }
        File file = new File(SDPATH + "//" + fileName);

        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));

            if (mIsReceiveSensorValue && mSensorEnabled) {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                String date = df.format(new Date());
                int pid = Process.myPid();
                bw.write("\n" + date + " " + "pid=" + pid + " " + mTitle + " /x:" + String.valueOf(event.values[0]));

                if (event.values.length > 1 && Math.abs(event.values[1]) > 0.001) {
                    bw.write(" " + " /y:" + String.valueOf(event.values[1]));
                }
                if (event.values.length > 2 && Math.abs(event.values[2]) > 0.001) {
                    bw.write(" " + " /z:" + String.valueOf(event.values[2]));
                }
            }

            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public SensorEventListener getSensorEventListener() {
        return mSensorEventListener;
    }


    public TriggerEventListener getTriggerSensorEventListener() {
        return mTriggerEventListener;
    }

    public void setViewItem(TextView noValueText, LinearLayout status, CheckBox value_x, CheckBox value_y, CheckBox value_z) {
        /*childNoValueText = noValueText;
        mStatus = status;
        checkbox_x = value_x;
        checkbox_y = value_y;
        checkbox_z = value_z;

        Log.d(TAG,"setViewItem:" + childNoValueText
                + ",checkbox_x:" + checkbox_x
                + ",checkbox_y:" + checkbox_y
                + ",checkbox_z:" + checkbox_z);*/
    }

    public void setSensorEnable(boolean enable, boolean updateView) {
        Log.d(TAG, "setSensorEnable ,enable:" + enable + ",mSensorEnabled:" + mSensorEnabled + ",updateView:" + updateView + ",Sensor:" + mSensor);
        boolean result = false;
        if (enable) {
            if (!mSensorEnabled) {
                mSensorEnabled = true;
                if (isOneShotSensor()) {
                    result = mSensorManager.requestTriggerSensor(mTriggerEventListener, mSensor);
                } else {
                    Log.d(TAG, "setSensorEnable ,enable:mSensorEventListener:" + mSensorEventListener + ",mSensor:" + mSensor + ",mSensorDelayRate" + mSensorDelayRate);
                    result = mSensorManager.registerListener(mSensorEventListener, mSensor, mSensorDelayRate);
                }
            }
        } else {
            if (mSensorEnabled) {
                mSensorEnabled = false;
                if (isOneShotSensor()) {
                    result = mSensorManager.cancelTriggerSensor(mTriggerEventListener, mSensor);
                } else {
                    mSensorManager.unregisterListener(mSensorEventListener);
                }
            }
        }

        if (updateView && !mSensorEnabled) {
            mDynamicPlotView.resetView();
            mDynamicPlotView.setCurveColor(mCheckbox_x.getTextColors().getDefaultColor(), mCheckbox_y.getTextColors().getDefaultColor(), mCheckbox_z.getTextColors().getDefaultColor());
            mSensorDelayRate = SensorManager.SENSOR_DELAY_NORMAL;
            if (!isOneShotSensor()) {
                mRadioSensorDelayRate.get(mSensorDelayRate).setChecked(true);
            }
        }
        Log.d(TAG, "setSensorEnable ,enable:result:" + result);
    }
}