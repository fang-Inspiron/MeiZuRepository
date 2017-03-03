package com.android.sensortest;

import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Rect;
import android.hardware.SensorEvent;
import android.hardware.TriggerEvent;
import android.util.AttributeSet;
import android.util.Log;

public class DynamicLineChartView extends View{
    private static final String TAG = "DynamicLineChartView";
    private static final int EVENT_REMOVE_HORIZON_THRESHOLD = 100;
    private static final int EVENT_VALUE_HORIZON = 10000;

    private final static String X_KEY = "Xpos";
    private final static String Y_KEY = "Ypos";

    private Context mContext;

    private Paint mPaint = new Paint();
    private List<Map<String, Integer>> mListPoint = new ArrayList<Map<String,Integer>>();

    private float mXpositionMax;
    private String mRateString;
    private Paint mTextPaint = new Paint();
    private Paint mLinePaint = new Paint();
    private Paint mCurvePaint = new Paint();
    private static float mScale;
    private int mTextSize = 16;
    private CurvePath[] mCurvePath = { new CurvePath(), new CurvePath(), new CurvePath()};
    private double mInterval;
    private long mLastTime = 0L;
    private double mRate;

    public DynamicLineChartView(Context context) {
        super(context);
        init(context);
    }

    public DynamicLineChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DynamicLineChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        if (mContext == null) {
            mContext = context;
            mScale = mContext.getResources().getDisplayMetrics().density;
            mTextSize = getTextScale(8);
            mTextPaint.setStrokeWidth(1.0F);
            mTextPaint.setStyle(Paint.Style.STROKE);
            mTextPaint.setTextSize(mTextSize);
            mTextPaint.setAntiAlias(true);

            mCurvePaint.setStrokeWidth(1.5F);
            mCurvePaint.setStyle(Paint.Style.STROKE);
            mCurvePaint.setAntiAlias(true);
        }
    }

    private static int getTextScale(int textNum)
    {
      return (int)(textNum * mScale + 0.5f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        canvas.drawARGB(255, 255, 255, 255);

        Rect localRect = canvas.getClipBounds();

        if ((localRect.width() == 0) || (localRect.height() == 0) || mRateString == null) {
            return;
        }

        for(CurvePath path: mCurvePath) {
            path.setClipBoundsRect(localRect);
        }
        canvas.translate(0.0F, localRect.height() / 2);
        Log.d(TAG,"localRect:"+localRect+",canvas.getClipBounds:" + canvas.getClipBounds());
        mLinePaint.setColor(R.color.gray);

        CurvePath.drawAxis(canvas, mLinePaint, mXpositionMax, mTextPaint, mRateString);

        for(CurvePath path: mCurvePath){
            path.drawLineChart(canvas, mCurvePaint, mXpositionMax);
        }

        super.onDraw(canvas);
    }

    public void setCurvePath(SensorEvent event){
        int index = 0;
        if (mLastTime == 0L) {
            mInterval = 0.0D;
            mLastTime = event.timestamp;
            mRate = 0.0D;
        } else {
            mInterval = event.timestamp - mLastTime;
            mLastTime = event.timestamp;
            if (mInterval != 0D) {
                mRate = 1000000000.0D / mInterval;
            }
        }

        while(event!=null && event.values.length >0 && index < mCurvePath.length && index < event.values.length) {
            mCurvePath[index].setLinePoint(-event.values[index]);
            mXpositionMax = Math.max(mCurvePath[index].getEventRawMax() , mXpositionMax);
            Log.d(TAG,"setCurvePath mXpositionMax:"+mXpositionMax+",index:"+index);
            index++;
        }

        mRateString = (int)mRate + "hz/"+(int)(mInterval / 1000000.0D) +"ms";
        Log.d(TAG,"mRateString  event:{"+event.timestamp+","+ Arrays.toString(event.values)+"}"+",mRateString:"+mRateString+",mRate:"+mRate
                +",mInterval:"+mInterval
                +",mInterval / 1000000.0D:"+(mInterval / 1000000.0D));
    }

    public void setCurvePath(TriggerEvent event){
        int index = 0;
        if (mLastTime == 0L) {
            mInterval = 0.0D;
            mLastTime = event.timestamp;
            mRate = 0.0D;
        } else {
            mInterval = event.timestamp - mLastTime;
            if (mInterval != 0D) {
                mRate = 1000000000.0D / mInterval;
            }
        }

        while(event!=null && event.values.length >0 && index < mCurvePath.length && index < event.values.length) {
            mCurvePath[index].setLinePoint(-event.values[index]);
            mXpositionMax = Math.max(mCurvePath[index].getEventRawMax() , mXpositionMax);
            index++;
        }

        mRateString = (int)mRate + "hz/"+(int)(mInterval / 1000000.0D) +"ms";
    }

    public void setSelection(boolean x_ischeck, boolean y_ischeck, boolean z_ischeck){
        mCurvePath[0].setSelection(x_ischeck);
        mCurvePath[1].setSelection(y_ischeck);
        mCurvePath[2].setSelection(z_ischeck);
    }

    public void setCurveColor(int x_color, int y_color, int z_color){
        mCurvePath[0].setCurveColor(x_color);
        mCurvePath[1].setCurveColor(y_color);
        mCurvePath[2].setCurveColor(z_color);
    }
    
    public void resetView() {
        mXpositionMax = 0.0f;
        mRateString = null;

        mScale = 0;
        mTextSize = 16;
        mInterval = 0.0D;
        mLastTime = 0L;
        mRate = 0D;
        init(mContext);
        for(CurvePath patch : mCurvePath) {
            patch.CurvePathResume();
        }
    }
}
