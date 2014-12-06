package com.elmeripoikolainen.habifier;

/**
 * Created by elmeripoikolainen on 28/10/14.
 */

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class HactivityDataSource {

    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;

//    private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
//            MySQLiteHelper.COLUMN_HACTIVITY, MySQLiteHelper.COLUMN_TIME};

    private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_HACTIVITY, MySQLiteHelper.COLUMN_TIME, MySQLiteHelper.COLUMN_DATE};

    //For hactivities list
    private String[] allColumnsList = { MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_HACTIVITY, MySQLiteHelper.COLUMN_COLOR, MySQLiteHelper.COLUMN_CHECKED};

    private SimpleDateFormat dateFormat;
    private SimpleDateFormat dateFormatShort;


    public HactivityDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss", Locale.getDefault());
        dateFormatShort = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void reset() throws SQLException {
        database = dbHelper.getWritableDatabase();
        database.delete(MySQLiteHelper.TABLE_HACTIVITIES, null, null);
        database.delete(MySQLiteHelper.TABLE_HACTIVITIES_LIST, null, null);
        database.close();
        database = dbHelper.getWritableDatabase();
        //this.dbHelper.onCreate(this.database);

    }

    public void close() {
        dbHelper.close();
    }


    /*
        Creates a new hacktivity
     */
    public Hactivity createHactivity(String hactivity) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_HACTIVITY, hactivity);
        String date_now = getDateTime();
        values.put(MySQLiteHelper.COLUMN_DATE, date_now);
        long insertId = database.insert(MySQLiteHelper.TABLE_HACTIVITIES, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_HACTIVITIES,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Hactivity newHactivity = cursorToHactivity(cursor);
        cursor.close();
        return newHactivity;
    }

    //TODO THIS FUNCTION IS UNIMPORTANT, MAY BE REMOVED
    public Hactivity createHactivityDayBefore(String hactivity) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_HACTIVITY, hactivity);

        String date_now = getDateTimeCorrupted();
        values.put(MySQLiteHelper.COLUMN_DATE, date_now);
        values.put(MySQLiteHelper.COLUMN_TIME, 1000 * 60 * 60 * 1.5);
        long insertId = database.insert(MySQLiteHelper.TABLE_HACTIVITIES, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_HACTIVITIES,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Hactivity newHactivity = cursorToHactivity(cursor);
        cursor.close();
        return newHactivity;
    }


    public void deleteHactivities(Hactivity hactivity) {
        long id = hactivity.getId();
        System.out.println("Hactivity deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_HACTIVITIES, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    /*
        Gets ALL of the hacktivities
     */
    public List<Hactivity> getAllHactivities() {
        List<Hactivity> hactivities = new ArrayList<Hactivity>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_HACTIVITIES,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Hactivity hactivity = cursorToHactivity(cursor);
            hactivities.add(hactivity);
            cursor.moveToNext();
        }
        Log.d("Cursor, position", Integer.toString( cursor.getPosition()));
        // make sure to close the cursor
        cursor.close();
        return hactivities;
    }
    /*
        Get hactivities from a certain day
     */
    public List<Hactivity> getHactivitiesInDay(Date date){
        List<Hactivity> hactivities = new ArrayList<Hactivity>();
        String dateFormatted = dateFormatShort.format(date);

        String Query = "Select * from " + MySQLiteHelper.TABLE_HACTIVITIES + " where " + MySQLiteHelper.COLUMN_DATE + " LIKE '%"
                + dateFormatted+"%'";
        Cursor cursor = database.rawQuery(Query, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Hactivity hactivity = cursorToHactivity(cursor);
            hactivities.add(hactivity);
            cursor.moveToNext();
        }
        //Log.d("Cursor, position", Integer.toString( cursor.getPosition()));
        // make sure to close the cursor
        cursor.close();
        return hactivities;
    }


    /*
        Creates hactivity and sets values based on the database.
     */
    private Hactivity cursorToHactivity(Cursor cursor) {
        Hactivity hactivity = new Hactivity();
        hactivity.setId(cursor.getLong(0));
        hactivity.setHactivity(cursor.getString(1));
        hactivity.setTime(cursor.getInt(2));

        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
        Date date = new Date();
        try{
            date = formatter.parse(cursor.getString(3));
            hactivity.setDate(date);
        } catch (java.text.ParseException ex){
            Log.d("Parse failed", ex.getMessage());
        }

        hactivity.setDate(date);
        return hactivity;
    }



    //Checks if if hacktivity is already in database
    //TODO: Add date checking also! :)
    public boolean checkEvent(String hactivity)
    {

        Cursor cursor = database.query(MySQLiteHelper.TABLE_HACTIVITIES,
                new String[] { MySQLiteHelper.COLUMN_HACTIVITY},
                MySQLiteHelper.COLUMN_HACTIVITY + " = ?  " ,
                new String[] {hactivity},
                null, null, null, null);

        if(cursor.moveToFirst())
            return true; //row exists
        else
            return false;
    }


    public void updateHactivity(String hactivity, long time, int id) {
        boolean isHactivityInDatabase = checkEvent(hactivity);
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_TIME, time);
        database.update(MySQLiteHelper.TABLE_HACTIVITIES, values , MySQLiteHelper.COLUMN_ID + " = " + id + " ", null);
    }


    private String getDateTime() {
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String reportDate = df.format(date);
        //Log.d("Date created at beginning", reportDate);
        return dateFormat.format(date);
    }

    private String getDateTimeCorrupted() {
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000L);
        String reportDate = dateFormat.format(date);
        Log.d("Date created at beginning", reportDate);
        return dateFormat.format(date);
    }
    /*
        Check amount entries for today.
     */

    public boolean isContainsEntryForDay(Date date){
        String dateFormatted = dateFormatShort.format(date);
        return CheckIsDataAlreadyInDBorNot(MySQLiteHelper.TABLE_HACTIVITIES, MySQLiteHelper.COLUMN_DATE, dateFormatted);
    }

    /*
        Check amount of entries for a day.
     */

    public int amountOfEntriesForDay(Date date){
        String dateFormatted = dateFormatShort.format(date);
        return amountOfEntriesinTheDatabase(MySQLiteHelper.TABLE_HACTIVITIES, MySQLiteHelper.COLUMN_DATE, dateFormatted);
    }

    /*
        Check if the data is in the database.
     */
    public boolean CheckIsDataAlreadyInDBorNot(String TableName,
                                                      String dbfield, String fieldValue) {
        String Query = "Select * from " + TableName + " where " + dbfield + " LIKE '%"
                + fieldValue+"%'";
        Cursor cursor = database.rawQuery(Query, null);
        if(cursor.getCount()<=0){
            return false;
        }
        return true;
    }
    /*
        Returns the amount of entries in the database
     */
    public int amountOfEntriesinTheDatabase(String TableName,
                                            String dbfield, String fieldValue){
        String Query = "Select * from " + TableName + " where " + dbfield + " LIKE '%"
                + fieldValue+"%'";
        Cursor cursor = database.rawQuery(Query, null);
        if(cursor.getCount()>=0){
            return cursor.getCount();
        }
        return 0;
    }

    /*
        Functions related to hactivity list. Hactivity list is a database keeping track of what user wants to track.
     */
    public List<Hactivity> getHactivityList() {
        List<Hactivity> hactivities = new ArrayList<Hactivity>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_HACTIVITIES_LIST,
                allColumnsList, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Hactivity hactivity = cursorToHactivityList(cursor);
            hactivities.add(hactivity);
            cursor.moveToNext();
        }
        Log.d("Cursor, position", Integer.toString( cursor.getPosition()));
        // make sure to close the cursor
        cursor.close();
        return hactivities;
    }

    /*
        Deletes all hactivities from the list that keeps track of all hactivities.
     */
    public void deleteHactivitiesList(Hactivity hactivity) {
        long id = hactivity.getId();
        System.out.println("Hactivity deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_HACTIVITIES_LIST, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }
    /*
        Creates a new hactivity in the list that keeps track of all hactivities.
     */

    public Hactivity createHactivityList(String hactivity) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_HACTIVITY, hactivity);
        values.put(MySQLiteHelper.COLUMN_CHECKED, false);
        values.put(MySQLiteHelper.COLUMN_COLOR, "blabla");
        long insertId = database.insert(MySQLiteHelper.TABLE_HACTIVITIES_LIST, null,
                values);
        Log.d("Insert id result ", Integer.toString((int)insertId)); //RETURNS FALSE

        Cursor cursor = database.query(MySQLiteHelper.TABLE_HACTIVITIES_LIST,
                allColumnsList, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        Log.d("Cursor moveToFirst() result. ", Boolean.toString(cursor.moveToFirst()));

        Hactivity newHactivity = cursorToHactivityList(cursor);
        cursor.close();
        return newHactivity;
    }

    public int amountOfEntriesInTheDatabaseList(){
        return amountOfEntriesInTheDatabaseGeneral(MySQLiteHelper.TABLE_HACTIVITIES_LIST);
    }

    private int amountOfEntriesInTheDatabaseGeneral(String tableName){
        int count = (int) DatabaseUtils.queryNumEntries(database, tableName);
        return count;
    }

    /*
    Creates hactivity and sets values based on the database.
 */
    private Hactivity cursorToHactivityList(Cursor cursor) {
        Hactivity hactivity = new Hactivity();
        hactivity.setId(cursor.getLong(0));
        hactivity.setHactivity(cursor.getString(1));
        hactivity.setSelected(cursor.getInt(3));
        return hactivity;
    }


}
