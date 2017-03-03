/**
 * 
 */
package com.android.sensortest;

/**
 * @author wangxiaoyong
 *
 */
public class SensorCheckListItemGroup implements SensorTestGroup {
    private String mTitle;

    public SensorCheckListItemGroup() {
        
    }

    public void setGroupTitle(String title){
        mTitle = title;
    }

    public String getGroupTitle() {
        return mTitle;
    }
}
