package com.elmeripoikolainen.habifier;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import it.gmariotti.android.example.colorpicker.calendarstock.ColorPickerDialog;
import it.gmariotti.android.example.colorpicker.calendarstock.ColorPickerSwatch;
import it.gmariotti.android.example.colorpicker.calendarstock.SettingsPickerActivity;
import it.gmariotti.android.example.colorpicker.dashclockpicker.ColorPickerDialogDash;
import it.gmariotti.android.example.colorpicker.dashclockpicker.SettingsActivity;
import it.gmariotti.android.example.colorpicker.internal.CalendarColorSquare;
import it.gmariotti.android.example.colorpicker.internal.NsMenuAdapter;
import it.gmariotti.android.example.colorpicker.internal.NsMenuItemModel;
import it.gmariotti.android.example.colorpicker.Utils;


public class DefineHactivity extends Activity {
    private EditText hactivity_name;
    private Button cancelbutton, display;
    private Button colorbutton;
    private Context context;
    private int mSelectedColorCal0 = 0;
    private int mSelectedColorDash1 = 0;
    private HactivityDataSource datasource;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_define_hactivity);
        setTitle("New Activity");

        context = this.getApplicationContext();

        hactivity_name = (EditText) findViewById(R.id.define_hactivity_hactivity_name);
        colorbutton = (Button) findViewById(R.id.defineHactivitySelectColor);
        cancelbutton = (Button) findViewById(R.id.defineHactivityCancel);

        colorbutton.setBackgroundColor(getResources().getColor(R.color.green));
        mSelectedColorDash1 = getResources().getColor(R.color.green);


        datasource = new HactivityDataSource(this);
        //datasource.reset();
        datasource.open();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.define_hactivity, menu);
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

    public void colorButtonClicked(View view) {
        int[] mColor = Utils.ColorUtils.colorChoice(this);

        ColorPickerDialogDash colordashfragment = ColorPickerDialogDash.newInstance(
                R.string.color_picker_default_title,
                mColor,
                mSelectedColorDash1,
                5);

        //Implement listener to get color value
        colordashfragment.setOnColorSelectedListener(new ColorPickerDialogDash.OnColorSelectedListener(){

            @Override
            public void onColorSelected(int color) {
                mSelectedColorDash1=color;
                colorbutton.setBackgroundColor(mSelectedColorDash1);
            }

        });

        colordashfragment.show(getFragmentManager(), "dash");



        //Possibly breaks code (":D") //Does not seem to do anything...
        Toast.makeText(view.getContext(), "Click here",
                Toast.LENGTH_SHORT).show();
    }

    /*
        Return to previous activity without saving.
     */
    public void onCancelButtonClicked(View view){
       Intent returnIntent = new Intent();
       setResult(RESULT_CANCELED, returnIntent);
       finish();
    }

    /*
        Return to previous activity with saving. Add new hactivity to hactivitydatabase.
     */
    public void onDoneButtonClicked(View view){
        String hactivity_text = hactivity_name.getText().toString();
        //Also check hactivity with that name exists
        if (hactivity_text != ""){
            //Update color value of hactivity
            String hexColorValue =  Integer.toHexString(mSelectedColorDash1);
            //Update the string
            datasource.createHactivityList(hactivity_name.getText().toString(), hexColorValue, false); //false as it is not checked

        } else {
            Toast.makeText(view.getContext(), "Please name your activity",
                    Toast.LENGTH_SHORT).show();
        }

        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }


}
