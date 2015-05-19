package com.villoro.expensor_beta.sync.parse;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.parse.ParseUser;
import com.villoro.expensor_beta.R;
import com.villoro.expensor_beta.data.ExpensorContract;
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
        String whereClause;
        if(tableName.equals(Tables.TABLENAME_PEOPLE)){
            whereClause = Tables.EMAIL + " = '" + contentValues.getAsString(Tables.EMAIL) + "'";
        } else {
            whereClause = Tables.PARSE_ID_NAME + " = '" + parseID + "'";
        }

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
        Log.d("", "trying to insert " + contentValues.toString());

        if (_id >= 0) {
            if (updatedAtParse > updatedAtSQL || tableName.equals(Tables.TABLENAME_PEOPLE)){
                Log.d("", "updatedAtParse > upadatedAtSQL -> inserting");
                database.update(tableName, contentValues, whereClause, null);
                return true;
            } else {
                Log.d("", "updatedAtParse < upadatedAtSQL -> not inserting");
                return false;
            }
        } else {
            Log.d("", "this object don't exist");
            contentValues.put(Tables.DELETED, Tables.FALSE);
            database.insert(tableName, null, contentValues);
            return true;
        }
    }

    public static long updateWithId(Context context, ContentValues contentValues, String tableName,
                                    long _id){
        String whereClause = Tables.ID + " = " + _id;

        ExpensorDbHelper mOpenHelper = new ExpensorDbHelper(context);
        final SQLiteDatabase database = mOpenHelper. getWritableDatabase();

        return database.update(tableName, contentValues, whereClause, null);
    }

    public static Cursor getSmartCursor(Context context, String tableName, long updatedAt, long peopleID){
        ExpensorDbHelper mOpenHelper = new ExpensorDbHelper(context);

        String query = ParseQueries.queryParse(tableName, updatedAt, peopleID);
        if(query.length() > 0){
            return mOpenHelper.getReadableDatabase().rawQuery(query, null);
        } else {
            return mOpenHelper.getReadableDatabase().query(tableName, null,
                    Tables.LAST_UPDATE + " > " + updatedAt, null, null, null, Tables.LAST_UPDATE);
        }
    }

    public static long getIdFromParseId(Context context, String tableName, String parseID, String whichColumn){
        ExpensorDbHelper mOpenHelper = new ExpensorDbHelper(context);
        Cursor cursor = mOpenHelper.getReadableDatabase().query(tableName, new String[]{Tables.ID},
                whichColumn + " = '" + parseID + "'", null, null, null, null);

        long output;
        if(cursor.moveToFirst()) {
            output = cursor.getLong(cursor.getColumnIndex(Tables.ID));
        } else {
            output = 0;
        }
        cursor.close();
        return output;
    }

    public static String getPublicIdFromParseId(Context context, String tableName, String parseID){
        ExpensorDbHelper mOpenHelper = new ExpensorDbHelper(context);
        Cursor cursor = mOpenHelper.getReadableDatabase().query(tableName, new String[]{Tables.POINTS},
                Tables.PARSE_ID_NAME + " = '" + parseID + "'", null, null, null, null);

        String output;
        if(cursor.moveToFirst()) {
            output = cursor.getString(cursor.getColumnIndex(Tables.POINTS));
        } else {
            output = null;
        }
        cursor.close();
        return output;
    }

    public static ArrayList<String> getPeopleInGroup(Context context, String groupParseId) {
        if(groupParseId != null) {
            ExpensorDbHelper mOpenHelper = new ExpensorDbHelper(context);
            String query = ParseQueries.queryPeopleInGroup(groupParseId);
            Cursor cursor = mOpenHelper.getReadableDatabase().rawQuery(query, null);

            Log.d("ParseAdapter", "cursor count= " + cursor.getCount());

            ArrayList<String> output = new ArrayList<>();
            if (cursor.moveToFirst()) {
                do {
                    output.add(cursor.getString(cursor.getColumnIndex(Tables.USER_ID)));
                } while (cursor.moveToNext());
            }
            cursor.close();
            return output;
        } else {
            return new ArrayList<>();
        }
    }

    public static ArrayList<String> getEmailsPeopleWithNoPoints(Context context){
        ExpensorDbHelper mOpenHelper = new ExpensorDbHelper(context);
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor cursor = db.query(
                Tables.TABLENAME_PEOPLE,
                new String[]{Tables.EMAIL},
                Tables.USER_ID + " IS NULL",
                null, null, null, null);
        ArrayList<String> output = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                output.add(cursor.getString(cursor.getColumnIndex(Tables.EMAIL)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return output;
    }

    public static boolean thatPersonHadPreviouslyUserID(Context context, String email){
        ExpensorDbHelper mOpenHelper = new ExpensorDbHelper(context);
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor cursor = db.query(
                Tables.TABLENAME_PEOPLE,
                new String[]{Tables.USER_ID},
                Tables.EMAIL + " = '" + email + "'",
                null, null, null, null);
        boolean output;
        if (cursor.moveToFirst()) {
            String userID = cursor.getString(cursor.getColumnIndex(Tables.USER_ID));
            output = userID != null || userID.length() > 0;
        }
        output = false;
        cursor.close();
        return output;
    }

    public static String getMyPublicId(Context context){
        ExpensorDbHelper mOpenHelper = new ExpensorDbHelper(context);
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        String email = ParseUser.getCurrentUser().getEmail();

        Cursor cursor = db.query(
                Tables.TABLENAME_PEOPLE,
                new String[]{Tables.EMAIL, Tables.PARSE_ID_NAME, Tables.USER_ID, Tables.POINTS},
                Tables.EMAIL + " = '" + email + "'",
                null, null, null, null);
        if(cursor.moveToFirst()) {
            String output = cursor.getString(cursor.getColumnIndex(Tables.POINTS));
            cursor.close();
            return output;
        } else {
            cursor.close();
            return null;
        }
    }

    public static long getMyId(Context context){
        ExpensorDbHelper mOpenHelper = new ExpensorDbHelper(context);
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        String email = ParseUser.getCurrentUser().getEmail();

        Cursor cursor = db.query(
                Tables.TABLENAME_PEOPLE,
                new String[]{Tables.EMAIL, Tables.ID},
                Tables.EMAIL + " = '" + email + "'",
                null, null, null, null);
        if(cursor.moveToFirst()) {
            long id = cursor.getLong(cursor.getColumnIndex(Tables.ID));
            cursor.close();
            return id;
        } else {
            cursor.close();
            return -1;
        }
    }

    public static void insertMyself(Context context, ParseUser parseUser, String parsePublicPeopleID){
        ExpensorDbHelper mOpenHelper = new ExpensorDbHelper(context);
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Tables.NAME, context.getString(R.string.me));
        values.put(Tables.EMAIL, parseUser.getEmail());
        values.put(Tables.USER_ID, parseUser.getObjectId());
        values.put(Tables.LAST_UPDATE, ExpensorContract.getDateUTC().getTime());
        if(parsePublicPeopleID != null){
            values.put(Tables.POINTS, parsePublicPeopleID);
        }
        Log.d("", "trying to insert= " + values.toString());
        context.getContentResolver().insert(ExpensorContract.PeopleEntry.PEOPLE_URI, values);
    }

    public static int updatePeoplePointsTo(Context context, String email, String parseUserID){
        ExpensorDbHelper mOpenHelper = new ExpensorDbHelper(context);
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Tables.USER_ID, parseUserID);
        values.put(Tables.LAST_UPDATE, ExpensorContract.getDateUTC().getTime());
        return db.update(Tables.TABLENAME_PEOPLE, values, Tables.EMAIL + "= '" + email + "'", null);
    }

    public static void deleteAll(Context context){
        ExpensorDbHelper mOpenHelper = new ExpensorDbHelper(context);
        mOpenHelper.restartDatabase();
    }
}
