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


public class SensorListExpandableListAdapter extends BaseExpandableListAdapter {

    private static final String TAG = "SensorListExpandableListAdapter";

    public SensorListOnGroupCollapseListener mOnGroupCollapseListener;
    public SensorListOnGroupExpandListener mOnGroupExpandListener;
    private SensorListListener mSensorListListener;
    private List<SensorListItemGroup> mGroups;
    private List<SensorListItemChild> mChilds;
    private ExpandableListView mListView;
    private Context mContext;
    private SensorManager mSensorManager;

    public SensorListExpandableListAdapter(Activity activity, ExpandableListView listView) {
        mContext = activity.getApplicationContext();
        mListView = listView;
        mOnGroupCollapseListener = new SensorListOnGroupCollapseListener();
        mOnGroupExpandListener = new SensorListOnGroupExpandListener();
        
        mSensorListListener = new SensorListListener(mContext);
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        //init SensorListItemGroup
        initSensorList();

        mSensorListListener.SetSensorListView(mChilds);
    }

    private void initSensorList() {
        mGroups = new ArrayList<SensorListItemGroup>();
        mChilds = new ArrayList<SensorListItemChild>();

        for (int index = 0; index < mSensorListListener.getSensorListNum(); index++ ) {
            SensorListItemGroup group = new SensorListItemGroup();
            group.setGroupTitle(mSensorListListener.getSensorName(index));
            Log.d(TAG,"initSensorList : index:"+index+",Title:"+mSensorListListener.getSensorName(index));
            mGroups.add(group);

            SensorListItemChild child = new SensorListItemChild(mContext, mSensorListListener.getSensorName(index),mSensorManager);
            mChilds.add(child);
        }
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        Log.d(TAG,"getChild groupPosition:"+groupPosition+",childPosition:"+childPosition);
        return mChilds.get(groupPosition);
    }

    @Override
    public Object getGroup(int groupPosition) {
        Object a = new Object();
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
        /*if (convertView == null) {
            convertView = mListView.inflate(mContext, R.layout.sensor_list_child, null);
        }

        TextView childText = (TextView)convertView.findViewById(R.id.childNoValueYetText);
        CheckBox checkbox_x = (CheckBox)convertView.findViewById(R.id.value_x);
        CheckBox checkbox_y = (CheckBox)convertView.findViewById(R.id.value_y);
        CheckBox checkbox_z = (CheckBox)convertView.findViewById(R.id.value_z);
        LinearLayout linearLayout_status = (LinearLayout)convertView.findViewById(R.id.status);
        Log.d(TAG,"getChildView groupPosition:" + groupPosition + ",childPosition:" + childPosition + ",isLastChild:"+isLastChild);
        Log.d(TAG,"getChildView childText:" + childText);
        Log.d(TAG,"getChildView checkbox_x:" + checkbox_x);
        Log.d(TAG,"getChildView checkbox_y:" + checkbox_y);
        Log.d(TAG,"getChildView checkbox_z:" + checkbox_z);
        Log.d(TAG,"getChildView mChilds.get(groupPosition):" + mChilds.get(groupPosition));

        if (mChilds.get(groupPosition) != null) {
            mChilds.get(groupPosition).setViewItem(childText, linearLayout_status, checkbox_x, checkbox_y, checkbox_z);
        }*/
        convertView = mChilds.get(groupPosition);
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
        Log.d(TAG,"getGroupCount:" + mSensorListListener.getSensorListNum());
        return mSensorListListener.getSensorListNum();
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
        mSensorListListener.setSensorEnabled(groupPosition, false);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
        mSensorListListener.setSensorEnabled(groupPosition, true);
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

    public class SensorListOnGroupCollapseListener implements
            ExpandableListView.OnGroupCollapseListener {
        private boolean mCalledOnGroupCollapse = false;

        public void onGroupCollapse(int groupPosition) {
            mCalledOnGroupCollapse = true;
        }

        public boolean hasCalledOnGroupCollapse() {
            return mCalledOnGroupCollapse;
        }

        public void reset() {
            mCalledOnGroupCollapse = false;
        }
    }

    public class SensorListOnGroupExpandListener implements
            ExpandableListView.OnGroupExpandListener {
        private boolean mCalledOnGroupCollapse = false;

        public void onGroupExpand(int groupPosition) {
            mCalledOnGroupCollapse = true;
        }

        public boolean hasCalledOnGroupCollapse() {
            return mCalledOnGroupCollapse;
        }

        public void reset() {
            mCalledOnGroupCollapse = false;
        }
    }
}
