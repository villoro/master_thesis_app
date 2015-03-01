package com.villoro.expensor_beta.data;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.villoro.expensor_beta.sync.parse.ParseSync;

/**
 * Created by Arnau on 19/01/2015.
 */
public class ExpensorDbHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "expensor.db";

    private Context context;

    public ExpensorDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    private void createTable(String tableName, SQLiteDatabase db)
    {
        Tables table = new Tables(tableName);
        db.execSQL(table.createTable());
    }

    private void dropTable(String tableName, SQLiteDatabase db)
    {
        Tables table = new Tables(tableName);
        db.execSQL(table.dropTable());
    }

    public void restartDatabase(){
        try {
            SQLiteDatabase db = getWritableDatabase();
            for (String tableName : Tables.TABLES)
            {
                dropTable(tableName, db);
                createTable(tableName, db);
            }
            ParseSync.resetLastSync(context);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            for (String tableName : Tables.TABLES)
            {
                createTable(tableName, db);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            for (String tableName : Tables.TABLES)
            {
                dropTable(tableName, db);
                createTable(tableName, db);
            }
            ParseSync.resetLastSync(context);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
