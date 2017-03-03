package com.android.sensortest;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.Log;

public class CurvePath {
    private static final String TAG = "CurvePath";
    private static final int EVENT_REMOVE_HORIZON_THRESHOLD = 100;

    private Path mPath = new Path();
    private Path mBackupPath = new Path();
    private int mCounter = 0;
    private float mEventRawMin = Float.MAX_VALUE;
    private float mEventRawMax = Float.MIN_VALUE;
    private float mBackupEventRawMin = Float.MAX_VALUE;
    private float mBackupEventRawMax = Float.MIN_VALUE;
    private float mLastEventRawValue = 0.0F;
    private boolean mSelection = true;
    private boolean mHasPathView = false;
    private int mCurveColor = Color.RED;
    private Rect mCanvasClipBoundsRect  = new Rect();

    public void CurvePathResume(){
        mPath.reset();
        mBackupPath.reset();
        mCounter = 0;
        mEventRawMin = Float.MAX_VALUE;
        mEventRawMax = -Float.MAX_VALUE;
        mBackupEventRawMin = Float.MAX_VALUE;
        mBackupEventRawMax = -Float.MAX_VALUE;
        mLastEventRawValue = 0.0F;
        mSelection = true;
        mHasPathView = false;
        mCurveColor = Color.RED;
    }

    public static void drawAxis(Canvas canvas, Paint paintLine, float paramFloat, Paint paintText, String paramString)
    {
        Rect localRect = canvas.getClipBounds();
        float f1 = localRect.right - localRect.left;
        float f2 = (localRect.bottom - localRect.top) / 2;
        float f3 = paintText.getTextSize();
        paintText.setColor(Color.GRAY);
        paintText.setTextAlign(Paint.Align.LEFT);
        paintText.setAntiAlias(true);
        canvas.drawText(String.valueOf(Math.round(paramFloat)), 1.0F, 1.0F + f3 - f2, paintText);
        canvas.drawText("-" + (Math.round(paramFloat)), 1.0F, f2 - 1.0F, paintText);
        canvas.drawText("0", 1.0F, f3 + 1.0F, paintText);
        paintText.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(paramString, f1 / 2.0F, f2 - 1.0F, paintText);


        /*
        int j = (int)Math.pow(10.0D, String.valueOf((int)Math.abs(paramFloat)).length() - 1);
        int i = 1;

        while (true) {
            if (i >= paramFloat / j) {
                paintLine.setStrokeWidth(2.0F);
                paintLine.setColor(Color.BLACK);
                canvas.drawLine(0.0F, 0.0F, f1, 0.0F, paintLine);
                Log.d(TAG,"return drawAxis j:"+j+",i:"+i+",localRect:"+localRect+",f1:"+f1+",f2:"+f2+",f3:"+f3);
                return;
            }
            int k = (int)(j * f2 * i / paramFloat);
            paintLine.setColor(Color.GRAY);
            canvas.drawLine(0.0F, k, f1, k, paintLine);
            canvas.drawLine(0.0F, -k, f1, -k, paintLine);
            Log.d(TAG,"drawAxis j:"+j+",i:"+i+",k:"+k+",localRect:"+localRect+",f1:"+f1+",f2:"+f2+",f3:"+f3);
            i += 1;
        }
        */

        paintLine.setStyle(Paint.Style.STROKE);
        paintLine.setAntiAlias(true);
        paintLine.setStrokeWidth(2.0F);
        paintLine.setColor(Color.BLACK);
        canvas.drawLine(0.0F, 0.0F, f1, 0.0F, paintLine);

        paintLine.setStrokeWidth(1.0F);
        paintLine.setColor(Color.GRAY);

        for(int i= 1; i<=3; i++){
            int k = i * ((int)(f2/3));
            canvas.drawLine(0.0F, k, f1, k, paintLine);
            canvas.drawLine(0.0F, -k, f1, -k, paintLine);
            Log.d(TAG,"drawAxis i:"+i+",k:"+k+",localRect:"+localRect+",f1:"+f1+",f2:"+f2+",f3:"+f3);
        }
    }

    public void drawLineChart(Canvas canvas, Paint paintCurve, float maxValue)
    {
        if (mSelection && mHasPathView) {

              try {
                Object localObject = canvas.getClipBounds();
                float f2 = ((Rect)localObject).right - ((Rect)localObject).left;
                float f1 = (((Rect)localObject).bottom - ((Rect)localObject).top) / 2;

                f2 /= 100.0F;
                maxValue = f1 / maxValue;

                Matrix matrix = new Matrix();

                matrix.setScale(f2, maxValue);

                paintCurve.setColor(mCurveColor);

                mPath.transform(matrix);
                mBackupPath.transform(matrix);
                //paintCurve.setColor(Color.RED);
                canvas.translate(-mCounter * f2, 0.0F);
                canvas.drawPath(mBackupPath, paintCurve);

                canvas.translate(100.0F * f2, 0.0F);
                canvas.drawPath(mPath, paintCurve);
                canvas.translate((mCounter - EVENT_REMOVE_HORIZON_THRESHOLD) * f2, 0.0F);
        
                Matrix matrixReset = new Matrix();
                matrix.invert(matrixReset);

                mPath.transform(matrixReset);
                mBackupPath.transform(matrixReset);
                Log.d(TAG,"drawLineChart f2:="+f2+",f1:"+f1+",mCounter:"+mCounter);
              } catch(Exception e){
                  
              }
        }
    }

    /**  
     * @param eventRaw  which x position you want to draw.
     */
    public void setLinePoint(float eventRaw)
    {
        Log.d(TAG,"eventRaw :="+eventRaw +",mCounter:"+mCounter);
        if (mCounter > EVENT_REMOVE_HORIZON_THRESHOLD) {
            mBackupPath = mPath;
            mPath = new Path();
            mCounter = 0;
            mBackupEventRawMin = mEventRawMin;
            mBackupEventRawMax = mEventRawMax;
            mEventRawMin = Float.MAX_VALUE;
            mEventRawMax = -Float.MAX_VALUE;
        }

        if(eventRaw > mEventRawMax) {
            mEventRawMax = eventRaw;
        }
        
        if (eventRaw < mEventRawMin) {
            mEventRawMin = eventRaw;
        }

        if (mCounter == 0) {
            mPath.moveTo(0.0f, eventRaw);
            //mPath.lineTo(mCounter, eventRaw);
            mLastEventRawValue = eventRaw;
        } else {
            mPath.lineTo((float)mCounter, eventRaw);
            //mPath.moveTo(mCounter, eventRaw);
            mLastEventRawValue = eventRaw;
        }
        mCounter += 1;
        Log.d(TAG,"eventRaw 2 :="+eventRaw +",mCounter:"+mCounter +",mEventRawMax:"+mEventRawMax+",mEventRawMin:"+mEventRawMin);
        mHasPathView = true;
    }

    public float getEventRawMax() {
        return Math.max(Math.abs(mEventRawMax), Math.abs(mEventRawMin));
    }

    public void setClipBoundsRect(Rect rect) {
        mCanvasClipBoundsRect.bottom = rect.bottom;
        mCanvasClipBoundsRect.left = rect.left;
        mCanvasClipBoundsRect.right = rect.right;
        mCanvasClipBoundsRect.top = rect.top;
    }
    public void setSelection(boolean selection) {
        mSelection = selection;
    }

    public void setCurveColor(int color) {
        mCurveColor = color;
        Log.d(TAG,"mCurveColor :="+mCurveColor);
    }
}
