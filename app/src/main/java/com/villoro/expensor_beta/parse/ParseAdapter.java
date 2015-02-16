package com.villoro.expensor_beta.parse;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.villoro.expensor_beta.data.ExpensorDbHelper;
import com.villoro.expensor_beta.data.Tables;

import java.util.ArrayList;

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
            contentValues.put(Tables.DELETED, Tables.DELETED_FALSE);
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

    public static Cursor getSmartCursor(Context context, String tableName, long updatedAt){
        ExpensorDbHelper mOpenHelper = new ExpensorDbHelper(context);

        String query = ParseQueries.queryParse(tableName, updatedAt);
        if(query.length() > 0){
            return mOpenHelper.getReadableDatabase().rawQuery(query, null);
        } else {
            return mOpenHelper.getReadableDatabase().query(tableName, null,
                    Tables.LAST_UPDATE + " > " + updatedAt, null, null, null, Tables.LAST_UPDATE);
        }
    }

    public static long getIdFromParseId(Context context, String tableName, String parseID){
        ExpensorDbHelper mOpenHelper = new ExpensorDbHelper(context);
        Cursor cursor = mOpenHelper.getReadableDatabase().query(tableName, new String[]{Tables.ID},
                Tables.PARSE_ID_NAME + " = '" + parseID + "'", null, null, null, null);

        cursor.moveToFirst();
        long output = cursor.getLong(cursor.getColumnIndex(Tables.ID));
        cursor.close();
        return output;
    }

    public static ArrayList<String> getPeopleInGroup(Context context, String groupParseId) {
        if(groupParseId != null) {
            ExpensorDbHelper mOpenHelper = new ExpensorDbHelper(context);
            String query = ParseQueries.queryPeopleInGroup(groupParseId);
            Cursor cursor = mOpenHelper.getReadableDatabase().rawQuery(query, null);

            ArrayList<String> output = new ArrayList<>();
            if (cursor.moveToFirst()) {
                do {
                    output.add(cursor.getString(cursor.getColumnIndex(Tables.POINTS)));
                } while (cursor.moveToNext());
            }
            cursor.close();
            return output;
        } else {
            return new ArrayList<>();
        }
    }

}
