package com.villoro.expensor_beta.data;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Arnau on 19/01/2015.
 */
public class ExpensorDbHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "expensor.db";

    public ExpensorDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void createTable(String tableName, SQLiteDatabase db)
    {
        Tables table = new Tables(tableName);
        db.execSQL(table.createTable());
    }

    public void dropTable(String tableName, SQLiteDatabase db)
    {
        Tables table = new Tables(tableName);
        db.execSQL(table.dropTable());
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
