
package com.github.mikephil.charting.formatter;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.ViewPortHandler;
import android.util.Log;
import java.text.DecimalFormat;

/**
 * This IValueFormatter is just for convenience and simply puts a "%" sign after
 * each value. (Recommeded for PieChart)
 *
 * @author Philipp Jahoda
 */
public class CoordinatePointsFormatter implements IValueFormatter
{
    private static final String TAG = "CoordinatePointsFormatter";
    protected DecimalFormat mFormat;
    private int mDataSetIndex = 0;

    public CoordinatePointsFormatter() {
        mFormat = new DecimalFormat("#");
    }

    /**
     * Allow a custom decimalformat
     *
     * @param format
     */
    public CoordinatePointsFormatter(DecimalFormat format) {
        this.mFormat = format;
    }

    // IValueFormatter
    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
       	float x = entry.getX();
        float y = entry.getY();
       
        if (viewPortHandler !=null && mDataSetIndex%2 == 0) {
            viewPortHandler.setDragOffsetX(22f);
        } else {
            viewPortHandler.setDragOffsetY(22f);
        }
        mDataSetIndex++;
        Log.d(TAG,"getFormattedValue value:"+value+",entry:"+entry+",dataSetIndex:"+dataSetIndex+",viewPortHandler:"+viewPortHandler+",mDataSetIndex:"+mDataSetIndex);
        return ("("+ mFormat.format(x) +", "+ mFormat.format(y)+")");
    }
}
