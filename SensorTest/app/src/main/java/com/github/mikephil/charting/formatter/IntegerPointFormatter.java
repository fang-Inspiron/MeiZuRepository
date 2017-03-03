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
public class IntegerPointFormatter implements IValueFormatter, IAxisValueFormatter
{
    protected DecimalFormat mFormat;

    public IntegerPointFormatter() {
        mFormat = new DecimalFormat("#");
    }

    /**
     * Allow a custom decimalformat
     *
     * @param format
     */
    public IntegerPointFormatter(DecimalFormat format) {
        this.mFormat = format;
    }

    // IValueFormatter
    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return mFormat.format(value);
    }

    // IAxisValueFormatter
    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return mFormat.format(value);
    }
    
    @Override
    public int getDecimalDigits() {
        //return super.getDecimalDigits();
        return 1;
    }
}


