package com.elmeripoikolainen.habifier;

/**
 * Created by elmeripoikolainen on 28/10/14.
 */

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.w3c.dom.Comment;

public class HactivityDataSource {

    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;

//    private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
//            MySQLiteHelper.COLUMN_HACTIVITY, MySQLiteHelper.COLUMN_TIME};

    private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_HACTIVITY, MySQLiteHelper.COLUMN_TIME, MySQLiteHelper.COLUMN_DATE};

    public HactivityDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void reset() throws SQLException {
        database = dbHelper.getWritableDatabase();
        database.delete(MySQLiteHelper.TABLE_HACTIVITIES, null, null);
        database.close();
        database = dbHelper.getWritableDatabase();
        //this.dbHelper.onCreate(this.database);

    }

    public void close() {
        dbHelper.close();
    }

    public Hactivity createHactivity(String hactivity) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_HACTIVITY, hactivity);
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
        // make sure to close the cursor
        cursor.close();
        return hactivities;
    }

    private Hactivity cursorToHactivity(Cursor cursor) {
        Hactivity hactivity = new Hactivity();
        hactivity.setId(cursor.getLong(0));
        hactivity.setComment(cursor.getString(1));
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
        //if(isHactivityInDatabase){
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_TIME, time);
        database.update(MySQLiteHelper.TABLE_HACTIVITIES, values , MySQLiteHelper.COLUMN_ID + " = " + id + " ", null);
        //} else {
        //    createHactivity(hactivity);
        //}
    }
}
