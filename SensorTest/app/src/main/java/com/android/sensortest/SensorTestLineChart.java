/**
 *
 */
package com.android.sensortest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Iterator;

import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import static android.widget.LinearLayout.LayoutParams;
import android.os.SystemClock;
import android.content.Context;
import android.view.LayoutInflater;
import android.os.Handler;
import android.util.AttributeSet;
import android.os.Message;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.formatter.CoordinatePointsFormatter;

import android.graphics.Color;
import android.graphics.Typeface;

/**
 * @author wangxiaoyong
 *
 */
public class SensorTestLineChart extends LineChart implements OnChartValueSelectedListener{
    private static final String TAG = "SensorTestLineChart";
    private Context mContext;

    private LinearLayout mSensorCtsTestResultLayout;
    private boolean mShowView = false;

    public SensorTestLineChart(Context context) {
        super(context);
        Log.d(TAG, "SensorCtsListItemLineChart create");
        mContext = context;

        inflateLayout(mContext);
        Log.d(TAG, "SensorCtsListItemLineChart init");

    }

    public SensorTestLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SensorTestLineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void inflateLayout(Context context) {
        setBackgroundColor(Color.WHITE);
        // no description text
        getDescription().setEnabled(true);

        // enable touch gestures
        setTouchEnabled(true);

        setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        setDragEnabled(true);
        setScaleEnabled(true);
        setDrawGridBackground(false);
        setHighlightPerDragEnabled(true);
        ViewGroup.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, dip2px(context, 350));
        setLayoutParams(lp);
    }

    private int dip2px(Context context, float dipValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5f);
    }

    public void ShowView() {
        setVisibility(mShowView ? View.VISIBLE : View.GONE);
    }

    public void setLineChartData(LinkedHashMap<Integer,Integer> statisticsDataList, String label, String description) {
        if (statisticsDataList != null && !statisticsDataList.isEmpty()) {
            clear();

            ArrayList<Entry> Values = new ArrayList<Entry>();
            Iterator iter = statisticsDataList.entrySet().iterator();
            int xMax = 0;
            while (iter.hasNext()) { 
                Map.Entry entry = (Map.Entry) iter.next();
                Object key = entry.getKey();
                Object val = entry.getValue();
                Values.add(new Entry( (float)((Integer)key).intValue(), (float)((Integer)val).intValue()));
                if (xMax <= ((Integer)val).intValue()){
                    xMax = ((Integer)val).intValue();
                }
            }


            // setDescription Text
            if (description != null) {
                getDescription().setText(description);
                getDescription().setTextSize(10f);
            } else {
                getDescription().setEnabled(false);
            }
            // get the legend (only possible after setting data)
            Legend l = getLegend();
            l.setEnabled(true);
            l.setPosition(LegendPosition.RIGHT_OF_CHART_INSIDE);

            // create a dataset and give it a type
            LineDataSet set1 = new LineDataSet(Values, label);
            set1.setAxisDependency(AxisDependency.LEFT);
            set1.setColor(ColorTemplate.getHoloBlue());
            set1.setValueTextColor(ColorTemplate.getHoloBlue());
            set1.setLineWidth(1.5f);
            set1.setDrawCircles(false);
            set1.setCircleSize(2.0f);
            set1.setDrawValues(true);
            //set1.setFillAlpha(65);
            set1.setFillColor(ColorTemplate.getHoloBlue());
            set1.setCircleColors(ColorTemplate.getHoloBlue());
            set1.setHighLightColor(Color.rgb(244, 117, 117));
            set1.setCircleSize(5f);
            set1.setDrawCircleHole(true);

            XAxis xAxis = getXAxis();
            xAxis.setEnabled(true);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            //xAxis.setTypeface(Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf"));
            xAxis.setTextSize(8f);
            xAxis.setTextColor(Color.BLACK);
            xAxis.setDrawAxisLine(true);
            xAxis.setDrawGridLines(true);
            xAxis.setCenterAxisLabels(true);
            xAxis.setAvoidFirstLastClipping(true);
            //xAxis.setAxisLineWidth(10f); //设置X轴宽度
            //xAxis.setGranularity(1f); // one hour

            YAxis leftAxis = getAxisLeft();
            leftAxis.setEnabled(true);
            leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
            //leftAxis.setTypeface(Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf"));
            leftAxis.setTextColor(ColorTemplate.getHoloBlue());
            leftAxis.setDrawGridLines(true);
            leftAxis.setGranularityEnabled(true);
            leftAxis.setAxisMinimum(0f);
            leftAxis.setTextSize(10f);
            leftAxis.setAxisMaximum(xMax + Math.round(xMax * 0.2));
            //leftAxis.setYOffset(9f);
            leftAxis.setTextColor(ColorTemplate.getHoloBlue());
            leftAxis.setDrawAxisLine(true);

            LineData data = new LineData(set1);
            data.setValueTextColor(ColorTemplate.getHoloBlue());
            data.setValueTextSize(5f);
            data.setHighlightEnabled(true);
            data.setValueFormatter(new CoordinatePointsFormatter());
            data.setDrawValues(true);

            setData(data);

            mShowView = true;

            YAxis rightAxis = getAxisRight();
            rightAxis.setEnabled(false);
            setOnChartValueSelectedListener(this);
        }

        //updateView();
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());

        centerViewToAnimated(e.getX(), e.getY(), getData().getDataSetByIndex(h.getDataSetIndex())
                .getAxisDependency(), 500);
        //mChart.zoomAndCenterAnimated(2.5f, 2.5f, e.getX(), e.getY(), mChart.getData().getDataSetByIndex(dataSetIndex)
        // .getAxisDependency(), 1000);
        //mChart.zoomAndCenterAnimated(1.8f, 1.8f, e.getX(), e.getY(), mChart.getData().getDataSetByIndex(dataSetIndex)
        // .getAxisDependency(), 1000);
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }
}

