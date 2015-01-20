package com.villoro.expensor_beta.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

/**
 * Created by Arnau on 19/01/2015.
 */
public class ExpensorProvider extends ContentProvider{

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private ExpensorDbHelper mOpenHelper;

    //what I need to retrive
    private static final int EXPENSE = 100;
    private static final int EXPENSE_WITH_ID = 101;

    private static final int INCOME = 200;
    private static final int INCOME_WITH_ID = 201;

    private static final int CATEGORIES = 300;
    private static final int CATEGORIES_WITH_ID = 301;

    private static final int PEOPLE = 400;
    private static final int PEOPLE_WITH_ID = 401;
    private static final int PEOPLE_IN_GROUP = 402;

    private static final int GROUPS = 500;
    private static final int GROUPS_WITH_ID = 501;

    private static final int TRANSACTIONS_GROUP = 600;
    private static final int TRANSACTIONS_GROUP_WITH_ID = 601;

    private static final int TRANSACTIONS_PEOPLE = 700;
    private static final int TRANSACTIONS_PEOPLE_WITH_ID = 701;
    private static final int WHO_PAID = 801;
    private static final int WHO_SPENT = 901;

    public static UriMatcher buildUriMatcher() {
        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ExpensorContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, Tables.TABLENAME_EXPENSE, EXPENSE);
        matcher.addURI(authority, Tables.TABLENAME_EXPENSE + "/*", EXPENSE_WITH_ID);

        matcher.addURI(authority, Tables.TABLENAME_INCOME, INCOME);
        matcher.addURI(authority, Tables.TABLENAME_INCOME + "/*", INCOME_WITH_ID);

        matcher.addURI(authority, Tables.TABLENAME_CATEGORIES, CATEGORIES);
        matcher.addURI(authority, Tables.TABLENAME_CATEGORIES + "/*", CATEGORIES_WITH_ID);

        matcher.addURI(authority, Tables.TABLENAME_PEOPLE, PEOPLE);
        matcher.addURI(authority, Tables.TABLENAME_PEOPLE + "/*", PEOPLE_WITH_ID);
        matcher.addURI(authority, Tables.TABLENAME_PEOPLE_IN_GROUP + "/*/*", PEOPLE_IN_GROUP); //peopleID/groupID

        matcher.addURI(authority, Tables.TABLENAME_GROUPS, GROUPS);
        matcher.addURI(authority, Tables.TABLENAME_GROUPS + "/*", GROUPS_WITH_ID);

        matcher.addURI(authority, Tables.TABLENAME_TRANSACTIONS_GROUP, TRANSACTIONS_GROUP);
        matcher.addURI(authority, Tables.TABLENAME_TRANSACTIONS_GROUP + "/*", TRANSACTIONS_GROUP_WITH_ID);

        matcher.addURI(authority, Tables.TABLENAME_TRANSACTIONS_PEOPLE, TRANSACTIONS_PEOPLE);
        matcher.addURI(authority, Tables.TABLENAME_TRANSACTIONS_PEOPLE + "/*", TRANSACTIONS_PEOPLE_WITH_ID);

        matcher.addURI(authority, Tables.TABLENAME_WHO_PAID + "/*/*", WHO_PAID);
        matcher.addURI(authority, Tables.TABLENAME_WHO_SPENT + "/*/*", WHO_SPENT);

        return matcher;
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new ExpensorDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "expense"
            case EXPENSE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        Tables.TABLENAME_EXPENSE,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case EXPENSE_WITH_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        Tables.TABLENAME_EXPENSE,
                        projection,
                        Tables.ID + " = '" + ContentUris.parseId(uri) + "'",
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case CATEGORIES: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        Tables.TABLENAME_CATEGORIES,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case CATEGORIES_WITH_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        Tables.TABLENAME_CATEGORIES,
                        projection,
                        Tables.ID + " = '" + ContentUris.parseId(uri) + "'",
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case EXPENSE:
                return ExpensorContract.ExpenseEntry.CONTENT_TYPE;
            case EXPENSE_WITH_ID:
                return ExpensorContract.ExpenseEntry.CONTENT_ITEM_TYPE;
            case CATEGORIES:
                return ExpensorContract.CategoriesEntry.CONTENT_TYPE;
            case CATEGORIES_WITH_ID:
                return ExpensorContract.CategoriesEntry.CONTENT_ITEM_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match){
            case EXPENSE: {
                long _id = db.insert(Tables.TABLENAME_EXPENSE, null, values);
                if(_id > 0)
                    returnUri = ExpensorContract.ExpenseEntry.buildExpenseUri(_id);
                else
                    throw new SQLException("Failed to insert to row into " + uri);
                break;
            }
            case CATEGORIES: {
                long _id = db.insert(Tables.TABLENAME_CATEGORIES, null, values);
                if(_id > 0)
                    returnUri = ExpensorContract.CategoriesEntry.buildCategoriesUri(_id);
                else
                    throw new SQLException("Failed to insert to row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        switch (match) {
            case EXPENSE:
                rowsDeleted = db.delete(Tables.TABLENAME_EXPENSE, selection, selectionArgs);
                break;
            case CATEGORIES:
                rowsDeleted = db.delete(Tables.TABLENAME_CATEGORIES, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case EXPENSE:
                rowsUpdated = db.update(Tables.TABLENAME_EXPENSE, values, selection, selectionArgs);
                break;
            case CATEGORIES:
                rowsUpdated = db.update(Tables.TABLENAME_CATEGORIES, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (selection == null || rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}
