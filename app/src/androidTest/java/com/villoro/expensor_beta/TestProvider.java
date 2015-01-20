package com.villoro.expensor_beta;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.test.AndroidTestCase;
import android.util.Log;

import com.villoro.expensor_beta.data.ExpensorContract;
import com.villoro.expensor_beta.data.ExpensorProvider;
import com.villoro.expensor_beta.data.Tables;

/**
 * Created by Arnau on 19/01/2015.
 */
public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    // brings our database to an empty state
    public void deleteAllRecords() {
        Log.e("", ExpensorContract.ExpenseEntry.CONTENT_URI.toString());

        mContext.getContentResolver().delete(
                ExpensorContract.CategoriesEntry.CONTENT_URI,
                null,
                null
        );

        mContext.getContentResolver().delete(
                ExpensorContract.ExpenseEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                ExpensorContract.ExpenseEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals(0, cursor.getCount());
        cursor.close();


        cursor = mContext.getContentResolver().query(
                ExpensorContract.CategoriesEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals(0, cursor.getCount());
        cursor.close();
    }

    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    public void setUp() {
        deleteAllRecords();

    }


    public void testInsertReadProvider() {

        ContentValues testValues = TestDb.createCategoryFoodValues();

        Uri categoriesUri = mContext.getContentResolver().insert(ExpensorContract.CategoriesEntry.CONTENT_URI, testValues);

        //Log.d("", categoriesUri.toString());
        long categoriesRowId = ContentUris.parseId(categoriesUri);

        // Verify we got a row back.
        assertTrue(categoriesRowId != -1);


        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                ExpensorContract.CategoriesEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestDb.validateCursor(cursor, testValues);

        // Now see if we can successfully query if we include the row id
        cursor = mContext.getContentResolver().query(
                ExpensorContract.CategoriesEntry.buildCategoriesUri(categoriesRowId),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestDb.validateCursor(cursor, testValues);

        // Fantastic.  Now that we have a location, add some weather!
        ContentValues expenseValues = TestDb.createExpenseValues(categoriesRowId);

        Uri expenseInsertUri = mContext.getContentResolver()
                .insert(ExpensorContract.ExpenseEntry.CONTENT_URI, expenseValues);
        assertTrue(expenseInsertUri != null);

        // A cursor is your primary interface to the query results.
        Cursor expenseCursor = mContext.getContentResolver().query(
                ExpensorContract.ExpenseEntry.CONTENT_URI,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestDb.validateCursor(expenseCursor, expenseValues);
    }




    public void testGetType() {
        // content://com.example.android.sunshine.app/weather/
        String type = mContext.getContentResolver().getType(ExpensorContract.ExpenseEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        assertEquals(ExpensorContract.ExpenseEntry.CONTENT_TYPE, type);

        long testId = 2;
        // content://com.example.android.sunshine.app/weather/94074
        type = mContext.getContentResolver().getType(
                ExpensorContract.ExpenseEntry.buildExpenseUri(testId));
        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        assertEquals(ExpensorContract.ExpenseEntry.CONTENT_ITEM_TYPE, type);

        // content://com.example.android.sunshine.app/location/
        type = mContext.getContentResolver().getType(ExpensorContract.CategoriesEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.example.android.sunshine.app/location
        assertEquals(ExpensorContract.CategoriesEntry.CONTENT_TYPE, type);

        // content://com.example.android.sunshine.app/location/1
        type = mContext.getContentResolver().getType(ExpensorContract.CategoriesEntry.buildCategoriesUri(1));
        // vnd.android.cursor.item/com.example.android.sunshine.app/location
        assertEquals(ExpensorContract.CategoriesEntry.CONTENT_ITEM_TYPE, type);
    }


    public void testUpdateCategory() {
        // Create a new map of values, where column names are the keys
        ContentValues values = TestDb.createCategoryFoodValues();

        Uri categoryId = mContext.getContentResolver().
                insert(ExpensorContract.CategoriesEntry.CONTENT_URI, values);
        long categoryRowId = ContentUris.parseId(categoryId);

        // Verify we got a row back.
        assertTrue(categoryRowId != -1);
        Log.d(LOG_TAG, "New row id: " + categoryRowId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(Tables.ID, categoryRowId);
        updatedValues.put(Tables.NAME, "Transport");

        int count = mContext.getContentResolver().update(
                ExpensorContract.CategoriesEntry.CONTENT_URI, updatedValues, Tables.ID + "= ?",
                new String[] { Long.toString(categoryRowId)});

        assertEquals(count, 1);

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                ExpensorContract.CategoriesEntry.buildCategoriesUri(categoryRowId),
                null,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null // sort order
        );

        TestDb.validateCursor(cursor, updatedValues);
    }


    // Make sure we can still delete after adding/updating stuff
    public void testDeleteRecordsAtEnd() {
        deleteAllRecords();
    }

    // Helper Methods

    // The target api annotation is needed for the call to keySet -- we wouldn't want
    // to use this in our app, but in a test it's fine to assume a higher target.
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    void addAllContentValues(ContentValues destination, ContentValues source) {
        for (String key : source.keySet()) {
            destination.put(key, source.getAsString(key));
        }
    }

    //static final String KALAMAZOO_LOCATION_SETTING = "kalamazoo";
    //static final String KALAMAZOO_WEATHER_START_DATE = "20140625";


    long locationRowId;
    static ContentValues createAnotherExpenseValues(long categoryId) {
        ContentValues expenseValues = new ContentValues();
        expenseValues.put(Tables.DATE, "20150119");
        expenseValues.put(Tables.CATEGORY_ID, categoryId);
        expenseValues.put(Tables.AMOUNT, 256);
        expenseValues.put(Tables.COMMENTS, "hot dog");
        expenseValues.put(Tables.FROM, "nidea");

        return expenseValues;
    }

    static ContentValues createAnotherCategoryValues() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(Tables.LETTER, "F");
        testValues.put(Tables.NAME, "Food");
        testValues.put(Tables.TYPE, Tables.TYPE_EXPENSE);
        testValues.put(Tables.COLOR, 22);

        return testValues;
    }
}
