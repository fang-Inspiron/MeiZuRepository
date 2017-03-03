/**
 * 
 */
package com.android.sensortest;

/**
 * @author wangxiaoyong
 *
 */
public class SensorListItemGroup implements SensorTestGroup {
    private String mTitle;

    public SensorListItemGroup() {
        
    }

    public void setGroupTitle(String title){
        mTitle = title;
    }

    public String getGroupTitle() {
        return mTitle;
    }
}
