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
import android.widget.TableLayout;
import android.widget.TableRow;

import static android.widget.LinearLayout.LayoutParams;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;  
import android.widget.EditText;
import android.widget.TextView;

import android.graphics.Color;

/**
 * @author wangxiaoyong
 *
 */
public class SensorTestTable extends LinearLayout{
    private static final String TAG = "SensorTestTable";
    private Context mContext;

    private View mLayoutView;
    private TextView mTable1NameTextView;
    private TextView mTable2NameTextView;

    private TableLayout mTableLayout1;
    private TableLayout mTableLayout2;
    private View mTableItem;
    private boolean mShowView = false;

    public SensorTestTable(Context context) {
        super(context);
        Log.d(TAG, "SensorTestTable create");
        mContext = context;

        inflateLayout(mContext);
        Log.d(TAG, "SensorTestTable init");
    }

    public SensorTestTable(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SensorTestTable(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void inflateLayout(Context context) {
        LayoutInflater layoutInflater= (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayoutView = layoutInflater.inflate(R.layout.table, this, true);

        mTable1NameTextView = (TextView)mLayoutView.findViewById(R.id.table1name);
        mTable2NameTextView = (TextView)mLayoutView.findViewById(R.id.table2name);

        mTableLayout1 = (TableLayout)mLayoutView.findViewById(R.id.table1);
        mTableLayout1.setStretchAllColumns(true);
        mTableLayout2 = (TableLayout)mLayoutView.findViewById(R.id.table2);
        mTableLayout2.setStretchAllColumns(true);

        //mTableItem = layoutInflater.inflate(R.layout.tableitem, mTableLayout, true);
    }

    private int dip2px(Context context, float dipValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5f);
    }

    public void ShowView() {
        setVisibility(mShowView ? View.VISIBLE : View.GONE);
    }

    private void setTableData(TableLayout tablelayout, int[] datax, int[] datay, TextView textView, String textViewName) {
        if (datax != null && datay != null) {
            TableRow tableRow = new TableRow(mContext);
            tableRow.setBackgroundColor(R.color.gray);
            TextView tv1 = new TextView(mContext);
            tv1.setText("0");
            tv1.setTextColor(R.color.black);
            tv1.setBackgroundResource(R.drawable.shapee);
            TextView tv2 = new TextView(mContext);
            tv2.setText(String.valueOf(datay[0]));
            tv2.setTextColor(R.color.black);
            tv2.setBackgroundResource(R.drawable.shapee);

            tableRow.addView(tv1);
            tableRow.addView(tv2);

            tablelayout.addView(tableRow, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            for(int row = 1; row < datay.length; row++) {
                TableRow tableRows = new TableRow(mContext);
                tableRows.setBackgroundColor(R.color.gray);

                TextView tv1s = new TextView(mContext);
                tv1s.setText(String.valueOf(datax[row-1]));
                tv1s.setTextColor(R.color.black);
                tv1s.setBackgroundResource(R.drawable.shapee);

                TextView tv2s = new TextView(mContext);
                tv2s.setText(String.valueOf(datay[row]));
                tv2s.setTextColor(R.color.black);
                tv2s.setBackgroundResource(R.drawable.shapee);

                tableRows.addView(tv1s);
                tableRows.addView(tv2s);
                tablelayout.addView(tableRows, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            }
            tablelayout.setVisibility(View.VISIBLE);
            if (textViewName!=null) {
                textView.setText(textViewName);
                textView.setVisibility(View.VISIBLE);
            }
        } else {
            tablelayout.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
        }
    }

    public void setTableData(int[] datax, int[] datay, String table1name, int[] dataxx, int[] datayy, String table2name) {
        setTableData(mTableLayout1, datax, datay, mTable1NameTextView, table1name);
        setTableData(mTableLayout2, dataxx, datayy, mTable2NameTextView, table2name);
    }
}
