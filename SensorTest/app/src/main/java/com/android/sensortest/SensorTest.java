package com.android.sensortest;

import java.util.Locale;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.util.Log;

public class SensorTest extends Activity implements ActionBar.TabListener {
    private static final String TAG = "SensorTest";
    private static final int TAB_NUM = 2;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
     * derivative, which will keep every loaded fragment in memory. If this
     * becomes too memory intensive, it may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;
    PlaceholderFragment mPlaceholderFragment;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    private int mTabSelectedItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_test);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
        mPlaceholderFragment =  new PlaceholderFragment();

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager
                .setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        actionBar.setSelectedNavigationItem(position);
                    }
                });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(actionBar.newTab()
                    .setText(mSectionsPagerAdapter.getPageTitle(i))
                    .setTabListener(this));
        }

        mTabSelectedItem = 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sensor_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab,
                              FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        int position = tab.getPosition();
        Fragment fragment = mSectionsPagerAdapter.getItem(position);

        mViewPager.setCurrentItem(position);
        mTabSelectedItem = position;
        Log.d(TAG, "onTabSelected tab.getPosition():" + tab.getPosition()
                + ",fragment:"+fragment + ",fragment.getView():" + fragment.getView());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab,
                                FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab,
                                FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        Fragment mFragment[];
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            mFragment = new Fragment[TAB_NUM];
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class
            // below).
            Log.d(TAG, "SectionsPagerAdapter getItem:"+position + ",mFragment[position]:"+mFragment[position]);
            if (mFragment[position] == null) {
                mFragment[position] = mPlaceholderFragment.newInstance(position + 1);//PlaceholderFragment.newInstance(position + 1);
            }
            return mFragment[position];
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return TAB_NUM;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private final String ARG_SECTION_NUMBER = "section_number";

        //SensorList
        private ExpandableListView listView;
        private SensorListExpandableListAdapter SensorListAdapter;

        //SensorCTS
//        private ExpandableListView SensorCtsListView;
//        private SensorCtsExpandableListAdapter SensorCtsListAdapter;

        //SensorCheck
        private ExpandableListView SensorCheckListView;
        private SensorCheckListExpandableListAdapter SensorCheckListAdapter;

        View mRootView[] = new View[TAB_NUM];
        /**
         * Returns a new instance of this fragment for the given section number.
         */
        public PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView;
            int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);

            StackTraceElement[] stack = new Throwable().getStackTrace();
            for (StackTraceElement element : stack)
            {
                Log.d(TAG, "   |----" + element.toString());
            }
            switch (sectionNumber) {
                case 1:
                    if (mRootView[0] == null) {
                        mRootView[0] = inflater.inflate(R.layout.fragment_sensor_test_0,
                                container, false);
                        if (listView == null) {
                            listView = (ExpandableListView) mRootView[0].findViewById(R.id.expandableListViewSensorList);
                        }
                        if (SensorListAdapter == null) {
                            SensorListAdapter = new SensorListExpandableListAdapter(getActivity(), listView);
                            listView.setOnGroupCollapseListener(SensorListAdapter.mOnGroupCollapseListener);
                            listView.setOnGroupExpandListener(SensorListAdapter.mOnGroupExpandListener);
                            listView.setAdapter(SensorListAdapter);
                        }
                    }
                    break;
//                case 2:
//                    if (mRootView[1] == null) {
//                        mRootView[1] = inflater.inflate(R.layout.fragment_sensor_test_1,
//                                container, false);
//                        if (SensorCtsListView == null) {
//                            SensorCtsListView = (ExpandableListView) mRootView[1].findViewById(R.id.expandableListViewSensorCts);
//                        }
//                        if (SensorCtsListAdapter == null) {
//                            SensorCtsListAdapter = new SensorCtsExpandableListAdapter(getActivity(), SensorCtsListView);
//                            SensorCtsListView.setAdapter(SensorCtsListAdapter);
//                        }
//                    }
//                    break;
                case 2:
                /*
                if (mRootView[2] == null) {
                    mRootView[2] = inflater.inflate(R.layout.fragment_sensor_test_2,
                        container, false);
                }*/
                    if (mRootView[1] == null) {
                        mRootView[1] = inflater.inflate(R.layout.fragment_sensor_test_2,
                                container, false);
                        if (SensorCheckListView == null) {
                            SensorCheckListView = (ExpandableListView) mRootView[1].findViewById(R.id.expandableCheckListViewSensorCts);
                        }
                        if (SensorCheckListAdapter == null) {
                            SensorCheckListAdapter = new SensorCheckListExpandableListAdapter(getActivity(), SensorCheckListView);
                            SensorCheckListView.setAdapter(SensorCheckListAdapter);
                        }
                    }
                    break;
                default:
                    if (mRootView[0] == null) {
                        mRootView[0] = inflater.inflate(R.layout.fragment_sensor_test_0,
                                container, false);
                        if (listView == null) {
                            listView = (ExpandableListView) mRootView[0].findViewById(R.id.expandableListViewSensorList);
                        }
                        if (SensorListAdapter == null) {
                            SensorListAdapter = new SensorListExpandableListAdapter(getActivity(), listView);
                            listView.setOnGroupCollapseListener(SensorListAdapter.mOnGroupCollapseListener);
                            listView.setOnGroupExpandListener(SensorListAdapter.mOnGroupExpandListener);
                            listView.setAdapter(SensorListAdapter);
                        }
                    }
                    break;
            }
            Log.d(TAG, "onCreateView sectionNumber:"+sectionNumber);
            Log.d(TAG, "onCreateView listView:"+listView);
            Log.d(TAG, "onCreateView mRootView[0]:"+mRootView[0]+"mRootView[1]:"+mRootView[1]);
            return mRootView[sectionNumber-1];
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Fragment fragment = mSectionsPagerAdapter.getItem(mTabSelectedItem);

        mViewPager.setCurrentItem(mTabSelectedItem);
        Log.d(TAG, "onResume mTabSelectedItem:"+mTabSelectedItem);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}