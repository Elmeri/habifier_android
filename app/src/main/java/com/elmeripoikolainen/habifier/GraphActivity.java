package com.elmeripoikolainen.habifier;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import java.util.ArrayList;
import java.util.Locale;

import com.echo.holographlibrary.Bar;
import com.echo.holographlibrary.BarGraph;
import com.echo.holographlibrary.Line;
import com.echo.holographlibrary.LineGraph;
import com.echo.holographlibrary.LinePoint;
import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.PieSlice;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class GraphActivity extends FragmentActivity implements
        ActionBar.TabListener{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    //Spinner for day selector
    Spinner spinner;
    ArrayAdapter<CharSequence> adapter;

    private static long[] chronometerTimeArray;
    public final static int PIE_CHART = 0;
    public final static int LINE_GRAPH = 2;
    public final static int BAR_GRAPH = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_activity);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        // Show the Up button in the action bar.
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(
                getSupportFragmentManager());

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
        //Added
        Intent intent = getIntent();
        chronometerTimeArray = intent.getLongArrayExtra(HabifierActivity.EXTRA_MESSAGE);

/*        spinner = (Spinner) findViewById(R.id.days_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        adapter = ArrayAdapter.createFromResource(this,
                R.array.days_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.graph, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab,
                              FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
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

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a DummySectionFragment (defined as a static inner class
            // below) with the page number as its lone argument.
            Fragment fragment;
            Bundle args;
            switch (position){
                case PIE_CHART:
                    fragment = new DummySectionFragment();
                    args = new Bundle();
                    args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
                    fragment.setArguments(args);
                    return fragment;
//                case LINE_GRAPH:
//                    fragment = new LineSectionFragment();
//                    args = new Bundle();
//                    args.putInt(LineSectionFragment.ARG_SECTION_NUMBER, position + 1);
//                    fragment.setArguments(args);
//                    return fragment;
                case BAR_GRAPH:
                    fragment = new BarSectionFragment();
                    args = new Bundle();
                    args.putInt(BarSectionFragment.ARG_SECTION_NUMBER, position + 1);
                    fragment.setArguments(args);
                    return fragment;
            }
            fragment = new DummySectionFragment();
            args = new Bundle();
            args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return "Donut Graph".toUpperCase(l);
                case 1:
                    return "Bar Graph".toUpperCase(l);
                case 2:
                    return "Line Graph".toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A dummy fragment representing a section of the app, but that simply
     * displays dummy text.
     */
    public static class DummySectionFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        public static final String ARG_SECTION_NUMBER = "section_number";

        private PieGraph pieGraph;

        public DummySectionFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(
                    R.layout.fragment_graph_activity_pie_graph, container, false);
            TextView dummyTextView = (TextView) rootView
                    .findViewById(R.id.section_label);
            dummyTextView.setText(Integer.toString(getArguments().getInt(
                    ARG_SECTION_NUMBER)));


            pieGraph = (PieGraph) rootView.findViewById(R.id.graph);
            PieSlice slice = new PieSlice();
            slice.setColor(Color.parseColor("#99CC00"));
            slice.setGoalValue(chronometerTimeArray[0]);
            pieGraph.addSlice(slice);
            slice = new PieSlice();
            slice.setColor(Color.parseColor("#FFBB33"));
            slice.setGoalValue(chronometerTimeArray[1]);
            pieGraph.addSlice(slice);
            slice = new PieSlice();
            slice.setColor(Color.parseColor("#AA66CC"));
            slice.setGoalValue(chronometerTimeArray[2]);
            pieGraph.addSlice(slice);
            slice = new PieSlice();
            slice.setColor((getResources().getColor(R.color.blue)));
            slice.setGoalValue(chronometerTimeArray[3]);
            pieGraph.addSlice(slice);
            pieGraph.setInnerCircleRatio(200);
            pieGraph.setPadding(3);

//            for (PieSlice s : pieGraph.getSlices())
//                s.setGoalValue((float)Math.random() * 10);
            pieGraph.setDuration(1000);//default if unspecified is 300 ms
            pieGraph.setInterpolator(new AccelerateDecelerateInterpolator());//default if unspecified is linear; constant speed
            pieGraph.animateToGoalValues();


            return rootView;
        }
    }


    public static class LineSectionFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        public static final String ARG_SECTION_NUMBER = "section_number";

        private LineGraph li;

        public LineSectionFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(
                    R.layout.fragment_graph_activity_line_graph, container, false);
            TextView dummyTextView = (TextView) rootView
                    .findViewById(R.id.section_label);
            dummyTextView.setText(Integer.toString(getArguments().getInt(
                    ARG_SECTION_NUMBER)));

            Line l = new Line();
            LinePoint p = new LinePoint();
            p.setX(0);
            p.setY((int)chronometerTimeArray[0]/1000);
            l.addPoint(p);
            p = new LinePoint();
            p.setX(1);
            p.setY((int)chronometerTimeArray[1]/1000);
            l.addPoint(p);
            p = new LinePoint();
            p.setX(2);
            p.setY((int)chronometerTimeArray[2]/1000);
            l.addPoint(p);
            l.setColor(Color.parseColor("#FFBB33"));

            p = new LinePoint();
            p.setX(3);
            p.setY((int)chronometerTimeArray[3]/1000);
            l.addPoint(p);

            int max = Math.max((int)chronometerTimeArray[0]/1000, Math.max((int)chronometerTimeArray[1]/1000, Math.max((int) chronometerTimeArray[2] / 1000, (int) chronometerTimeArray[3] / 1000)) );
            li = (LineGraph) rootView.findViewById(R.id.graphLine);
            li.addLine(l);
            li.setRangeY(0, max);
            li.setLineToFill(0);

            return rootView;
        }
    }


    public static class BarSectionFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        public static final String ARG_SECTION_NUMBER = "section_number";

        private BarGraph g;

        public BarSectionFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(
                    R.layout.fragment_graph_activity_bar_graph, container, false);
            TextView dummyTextView = (TextView) rootView
                    .findViewById(R.id.section_label);
            dummyTextView.setText(Integer.toString(getArguments().getInt(
                    ARG_SECTION_NUMBER)));


            //TODO Create a format data function for these

            ArrayList<Bar> points = new ArrayList<Bar>();
            Bar d = new Bar();
            d.setColor(Color.parseColor("#99CC00"));
            d.setName("Work");
            d.setGoalValue((int)chronometerTimeArray[0]/1000);
            d.setValueSuffix(" s");
            Bar d2 = new Bar();
            d2.setColor(Color.parseColor("#FFBB33"));
            d2.setName("Study");
            d2.setGoalValue((int)chronometerTimeArray[1]/1000);
            d2.setValueSuffix(" s");
            Bar d3 = new Bar();
            d3.setColor(Color.parseColor("#AA66CC"));
            d3.setName("Leisure");
            d3.setGoalValue((int)chronometerTimeArray[2]/1000);
            d3.setValueSuffix(" s");
            Bar d4 = new Bar();
            d4.setColor(getResources().getColor(R.color.blue));
            d4.setName("Eating");
            d4.setGoalValue((int)chronometerTimeArray[3]/1000);
            d4.setValueSuffix(" s");
/*            Bar d5 = new Bar();
            d5.setColor(Color.parseColor("#AA66CC"));
            d5.setName("Bother");
            d5.setGoalValue(10);
            d5.setValueSuffix(" s");*/
            points.add(d);
            points.add(d2);
            points.add(d3);
            points.add(d4);
            //points.add(d5);

            g = (BarGraph)rootView.findViewById(R.id.graphBar);
            g.setBars(points);

            g.setDuration(1200);//default if unspecified is 300 ms
            g.setInterpolator(new AccelerateDecelerateInterpolator());//Only use over/undershoot  when not inserting/deleting
            g.setValueStringPrecision(1); //1 decimal place. 0 by default for integers.
            g.animateToGoalValues();


            return rootView;
        }
    }
}
