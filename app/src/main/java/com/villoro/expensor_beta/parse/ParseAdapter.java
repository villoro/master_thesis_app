package com.villoro.expensor_beta.parse;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.villoro.expensor_beta.data.ExpensorDbHelper;
import com.villoro.expensor_beta.data.Tables;

/**
 * Created by Arnau on 26/01/2015.
 */
public class ParseAdapter {

    public static boolean tryToInsertSQLite(Context context, ContentValues contentValues, String tableName) {
        ExpensorDbHelper mOpenHelper = new ExpensorDbHelper(context);
        final SQLiteDatabase database = mOpenHelper. getWritableDatabase();

        String parseID = contentValues.getAsString(Tables.PARSE_ID_NAME);
        long updatedAtParse = contentValues.getAsLong(Tables.LAST_UPDATE);
        String whereClause = Tables.PARSE_ID_NAME + " = '" + parseID + "'";

        Cursor cursor = database.query(tableName,
                new String[]{Tables.ID, Tables.PARSE_ID_NAME, Tables.LAST_UPDATE},
                whereClause, null, null, null, null);

        long _id = -1;
        long updatedAtSQL = 0;

        if (cursor.moveToFirst()) {
            _id = cursor.getLong(cursor.getColumnIndex(Tables.ID));
            updatedAtSQL = cursor.getLong(cursor.getColumnIndex(Tables.LAST_UPDATE));
        }
        cursor.close();

        if (_id >= 0) {
            if (updatedAtParse > updatedAtSQL){
                Log.d("", "updating " + contentValues.toString());
                database.update(tableName, contentValues, whereClause, null);
                return true;
            } else {
                Log.d("", "updatedAtParse < updatedAtSQL");
                return false;
            }
        } else {
            Log.d("", "inserting " + contentValues.toString());
            database.insert(tableName, null, contentValues);
            return true;
        }
    }

    public static long updateWithId(Context context, ContentValues contentValues, String tableName,
                                    long _id){
        String whereClause = Tables.ID + " = " + _id;

        ExpensorDbHelper mOpenHelper = new ExpensorDbHelper(context);
        final SQLiteDatabase database = mOpenHelper. getWritableDatabase();

        Log.d("", "updating parseID & updatedAt");

        return database.update(tableName, contentValues, whereClause, null);
    }


    public static Cursor getParseCursor(Context context, String tableName, long updatedAt) {
        ExpensorDbHelper mOpenHelper = new ExpensorDbHelper(context);

        return mOpenHelper.getReadableDatabase().query(tableName, null,
                Tables.LAST_UPDATE + " > " + updatedAt, null, null, null, Tables.LAST_UPDATE);
    }
}
