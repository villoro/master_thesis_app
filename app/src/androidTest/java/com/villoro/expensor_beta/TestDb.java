package com.villoro.expensor_beta;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.villoro.expensor_beta.data.ExpensorDbHelper;
import com.villoro.expensor_beta.data.Tables;

import java.util.Map;
import java.util.Set;

/**
 * Created by Arnau on 19/01/2015.
 */
public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(ExpensorDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new ExpensorDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    public void testInsertReadDb() {

        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        ExpensorDbHelper dbHelper = new ExpensorDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues testValues = createCategoryFoodValues();
        long categoryId = db.insert(Tables.TABLENAME_CATEGORIES, null, testValues);

        // Verify we got a row back.
        assertTrue(categoryId != -1);
        Log.d(LOG_TAG, "New row id: " + categoryId);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.


        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                Tables.TABLENAME_CATEGORIES,  // Table to Query
                null,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        validateCursor(cursor, testValues);

        // Fantastic.  Now that we have a location, add some weather!
        ContentValues expenseValues = createExpenseValues(categoryId);

        long expenseId = db.insert(Tables.TABLENAME_EXPENSE, null, expenseValues);
        assertTrue(expenseId != -1);

        Cursor expenseCursor = db.query(
                Tables.TABLENAME_EXPENSE,
                null, // leaving "columns" null just returns all the columns.
                null, null, null, null, null
        );

        if (!expenseCursor.moveToFirst()) {
            fail("No weather data returned!");
        }

        validateCursor(expenseCursor, expenseValues);

        dbHelper.close();
    }


    static ContentValues createExpenseValues(long categoryId) {
        ContentValues expenseValues = new ContentValues();
        expenseValues.put(Tables.DATE, "20150119");
        expenseValues.put(Tables.CATEGORY_ID, categoryId);
        expenseValues.put(Tables.AMOUNT, 256);
        expenseValues.put(Tables.COMMENTS, "hot dog");
        expenseValues.put(Tables.FROM, "nidea");

        return expenseValues;
    }

    static ContentValues createCategoryFoodValues() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(Tables.LETTER, "F");
        testValues.put(Tables.NAME, "Food");
        testValues.put(Tables.TYPE, Tables.TYPE_EXPENSE);
        testValues.put(Tables.COLOR, 22);

        return testValues;
    }

    static void validateCursor(Cursor valueCursor, ContentValues expectedValues) {

        assertTrue(valueCursor.moveToFirst());

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue, valueCursor.getString(idx));
        }
        valueCursor.close();
    }
}