package com.elmeripoikolainen.habifier;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.os.Build;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.os.SystemClock;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;

import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.PieSlice;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class HabifierActivity extends Activity {

    public final static String CHRONOMETERARRAY_MESSAGE = "com.elmeripoikolainen.habifier.CHRONOMETERARRAY_MESSAGE";
    public final static String CHRONOMETERNAME_MESSAGE = "com.elmeripoikolainen.habifier.CHRONOMETERNAME_MESSAGE";

    private Chronometer chronometer1;
    private long chronometer1_time;
    private long chronometer2_time;
    private long chronometer3_time;
    private int lastChronometerIndex;
    private Button button_1;
    private Button button_2;
    private Button button_3;
    private Button button_4;
    private Chronometer[] chronometerArray;
    private long[] chronometerTimeArray;
    private String[] chronometerNameArray = {"Work", "Study", "Leisure", "Eat"};
    private List<Hactivity> hactivitiesArray = new ArrayList<Hactivity>();
    private ImageButton[] buttonArray;

    private int chronometerLength = 4;

    private HactivityDataSource datasource;

    //Piechart
    private PieGraph pieGraph;
    private PieSlice[] pieSlices;

    //Periodical task
    private int mInterval = 100; // 5 seconds by default, can be changed later
    private Handler mHandler;

    //Is chronometerrunning
    boolean isChronometerRunning = false;
    boolean isPauseVisible = false;
    private long stoppedTime = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habifier);
        setContentView(R.layout.activity_test);
        chronometerArray = new Chronometer[4];
        chronometerTimeArray = new long[4];
        chronometer1 = ((Chronometer) findViewById(R.id.chronometer1));
        for (int i = 0; i < chronometerLength; i++){
            chronometerTimeArray[i] = 0;
        }

        lastChronometerIndex = -1;
        buttonArray = new ImageButton[3];
        button_1 = ((Button) findViewById(R.id.button_1));
        button_2 = ((Button) findViewById(R.id.button_2));
        button_3 = ((Button) findViewById(R.id.button_3));
        button_4 = ((Button) findViewById(R.id.button_4));

        Date dateNow = new Date();

        datasource = new HactivityDataSource(this);
        //datasource.reset();
        datasource.open();
        //Remove old table (for testing purposes (insert lenny face) ( ͡° ͜ʖ ͡ °))
        datasource.reset(); //TODO Remove this for final version!!

        //TODO when this is fixed, the back button should work as intented
        //TODO Before creating, check if they exist in the database !!!!!!!!!!!!!!!!!!!!!
        for (int i = 0; i < chronometerLength; i++){
            Log.d("Datasource is creating things", "index");
            hactivitiesArray.add(datasource.createHactivity(chronometerNameArray[i]));
        }
        for (int i = 0; i < chronometerLength; i++){
            Log.d("Datasource is creating things", "index");
            datasource.createHactivityDayBefore(chronometerNameArray[i]);
        }

        //Chart
        pieGraph = (PieGraph)findViewById(R.id.graph);
        PieSlice slice = new PieSlice();
        slice.setColor(getResources().getColor(R.color.green));
        //slice.setColor(Color.parseColor("#FFBB33"));
        slice.setValue(1);
        pieGraph.addSlice(slice);
        slice = new PieSlice();
        slice.setColor(getResources().getColor(R.color.yellow));
        slice.setValue(1);
        pieGraph.addSlice(slice);
        slice = new PieSlice();
        slice.setColor(getResources().getColor(R.color.violet));
        slice.setValue(1);
        pieGraph.addSlice(slice);
        slice = new PieSlice();
        slice.setColor(getResources().getColor(R.color.blue));
        slice.setValue(1);
        pieGraph.addSlice(slice);

        datasource = new HactivityDataSource(this);
        datasource.open();

        List<Hactivity> values = datasource.getAllHactivities();

        // use the SimpleCursorAdapter to show the
        // elements in a ListView
        //ArrayAdapter<Hactivity> adapter = new ArrayAdapter<Hactivity>(this,
        //        android.R.layout.simple_list_item_1, values);
        //setListAdapter(adapter);

        //Periodical task
        mHandler = new Handler();
        //startRepeatingTask();

        debugLogHactivities();
        Arrays.asList(chronometerArray).indexOf("test");

    }

    @Override
    protected void onPause(){
        super.onPause();

    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    //Periodical task
    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            updateTimer(); //this function can change value of mInterval.
            mHandler.postDelayed(mStatusChecker, mInterval);
        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.habifier,menu);
        //getMenuInflater().inflate(R.menu.habifier, menu);

        //button_1.getBackground().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0xFFAA0000));
        //button_2.getBackground().setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);
        //button_3.getBackground().setColorFilter(0xAA66CC00, PorterDuff.Mode.MULTIPLY);
        return super.onCreateOptionsMenu(menu);
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

    public void resetButtonClicked(View view) {
        String tagTemp = view.getTag().toString();
        int buttonIndex =  Integer.parseInt(tagTemp.substring(0,1));
        if(lastChronometerIndex == -1){
            lastChronometerIndex = 0;
            updateTimer();
        }
        if(!isChronometerRunning){
            resumeChronometerAction();
        }
        datasource.updateHactivity(chronometerNameArray[lastChronometerIndex], chronometerTimeArray[lastChronometerIndex], (int) hactivitiesArray.get(lastChronometerIndex).getId() );
        resetTimer(buttonIndex);
        datasource.updateHactivity(chronometerNameArray[buttonIndex], chronometerTimeArray[buttonIndex], (int) hactivitiesArray.get(buttonIndex).getId() );
        Log.d("Name, Item, Id: ", chronometerNameArray[buttonIndex] + " "+ Integer.toString((int)chronometerTimeArray[buttonIndex]) + " " +Integer.toString( (int) hactivitiesArray.get(buttonIndex).getId()));
        //Causes the datebase not be updated, buttonIndex is wrong
        //TODO Figure out a way to do it ( this )  better
        //Button index


        Button button_pause=(Button)findViewById(R.id.button_pause);
        if(!isPauseVisible) {
            button_pause.setVisibility(View.VISIBLE);
            isPauseVisible = true;
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void resetTimer(int id) {
        if (!chronometer1.isActivated())
        {
            long elapsedMillis = SystemClock.elapsedRealtime() - chronometer1.getBase();
            chronometerTimeArray[lastChronometerIndex] = elapsedMillis;
            pieGraph.getSlice(lastChronometerIndex).setGoalValue((int)elapsedMillis);
            pieGraph.animateToGoalValues();
            chronometer1.stop();
        }
        chronometer1.setBase(SystemClock.elapsedRealtime() - chronometerTimeArray[id]);
        lastChronometerIndex = id;
        chronometer1.start();
    }


    //Periodical task to update the timer
    public void updateTimer(){
        if (!chronometer1.isActivated())
        {
            long elapsedMillis = 0;
            if(stoppedTime > 0 ){
                chronometer1.setBase(SystemClock.elapsedRealtime() - chronometerTimeArray[lastChronometerIndex]);
                elapsedMillis = SystemClock.elapsedRealtime() - chronometer1.getBase();
                stoppedTime = 0;
            } else {
                elapsedMillis = SystemClock.elapsedRealtime() - chronometer1.getBase();
            }
            //TODO: change to real elapsed time
            chronometerTimeArray[lastChronometerIndex] = elapsedMillis;
            pieGraph.getSlice(lastChronometerIndex).setGoalValue((int)elapsedMillis);
            pieGraph.animateToGoalValues();
        }
    }


    public void contructPieGraph() {
        pieGraph.removeSlices();
        PieSlice slice = new PieSlice();
        slice.setColor(Color.parseColor("#99CC00"));
        slice.setGoalValue((int)chronometerTimeArray[0]);
        pieGraph.addSlice(slice);
        slice = new PieSlice();
        slice.setColor(Color.parseColor("#FFBB33"));
        slice.setGoalValue((int)chronometerTimeArray[1]);
        pieGraph.addSlice(slice);
        slice = new PieSlice();
        slice.setColor(Color.parseColor("#AA66CC"));
        slice.setGoalValue((int)chronometerTimeArray[2]);
        pieGraph.addSlice(slice);

        pieGraph.setDuration(6000);//default if unspecified is 300 ms
        pieGraph.setInterpolator(new AccelerateDecelerateInterpolator());//default if unspecified is linear; constant speed
        pieGraph.animateToGoalValues();

    }

    public void graphButtonClicked(View view) {
        Intent intent = new Intent(this, GraphActivity.class);
        //updateChronometerTimeArray();
        datasource.updateHactivity(chronometerNameArray[lastChronometerIndex], chronometerTimeArray[lastChronometerIndex], (int) hactivitiesArray.get(lastChronometerIndex).getId() );
        intent.putExtra(CHRONOMETERARRAY_MESSAGE, chronometerTimeArray);
        Log.d("chronometerArrayLenght at habifieractivity", Integer.toString(chronometerNameArray.length));
        intent.putExtra(CHRONOMETERNAME_MESSAGE, chronometerNameArray);
        startActivity(intent);
    }

    public void pauseButtonClicked(View view) {
        resumeChronometerAction();
    }

    public void resumeChronometerAction(){
        if(isChronometerRunning) {
            chronometer1.stop();
            stopRepeatingTask();
            stoppedTime = SystemClock.elapsedRealtime(); //For example stopped at 1000s continued at 1020s
            //At this time there is 20s delay
            //
            isChronometerRunning = false;
        } else {
            chronometer1.start();
            startRepeatingTask();
            isChronometerRunning = true;
        }
    }


    public void updateChronometerTimeArray() {
        List<Hactivity> hactivities  = datasource.getAllHactivities(); //TODO CONTINUE
        Log.d("Number of hactivities", Integer.toString(hactivities.size()));
        int i = 0;
        for (Hactivity item : hactivities){
            //if((int)item.getId() < 4) {
                Log.d("HabifierActivity, getName()", item.getHactivity());
                chronometerTimeArray[i] = item.getTime();
                Log.d("HabifierActivity, getId()", Integer.toString((int) item.getId()));
                Log.d("HabifierActivity, getTime()", Integer.toString((int) item.getTime()));
                Log.d("HabifierActivity, getDate()", Integer.toString((int) item.getDate().getTime()));

                i++;
            //}
            //
        }

        i = 0;

        for (Hactivity item : hactivitiesArray){
            //if((int)item.getId() < 4) {
            Log.d("name", item.getHactivity());
            chronometerTimeArray[i] = item.getTime();
            Log.d("HabifierActivity, getId()", Integer.toString((int) item.getId()));
            Log.d("HabifierActivity, getTime()", Integer.toString((int) item.getTime()));
            Log.d("HabifierActivity, getDate()", Integer.toString((int) item.getDate().getTime()));

            i++;
            //}
            //
        }


    }

    public void debugLogHactivities(){
        List<Hactivity> hactivities  = datasource.getAllHactivities();
        int i = 0;
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        for (Hactivity item : hactivities){
            //if((int)item.getId() < 4) {
            Log.d("name", item.getHactivity());
            Log.d("HabifierActivity, getId()", Integer.toString((int) item.getId()));
            Log.d("HabifierActivity, getTime()", Integer.toString((int) item.getTime()));
            Log.d("HabifierActivity, getDate()", Integer.toString((int) item.getDate().getTime()));
            String reportDate = df.format(item.getDate());
            Log.d("Item date, Date()", reportDate);

            i++;
            //}
            //
        }
    }


}




