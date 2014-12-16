package com.elmeripoikolainen.habifier;

/**
 * Created by elmeripoikolainen on 28/10/14.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_HACTIVITIES = "hactivities";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_HACTIVITY = "hactivity";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_DATE = "hactivityDate";

    // Keeps track of all the (h)activities that user wants to track
    public static final String TABLE_HACTIVITIES_LIST = "hactivities_list";
    public static final String COLUMN_COLOR = "color";
    public static final String COLUMN_CHECKED = "checked";

    private static final String DATABASE_NAME = "commments.db";
    private static final int DATABASE_VERSION = 5;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_HACTIVITIES + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_HACTIVITY
            + " text not null, " + COLUMN_TIME + " integer, " + COLUMN_DATE + " datetime default current_timestamp, "
            + COLUMN_COLOR + " text not null);";


    //Change this to keep list of activities
    private static final String DATABASE_CREATE_HACTIVITY_LIST = "create table "
            + TABLE_HACTIVITIES_LIST + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_HACTIVITY
            + " text not null, " + COLUMN_COLOR + " text not null, "+ COLUMN_CHECKED +" numeric);";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
        database.execSQL(DATABASE_CREATE_HACTIVITY_LIST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HACTIVITIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HACTIVITIES_LIST);
        onCreate(db);
    }



}
