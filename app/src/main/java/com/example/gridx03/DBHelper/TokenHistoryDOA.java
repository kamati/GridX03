package com.example.gridx03.DBHelper;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.gridx03.Models.TokenHistory;

import java.io.Serializable;
import java.util.ArrayList;

public class TokenHistoryDOA implements Serializable {

    private static final String TAG = "PowerDOA";

    private Context mContext;
    private SQLiteDatabase mDatabase;
    private DHTokenHistory mDbHelper;
    private String[] mAllColumns = {
            DHTokenHistory.KEY_ID,
            DHTokenHistory.TOKEN_ID,
            DHTokenHistory.DATE,
            DHTokenHistory.AMOUNT,
    };

    public TokenHistoryDOA(Context context) {
        mDbHelper = new DHTokenHistory(context);
        this.mContext = context;
        // open the database
        try {
            open();
        } catch (SQLException e) {
            Log.e(TAG, "SQLException on openning database " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void open() throws SQLException {
        mDatabase = mDbHelper.getWritableDatabase();
    }
    public void close() {
        mDbHelper.close();
    }

    public TokenHistory CreateTokenItem(String tokenID,String date,String amount){

        ContentValues values = new ContentValues();
        values.put(DHTokenHistory.TOKEN_ID, tokenID);
        values.put(DHTokenHistory.DATE, date);
        values.put(DHTokenHistory.AMOUNT, amount);


        long insertId = mDatabase
                .insert(DHTokenHistory.TABLE_TOKEN_HISTORY, null, values);
        Cursor cursor = mDatabase.query(DHTokenHistory.TABLE_TOKEN_HISTORY, mAllColumns,
                DHTokenHistory.KEY_ID + " = " + insertId, null, null,
                null, null);
        cursor.moveToFirst();
        TokenHistory Stat = cursorToToken(cursor);
        cursor.close();
        return Stat;
    }

    private TokenHistory cursorToToken(Cursor cursor) {
        TokenHistory Stat = new TokenHistory();
        Stat.setID(cursor.getLong(0));
        Stat.setmTokeID(cursor.getString(1));
        Stat.setmDate(cursor.getString(2));
        Stat.setmAmount(cursor.getString(3));
        return Stat;
    }

    public ArrayList<TokenHistory> getAlltokens() {
        ArrayList<TokenHistory> listStats = new ArrayList<TokenHistory>();

        Cursor cursor = mDatabase.query(DHTokenHistory.TABLE_TOKEN_HISTORY, mAllColumns,
                null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            TokenHistory token = cursorToToken(cursor);
            listStats.add(token);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return listStats;
    }

    public void deleteAllTokensStats() {

        mDatabase.delete(DHTokenHistory.TABLE_TOKEN_HISTORY, null, null);
    }


}
