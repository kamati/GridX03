package com.example.gridx03.DBHelper;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DHTokenHistory extends SQLiteOpenHelper {
    private static final String TAG ="DHTokenHistory";

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "usersdb.db";
    public static final String TABLE_TOKEN_HISTORY = "table_token_history";

    public static final String KEY_ID = "id";
    public static final String TOKEN_ID = "token_id";
    public static final String DATE = "date";
    public static final String AMOUNT = "amount";

    private String CREATE_TABLE = "CREATE TABLE " + TABLE_TOKEN_HISTORY + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + TOKEN_ID + " TEXT,"
            + DATE + " TEXT,"
            + AMOUNT + " TEXT" + ")";

    private Context mContext;

    public DHTokenHistory(Context context){
        super(context,  DB_NAME, null, DB_VERSION);
         mContext = context;
    }


    public DHTokenHistory(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context,  DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {

        try {
            database.execSQL(CREATE_TABLE);

            Log.d(TAG, "tables Creating tabl " );
            Toast.makeText(mContext,"Creating new database",Toast.LENGTH_LONG).show();

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(mContext, "Problem Creating table", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Problem Creating table " + e.getMessage());
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOKEN_HISTORY);
        onCreate(db);
    }



}
