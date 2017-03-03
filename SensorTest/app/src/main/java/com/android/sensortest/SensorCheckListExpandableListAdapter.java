package com.android.sensortest;

import android.app.Activity;
import android.content.Context;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.CheckBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class SensorCheckListExpandableListAdapter extends BaseExpandableListAdapter {
    private static final String TAG = "SensorCheckListExpandableListAdapter";

    private Context mContext;
    private ExpandableListView mListView;
    private SensorManager mSensorManager;

    private List<SensorCheckListItemGroup> mGroups;
    private List<? super SensorCheckListItemChild> mChilds;

    public SensorCheckListExpandableListAdapter(Activity activity, ExpandableListView listView) {
        mContext = activity.getApplicationContext();
        mListView = listView;

        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);

        //init SensorListItemGroup
        init();

        //mSensorListListener.SetSensorListView(mChilds);
    }

    private void init() {
        mGroups = new ArrayList<SensorCheckListItemGroup>();
        mChilds = new ArrayList<SensorCheckListItemChild>();

        //休眠唤醒耗时分析 Waking up
        SensorCheckListItemGroup wakingUpAndGoingtoSleepGroup = new SensorCheckListItemGroup();
        wakingUpAndGoingtoSleepGroup.setGroupTitle(mContext.getResources().getString(R.string.wakingup));
        mGroups.add(wakingUpAndGoingtoSleepGroup);
        WakingUpAndGoingtoSleep wakingUpAndGoingtoSleepController = new WakingUpAndGoingtoSleep(mContext, R.layout.wakeup);
        mChilds.add(wakingUpAndGoingtoSleepController);


        //Power锁状态分析 WakeLock
        SensorCheckListItemGroup wakeLockGroup = new SensorCheckListItemGroup();
        wakeLockGroup.setGroupTitle(mContext.getResources().getString(R.string.wakelockstate));
        mGroups.add(wakeLockGroup);
        WakeLock WakeLockController = new WakeLock(mContext, R.layout.wakelockstate);
        mChilds.add(WakeLockController);

//        //通知灯 LIGHT_ID_NOTIFICATIONS
//        SensorCheckListItemGroup notificationLight = new SensorCheckListItemGroup();
//        notificationLight.setGroupTitle(mContext.getResources().getString(R.string.Notification_Light));
//        mGroups.add(notificationLight);
//        NotificationLight notificationLightController = new NotificationLight(mContext, R.layout.notificationlight);
//        mChilds.add(notificationLightController);
//
//        //激活标致 ActivatePhone
//        SensorCheckListItemGroup activatePhone = new SensorCheckListItemGroup();
//        activatePhone.setGroupTitle(mContext.getResources().getString(R.string.ActivatePhone));
//        mGroups.add(activatePhone);
//        ActivatePhone activatePhoneController = new ActivatePhone(mContext, R.layout.activatephone);
//        mChilds.add(activatePhoneController);
//
//        //光感曲线 AutoBrightnessSpline
//        SensorCheckListItemGroup autoBrightnessSplineGroup = new SensorCheckListItemGroup();
//        autoBrightnessSplineGroup.setGroupTitle(mContext.getResources().getString(R.string.AutoBrightnessSpline));
//        mGroups.add(autoBrightnessSplineGroup);
//        AutoBrightnessSpline AutoBrightnessSplineController = new AutoBrightnessSpline(mContext, R.layout.autobrightnessspline);
//        mChilds.add(AutoBrightnessSplineController);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        Log.d(TAG,"getChild groupPosition:"+groupPosition+",childPosition:"+childPosition);
        return mChilds.get(groupPosition);
    }

    @Override
    public Object getGroup(int groupPosition) {
        Log.d(TAG,"getGroup groupPosition:" + groupPosition + ",Group:"+mGroups.get(groupPosition));
        return mGroups.get(groupPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return  groupPosition * 100 + childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {
        Log.d(TAG,"getChildView groupPosition:" + groupPosition + ",childPosition:" + childPosition + ",isLastChild:"+isLastChild);
        convertView = (View)mChilds.get(groupPosition);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public int getGroupCount() {
        Log.d(TAG,"getGroupCount:" + mGroups.size());
        return mGroups.size();
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
        //mSensorListListener.setSensorEnabled(groupPosition, false);
        ((SensorCheckListItemChild)mChilds.get(groupPosition)).onCollapsed();
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
        //mSensorListListener.setSensorEnabled(groupPosition, true);
        ((SensorCheckListItemChild)mChilds.get(groupPosition)).onExpanded();
    }

    @Override
    public long getGroupId(int groupPosition) {
        Log.d(TAG,"getGroupId groupPosition : " + groupPosition);
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
            View convertView, ViewGroup parent) {
        Log.d(TAG,"getGroupView groupPosition:"+groupPosition+",isExpanded:"+isExpanded+",convertView:"+convertView);
        if (convertView == null) {
            convertView = mListView.inflate(mContext, R.layout.sensor_list_group, null);
        }
        TextView groupName = (TextView)convertView.findViewById(R.id.groupText);
        groupName.setText(mGroups.get(groupPosition).getGroupTitle());
        return convertView;
    }
}
