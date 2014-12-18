package com.elmeripoikolainen.habifier;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Addhactivity extends Activity implements AdapterView.OnItemClickListener {

    private List<Map<String, String>> planetsList = new ArrayList<Map<String,String>>();
    private SimpleAdapter simpleAdpt;
    private ArrayAdapter<Hactivity> adapter;
    List<Hactivity> list = new ArrayList<Hactivity>();
    private HactivityDataSource datasource;

    private int hactivity_positon = -1;

    private final int CONTEXT_MENU_VIEW =1;
    private final int CONTEXT_MENU_EDIT =2;
    private final int CONTEXT_MENU_ARCHIVE =3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addhactivity);
        setTitle("Manage activities");

        //Init datasource
        datasource = new HactivityDataSource(this);
        datasource.open();


        // We get the ListView component from the layout
        final ListView lv = (ListView) findViewById(R.id.listView);
        // This is a simple adapter that accepts as parameter
        // Context
        // Data list
        // The row layout that is used during the row creation
        // The keys used to retrieve the data
        // The View id used to show the data. The key number and the view id must match
        //simpleAdpt = new SimpleAdapter(this, planetsList, android.R.layout.simple_list_item_1, new String[] {"planet"}, new int[] {android.R.id.text1});

        adapter = new HactivityAdapter(this,getHactivity());

        lv.setAdapter(adapter);

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                hactivity_positon = pos;
                registerForContextMenu(lv);
                openContextMenu(lv);

                return true;
            }
        });
        lv.setLongClickable(true);


        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent); //We want to refresh the main screen after returning from this activity

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenu.ContextMenuInfo menuInfo) {
        //Context menu
        menu.setHeaderTitle("Select action: "); //Add specific text
        menu.add(Menu.NONE, CONTEXT_MENU_VIEW, Menu.NONE, "Add");
        //menu.add(Menu.NONE, CONTEXT_MENU_EDIT, Menu.NONE, "Edit");
        menu.add(Menu.NONE, CONTEXT_MENU_ARCHIVE, Menu.NONE, "Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch(item.getItemId())
        {
            case CONTEXT_MENU_VIEW:
            {
                addNewHactivity();
            }
            break;
            case CONTEXT_MENU_EDIT:
            {
                // Edit Action

            }
            break;
            case CONTEXT_MENU_ARCHIVE:
            {
                List<Hactivity> hactivitiesList = datasource.getHactivityList();
                datasource.deleteHactivitiesList(hactivitiesList.get(hactivity_positon)); // Check
                Log.d("Context menu delete hactivity position: ", Integer.toString(hactivity_positon));
                Date date = new Date();
                datasource.deleteHactivitiesToday(hactivitiesList.get(hactivity_positon), date);
                //Invalidate view
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
            break;
        }

        return super.onContextItemSelected(item);
    }




    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onStop(){
        //Add selected list items to database

        // We get the ListView component from the layout
//        ListView lv = (ListView) findViewById(R.id.listView);
//        lv.getAdapter().getItem(0);
//        for (int i = 0; i <   datasource.getHactivityList().size(); i++){
//             //Hactivity temp =  lv.getAdapter().getItem(i);
//        }

        super.onStop();
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
        } else if (id == R.id.menuAddHactivityFinished){
            Intent returnIntent = new Intent();
            setResult(RESULT_CANCELED, returnIntent);
            finish();
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

    @Override
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
        List<Hactivity> hactivitiesList = datasource.getHactivityList();
        if(hactivitiesList.size() < 8){
            Intent intent = new Intent(this, DefineHactivity.class);
            startActivityForResult(intent,1);
        } else {
            //Context toastContext = context;
            Context context = getApplicationContext();
            CharSequence text = "Total of 8 activities are allowed at the same time.";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }


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

