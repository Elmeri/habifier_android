package com.elmeripoikolainen.habifier;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.GradientDrawable;
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
import android.view.View.OnClickListener;
import android.os.SystemClock;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

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
    public final static String CHRONOMETERCOLOR_MESSAGE = "com.elmeripoikolainen.habifier.CHRONOMETERCOLOR_MESSAGE";

    private Chronometer chronometer1;
    private long chronometer1_time;
    private long chronometer2_time;
    private long chronometer3_time;
    private int lastChronometerIndex;
    private Button button_1;
    private Button button_2;
    private Button button_3;
    private Button button_4;
    private Button buttonActivitySelector;
    private Chronometer[] chronometerArray;
    private long[] chronometerTimeArray;
    private int chronometerLength = 4;
    private String[] chronometerNameArray = {"Work", "Study", "Leisure", "Eat"};
    private int[] chronometerColorArray = new int[chronometerLength];
    private int[] chronometerIndexArray = {0,1,2,3};
    private List<Hactivity> hactivitiesArray = new ArrayList<Hactivity>();
    private Button[] buttonArray;


    private HactivityDataSource datasource;

    //Piechart
    private PieGraph pieGraph;
    private PieSlice[] pieSlices;

    //Periodical task
    private int mInterval = 100; // 5 seconds by default, can be changed later
    private int mIntervalSave = 600; // Every 1 min save
    private int mIntervalCount = 0;
    private Handler mHandler;

    //Is chronometerrunning
    boolean isChronometerRunning = false;
    boolean isPauseVisible = false;
    private long stoppedTime = 1;

    //Amount of entries in database
    private int amountOfEntriesInDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habifier);
        setContentView(R.layout.activity_test);
        chronometer1 = ((Chronometer) findViewById(R.id.chronometer1));
        chronometerArray = new Chronometer[4];
        chronometerTimeArray = new long[4];
        chronometerColorArray[0] = getResources().getColor(R.color.green);
        chronometerColorArray[1] = getResources().getColor(R.color.yellow);
        chronometerColorArray[2] = getResources().getColor(R.color.violet);
        chronometerColorArray[3] = getResources().getColor(R.color.blue);
        buttonArray = new Button[4];
        button_1 = ((Button) findViewById(R.id.button_1));
        button_2 = ((Button) findViewById(R.id.button_2));
        button_3 = ((Button) findViewById(R.id.button_3));
        button_4 = ((Button) findViewById(R.id.button_4));
        buttonArray[0] = ((Button) findViewById(R.id.button_1));
        buttonArray[1] = ((Button) findViewById(R.id.button_2));
        buttonArray[2] = ((Button) findViewById(R.id.button_3));
        buttonArray[3] = ((Button) findViewById(R.id.button_4));

        for(int i = 0; i < 4; i++ ) {
            buttonArray[i].setVisibility(View.VISIBLE);
        }

        Date dateNow = new Date();

        datasource = new HactivityDataSource(this);
        datasource.open();
//        datasource.reset();
//        datasource.open();


        pieGraph = (PieGraph)findViewById(R.id.graph);

        pieGraph.setInnerCircleRatio(200);
        pieGraph.setPadding(3);

        if(datasource.amountOfEntriesInTheDatabaseList() == 0){
            //Add basic activities in the list
            for (int i = 0; i < chronometerNameArray.length ; i++)
                datasource.createHactivityList(chronometerNameArray[i], Integer.toHexString(chronometerColorArray[i]), true); //Default 4 of them are checked
            //Also update the color values or consider making that as an input argument?
        } else{
            List<Hactivity> hactivitiesListArray = datasource.getHactivityList();
            for (Hactivity current : hactivitiesListArray){
                if(current.isSelected()){
                    //Add to database here if not already there. Or not here? Here.
                    Log.d("Is hactivity in database boolean: ", Boolean.toString(datasource.isHactivityInDatabase(current, dateNow)));
                    if(!datasource.isHactivityInDatabase(current, dateNow)){
                        datasource.createHactivity(current.getHactivity(), current.getColor());
                    }
                }
            }
            List<Hactivity> hactivities = datasource.getHactivitiesInDay(dateNow); //Actually this needs to change
            amountOfEntriesInDatabase = datasource.amountOfEntriesForDay(dateNow);
            chronometerNameArray = new String[amountOfEntriesInDatabase];
            chronometerColorArray = new int[amountOfEntriesInDatabase];
            chronometerTimeArray = new long[amountOfEntriesInDatabase];
            Log.d("Amount of entries in the database", Integer.toString(amountOfEntriesInDatabase));
            int k = 0; //K keeps track of the color array position
            Log.d("Is amount of entries bigger than 0 ?", Boolean.toString(0 < amountOfEntriesInDatabase));

            for (int i = 0; i < amountOfEntriesInDatabase; i++) {
                chronometerNameArray[i] = hactivities.get(i).getHactivity();
                chronometerColorArray[i] = (int) Long.parseLong(hactivities.get(i).getColor(), 16);
            }

            //Initializes button array
            int buttonIndex = 0;
            k = 0;
            for (Hactivity current : hactivitiesListArray){
                if(current.isSelected()){
                    for(k = 0; k < chronometerNameArray.length ; k++){
                        if(current.getHactivity().equals(chronometerNameArray[k])){
                            break;
                        }
                    }
                    GradientDrawable gdDefault = new GradientDrawable();
                    gdDefault.setColor(chronometerColorArray[k]);
                    gdDefault.setCornerRadius(12);
                    gdDefault.setStroke(3, chronometerColorArray[k]);

                    buttonArray[buttonIndex].setBackground(gdDefault);

                    buttonArray[buttonIndex].setText(chronometerNameArray[k]);
//                    buttonArray[buttonIndex].setBackgroundResource(R.drawable.rectangle_button);
//                    buttonArray[buttonIndex].setBackgroundColor(chronometerColorArray[k]);
//                    buttonArray[buttonIndex].setPadding(10,10,10,10);
                    chronometerIndexArray[buttonIndex] = k; //Maps pressings of the buttons to right hactivities
                    buttonIndex++;
                }
            }
            if(buttonIndex < 3){
                for(; buttonIndex < 4; buttonIndex++ ){
                    buttonArray[buttonIndex].setVisibility(View.INVISIBLE);
                }
            }

        }



        //Initialize hactivities TODO: Add dynamically defined hactivities. Change the in chronometer array!
        if(datasource.isContainsEntryForDay(dateNow)) {
            List<Hactivity> hactivities = datasource.getHactivitiesInDay(dateNow); //Actually this needs to change
            // List<Hactivity> hactivitiesTodaySelected = //Modify hactivities to match this somehow (for loop getselected)
            amountOfEntriesInDatabase = datasource.amountOfEntriesForDay(dateNow);
            chronometerArray = new Chronometer[amountOfEntriesInDatabase];
            Log.d("Amount of entries in the database", Integer.toString(amountOfEntriesInDatabase));
            for (int i = 0; i < amountOfEntriesInDatabase; i++){
                // First, let's update the pie graph
                PieSlice slice = new PieSlice();
                slice.setColor(chronometerColorArray[i]); //create color array
                if(hactivities.get(i).getTime() > 0){
                    slice.setValue(hactivities.get(i).getTime());
                    slice.setGoalValue(hactivities.get(i).getTime());
                } else {
                    slice.setValue(1); //1 is minimum value
                    slice.setGoalValue(1);
                }

                pieGraph.addSlice(slice);


                // Now, update the chronometer time array
                //Log.d("Updating chronometer time array with value: ", Integer.toString((int)hactivities.get(i).getTime()));
                chronometerTimeArray[i] = hactivities.get(i).getTime();
                // Now update the hactivitiesArray
                if(hactivitiesArray.size() <  chronometerTimeArray.length){
                    hactivitiesArray.add(hactivities.get(i));
                } else {
                    hactivitiesArray.remove(i);
                    hactivities.add(i,hactivities.get(i));
                }

            }


        } else {
            Log.d("No previous hactivities found: Length of chronometer", Integer.toString(chronometerLength));
            for (int i = 0; i < chronometerTimeArray.length; i++){
                Log.d("Datasource is creating things", "index");
                // First, let's update the pie graph
                PieSlice slice = new PieSlice();
                slice.setColor(chronometerColorArray[i]); //create color array
                slice.setValue(1);
                slice.setGoalValue(1);
                pieGraph.addSlice(slice);

                //Make database entries for today
                hactivitiesArray.add(datasource.createHactivity(chronometerNameArray[i], Integer.toHexString(chronometerColorArray[i])));

                // Now, update the chronometer time array
                chronometerTimeArray[i] = 0;
            }
        }

        chronometer1.setBase(SystemClock.elapsedRealtime() - chronometerTimeArray[0]);

        //TODO: Theory, when this is -1 all of the values need to be animated to goal values
        lastChronometerIndex = -1;

        //Periodical task
        mHandler = new Handler();

        //debugLogHactivities();
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
        } else if ( id == R.id.menuAddANewActivity ) {
            if(lastChronometerIndex != -1){
                resumeChronometerAction();
                datasource.updateHactivity(chronometerNameArray[lastChronometerIndex], chronometerTimeArray[lastChronometerIndex], (int) hactivitiesArray.get(lastChronometerIndex).getId() );
            }
            Intent intent = new Intent(this, Addhactivity.class);
            startActivityForResult(intent,1);
            //startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
//            if(resultCode == RESULT_OK){
//                String result=data.getStringExtra("result");
//            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        }
    }//onActivityResult



    public void resetButtonClicked(View view) {
        for (int i = 0; i < chronometerTimeArray.length; i++){
            Log.d("Checking chronometer time array with value: ", Integer.toString((int) pieGraph.getSlice(i).getGoalValue()));
        }

        String tagTemp = view.getTag().toString();
        int buttonIndex =  chronometerIndexArray[Integer.parseInt(tagTemp.substring(0,1))];
        if(lastChronometerIndex == -1){
            lastChronometerIndex = chronometerIndexArray[0];
            updateTimer();
        }
        if(!isChronometerRunning){
            resumeChronometerAction();
        }
        datasource.updateHactivity(chronometerNameArray[lastChronometerIndex], chronometerTimeArray[lastChronometerIndex], (int) hactivitiesArray.get(lastChronometerIndex).getId() );
        resetTimer(buttonIndex);
        datasource.updateHactivity(chronometerNameArray[buttonIndex], chronometerTimeArray[buttonIndex], (int) hactivitiesArray.get(buttonIndex).getId() );
        //Log.d("Name, Item, Id: ", chronometerNameArray[buttonIndex] + " "+ Integer.toString((int)chronometerTimeArray[buttonIndex]) + " " +Integer.toString( (int) hactivitiesArray.get(buttonIndex).getId()));
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
//            if(mIntervalCount > mIntervalSave){
//                mIntervalCount = 0;
//                if(lastChronometerIndex != -1) {
//                    datasource.updateHactivity(chronometerNameArray[lastChronometerIndex], chronometerTimeArray[lastChronometerIndex], (int) hactivitiesArray.get(lastChronometerIndex).getId() );
//                }
//
//            }
//            mIntervalCount++;

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



    public void graphButtonClicked(View view) {
        Log.d("Last Chronometer Index at graphButton clicked.", Integer.toString(lastChronometerIndex));
        if(datasource.amountOfEntriesForDay(new Date()) > 0){
            Intent intent = new Intent(this, GraphActivity.class);
            //updateChronometerTimeArray();
            if (lastChronometerIndex != -1)
                datasource.updateHactivity(chronometerNameArray[lastChronometerIndex], chronometerTimeArray[lastChronometerIndex], (int) hactivitiesArray.get(lastChronometerIndex).getId() );
            intent.putExtra(CHRONOMETERARRAY_MESSAGE, chronometerTimeArray);
            Log.d("chronometerArrayLenght at habifieractivity", Integer.toString(chronometerNameArray.length));
            intent.putExtra(CHRONOMETERNAME_MESSAGE, chronometerNameArray);
            intent.putExtra(CHRONOMETERCOLOR_MESSAGE, chronometerColorArray);
            startActivity(intent);
        } else {
            Context context = getApplicationContext();
            CharSequence text = "Please, try running the chronometer for a couple of times. Thank you.";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }

    }

    public void manageHactivities(View view){
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






    /*
        Dark corner of the code.
     */
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




