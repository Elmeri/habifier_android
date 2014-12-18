package com.elmeripoikolainen.habifier;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;



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

    //For modifying the graphs
    private static long[] chronometerTimeArray;
    private static String[] chronometerNameArray;
    private static int[] chronometerColorArray;
    private static Date graphMinDate;
    private static Date chosenDate = new Date();

    public final static int PIE_CHART = 0;
    public final static int BAR_GRAPH = 1;
    public final static int LINE_GRAPH = 2;
    public final static long day_in_long =  +  24 * 60 * 60 * 1000L;
    public final static double HOUR_IN_MS = 60*60*1000.0;
    public final static double MINUTE_IN_MS = 60*1000.0;

    public final static int BAR_GRAPH_SECONDS = 0;
    public final static int BAR_GRAPH_MINUTES = 1;
    public final static int BAR_GRAPH_HOURS = 2;

    private HactivityDataSource datasource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_activity);
        setTitle("Graphs");

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
        Bundle extras = intent.getExtras();
        chronometerTimeArray = extras.getLongArray(HabifierActivity.CHRONOMETERARRAY_MESSAGE);
        chronometerNameArray = extras.getStringArray(HabifierActivity.CHRONOMETERNAME_MESSAGE);
        chronometerColorArray = extras.getIntArray(HabifierActivity.CHRONOMETERCOLOR_MESSAGE);

        //Log.d("chronometerTimeArrayLength",  Integer.toString(chronometerTimeArray.length));
        //Log.d("chronometerArrayLenght", Integer.toString(chronometerNameArray.length));


        datasource = new HactivityDataSource(this);
        //datasource.reset();
        datasource.open();

        datasource.getAllHactivities();

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
            case R.id.menuToday:
                //openSearch();
                Date date_now = new Date();
                changeValues(date_now, 1);
                return true;
            case R.id.menuYesterday:
                //openSettings();
                Date date_yesterday = new Date(System.currentTimeMillis() - day_in_long);
                changeValues(date_yesterday, 2);
                return true;
            case R.id.menuSpecifyADay:
                showDatePickerDialog();
                //changeValues(chosenDate, 3);
                //changeValues(3);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //TODO: Change this to more dynamic
    //TODO: No longer crashes if this is dynamic
    public void changeValues(Date day_selected, int choice){

        List<Hactivity> hactivities  = datasource.getAllHactivities();

        List<Hactivity> hactivitiesInDay = datasource.getHactivitiesInDay(day_selected);

        Log.d("Hactivities in day length =",  Integer.toString(hactivitiesInDay.size()));

        //Dynamically updated chronometer array
        chronometerTimeArray = new long[hactivitiesInDay.size()];
        chronometerColorArray = new int[hactivitiesInDay.size()];
        chronometerNameArray = new String[hactivitiesInDay.size()];

        int toastFlag = 1;
        for (int i = 0; i < hactivitiesInDay.size(); i++){
            Hactivity hactivity = hactivitiesInDay.get(i);
            chronometerNameArray[i] = hactivity.getHactivity();
            chronometerTimeArray[i] = hactivity.getTime();
            chronometerColorArray[i] = (int) Long.parseLong(hactivity.getColor(), 16);  //change to hex string
            toastFlag = 0;
        }

        //


//        Log.d("Number of hactivities", Integer.toString(hactivities.size()));
//        if(DateUtils.isToday((new Date()).getTime())){
//            Log.d("Dateutils success", "is today");
//        }
//        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
//        String reportDate = df.format(new Date());
//        Log.d("Item date, Date()", reportDate);
//        String chosenDateofChangeValues = df.format((day_selected));



        //Change this to fetch only the selected day of hactivities


//        for (Hactivity item : hactivities) {
//            int index = Arrays.asList(chronometerNameArray).indexOf(item.getHactivity());
//            reportDate = df.format(item.getDate());
//            //Log.d("Item date", reportDate);
//            //Log.d("Itemdate in time", Long.toString(item.getDate().getTime()/day_in_long));
//            if ( DateUtils.isToday( item.getDate().getTime()) && DateUtils.isToday(day_selected.getTime() ) && choice == 1 ) {
//                Log.d("Item number, today",  Integer.toString((int) item.getId()) );
//                chronometerTimeArray[index] = item.getTime();
//                toastFlag = 0;
//            } else if (DateUtils.isToday(item.getDate().getTime() +  day_in_long) && DateUtils.isToday(day_selected.getTime()  +  day_in_long) && choice == 2 ){
//                Log.d("Item number, yesterday",  Integer.toString((int) item.getId()) );
//                chronometerTimeArray[index] = item.getTime();
//                toastFlag = 0;
//            } //else if (item.getDate().equals(chosenDate) && choice == 3){ // BUG IN EQUALS :-ooooo
//            //else if (item.getDate().getTime()/day_in_long == chosenDate.getTime()/day_in_long && choice == 3){
//            else if (isSameDay(item.getDate(), chosenDate) && choice == 3){
//                Log.d("Item number, chosen Date",  Integer.toString((int) item.getId()) );
//                chronometerTimeArray[index] = item.getTime();
//                Log.d("Item number, getTime value", Integer.toString((int) item.getTime() ));
//                toastFlag = 0;
//            }
//
//        }

        if (toastFlag == 1){
            Context context = getApplicationContext();
            CharSequence text = "Sorry, there is no entry for that day.";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return;
        }

            //if( day == today ){
        //  //get all values days with the selected day
        // } if else ( day == yesterday ) {
        // get all values of days with selected day
        // update all graphs with the selected day
        // }

        changeGraphValues();


        Log.d("Test", "test");
    }

    public boolean isSameDay(Date date1, Date date2){
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();

        c1.setTime(date1);
        c2.setTime(date2);

        int yearDiff = c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR);
        int monthDiff = c1.get(Calendar.MONTH) - c2.get(Calendar.MONTH);
        int dayDiff = c1.get(Calendar.DAY_OF_MONTH) - c2.get(Calendar.DAY_OF_MONTH);
        if (yearDiff == 0 && monthDiff == 0 && dayDiff == 0){
            return true;
        } else {
            return false;
        }


    }

    public void changeGraphValues(){
        PieGraph pieGraph = (PieGraph) findViewById(R.id.graph);

        pieGraph.removeSlices();

        for(int i = 0; i < chronometerTimeArray.length ; i++){
            PieSlice slice = new PieSlice();
            slice.setGoalValue(chronometerTimeArray[i]);
            slice.setColor(chronometerColorArray[i]);
            pieGraph.addSlice(slice);
        }
        pieGraph.setDuration(1000);
        pieGraph.setInterpolator(new AccelerateDecelerateInterpolator());
        pieGraph.animateToGoalValues();
        // TODO: changing values does not run smoothly



        BarGraph g = (BarGraph) findViewById(R.id.graphBar);

        int timeFormat = BAR_GRAPH_SECONDS;
        for (int i = 0; i < chronometerTimeArray.length ; i++){
            if ( chronometerTimeArray[i] > HOUR_IN_MS) { //Over a hour
                timeFormat = BAR_GRAPH_HOURS;
            } else if (chronometerTimeArray[i] > MINUTE_IN_MS ) { //Over a minute
                timeFormat = BAR_GRAPH_MINUTES;
            }
        }

        ArrayList<Bar> points = new ArrayList<Bar>();

        for (int i = 0; i < chronometerTimeArray.length ; i++){
            Bar d = new Bar();
            d.setColor(chronometerColorArray[i]);
            d.setName(chronometerNameArray[i]);
            d = formatBar(d, i, timeFormat);
            points.add(d);
        }

/*        Bar d = new Bar();
        d.setColor(Color.parseColor("#99CC00"));
        d.setName("Work");
        d = formatBar(d,0,timeFormat);

        Bar d2 = new Bar();
        d2.setColor(Color.parseColor("#FFBB33"));
        d2.setName("Study");
        d2 = formatBar(d2,1,timeFormat);

        Bar d3 = new Bar();
        d3.setColor(Color.parseColor("#AA66CC"));
        d3.setName("Leisure");
        d3 = formatBar(d3,2,timeFormat);

        Bar d4 = new Bar();
        d4.setColor(getResources().getColor(R.color.blue));
        d4.setName("Eating");
        d4 = formatBar(d4,3,timeFormat);

*//*            Bar d5 = new Bar();
            d5.setColor(Color.parseColor("#AA66CC"));
            d5.setName("Bother");
            d5.setGoalValue(10);
            d5.setValueSuffix(" s");*//*
        points.add(d);
        points.add(d2);
        points.add(d3);
        points.add(d4);
        //points.add(d5);*/


        g.setBars(points);

        g.setDuration(1200);//default if unspecified is 300 ms
        g.setInterpolator(new AccelerateDecelerateInterpolator());//Only use over/undershoot  when not inserting/deleting
        g.setValueStringPrecision(1); //1 decimal place. 0 by default for integers.
        g.animateToGoalValues();

    }

    public Bar formatBar(Bar bar, int index, int timeFormat){
        if(timeFormat == BAR_GRAPH_SECONDS){
            bar.setGoalValue((int)chronometerTimeArray[index]/1000);
            bar.setValueSuffix(" s");
        } else if (timeFormat == BAR_GRAPH_MINUTES){
            bar.setGoalValue((int)(chronometerTimeArray[index]/MINUTE_IN_MS));
            int temp = ((int)chronometerTimeArray[index]/1000)% ((int)60);
            // use % to show remaining value in the suffix
            bar.setValueSuffix(" m " + Integer.toString(temp) + " s");

        } else if (timeFormat == BAR_GRAPH_HOURS){
            bar.setGoalValue((int)(chronometerTimeArray[index]/HOUR_IN_MS));
            int temp = ((int)chronometerTimeArray[index]/(1000*60))% ((int)60);
            //  use % to show remaining value in the suffix
            bar.setValueSuffix(" h " + Integer.toString(temp) + " m");
        }
        return bar;
    }

    public void showDatePickerDialog() { //Should have View v as argument
        graphMinDate = getHactivitiesMinDate();
        DatePickerFragment newFragment = new DatePickerFragment();

        newFragment.setCallBack(ondate);

        //newFragment.setC


        //newFragment.show(getSupportFragmentManager(), "datePicker");
        newFragment.show(getFragmentManager(), "datePicker");

    }

    OnDateSetListener ondate = new OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month,
                              int day) {
            Calendar calendar = Calendar.getInstance();
            calendar.clear();
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            chosenDate = calendar.getTime();
            // Do something with the date chosen by the user
            changeValues(chosenDate, 3);
        }
    };

    public Date getHactivitiesMinDate(){
        List<Hactivity> hactivities  = datasource.getAllHactivities();
        Date min_date = new Date();
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String deportRate = df.format(min_date);
        Log.d("Item date, Date()", deportRate);
        for (Hactivity item : hactivities) {
            String reportDate = df.format(item.getDate());
            Log.d("Item date, Date()", reportDate);
            Log.d("Comparing minimum date", "testing");
            if(item.getDate().before(min_date)){
                min_date = item.getDate();
                Log.d("Comparing minimum date,", "success, day is before the date");
            }
        }
        String reportDate = df.format(min_date);
        Log.d("Item date, Date()", reportDate);
        return min_date;
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
                    return "Pie Graph".toUpperCase(l);
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
//            dummyTextView.setText(Integer.toString(getArguments().getInt(
//                    ARG_SECTION_NUMBER)));

            pieGraph = (PieGraph) rootView.findViewById(R.id.graph);

            setValues();

            return rootView;
        }


        public void setValues(){
            for(int i = 0; i < chronometerTimeArray.length; i++){
                PieSlice slice = new PieSlice();
                slice.setColor(chronometerColorArray[i]);
                slice.setGoalValue(chronometerTimeArray[i]);
                pieGraph.addSlice(slice);
            }


            //PieSlice slice = new PieSlice();
/*            slice.setColor(Color.parseColor("#99CC00"));
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
            pieGraph.addSlice(slice);*/

            //pieGraph.setInnerCircleRatio(200);
            pieGraph.setPadding(3);

//            for (PieSlice s : pieGraph.getSlices())
//                s.setGoalValue((float)Math.random() * 10);
            pieGraph.setDuration(1000);//default if unspecified is 300 ms
            pieGraph.setInterpolator(new AccelerateDecelerateInterpolator());//default if unspecified is linear; constant speed
            pieGraph.animateToGoalValues();
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
//            dummyTextView.setText(Integer.toString(getArguments().getInt(
//                    ARG_SECTION_NUMBER)));
            g = (BarGraph)rootView.findViewById(R.id.graphBar);
            setValues();

            return rootView;
        }

        public void setValues(){
            //TODO Create a format data function for these

            int timeFormat = BAR_GRAPH_SECONDS;
            for (int i = 0; i < chronometerTimeArray.length ; i++){
                if (chronometerTimeArray[i] > MINUTE_IN_MS ) { //Over a minute
                    timeFormat = BAR_GRAPH_MINUTES;
                } else if ( chronometerTimeArray[i] > HOUR_IN_MS) { //Over a hour
                    timeFormat = BAR_GRAPH_HOURS;
                }
            }



            ArrayList<Bar> points = new ArrayList<Bar>();
            for (int i = 0; i < chronometerTimeArray.length ; i++){
                Bar d = new Bar();
                d.setColor(chronometerColorArray[i]);
                d.setName(chronometerNameArray[i]);
                d = formatBar(d, i, timeFormat);
                points.add(d);
            }

          /*  Bar d = new Bar();
            d.setColor(Color.parseColor("#99CC00"));
            d.setName("Work");
            d = formatBar(d,0,timeFormat);

            Bar d2 = new Bar();
            d2.setColor(Color.parseColor("#FFBB33"));
            d2.setName("Study");
            d2 = formatBar(d2,1,timeFormat);

            Bar d3 = new Bar();
            d3.setColor(Color.parseColor("#AA66CC"));
            d3.setName("Leisure");
            d3 = formatBar(d3,2,timeFormat);

            Bar d4 = new Bar();
            d4.setColor(getResources().getColor(R.color.blue));
            d4.setName("Eating");
            d4 = formatBar(d4,3,timeFormat);

*//*            Bar d5 = new Bar();
            d5.setColor(Color.parseColor("#AA66CC"));
            d5.setName("Bother");
            d5.setGoalValue(10);
            d5.setValueSuffix(" s");*//*
            points.add(d);
            points.add(d2);
            points.add(d3);
            points.add(d4);
            //points.add(d5);*/


            g.setBars(points);

            g.setDuration(1200);//default if unspecified is 300 ms
            g.setInterpolator(new AccelerateDecelerateInterpolator());//Only use over/undershoot  when not inserting/deleting
            g.setValueStringPrecision(1); //1 decimal place. 0 by default for integers.
            g.animateToGoalValues();
        }

        public Bar formatBar(Bar bar, int index, int timeFormat){
            if(timeFormat == BAR_GRAPH_SECONDS){
                bar.setGoalValue((int)chronometerTimeArray[index]/1000);
                bar.setValueSuffix(" s");
            } else if (timeFormat == BAR_GRAPH_MINUTES){
                bar.setGoalValue((float)(chronometerTimeArray[index]/MINUTE_IN_MS));
                bar.setValueSuffix(" m");
            } else if (timeFormat == BAR_GRAPH_HOURS){
                bar.setGoalValue((float)(chronometerTimeArray[index]/HOUR_IN_MS));
                bar.setValueSuffix(" h");
            }
            return bar;
        }
    }

    public static class DatePickerFragment extends DialogFragment
            //implements DatePickerDialog.OnDateSetListener
        {

        DatePickerDialog.OnDateSetListener onDateSet;


        public void setCallBack(DatePickerDialog.OnDateSetListener ondate) {
            onDateSet = ondate;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), onDateSet, year, month, day);
            dialog.getDatePicker().setMaxDate((new Date()).getTime()); //This might create bugs
            dialog.getDatePicker().setMinDate(graphMinDate.getTime());

            return dialog;
        }

//        public void onDateSet(DatePicker view, int year, int month, int day) {
//            Calendar calendar = Calendar.getInstance();
//            calendar.clear();
//            calendar.set(Calendar.MONTH, month);
//            calendar.set(Calendar.YEAR, year);
//            calendar.set(Calendar.DAY_OF_MONTH, day);
//            chosenDate = calendar.getTime();
//            // Do something with the date chosen by the user
//
//        }
    }
}
