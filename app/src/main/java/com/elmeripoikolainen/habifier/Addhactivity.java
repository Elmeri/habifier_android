package com.elmeripoikolainen.habifier;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Addhactivity extends Activity implements AdapterView.OnItemClickListener {

    private List<Map<String, String>> planetsList = new ArrayList<Map<String,String>>();
    private SimpleAdapter simpleAdpt;
    private ArrayAdapter<Hactivity> adapter;
    List<Hactivity> list = new ArrayList<Hactivity>();
    private HactivityDataSource datasource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addhactivity);

        //Init datasource
        datasource = new HactivityDataSource(this);
        datasource.open();


        // We get the ListView component from the layout
        ListView lv = (ListView) findViewById(R.id.listView);
        // This is a simple adapter that accepts as parameter
        // Context
        // Data list
        // The row layout that is used during the row creation
        // The keys used to retrieve the data
        // The View id used to show the data. The key number and the view id must match
        //simpleAdpt = new SimpleAdapter(this, planetsList, android.R.layout.simple_list_item_1, new String[] {"planet"}, new int[] {android.R.id.text1});

        adapter = new HactivityAdapter(this,getHactivity());

        lv.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.addhactivity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.menuAddHactivityAddNew){
            addNewHactivity();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    // This should check the amount of checkboxed checked. It should be four.
    public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
        TextView label = (TextView) v.getTag(R.id.hactivity_label);
        CheckBox checkbox = (CheckBox) v.getTag(R.id.hactivity_check);
        Toast.makeText(v.getContext(), label.getText().toString() + " " + isCheckedOrNot(checkbox), Toast.LENGTH_LONG).show();
    }

    private String isCheckedOrNot(CheckBox checkbox) {
        if(checkbox.isChecked())
            return "is checked";
        else
            return "is not checked";
    }

    private void addNewHactivity(){
        //New hactivity screen
        Intent intent = new Intent(this, DefineHactivity.class);
        startActivity(intent);
    }


    // This should list all the hactivities in the database
    // 1. get hactivities from database
    // 2. add the to the list.
    // Return the function (voila, bueno, wunderbar)
//    private List<Hactivity> getHactivity() {
//        Hactivity hactivity = new Hactivity();
//        hactivity.setHactivity("Linux");
//        list.add(hactivity);
//        Hactivity hactivity1 = new Hactivity();
//        hactivity1.setHactivity("Windows7");
//        list.add(hactivity1);
//        Hactivity hactivity2 = new Hactivity();
//        hactivity2.setHactivity("Suse");
//        list.add(hactivity2);
//        Hactivity hactivity3 = new Hactivity();
//        hactivity3.setHactivity("Eclipse");
//        list.add(hactivity3);
//        Hactivity hactivity4 = new Hactivity();
//        hactivity4.setHactivity("Ubuntu");
//        list.add(hactivity4);
//        Hactivity hactivity5 = new Hactivity();
//        hactivity5.setHactivity("Solaris");
//        list.add(hactivity5);
//        Hactivity hactivity6 = new Hactivity();
//        hactivity6.setHactivity("Android");
//        list.add(hactivity6);
//        Hactivity hactivity7 = new Hactivity();
//        hactivity7.setHactivity("iPhone");
//        list.add(hactivity7);
//        Hactivity hactivity8 = new Hactivity();
//        hactivity8.setHactivity("Java");
//        list.add(hactivity8);
//        Hactivity hactivity9 = new Hactivity();
//        hactivity9.setHactivity(".Net");
//        list.add(hactivity9);
//        Hactivity hactivity10 = new Hactivity();
//        hactivity10.setHactivity("PHP");
//        list.add(hactivity10);
//
//        return list;
//    }


    private List<Hactivity> getHactivity() {
        return datasource.getHactivityList();
    }
}

