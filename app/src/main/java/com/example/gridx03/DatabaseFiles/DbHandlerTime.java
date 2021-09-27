package com.example.gridx03.DatabaseFiles;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class DbHandlerTime extends SQLiteOpenHelper {
    private static final int DB_VERSION = 2;
    private static final String DB_NAME = "usersdb";
    private static final String TABLE_Time = "timetable";
    private static final String KEY_ID = "id";
    private static final String KEY_TIME = "time";
    private static final String KEY_DURING = "during";
    private static final String KEY_DAYS = "days";
    private static final String KEY_SWITCH = "switch1";

    public DbHandlerTime(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_Time + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_TIME + " TEXT,"
                + KEY_DURING + " TEXT,"
                + KEY_DAYS + " TEXT,"
                + KEY_SWITCH + " TEXT" + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_Time);
        onCreate(db);
    }

    public void insertTimeDetails(String time, String during, String days, String switch1) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_TIME, time);
        contentValues.put(KEY_DURING, during);
        contentValues.put(KEY_DAYS, days);
        contentValues.put(KEY_SWITCH, switch1);
        long newRowId = db.insert(TABLE_Time, null, contentValues);
        db.close();
    }

    public ArrayList<HashMap<String, String>> GetUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> userList = new ArrayList<>();
        String query = "SELECT time, during, days, switch1 FROM " + TABLE_Time;
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            HashMap<String, String> user = new HashMap<>();
            user.put("time", cursor.getString(cursor.getColumnIndex(KEY_TIME)));
            user.put("during", cursor.getString(cursor.getColumnIndex(KEY_DURING)));
            user.put("days", cursor.getString(cursor.getColumnIndex(KEY_DAYS)));
            user.put("switch1", cursor.getString(cursor.getColumnIndex(KEY_SWITCH)));
            userList.add(user);
        }
        return userList;
    }

    // Get User Details based on userid
    public ArrayList<HashMap<String, String>> GetUserByUserId(int userid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> userList = new ArrayList<>();
        String query = "SELECT time, during days, switch1 FROM " + TABLE_Time;
        Cursor cursor = db.query(TABLE_Time, new String[]{KEY_TIME, KEY_DURING, KEY_DAYS, KEY_SWITCH}, KEY_ID + "=?", new String[]{String.valueOf(userid)}, null, null, null, null);
        if (cursor.moveToNext()) {
            HashMap<String, String> user = new HashMap<>();
            user.put("time", cursor.getString(cursor.getColumnIndex(KEY_TIME)));
            user.put("during", cursor.getString(cursor.getColumnIndex(KEY_DURING)));
            user.put("days", cursor.getString(cursor.getColumnIndex(KEY_DAYS)));
            user.put("switch1", cursor.getString(cursor.getColumnIndex(KEY_SWITCH)));
            userList.add(user);
        }
        return userList;
    }

    // Delete User Details
    public void DeleteUser(int userid) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_Time, KEY_ID + " = ?", new String[]{String.valueOf(userid)});
        db.close();
    }

    // Update User Details
    public int UpdateUserDetails(String time, String during, String days, String switch1, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cVals = new ContentValues();
        cVals.put(KEY_TIME, time);
        cVals.put(KEY_DURING, during);
        cVals.put(KEY_DAYS, days);
        cVals.put(KEY_SWITCH, switch1);
        int count = db.update(TABLE_Time, cVals, KEY_ID + " = ?", new String[]{String.valueOf(id)});
        return count;
    }
}
