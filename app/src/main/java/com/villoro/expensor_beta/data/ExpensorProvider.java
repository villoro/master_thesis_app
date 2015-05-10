package com.villoro.expensor_beta.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Created by Arnau on 19/01/2015.
 */
public class ExpensorProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private ExpensorDbHelper mOpenHelper;

    private static final String GRAPH = ExpensorContract.GraphEntry.GRAPH;
    private static final String ALL = ExpensorContract.GraphEntry.ALL;

    //what I need to retrive
    private static final int EXPENSE = 100;
    private static final int EXPENSE_WITH_ID = 101;

    private static final int INCOME = 150;
    private static final int INCOME_WITH_ID = 151;

    private static final int GRAPH_EXPENSE = 200;
    private static final int GRAPH_INCOME = 201;
    private static final int GRAPH_EXPENSE_ALL = 202;
    private static final int GRAPH_INCOME_ALL = 203;

    private static final int CATEGORIES_EXPENSE = 300;
    private static final int CATEGORIES_INCOME = 301;
    private static final int CATEGORIES_WITH_ID = 302;

    private static final int PEOPLE = 400;
    private static final int PEOPLE_WITH_ID = 401;

    private static final int PEOPLE_IN_GROUP = 450;
    private static final int PEOPLE_IN_GROUP_WITH_ID = 451;

    private static final int GROUPS = 500;
    private static final int GROUPS_WITH_ID = 501;

    private static final int TRANSACTIONS_GROUP = 600;
    private static final int TRANSACTIONS_GROUP_WITH_ID = 601;

    private static final int TRANSACTIONS_PEOPLE = 700;
    private static final int TRANSACTIONS_PEOPLE_WITH_ID = 701;

    private static final int WHO_PAID_SPENT = 800;
    private static final int WHO_PAID_SPENT_WITH_ID = 801;

    private static final int HOW_TO_SETTLE = 900;
    private static final int HOW_TO_SETTLE_WITH_ID = 901;

    public static UriMatcher buildUriMatcher() {
        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ExpensorContract.CONTENT_AUTHORITY_EXPENSOR;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, Tables.TABLENAME_TRANSACTION_SIMPLE + "/" + Tables.TYPE_EXPENSE, EXPENSE);
        matcher.addURI(authority, Tables.TABLENAME_TRANSACTION_SIMPLE + "/" + Tables.TYPE_EXPENSE + "/#", EXPENSE_WITH_ID);

        matcher.addURI(authority, Tables.TABLENAME_TRANSACTION_SIMPLE + "/" + Tables.TYPE_INCOME, INCOME);
        matcher.addURI(authority, Tables.TABLENAME_TRANSACTION_SIMPLE + "/" + Tables.TYPE_INCOME + "/#", INCOME_WITH_ID);

        matcher.addURI(authority, GRAPH + "/" + Tables.TYPE_EXPENSE + "/#/#", GRAPH_EXPENSE);
        matcher.addURI(authority, GRAPH + "/" + Tables.TYPE_INCOME + "/#/#", GRAPH_INCOME);
        matcher.addURI(authority, GRAPH + "/" + Tables.TYPE_EXPENSE + "/" + ALL + "/#/#", GRAPH_EXPENSE_ALL);
        matcher.addURI(authority, GRAPH + "/" + Tables.TYPE_INCOME + "/" + ALL + "/#/#", GRAPH_INCOME_ALL);

        matcher.addURI(authority, Tables.TABLENAME_CATEGORIES + "/" + Tables.TYPE_EXPENSE, CATEGORIES_EXPENSE);
        matcher.addURI(authority, Tables.TABLENAME_CATEGORIES + "/" + Tables.TYPE_INCOME, CATEGORIES_INCOME);
        matcher.addURI(authority, Tables.TABLENAME_CATEGORIES + "/#", CATEGORIES_WITH_ID);

        matcher.addURI(authority, Tables.TABLENAME_PEOPLE, PEOPLE);
        matcher.addURI(authority, Tables.TABLENAME_PEOPLE + "/#", PEOPLE_WITH_ID);

        matcher.addURI(authority, Tables.TABLENAME_PEOPLE_IN_GROUP, PEOPLE_IN_GROUP);
        matcher.addURI(authority, Tables.TABLENAME_PEOPLE_IN_GROUP + "/#", PEOPLE_IN_GROUP_WITH_ID);

        matcher.addURI(authority, Tables.TABLENAME_GROUPS, GROUPS);
        matcher.addURI(authority, Tables.TABLENAME_GROUPS + "/#", GROUPS_WITH_ID);

        matcher.addURI(authority, Tables.TABLENAME_TRANSACTIONS_GROUP, TRANSACTIONS_GROUP);
        matcher.addURI(authority, Tables.TABLENAME_TRANSACTIONS_GROUP + "/#", TRANSACTIONS_GROUP_WITH_ID);

        matcher.addURI(authority, Tables.TABLENAME_TRANSACTIONS_PEOPLE, TRANSACTIONS_PEOPLE);
        matcher.addURI(authority, Tables.TABLENAME_TRANSACTIONS_PEOPLE + "/#", TRANSACTIONS_PEOPLE_WITH_ID);

        matcher.addURI(authority, Tables.TABLENAME_WHO_PAID_SPENT, WHO_PAID_SPENT);
        matcher.addURI(authority, Tables.TABLENAME_WHO_PAID_SPENT + "/#", WHO_PAID_SPENT_WITH_ID);

        matcher.addURI(authority, Tables.TABLENAME_HOW_TO_SETTLE, HOW_TO_SETTLE);
        matcher.addURI(authority, Tables.TABLENAME_HOW_TO_SETTLE + "/#", HOW_TO_SETTLE_WITH_ID);

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
                        Tables.TABLENAME_TRANSACTION_SIMPLE,
                        projection,
                        Tables.TYPE + " = '" + Tables.TYPE_EXPENSE + "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case EXPENSE_WITH_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        Tables.TABLENAME_TRANSACTION_SIMPLE,
                        projection,
                        Tables.ID + " = '" + ContentUris.parseId(uri) + "'",
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case INCOME: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        Tables.TABLENAME_TRANSACTION_SIMPLE,
                        projection,
                        Tables.TYPE + " = '" + Tables.TYPE_INCOME + "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case INCOME_WITH_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        Tables.TABLENAME_TRANSACTION_SIMPLE,
                        projection,
                        Tables.ID + " = '" + ContentUris.parseId(uri) + "'",
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case GRAPH_EXPENSE: {
                retCursor = mOpenHelper.getReadableDatabase().rawQuery(
                        ExpensorQueries.queryGraphExpense(
                                ExpensorContract.GraphEntry.getYearFromUri(uri),
                                ExpensorContract.GraphEntry.getMonthFromUri(uri)
                        ), null);
            }
            case GRAPH_INCOME: {
                retCursor = mOpenHelper.getReadableDatabase().rawQuery(
                        ExpensorQueries.queryGraphExpense(
                                ExpensorContract.GraphEntry.getYearFromUri(uri),
                                ExpensorContract.GraphEntry.getMonthFromUri(uri)
                        ), null);
            }
            case GRAPH_EXPENSE_ALL: {
                retCursor = mOpenHelper.getReadableDatabase().rawQuery(
                        ExpensorQueries.queryGraphExpense(
                                ExpensorContract.GraphEntry.getYearFromUriAll(uri),
                                ExpensorContract.GraphEntry.getMonthFromUriAll(uri)
                        ), null);
            }
            case GRAPH_INCOME_ALL: {
                retCursor = mOpenHelper.getReadableDatabase().rawQuery(
                        ExpensorQueries.queryGraphExpense(
                                ExpensorContract.GraphEntry.getYearFromUriAll(uri),
                                ExpensorContract.GraphEntry.getMonthFromUriAll(uri)
                        ), null);
            }
            case CATEGORIES_EXPENSE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        Tables.TABLENAME_CATEGORIES,
                        projection,
                        Tables.TYPE + " = '" + Tables.TYPE_EXPENSE + "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case CATEGORIES_INCOME: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        Tables.TABLENAME_CATEGORIES,
                        projection,
                        Tables.TYPE + " = '" + Tables.TYPE_INCOME + "'",
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
            case PEOPLE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        Tables.TABLENAME_PEOPLE,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case PEOPLE_IN_GROUP: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        Tables.TABLENAME_PEOPLE_IN_GROUP,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case GROUPS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        Tables.TABLENAME_GROUPS,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case TRANSACTIONS_GROUP: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        Tables.TABLENAME_TRANSACTIONS_GROUP,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case TRANSACTIONS_PEOPLE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        Tables.TABLENAME_TRANSACTIONS_PEOPLE,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case WHO_PAID_SPENT: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        Tables.TABLENAME_WHO_PAID_SPENT,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case HOW_TO_SETTLE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        Tables.TABLENAME_HOW_TO_SETTLE,
                        projection,
                        selection,
                        selectionArgs,
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

            case INCOME:
                return ExpensorContract.IncomeEntry.CONTENT_TYPE;
            case INCOME_WITH_ID:
                return ExpensorContract.IncomeEntry.CONTENT_ITEM_TYPE;

            case GRAPH_EXPENSE:
                return ExpensorContract.GraphEntry.CONTENT_ITEM_TYPE;
            case GRAPH_INCOME:
                return ExpensorContract.GraphEntry.CONTENT_ITEM_TYPE;
            case GRAPH_EXPENSE_ALL:
                return ExpensorContract.GraphEntry.CONTENT_TYPE;
            case GRAPH_INCOME_ALL:
                return ExpensorContract.GraphEntry.CONTENT_TYPE;

            case CATEGORIES_EXPENSE:
                return ExpensorContract.CategoriesEntry.CONTENT_TYPE;
            case CATEGORIES_INCOME:
                return ExpensorContract.CategoriesEntry.CONTENT_TYPE;
            case CATEGORIES_WITH_ID:
                return ExpensorContract.CategoriesEntry.CONTENT_ITEM_TYPE;

            case PEOPLE:
                return ExpensorContract.PeopleEntry.CONTENT_TYPE;
            case PEOPLE_WITH_ID:
                return ExpensorContract.PeopleEntry.CONTENT_ITEM_TYPE;

            case PEOPLE_IN_GROUP:
                return ExpensorContract.PeopleInGroupEntry.CONTENT_TYPE;
            case PEOPLE_IN_GROUP_WITH_ID:
                return ExpensorContract.PeopleInGroupEntry.CONTENT_ITEM_TYPE;

            case GROUPS:
                return ExpensorContract.GroupEntry.CONTENT_TYPE;
            case GROUPS_WITH_ID:
                return ExpensorContract.GroupEntry.CONTENT_ITEM_TYPE;

            case TRANSACTIONS_PEOPLE:
                return ExpensorContract.TransactionPeopleEntry.CONTENT_TYPE;
            case TRANSACTIONS_PEOPLE_WITH_ID:
                return ExpensorContract.TransactionPeopleEntry.CONTENT_ITEM_TYPE;

            case TRANSACTIONS_GROUP:
                return ExpensorContract.TransactionGroupEntry.CONTENT_TYPE;
            case TRANSACTIONS_GROUP_WITH_ID:
                return ExpensorContract.TransactionGroupEntry.CONTENT_ITEM_TYPE;

            case WHO_PAID_SPENT:
                return ExpensorContract.WhoPaidSpentEntry.CONTENT_TYPE;
            case WHO_PAID_SPENT_WITH_ID:
                return ExpensorContract.WhoPaidSpentEntry.CONTENT_ITEM_TYPE;

            case HOW_TO_SETTLE:
                return ExpensorContract.HowToSettleEntry.CONTENT_TYPE;
            case HOW_TO_SETTLE_WITH_ID:
                return ExpensorContract.HowToSettleEntry.CONTENT_ITEM_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        if (values.get(Tables.LAST_UPDATE) == null) {
            values.put(Tables.LAST_UPDATE, ExpensorContract.getDateUTC().getTime());
        }
        if (values.get(Tables.DELETED) == null) {
            values.put(Tables.DELETED, Tables.FALSE);
        }
        Log.e("", "insertant= " + values.toString());

        switch (match) {
            case EXPENSE: {
                values.put(Tables.TYPE, Tables.TYPE_EXPENSE);
                long _id = db.insert(Tables.TABLENAME_TRANSACTION_SIMPLE, null, values);
                if (_id > 0)
                    returnUri = ExpensorContract.ExpenseEntry.buildExpenseUri(_id);
                else
                    throw new SQLException("Failed to insert to row into " + uri);
                break;
            }
            case INCOME: {
                values.put(Tables.TYPE, Tables.TYPE_INCOME);
                long _id = db.insert(Tables.TABLENAME_TRANSACTION_SIMPLE, null, values);
                if (_id > 0)
                    returnUri = ExpensorContract.ExpenseEntry.buildExpenseUri(_id);
                else
                    throw new SQLException("Failed to insert to row into " + uri);
                break;
            }
            case CATEGORIES_EXPENSE: {
                if (db.query(
                        Tables.TABLENAME_CATEGORIES, new String[]{Tables.NAME},
                        Tables.NAME + " = '" + values.get(Tables.NAME).toString() + "'",
                        null, null, null, null).getCount() == 0) {
                    values.put(Tables.TYPE, Tables.TYPE_EXPENSE);
                    long _id = db.insert(Tables.TABLENAME_CATEGORIES, null, values);
                    if (_id > 0)
                        returnUri = ExpensorContract.CategoriesEntry.buildCategoriesUri(_id);
                    else
                        throw new SQLException("Failed to insert to row into " + uri);
                } else {
                    Log.e("", "ja hi ha un amb aquest nom");
                    returnUri = uri;
                }

                break;
            }
            case CATEGORIES_INCOME: {
                if (db.query(
                        Tables.TABLENAME_CATEGORIES, new String[]{Tables.NAME},
                        Tables.NAME + " = '" + values.get(Tables.NAME).toString() + "'",
                        null, null, null, null).getCount() == 0) {
                    values.put(Tables.TYPE, Tables.TYPE_INCOME);
                    long _id = db.insert(Tables.TABLENAME_CATEGORIES, null, values);
                    if (_id > 0)
                        returnUri = ExpensorContract.CategoriesEntry.buildCategoriesUri(_id);
                    else
                        throw new SQLException("Failed to insert to row into " + uri);
                } else {
                    Log.e("", "ja hi ha un amb aquest nom");
                    returnUri = uri;
                }

                break;
            }
            case PEOPLE: {
                if (db.query(
                        Tables.TABLENAME_PEOPLE, new String[]{Tables.EMAIL},
                        Tables.EMAIL + " = '" + values.get(Tables.EMAIL).toString() + "'",
                        null, null, null, null).getCount() == 0) {
                    long _id = db.insert(Tables.TABLENAME_PEOPLE, null, values);
                    if (_id  > 0)
                        returnUri = ExpensorContract.PeopleEntry.buildPeopleUri(_id);
                    else
                        throw new SQLException("Failed to insert to row into " + uri);
                } else {
                    Log.e("", "ja hi ha un amb aquest email");
                    returnUri = uri;
                }

                break;
            }
            case GROUPS: {
                if (db.query(
                        Tables.TABLENAME_GROUPS, new String[]{Tables.NAME},
                        Tables.NAME + " = '" + values.get(Tables.NAME).toString() + "'",
                        null, null, null, null).getCount() == 0) {
                    long _id = db.insert(Tables.TABLENAME_GROUPS, null, values);
                    if (_id > 0)
                        returnUri = ExpensorContract.GroupEntry.buildGroupUri(_id);
                    else
                        throw new SQLException("Failed to insert to row into " + uri);
                } else {
                    Log.e("", "ja hi ha un amb aquest nom");
                    returnUri = uri;
                }

                break;
            }
            case PEOPLE_IN_GROUP: {
                //TODO check if person is in group
                long _id = db.insert(Tables.TABLENAME_PEOPLE_IN_GROUP, null, values);
                if (_id > 0)
                    returnUri = ExpensorContract.PeopleInGroupEntry.buildPeopleInGroupUri(_id);
                else
                    throw new SQLException("Failed to insert to row into " + uri);
                break;
            }
            case TRANSACTIONS_GROUP: {
                long _id = db.insert(Tables.TABLENAME_TRANSACTIONS_GROUP, null, values);
                if (_id > 0)
                    returnUri = ExpensorContract.TransactionGroupEntry.buildTransactionGroupUri(_id);
                else
                    throw new SQLException("Failed to insert to row into " + uri);
                break;
            }
            case TRANSACTIONS_PEOPLE: {
                long _id = db.insert(Tables.TABLENAME_TRANSACTIONS_PEOPLE, null, values);
                if (_id > 0)
                    returnUri = ExpensorContract.TransactionPeopleEntry.buildTransactionPeopleUri(_id);
                else
                    throw new SQLException("Failed to insert to row into " + uri);
                break;
            }
            case WHO_PAID_SPENT: {
                //TODO check if that person is in transaction
                long _id = db.insert(Tables.TABLENAME_WHO_PAID_SPENT, null, values);
                if (_id > 0)
                    returnUri = ExpensorContract.WhoPaidSpentEntry.buildWhoPaidSpentUri(_id);
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

        ContentValues values = new ContentValues();
        values.put(Tables.DELETED, Tables.TRUE);
        if (values.get(Tables.LAST_UPDATE) == null) {
            values.put(Tables.LAST_UPDATE, ExpensorContract.getDateUTC().getTime());
        }

        switch (match) {
            case EXPENSE:
                rowsDeleted = db.update(Tables.TABLENAME_TRANSACTION_SIMPLE, values, selection, selectionArgs);
                break;
            case INCOME:
                rowsDeleted = db.update(Tables.TABLENAME_TRANSACTION_SIMPLE, values, selection, selectionArgs);
                break;
            case CATEGORIES_EXPENSE:
                rowsDeleted = db.update(Tables.TABLENAME_CATEGORIES, values, selection, selectionArgs);
                break;
            case CATEGORIES_INCOME:
                rowsDeleted = db.update(Tables.TABLENAME_CATEGORIES, values, selection, selectionArgs);
                break;
            case PEOPLE:
                rowsDeleted = db.update(Tables.TABLENAME_PEOPLE, values, selection, selectionArgs);
                break;
            case PEOPLE_IN_GROUP:
                rowsDeleted = db.update(Tables.TABLENAME_PEOPLE_IN_GROUP, values, selection, selectionArgs);
                break;
            case GROUPS:
                rowsDeleted = db.update(Tables.TABLENAME_GROUPS, values, selection, selectionArgs);
                break;
            case TRANSACTIONS_PEOPLE:
                rowsDeleted = db.update(Tables.TABLENAME_TRANSACTIONS_PEOPLE, values, selection, selectionArgs);
                break;
            case TRANSACTIONS_GROUP:
                rowsDeleted = db.update(Tables.TABLENAME_TRANSACTIONS_GROUP, values, selection, selectionArgs);
                break;
            case WHO_PAID_SPENT:
                rowsDeleted = db.update(Tables.TABLENAME_WHO_PAID_SPENT, values, selection, selectionArgs);
                break;
            case HOW_TO_SETTLE:
                rowsDeleted = db.update(Tables.TABLENAME_HOW_TO_SETTLE, values, selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        Log.d("ExpensorProvider", "rows deleted= " + rowsDeleted);
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

        if (values.get(Tables.LAST_UPDATE) == null) {
            values.put(Tables.LAST_UPDATE, ExpensorContract.getDateUTC().getTime());
        }
        if (values.get(Tables.DELETED) == null) {
            values.put(Tables.DELETED, Tables.FALSE);
        }

        switch (match) {
            case EXPENSE:
                rowsUpdated = db.update(Tables.TABLENAME_TRANSACTION_SIMPLE, values, selection, selectionArgs);
                break;
            case INCOME:
                rowsUpdated = db.update(Tables.TABLENAME_TRANSACTION_SIMPLE, values, selection, selectionArgs);
                break;
            case CATEGORIES_EXPENSE:
                rowsUpdated = db.update(Tables.TABLENAME_CATEGORIES, values, selection, selectionArgs);
                break;
            case PEOPLE:
                rowsUpdated = db.update(Tables.TABLENAME_PEOPLE, values, selection, selectionArgs);
                break;
            case PEOPLE_IN_GROUP:
                rowsUpdated = db.update(Tables.TABLENAME_PEOPLE_IN_GROUP, values, selection, selectionArgs);
                break;
            case GROUPS:
                rowsUpdated = db.update(Tables.TABLENAME_GROUPS, values, selection, selectionArgs);
                break;
            case TRANSACTIONS_GROUP:
                rowsUpdated = db.update(Tables.TABLENAME_TRANSACTIONS_GROUP, values, selection, selectionArgs);
                break;
            case TRANSACTIONS_PEOPLE:
                rowsUpdated = db.update(Tables.TABLENAME_TRANSACTIONS_PEOPLE, values, selection, selectionArgs);
                break;
            case WHO_PAID_SPENT:
                rowsUpdated = db.update(Tables.TABLENAME_WHO_PAID_SPENT, values, selection, selectionArgs);
                break;
            case HOW_TO_SETTLE:
                rowsUpdated = db.update(Tables.TABLENAME_WHO_PAID_SPENT, values, selection, selectionArgs);
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